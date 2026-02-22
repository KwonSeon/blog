package com.snowk.blog.api.post.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PostProjectIdTest {

    @Test
    @DisplayName("PostProjectId는 값이 같으면 동등하고 해시코드도 같다")
    void equalsAndHashCode_followValueEquality() {
        PostProjectId a = new PostProjectId(1L, 2L);
        PostProjectId b = new PostProjectId(1L, 2L);
        PostProjectId c = new PostProjectId(2L, 2L);

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
        assertThat(a).isNotEqualTo(c);
    }
}
