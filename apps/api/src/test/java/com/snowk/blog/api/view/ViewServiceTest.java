package com.snowk.blog.api.view;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import org.junit.jupiter.api.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.*;
import org.springframework.transaction.support.TransactionTemplate;
import static org.junit.jupiter.api.Assertions.*;
class ViewServiceTest {
    JdbcTemplate jdbc;ViewService service;TransactionTemplate tx;LocalDate day=LocalDate.of(2026,11,14);
    @BeforeEach void setup() {
        var ds=new DriverManagerDataSource("jdbc:h2:mem:"+UUID.randomUUID()+";MODE=MySQL;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=10000","sa","");
        jdbc=new JdbcTemplate(ds);service=new ViewService(jdbc);tx=new TransactionTemplate(new DataSourceTransactionManager(ds));
        for(String kind:List.of("post","project")) {
            jdbc.execute("CREATE TABLE "+kind+"s ("+kind+"_id BIGINT PRIMARY KEY, visibility VARCHAR(20),status VARCHAR(20),view_count BIGINT DEFAULT 0,updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            jdbc.execute("CREATE TABLE "+kind+"_views ("+kind+"_id BIGINT,view_day DATE,visitor_hash CHAR(64),PRIMARY KEY("+kind+"_id,view_day,visitor_hash),FOREIGN KEY("+kind+"_id) REFERENCES "+kind+"s("+kind+"_id) ON DELETE CASCADE)");
            jdbc.update("INSERT INTO "+kind+"s ("+kind+"_id,visibility,status) VALUES (1,'PUBLIC',?)",kind.equals("post")?"PUBLISHED":"ACTIVE");
        }
    }
    long record(String kind,String visitor,LocalDate date) {return tx.execute(s->service.record(kind,1,visitor.repeat(64),date)).count();}
    @Test void duplicateVisitorsCountOncePerUtcDay() {
        assertEquals(1,record("posts","a",day));assertEquals(1,record("posts","a",day));assertEquals(2,record("posts","b",day));assertEquals(3,record("posts","a",day.plusDays(1)));assertEquals(1,record("projects","a",day));assertEquals(3,service.read("posts",1).count());
    }
    @Test void readsNeverIncrementAndUpdatesPreserveContentTime() {
        var time=jdbc.queryForObject("SELECT updated_at FROM posts",java.sql.Timestamp.class);assertEquals(0,service.read("posts",1).count());record("posts","a",day);assertEquals(time,jdbc.queryForObject("SELECT updated_at FROM posts",java.sql.Timestamp.class));
    }
    @Test void privateAndUnpublishedNeverCount() {
        for(String kind:List.of("posts","projects")) {
            jdbc.update("UPDATE "+kind+" SET visibility='PRIVATE'");assertThrows(RuntimeException.class,()->record(kind,"a",day));assertThrows(RuntimeException.class,()->service.read(kind,1));
            jdbc.update("UPDATE "+kind+" SET visibility='PUBLIC',status='DRAFT'");assertThrows(RuntimeException.class,()->record(kind,"a",day));
        }assertThrows(RuntimeException.class,()->service.read("posts",9));
    }
    @Test void concurrentDuplicatesAreSerialized() throws Exception {
        try(var pool=Executors.newFixedThreadPool(8)) {
            var jobs=new ArrayList<Callable<Long>>();for(int i=0;i<32;i++)jobs.add(()->record("posts","a",day));for(var result:pool.invokeAll(jobs))assertEquals(1,result.get());
        }assertEquals(1,service.read("posts",1).count());
    }
    @Test void retentionRemovesIdentifiersButKeepsLifetimeTotalAndCascadeWorks() {
        record("posts","a",day.minusDays(2));record("posts","a",day.minusDays(1));record("posts","a",day);service.prune(day);assertEquals(2,jdbc.queryForObject("SELECT COUNT(*) FROM post_views",Long.class));assertEquals(3,service.read("posts",1).count());jdbc.update("DELETE FROM posts");assertEquals(0,jdbc.queryForObject("SELECT COUNT(*) FROM post_views",Long.class));
    }
    @Test void malformedIdentifiersAndUnknownTypesFail() {
        assertThrows(RuntimeException.class,()->service.record("posts",1,"bad",day));assertThrows(RuntimeException.class,()->service.read("posts;DROP TABLE posts",1));
    }
}
