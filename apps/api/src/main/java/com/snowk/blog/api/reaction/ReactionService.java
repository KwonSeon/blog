package com.snowk.blog.api.reaction;

import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReactionService {
    private final JdbcTemplate jdbc;
    public ReactionService(JdbcTemplate jdbc) { this.jdbc = jdbc; }
    public record State(long count, boolean liked, boolean authenticated) {}
    private record Target(String table, String id, String likes, String status) {}
    private Target target(String kind) {
        return switch (kind) {
            case "posts" -> new Target("posts", "post_id", "post_likes", "PUBLISHED");
            case "projects" -> new Target("projects", "project_id", "project_likes", "ACTIVE");
            default -> throw new BaseException(CommonErrorStatus.RESOURCE_NOT_FOUND);
        };
    }
    public static List<String> subjects(String canonical, List<String> merged) {
        if (canonical == null) return List.of();
        var all = new LinkedHashSet<String>(); all.add(canonical);
        if (merged != null) all.addAll(merged);
        if (all.size() > 1000 || all.stream().anyMatch(s -> s == null || !s.matches("[0-9]{1,20}")))
            throw new BaseException(CommonErrorStatus.BAD_REQUEST);
        return List.copyOf(all);
    }
    private void requirePublic(Target t, long id, boolean lock) {
        var ids = jdbc.queryForList("SELECT " + t.id + " FROM " + t.table + " WHERE " + t.id
                + " = ? AND visibility = 'PUBLIC' AND status = ?" + (lock ? " FOR UPDATE" : ""), Long.class, id, t.status);
        if (ids.isEmpty()) throw new BaseException(CommonErrorStatus.RESOURCE_NOT_FOUND);
    }
    private State state(Target t, long id, List<String> subjects) {
        long count = jdbc.queryForObject("SELECT COUNT(*) FROM " + t.likes + " WHERE " + t.id + " = ?", Long.class, id);
        boolean liked = false;
        if (!subjects.isEmpty()) {
            var args = new ArrayList<Object>(); args.add(id); args.addAll(subjects);
            liked = jdbc.queryForObject("SELECT COUNT(*) FROM " + t.likes + " WHERE " + t.id + " = ? AND identity_subject IN ("
                    + String.join(",", Collections.nCopies(subjects.size(), "?")) + ")", Long.class, args.toArray()) > 0;
        }
        return new State(count, liked, !subjects.isEmpty());
    }
    @Transactional(readOnly = true)
    public State read(String kind, long id, List<String> subjects) {
        var t = target(kind); requirePublic(t, id, false); return state(t, id, subjects);
    }
    @Transactional
    public State set(String kind, long id, List<String> subjects, boolean liked) {
        if (subjects.isEmpty()) throw new BaseException(CommonErrorStatus.BAD_REQUEST);
        var t = target(kind); requirePublic(t, id, true);
        // Parent-row lock serializes all writes and publication/deletion changes for this content.
        var args = new ArrayList<Object>(); args.add(id); args.addAll(subjects);
        jdbc.update("DELETE FROM " + t.likes + " WHERE " + t.id + " = ? AND identity_subject IN ("
                + String.join(",", Collections.nCopies(subjects.size(), "?")) + ")", args.toArray());
        if (liked) jdbc.update("INSERT INTO " + t.likes + " (" + t.id + ", identity_subject) VALUES (?, ?)", id, subjects.getFirst());
        var result = state(t, id, subjects);
        jdbc.update("UPDATE " + t.table + " SET like_count = ?, updated_at = updated_at WHERE " + t.id + " = ?", result.count, id);
        return result;
    }
}
