package io.github.taoganio.mongodb.provider.spring.boot.autoconfigure;

import io.github.taoganio.mongodb.provider.spring.boot.MongoDS;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "spring.datasource.mongodb")
public class MongoDataSourceProperties {

    /**
     * 启用{@link MongoDS} AOP 切面
     */
    private boolean dsAop;
    /**
     * 为false时找不到对应的数据源定义时使用默认数据源, 否则抛出异常
     */
    private boolean strict;
    /**
     * 主数据源标识, 未定义时使用第一个
     */
    private String primary;
    /**
     * 所有数据源定义
     */
    private Map<String, MongoProperties> definitions;

    public boolean isDsAop() {
        return dsAop;
    }

    public void setDsAop(boolean dsAop) {
        this.dsAop = dsAop;
    }

    public boolean isStrict() {
        return strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public Map<String, MongoProperties> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(Map<String, MongoProperties> definitions) {
        this.definitions = definitions;
    }

}
