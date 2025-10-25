package io.github.taoganio.mongodb.provider.spring.boot.autoconfigure;

import io.github.taoganio.mongodb.provider.spring.boot.DynamicMongoDatabaseFactory;
import io.github.taoganio.mongodb.provider.spring.boot.MongoDS;
import io.github.taoganio.mongodb.provider.spring.boot.MongoDSContextHolder;
import io.github.taoganio.mongodb.provider.spring.boot.MongoDSMethodInterceptor;
import io.github.taoganio.mongodb.provider.MongoDataSourceProvider;
import io.github.taoganio.mongodb.provider.MongoDataSourceRoutingProvider;
import io.github.taoganio.mongodb.provider.MongoReadWriteDataSourceProvider;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.aop.support.annotation.AnnotationMethodMatcher;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.lang.Nullable;


/**
 * 动态Mongo配置数据源
 */
@AutoConfiguration(before = MongoAutoConfiguration.class,
        after = {MongoDataSourceProviderConfiguration.class, MongoDataSourceRoutingProvidersConfiguration.class})
@ConditionalOnProperty(prefix = "spring.datasource.mongodb", name = "ds-aop", havingValue = "true", matchIfMissing = true)
public class DynamicMongoDataSourceConfiguration implements ApplicationEventPublisherAware {

    @Bean
    @ConditionalOnMissingBean(name = "mongoDSAdvisor")
    public Advisor mongoDSAdvisor(@Nullable BeanFactory beanFactory) {
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        ComposablePointcut pointcut = new ComposablePointcut(new AnnotationMethodMatcher(MongoDS.class, true))
                .union(new AnnotationMatchingPointcut(MongoDS.class, true));
        advisor.setPointcut(pointcut);
        MongoDSMethodInterceptor interceptor = new MongoDSMethodInterceptor();
        interceptor.setBeanFactory(beanFactory);
        advisor.setAdvice(interceptor);
        return advisor;
    }

    @Bean
    @ConditionalOnMissingBean(MongoDatabaseFactory.class)
    public MongoDatabaseFactory dynamicMongoDatabaseFactory(MongoDataSourceProperties properties,
                                                            MongoDataSourceProvider dataSourceProvider,
                                                            @Nullable MongoDataSourceRoutingProvider dataSourceRoutingProvider,
                                                            @Nullable MongoReadWriteDataSourceProvider readWriteDataSourceProvider) {
        DynamicMongoDatabaseFactory databaseFactory = new DynamicMongoDatabaseFactory(dataSourceProvider);
        databaseFactory.setStrict(properties.isStrict());
        databaseFactory.setDataSourceRoutingProvider(dataSourceRoutingProvider);
        databaseFactory.setReadWriteDataSourceProvider(readWriteDataSourceProvider);
        return databaseFactory;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        MongoDSContextHolder.setEventPublisher(applicationEventPublisher);
    }
}
