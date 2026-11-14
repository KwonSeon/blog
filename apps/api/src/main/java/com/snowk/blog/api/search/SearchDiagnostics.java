package com.snowk.blog.api.search;

import com.snowk.blog.api.post.application.query.ListPublicPostsQuery;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;

import java.time.*;

@Service
public class SearchDiagnostics {
    private final JdbcTemplate jdbc;
    private final boolean enabled;

    public SearchDiagnostics(
            JdbcTemplate jdbc, @Value("${BLOG_SEARCH_DIAGNOSTICS:false}") boolean enabled) {
        this.jdbc = jdbc;
        this.enabled = enabled;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(ListPublicPostsQuery query, int results) {
        if (!enabled || !query.hasQuery()) return;
        int length = query.q().codePointCount(0, query.q().length());
        String lengthBucket =
                length <= 2 ? "1-2" : length <= 10 ? "3-10" : length <= 30 ? "11-30" : "31-80";
        String resultBucket =
                results == 0 ? "0" : results <= 5 ? "1-5" : results < 100 ? "6-99" : "100+";
        // Deliberately omit query text, IP, cookies and account identifiers.
        jdbc.update(
                "INSERT INTO"
                    + " search_daily_stats(search_day,language_bucket,length_bucket,result_bucket,requests)"
                    + " VALUES(?,?,?,?,1) ON DUPLICATE KEY UPDATE requests=requests+1",
                LocalDate.now(ZoneOffset.UTC),
                query.lang() == null ? "all" : query.lang().equals("ko") ? "ko" : "other",
                lengthBucket,
                resultBucket);
    }

    @Scheduled(initialDelay = 180000, fixedDelay = 3600000)
    public void prune() {
        if (enabled)
            jdbc.update(
                    "DELETE FROM search_daily_stats WHERE search_day<?",
                    LocalDate.now(ZoneOffset.UTC).minusDays(30));
    }
}
