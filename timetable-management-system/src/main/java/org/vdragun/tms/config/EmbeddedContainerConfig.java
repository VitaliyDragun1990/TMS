package org.vdragun.tms.config;

import java.util.Properties;

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
    public TomcatServletWebServerFactory tomcatFactory(Properties dataSourceProperties) {
        return new TomcatServletWebServerFactory() {
            
            @Override
            protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
                tomcat.enableNaming();
                return super.getTomcatWebServer(tomcat);
            }
            
            @Override
            protected void postProcessContext(Context context) {
                ContextResource resource = new ContextResource();
                resource.setName(dataSourceProperties.getProperty("name"));
                resource.setType(dataSourceProperties.getProperty("type"));
                
                dataSourceProperties.forEach((key, value) -> resource.setProperty(key.toString(), value));
                
                context.getNamingResources().addResource(resource);
            }
        };
    }
}
