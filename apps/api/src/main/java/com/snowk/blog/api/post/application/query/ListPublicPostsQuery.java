package com.snowk.blog.api.post.application.query;

public record ListPublicPostsQuery(
    String q,
    String lang
) {
    public ListPublicPostsQuery() {
        this(null, null);
    }

    public ListPublicPostsQuery {
        q = normalize(q);
        lang = normalize(lang);
    }

    public boolean hasQuery() {
        return q != null;
    }

    public boolean hasLang() {
        return lang != null;
    }

    private static String normalize(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
