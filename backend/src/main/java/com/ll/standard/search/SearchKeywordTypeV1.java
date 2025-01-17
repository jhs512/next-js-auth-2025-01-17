package com.ll.standard.search;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SearchKeywordTypeV1 {
    title("title"),
    content("content");

    private final String value;
}
