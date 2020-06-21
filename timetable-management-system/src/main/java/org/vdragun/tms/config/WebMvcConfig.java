package org.vdragun.tms.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.vdragun.tms.ui.web.converter.StudentToUpdateStudentDataConverter;
import org.vdragun.tms.ui.web.converter.TimetableToUpdateTimetableDataConverter;
import org.vdragun.tms.ui.web.converter.TitleCustomFormatter;
import org.vdragun.tms.util.Constants.View;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public TitleCustomFormatter titleCustomFormatter() {
        return new TitleCustomFormatter();
    }

    @Bean
    public StudentToUpdateStudentDataConverter studentToUpdateStudentDataConverter() {
        return new StudentToUpdateStudentDataConverter();
    }

    @Bean
    public TimetableToUpdateTimetableDataConverter timetableToUpdateTimetableDataConverter() {
        return new TimetableToUpdateTimetableDataConverter();
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");

        return interceptor;
    }

    @Bean
    public FilterRegistrationBean<RequestUriMemorizerFilter> filterRegistrationBean() {
        FilterRegistrationBean<RequestUriMemorizerFilter> registrationBean = new FilterRegistrationBean<>();
        RequestUriMemorizerFilter filter = new RequestUriMemorizerFilter();

        registrationBean.setFilter(filter);
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return registrationBean;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        WebMvcConfigurer.super.addViewControllers(registry);
        registry.addViewController("/").setViewName(View.HOME);
        registry.addViewController("/auth/signin").setViewName(View.SIGN_IN_FORM);
        registry.addRedirectViewController("/home", "/");
    }

}
