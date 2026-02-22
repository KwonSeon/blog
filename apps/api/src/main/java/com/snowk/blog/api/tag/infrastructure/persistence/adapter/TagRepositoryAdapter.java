package com.snowk.blog.api.tag.infrastructure.persistence.adapter;

import com.snowk.blog.api.tag.application.port.out.TagRepositoryPort;
import com.snowk.blog.api.tag.domain.entity.Tag;
import com.snowk.blog.api.tag.domain.enumtype.TagScope;
import java.util.Optional;

import com.snowk.blog.api.tag.infrastructure.persistence.jpa.TagJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TagRepositoryAdapter implements TagRepositoryPort {

    private final TagJpaRepository tagJpaRepository;

    @Override
    public Tag save(Tag tag) {
        return tagJpaRepository.save(tag);
    }

    @Override
    public Optional<Tag> findById(Long tagId) {
        return tagJpaRepository.findById(tagId);
    }

    @Override
    public Optional<Tag> findByScopeAndSlug(TagScope scope, String slug) {
        return tagJpaRepository.findByScopeAndSlug(scope, slug);
    }
}
