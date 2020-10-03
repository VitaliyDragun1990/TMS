package org.vdragun.tms.config;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class EmbeddedContainerConfig {

    @Bean
    public TomcatServletWebServerFactory tomcatFactory(JndiDataSourceProperties props) {
        return new TomcatServletWebServerFactory() {
            
            @Override
            protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
                tomcat.enableNaming();
                return super.getTomcatWebServer(tomcat);
            }
            
            @Override
            protected void postProcessContext(Context context) {
                ContextResource resource = new ContextResource();
                resource.setName(props.getResourceName());
                resource.setType(props.getResourceType());

                resource.setProperty("dataSource.implicitCachingEnabled", props.getImplicitCachingEnabled());
                resource.setProperty("dataSource.password", props.getPassword());
                resource.setProperty("dataSource.user", props.getUser());
                resource.setProperty("driverClassName", props.getDriverClassName());
                resource.setProperty("factory", props.getFactory());
                resource.setProperty("idleTimeout", props.getIdleTimeout());
                resource.setProperty("jdbcUrl", props.getJdbcUrl());
                resource.setProperty("maximumPoolSize", props.getMaximumPoolSize());
                resource.setProperty("minimumIdle", props.getMinimumIdle());
                
                context.getNamingResources().addResource(resource);
            }
        };
    }
}
