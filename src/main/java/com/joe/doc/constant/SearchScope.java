package com.joe.doc.constant;

/**
 * @author JoezBlackZ
 */
public enum SearchScope {

    /**
     * 文件搜索的范围
     */
    ALL("all"),
    FILENAME("filename"),
    FILE_CONTENT("content");

    private final String scope;

    public String getScope() {
        return scope;
    }

    SearchScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return scope;
    }

}
