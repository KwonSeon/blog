package com.snowk.blog.api.post.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PostTagIdTest {

    @Test
    @DisplayName("PostTagId는 값이 같으면 동등하고 해시코드도 같다")
    void equalsAndHashCode_followValueEquality() {
        PostTagId a = new PostTagId(1L, 2L);
        PostTagId b = new PostTagId(1L, 2L);
        PostTagId c = new PostTagId(1L, 3L);

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
        assertThat(a).isNotEqualTo(c);
    }
}
