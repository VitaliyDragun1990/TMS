package org.vdragun.tms.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

/**
 * Custom {@link LocaleResolver} implementation that resolves client locale
 * based on currently supported ones. If user locale is not among supported,
 * default one will be used.
 * 
 * @author Vitaliy Dragun
 *
 */
public class CustomLocaleResolver extends AcceptHeaderLocaleResolver {

    private List<Locale> supportedLocales;

    public CustomLocaleResolver(Locale defaultLocale, Locale... supportedLocales) {
        setDefaultLocale(defaultLocale);
        this.supportedLocales = new ArrayList<>(Arrays.asList(supportedLocales));
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String headerLang = request.getHeader("Accept-Language");
        return headerLang == null || headerLang.isEmpty() ? Locale.US
                : Locale.lookup(Locale.LanguageRange.parse(headerLang), supportedLocales);
    }
}
