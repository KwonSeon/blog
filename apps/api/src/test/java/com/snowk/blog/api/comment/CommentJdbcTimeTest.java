package com.snowk.blog.api.comment;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.*;

class CommentJdbcTimeTest {
    @Test
    void mysqlJavaTimeAndH2LegacyTypesProduceSameUtcValues() {
        var date = LocalDate.of(2026, 11, 14);
        var datetime = date.atTime(12, 34, 56);
        assertEquals(datetime, CommentService.dateTime(datetime));
        assertEquals(datetime, CommentService.dateTime(Timestamp.valueOf(datetime)));
        assertEquals(date, CommentService.date(date));
        assertEquals(date, CommentService.date(java.sql.Date.valueOf(date)));
    }
}
