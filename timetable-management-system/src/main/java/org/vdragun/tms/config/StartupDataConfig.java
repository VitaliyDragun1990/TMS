package org.vdragun.tms.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = { "org.vdragun.tms.util.initializer" })
@PropertySource("classpath:generator.properties")
public class StartupDataConfig {
}
