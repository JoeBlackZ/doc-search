package com.joe.doc.constant;

/**
 * @author JoezBlackZ
 */
public enum SearchType {

    /**
     * 文件搜索的范围
     */
    QUERY_STRING("QueryString"),
    SIMPLE_QUERY_STRING("SimpleQueryString"),
    MATCH_PHRASE("MatchPhrase");

    private final String typeName;

    public String getTypeName() {
        return typeName;
    }

    SearchType(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return typeName;
    }

}
