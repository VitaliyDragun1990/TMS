package org.vdragun.tms.config;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.vdragun.tms.ui.common.converter.LocalDateCustomFormatter;
import org.vdragun.tms.ui.common.converter.LocalDateTimeCustomFormatter;
import org.vdragun.tms.util.localizer.MessageLocalizer;
import org.vdragun.tms.util.localizer.TemporalLocalizer;
import org.vdragun.tms.util.localizer.UserLocaleMessageLocalizer;
import org.vdragun.tms.util.localizer.UserLocaleTemporalLocalizer;

/**
 * Contains common configuration for UI layer.
 * 
 * @author Vitaliy Dragun
 *
 */
@Configuration
public class WebConfig {

    @Autowired
    private MessageSource messageSource;

    @Bean
    public LocaleResolver localResolver() {
        return new CustomLocaleResolver(Locale.US, new Locale("en"));
    }

    @Bean
    public Validator validator() {
        LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
        factory.setValidationMessageSource(messageSource);

        return factory;
    }

    @Bean
    public MessageLocalizer messageLocalizer() {
        return new UserLocaleMessageLocalizer(messageSource);
    }

    @Bean
    public TemporalLocalizer temporalLocalizer() {
        return new UserLocaleTemporalLocalizer(messageSource);
    }

    @Bean
    public LocalDateCustomFormatter localDateCustomFormatter() {
        return new LocalDateCustomFormatter(messageSource);
    }

    @Bean
    public LocalDateTimeCustomFormatter localDateTimeCustomFormatter() {
        return new LocalDateTimeCustomFormatter(messageSource);
    }

}
