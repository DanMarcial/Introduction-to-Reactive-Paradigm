package com.example.demo.utils;

import org.slf4j.MDC;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

import java.util.function.Function;
import org.reactivestreams.Publisher;

public class MDCContextLifter {

    public static <T> Function<Publisher<T>, Publisher<T>> lift() {
        return publisher -> Mono.deferContextual(ctx -> {
            setMDC(ctx);
            return Mono.from(publisher);
        }).doFinally(signal -> MDC.clear());
    }

    private static void setMDC(ContextView contextView) {
        contextView.stream()
                .filter(e -> e.getKey() instanceof String)
                .forEach(e -> MDC.put((String) e.getKey(), String.valueOf(e.getValue())));
    }
}
