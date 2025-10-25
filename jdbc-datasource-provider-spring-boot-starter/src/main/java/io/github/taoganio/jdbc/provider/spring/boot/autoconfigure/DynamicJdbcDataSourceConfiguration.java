package io.github.taoganio.jdbc.provider.spring.boot.autoconfigure;

import io.github.taoganio.jdbc.provider.*;
import io.github.taoganio.jdbc.provider.spring.boot.DynamicJdbcDataSource;
import io.github.taoganio.jdbc.provider.spring.boot.JdbcDS;
import io.github.taoganio.jdbc.provider.spring.boot.JdbcDSContextHolder;
import io.github.taoganio.jdbc.provider.spring.boot.JdbcDSMethodInterceptor;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.aop.support.annotation.AnnotationMethodMatcher;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JndiDataSourceAutoConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;

import javax.sql.DataSource;

/**
 * 动态JDBC配置数据源
 */
@AutoConfiguration(
        before = {JndiDataSourceAutoConfiguration.class, DataSourceAutoConfiguration.class},
        after = {JdbcDataSourceProviderConfiguration.class, JdbcDataSourceRoutingProvidersConfiguration.class})
@ConditionalOnProperty(prefix = "spring.datasource.jdbc", name = "ds-aop", havingValue = "true", matchIfMissing = true)
public class DynamicJdbcDataSourceConfiguration implements ApplicationEventPublisherAware {

    @Bean
    @ConditionalOnMissingBean(name = "jdbcDSAdvisor")
    public Advisor jdbcDSAdvisor(@Nullable BeanFactory beanFactory) {
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        ComposablePointcut pointcut = new ComposablePointcut(new AnnotationMethodMatcher(JdbcDS.class, true))
                .union(new AnnotationMatchingPointcut(JdbcDS.class, true));
        advisor.setPointcut(pointcut);
        JdbcDSMethodInterceptor interceptor = new JdbcDSMethodInterceptor();
        interceptor.setBeanFactory(beanFactory);
        advisor.setAdvice(interceptor);
        return advisor;
    }

    @Bean
    @ConditionalOnMissingBean(DataSource.class)
    public DataSource dynamicJdbcDataSource(JdbcDataSourceProperties properties,
                                            JdbcDataSourceProvider dataSourceProvider,
                                            @Nullable JdbcDataSourceRoutingProvider dataSourceRoutingProvider,
                                            @Nullable JdbcReadWriteDataSourceProvider readWriteDataSourceProvider) {
        DynamicJdbcDataSource dataSource = new DynamicJdbcDataSource(dataSourceProvider);
        dataSource.setDataSourceRoutingProvider(dataSourceRoutingProvider);
        dataSource.setReadWriteDataSourceProvider(readWriteDataSourceProvider);
        dataSource.setStrict(properties.isStrict());
        return dataSource;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        JdbcDSContextHolder.setEventPublisher(applicationEventPublisher);
    }
}
