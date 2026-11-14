package com.snowk.blog.api.view;

import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;
import java.time.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ViewService {
    private final JdbcTemplate jdbc;
    public ViewService(JdbcTemplate jdbc) { this.jdbc = jdbc; }
    public record State(long count) {}
    private record Target(String table, String id, String views, String status) {}
    private Target target(String kind) {
        return switch(kind) {
            case "posts" -> new Target("posts","post_id","post_views","PUBLISHED");
            case "projects" -> new Target("projects","project_id","project_views","ACTIVE");
            default -> throw new BaseException(CommonErrorStatus.RESOURCE_NOT_FOUND);
        };
    }
    private long count(Target t, long id, boolean lock) {
        var rows = jdbc.queryForList("SELECT view_count FROM "+t.table+" WHERE "+t.id+"=? AND visibility='PUBLIC' AND status=?"+(lock?" FOR UPDATE":""),Long.class,id,t.status);
        if(rows.isEmpty()) throw new BaseException(CommonErrorStatus.RESOURCE_NOT_FOUND);
        return rows.getFirst();
    }
    @Transactional(readOnly=true)
    public State read(String kind,long id) { return new State(count(target(kind),id,false)); }
    @Transactional
    public State record(String kind,long id,String visitor,LocalDate day) {
        if(visitor==null || !visitor.matches("[a-f0-9]{64}")) throw new BaseException(CommonErrorStatus.BAD_REQUEST);
        var t=target(kind);long total=count(t,id,true);
        long duplicates=jdbc.queryForObject("SELECT COUNT(*) FROM "+t.views+" WHERE "+t.id+"=? AND view_day=? AND visitor_hash=?",Long.class,id,day,visitor);
        if(duplicates==0) {
            jdbc.update("INSERT INTO "+t.views+" ("+t.id+",view_day,visitor_hash) VALUES (?,?,?)",id,day,visitor);
            jdbc.update("UPDATE "+t.table+" SET view_count=view_count+1, updated_at=updated_at WHERE "+t.id+"=?",id);
            total++;
        }
        return new State(total);
    }
    @Transactional
    public void prune(LocalDate today) {
        // Keep today's and yesterday's keys; lifetime totals remain on the parent.
        for(String table:new String[]{"post_views","project_views"}) jdbc.update("DELETE FROM "+table+" WHERE view_day < ?",today.minusDays(1));
    }
}
