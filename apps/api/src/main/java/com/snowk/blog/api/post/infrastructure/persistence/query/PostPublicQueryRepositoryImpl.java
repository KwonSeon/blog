package com.snowk.blog.api.post.infrastructure.persistence.query;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.snowk.blog.api.common.domain.enumtype.Visibility;
import com.snowk.blog.api.post.application.query.ListPublicPostsQuery;
import com.snowk.blog.api.post.domain.entity.Post;
import com.snowk.blog.api.post.domain.entity.QPost;
import com.snowk.blog.api.post.domain.enumtype.PostStatus;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostPublicQueryRepositoryImpl implements PostPublicQueryRepository {

    private static final QPost post = QPost.post;

    private final JPAQueryFactory queryFactory;
    private final jakarta.persistence.EntityManager entityManager;

    @org.springframework.beans.factory.annotation.Value("${BLOG_SEARCH_FULLTEXT:false}")
    private boolean fulltext;

    @Override
    public List<Post> findPublicPosts(ListPublicPostsQuery query) {
        if (fulltext && query != null && query.hasQuery()) {
            if (query.q().codePointCount(0, query.q().length()) >= 2) {
                List<Post> ranked = indexed(query);
                if (!ranked.isEmpty()) return ranked;
            }
            // One-character/stopword-only queries retain literal substring semantics.
        }
        var selection =
                queryFactory
                        .selectFrom(post)
                        .where(
                                post.visibility.eq(Visibility.PUBLIC),
                                post.status.eq(PostStatus.PUBLISHED),
                                containsQuery(query),
                                matchesLanguage(query))
                        .orderBy(post.publishedAt.desc(), post.postId.desc());
        if (query != null && query.hasQuery()) selection.limit(100);
        return selection.fetch();
    }

    @SuppressWarnings("unchecked")
    private List<Post> indexed(ListPublicPostsQuery query) {
        String score = "MATCH(title,excerpt,content_md) AGAINST(:term IN NATURAL LANGUAGE MODE)";
        String sql =
                "SELECT * FROM posts WHERE visibility='PUBLIC' AND status='PUBLISHED' AND "
                        + score
                        + ">0"
                        + (query.hasLang() ? " AND lang=:lang" : "")
                        + " ORDER BY "
                        + score
                        + " DESC,published_at DESC,post_id DESC";
        var nativeQuery =
                entityManager
                        .createNativeQuery(sql, Post.class)
                        .setParameter("term", query.q())
                        .setMaxResults(100);
        if (query.hasLang()) nativeQuery.setParameter("lang", query.lang());
        return nativeQuery.getResultList();
    }

    private BooleanExpression containsQuery(ListPublicPostsQuery query) {
        if (query == null || !query.hasQuery()) {
            return null;
        }

        String keyword = query.q();
        StringExpression contentMdAsString =
                Expressions.stringTemplate("cast({0} as string)", post.contentMd);
        return post.title
                .containsIgnoreCase(keyword)
                .or(post.excerpt.containsIgnoreCase(keyword))
                .or(contentMdAsString.containsIgnoreCase(keyword));
    }

    private BooleanExpression matchesLanguage(ListPublicPostsQuery query) {
        if (query == null || !query.hasLang()) {
            return null;
        }

        return post.lang.eq(query.lang());
    }
}
