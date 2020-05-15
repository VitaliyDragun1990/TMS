package org.vdragun.tms.config;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.vdragun.tms.ui.web.converter.StringToLocalDateCustomFormatter;
import org.vdragun.tms.ui.web.converter.StringToLocalDateTimeCustomFormatter;
import org.vdragun.tms.ui.web.converter.TitleCustomFormatter;
import org.vdragun.tms.ui.web.util.Constants.Page;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private MessageSource messageSource;

    @Bean
    public LocaleResolver localResolver() {
        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        localeResolver.setDefaultLocale(Locale.US);

        return localeResolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");

        return interceptor;
    }

    @Override
    public Validator getValidator() {
        LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
        factory.setValidationMessageSource(messageSource);

        return factory;
    }

    @Bean
    public StringToLocalDateCustomFormatter stringToLocalDateCustomFormatter() {
        return new StringToLocalDateCustomFormatter(messageSource);
    }

    @Bean
    public StringToLocalDateTimeCustomFormatter stringToLocalDateTimeCustomFormatter() {
        return new StringToLocalDateTimeCustomFormatter(messageSource);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(stringToLocalDateCustomFormatter());
        registry.addFormatter(stringToLocalDateTimeCustomFormatter());
        registry.addFormatter(new TitleCustomFormatter());
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        WebMvcConfigurer.super.addViewControllers(registry);
        registry.addViewController("/").setViewName(Page.HOME);
        registry.addRedirectViewController("/home", "/");
    }

}
