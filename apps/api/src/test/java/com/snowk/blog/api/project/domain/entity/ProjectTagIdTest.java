package com.snowk.blog.api.project.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProjectTagIdTest {

    @Test
    @DisplayName("ProjectTagId는 값이 같으면 동등하고 해시코드도 같다")
    void equalsAndHashCode_followValueEquality() {
        ProjectTagId a = new ProjectTagId(1L, 2L);
        ProjectTagId b = new ProjectTagId(1L, 2L);
        ProjectTagId c = new ProjectTagId(1L, 3L);

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
        assertThat(a).isNotEqualTo(c);
    }
}
