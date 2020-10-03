package org.vdragun.tms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vdragun.tms.config.thymeleaf.CustomThymeleafDialect;

@Configuration
public class ThymeleafConfig {

    @Bean
    public CustomThymeleafDialect customThymeleafDialect() {
        return new CustomThymeleafDialect();
    }
}
