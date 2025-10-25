package io.github.taoganio.datasource.provider;

/**
 * 读写范围
 */
public enum ReadWriteScope {
    /**
     * 读
     */
    READ,
    /**
     * 写
     */
    WRITE,
    /**
     * 任意
     */
    ANY,
    /**
     * 未知，框架内部定义，不可在配置中使用
     */
    UNKNOWN;

    /**
     * 解析
     *
     * @param scope 范围
     * @return {@link ReadWriteScope}
     */
    public static ReadWriteScope parsing(String scope) {
        if (scope == null || scope.isEmpty()) {
            throw new IllegalArgumentException("Invalid scope: " + scope);
        }
        scope = scope.trim();
        return valueOf(scope.toUpperCase());
    }
}
