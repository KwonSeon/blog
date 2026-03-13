package com.snowk.blog.api.post.infrastructure.persistence.query;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.snowk.blog.api.common.domain.enumtype.Visibility;
import com.snowk.blog.api.post.application.query.ListPublicPostsQuery;
import com.snowk.blog.api.post.domain.entity.Post;
import com.snowk.blog.api.post.domain.entity.QPost;
import com.snowk.blog.api.post.domain.enumtype.PostStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostPublicQueryRepositoryImpl implements PostPublicQueryRepository {

    private static final QPost post = QPost.post;

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Post> findPublicPosts(ListPublicPostsQuery query) {
        return queryFactory.selectFrom(post)
            .where(
                post.visibility.eq(Visibility.PUBLIC),
                post.status.eq(PostStatus.PUBLISHED),
                containsQuery(query),
                matchesLanguage(query)
            )
            .orderBy(post.publishedAt.desc(), post.postId.desc())
            .fetch();
    }

    private BooleanExpression containsQuery(ListPublicPostsQuery query) {
        if (query == null || !query.hasQuery()) {
            return null;
        }

        String keyword = query.q();
        StringExpression contentMdAsString = Expressions.stringTemplate("cast({0} as string)", post.contentMd);
        return post.title.containsIgnoreCase(keyword)
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
