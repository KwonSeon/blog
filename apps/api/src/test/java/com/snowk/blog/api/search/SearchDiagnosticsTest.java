package com.snowk.blog.api.search;

import static org.junit.jupiter.api.Assertions.*;

import com.snowk.blog.api.post.application.query.ListPublicPostsQuery;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

class SearchDiagnosticsTest {
    @Test
    void requiresContiguousTermsAndDoesNotForwardSearchOperators() {
        assertEquals("+\"한국어\" +\"Spring\"", SearchTerms.booleanPhrase("한국어 Spring"));
        assertEquals("+\"hello\"", SearchTerms.booleanPhrase("+hello* -"));
        assertEquals("", SearchTerms.booleanPhrase("%"));
        assertEquals("", SearchTerms.booleanPhrase("a"));
    }

    @Test
    void validatesSearchInputWithoutTreatingWildcardsAsSyntax() {
        assertThrows(RuntimeException.class, () -> new ListPublicPostsQuery("a".repeat(81), null));
        assertThrows(RuntimeException.class, () -> new ListPublicPostsQuery("hello\nworld", null));
        assertThrows(RuntimeException.class, () -> new ListPublicPostsQuery("hello", "x' OR 1=1"));
        assertEquals("100%_", new ListPublicPostsQuery(" 100%_ ", null).q());
    }

    @Test
    void aggregatesOnlyBoundedBucketsAndPrunesOldDays() {
        var db =
                new JdbcTemplate(
                        new DriverManagerDataSource(
                                "jdbc:h2:mem:search;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
                                "sa",
                                ""));
        db.execute(
                "CREATE TABLE search_daily_stats(search_day DATE,language_bucket"
                        + " VARCHAR(8),length_bucket VARCHAR(8),result_bucket VARCHAR(8),requests"
                        + " BIGINT,PRIMARY"
                        + " KEY(search_day,language_bucket,length_bucket,result_bucket))");
        var service = new SearchDiagnostics(db, true);
        service.record(new ListPublicPostsQuery("private@example.test", "ko"), 0);
        service.record(new ListPublicPostsQuery("different@other.test", "ko"), 0);
        assertEquals(
                1, db.queryForObject("SELECT COUNT(*) FROM search_daily_stats", Integer.class));
        assertEquals(
                2, db.queryForObject("SELECT requests FROM search_daily_stats", Integer.class));
        db.update("UPDATE search_daily_stats SET search_day='2000-01-01'");
        service.prune();
        assertEquals(
                0, db.queryForObject("SELECT COUNT(*) FROM search_daily_stats", Integer.class));
    }
}
