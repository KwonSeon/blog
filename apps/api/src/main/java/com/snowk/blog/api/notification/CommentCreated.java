package com.snowk.blog.api.notification;

import java.util.List;

/** Domain event contains references only, never a comment body or email address. */
public record CommentCreated(long commentId, List<String> actorSubjects) {
    public CommentCreated {
        actorSubjects = List.copyOf(actorSubjects);
    }
}
