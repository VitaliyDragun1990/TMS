package org.vdragun.tms.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = { "org.vdragun.tms.core.application.service.impl" })
public class ServiceConfig {

}
