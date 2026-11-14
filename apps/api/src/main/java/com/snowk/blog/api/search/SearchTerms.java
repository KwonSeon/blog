package com.snowk.blog.api.search;

import java.util.regex.Pattern;

public final class SearchTerms {
    private SearchTerms() {}

    public static String booleanPhrase(String query) {
        var matcher = Pattern.compile("[\\p{L}\\p{N}]+").matcher(query);
        var parts = new java.util.ArrayList<String>();
        while (matcher.find()) {
            String token = matcher.group();
            if (token.codePointCount(0, token.length()) < 2) return "";
            parts.add("+\"" + token + "\"");
        }
        return String.join(" ", parts);
    }
}
