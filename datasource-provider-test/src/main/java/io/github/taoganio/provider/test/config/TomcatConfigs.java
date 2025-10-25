package io.github.taoganio.provider.test.config;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class TomcatConfigs {

    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
                tomcat.enableNaming();
                return super.getTomcatWebServer(tomcat);
            }

            @Override
            protected void postProcessContext(Context context) {
                ContextResource resource = new ContextResource();
                resource.setName("jdbc/mydb");
                resource.setType(DataSource.class.getName());
                resource.setProperty("driverClassName", "com.mysql.jdbc.Driver");
                resource.setProperty("url", "jdbc:mysql://localhost:3306/mydb");
                resource.setProperty("username", "root");
                resource.setProperty("password", "root");
                context.getNamingResources().addResource(resource);
                super.postProcessContext(context);
            }
        };
        return tomcat;
    }

}
