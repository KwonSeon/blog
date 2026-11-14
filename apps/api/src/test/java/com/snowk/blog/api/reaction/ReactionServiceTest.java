package com.snowk.blog.api.reaction;

import java.util.*;
import java.util.concurrent.*;
import javax.sql.DataSource;
import org.junit.jupiter.api.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import static org.junit.jupiter.api.Assertions.*;

class ReactionServiceTest {
    JdbcTemplate jdbc; ReactionService service; TransactionTemplate transaction;
    @BeforeEach void setup() {
        DataSource ds = new DriverManagerDataSource("jdbc:h2:mem:"+UUID.randomUUID()+";MODE=MySQL;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=10000", "sa", "");
        jdbc = new JdbcTemplate(ds); service = new ReactionService(jdbc); transaction = new TransactionTemplate(new DataSourceTransactionManager(ds));
        for (String kind : List.of("post", "project")) {
            jdbc.execute("CREATE TABLE "+kind+"s ("+kind+"_id BIGINT PRIMARY KEY, visibility VARCHAR(20), status VARCHAR(20), like_count BIGINT DEFAULT 0, updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            jdbc.execute("CREATE TABLE "+kind+"_likes ("+kind+"_id BIGINT, identity_subject VARCHAR(64), PRIMARY KEY ("+kind+"_id,identity_subject), FOREIGN KEY ("+kind+"_id) REFERENCES "+kind+"s("+kind+"_id) ON DELETE CASCADE)");
            jdbc.update("INSERT INTO "+kind+"s ("+kind+"_id,visibility,status) VALUES (1,'PUBLIC',?)",kind.equals("post") ? "PUBLISHED" : "ACTIVE");
        }
    }
    ReactionService.State set(String kind, List<String> ids, boolean liked) { return transaction.execute(s -> service.set(kind,1,ids,liked)); }
    @Test void repeatedLikeAndUnlikeAreIdempotent() {
        assertEquals(1,set("posts",List.of("10"),true).count());
        assertEquals(1,set("posts",List.of("10"),true).count());
        assertEquals(0,set("posts",List.of("10"),false).count());
        assertEquals(0,set("posts",List.of("10"),false).count());
    }
    @Test void usersAndContentTypesRemainIndependent() {
        set("posts",List.of("10"),true);set("posts",List.of("20"),true);set("projects",List.of("10"),true);
        assertEquals(1,set("posts",List.of("10"),false).count());
        assertTrue(service.read("posts",1,List.of("20")).liked());
        assertEquals(1,service.read("projects",1,List.of()).count());
    }
    @Test void anonymousReadNeverLeaksMembers() {
        set("posts",List.of("10"),true);var state=service.read("posts",1,List.of());
        assertEquals(1,state.count());assertFalse(state.liked());assertFalse(state.authenticated());
        assertThrows(RuntimeException.class,()->set("posts",List.of(),true));
    }
    @Test void privateDraftInactiveAndMissingTargetsRejectReadsAndWrites() {
        for(String kind:List.of("posts","projects")) {
            jdbc.update("UPDATE "+kind+" SET visibility='PRIVATE'");
            assertThrows(RuntimeException.class,()->service.read(kind,1,List.of()));
            assertThrows(RuntimeException.class,()->set(kind,List.of("10"),true));
            jdbc.update("UPDATE "+kind+" SET visibility='PUBLIC', status=?",kind.equals("posts")?"DRAFT":"ARCHIVED");
            assertThrows(RuntimeException.class,()->service.read(kind,1,List.of()));
            assertThrows(RuntimeException.class,()->set(kind,List.of("10"),true));
        }
        assertThrows(RuntimeException.class,()->service.read("posts",99,List.of()));
        assertThrows(RuntimeException.class,()->service.read("posts;DROP TABLE posts",1,List.of()));
    }
    @Test void deletionCascadesAndCountMatchesRows() {
        set("posts",List.of("10"),true);set("posts",List.of("20"),true);
        assertEquals(2,jdbc.queryForObject("SELECT like_count FROM posts",Long.class));
        jdbc.update("DELETE FROM posts WHERE post_id=1");assertEquals(0,jdbc.queryForObject("SELECT COUNT(*) FROM post_likes",Long.class));
    }
    @Test void mergedSubjectsAreRecognizedAndDeduplicatedOnInteraction() {
        set("posts",List.of("10"),true);set("posts",List.of("20"),true);
        var aliases=ReactionService.subjects("20",List.of("10","20"));
        assertTrue(service.read("posts",1,aliases).liked());
        assertEquals(1,set("posts",aliases,true).count());
        assertEquals("20",jdbc.queryForObject("SELECT identity_subject FROM post_likes",String.class));
        assertEquals(0,set("posts",aliases,false).count());
    }
    @Test void parallelDuplicateRequestsDoNotInflateCount() throws Exception {
        try(var pool=Executors.newFixedThreadPool(8)) {
            List<Callable<ReactionService.State>> tasks=new ArrayList<>();
            for(int i=0;i<32;i++) tasks.add(()->set("posts",List.of("10"),true));
            for(var f:pool.invokeAll(tasks))assertEquals(1,f.get().count());
        }
        assertEquals(1,jdbc.queryForObject("SELECT like_count FROM posts",Long.class));
    }
    @Test void malformedSignedSubjectValuesAreRejected() {
        assertThrows(RuntimeException.class,()->ReactionService.subjects("x",List.of()));
        assertThrows(RuntimeException.class,()->ReactionService.subjects("10",List.of("10') OR 1=1")));
        assertEquals(List.of("10","20"),ReactionService.subjects("10",List.of("10","20")));
    }
}
