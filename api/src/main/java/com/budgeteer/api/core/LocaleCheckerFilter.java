package com.budgeteer.api.core;

import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import org.reactivestreams.Publisher;

import java.util.Locale;

@Filter("${api.base-path}/**")
public class LocaleCheckerFilter implements HttpServerFilter {

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
        String langHeader = request.getHeaders().get("Accept-Language");
        if (langHeader == null || StringUtils.isEmpty(langHeader)) {
            Locale.setDefault(new Locale("en"));
        } else {
            String[] params = langHeader.split("-");
            if (params.length > 1) {
                Locale.setDefault(new Locale(params[0], params[1]));
            } else {
                Locale.setDefault(new Locale(params[0]));
            }
        }
        return chain.proceed(request);
    }
}
