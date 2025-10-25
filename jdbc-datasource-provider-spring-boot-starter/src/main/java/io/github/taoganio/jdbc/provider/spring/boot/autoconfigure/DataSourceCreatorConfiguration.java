package io.github.taoganio.jdbc.provider.spring.boot.autoconfigure;

import io.github.taoganio.jdbc.provider.spring.boot.creator.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration(proxyBeanMethods = false)
public class DataSourceCreatorConfiguration {

    @Bean
    @ConditionalOnMissingBean(DataSourceCreatorProvider.class)
    public DataSourceCreatorProvider dataSourceCreatorProvider(List<DataSourcePoolCreator> dataSourcePoolCreators,
                                                               List<DataSourceWrapper> dataSourceWrappers) {
        DefaultDataSourceCreatorProvider provider = new DefaultDataSourceCreatorProvider(dataSourcePoolCreators);
        provider.setDataSourceWrappers(dataSourceWrappers);
        return provider;
    }

    @Configuration(proxyBeanMethods = false)
    public static class PoolConfiguration {

        @Bean
        @ConditionalOnClass(name = "com.atomikos.jdbc.AtomikosDataSourceBean")
        @ConditionalOnMissingBean(AtomikosDataSourceCreator.class)
        public AtomikosDataSourceCreator atomikosPoolCreator() {
            return new AtomikosDataSourceCreator();
        }
    }

}
