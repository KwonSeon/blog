package com.snowk.blog.api.post.application.query;

public record ListPublicPostsQuery(String q, String lang) {
    public ListPublicPostsQuery() {
        this(null, null);
    }

    public ListPublicPostsQuery {
        q = normalize(q);
        lang = normalize(lang);
        if ((q != null && (q.length() > 80 || q.codePoints().anyMatch(Character::isISOControl)))
                || (lang != null && !lang.matches("[a-z]{2,3}(?:-[A-Z]{2})?")))
            throw new com.snowk.blog.api.global.exception.BaseException(
                    com.snowk.blog.api.global.error.CommonErrorStatus.BAD_REQUEST);
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
