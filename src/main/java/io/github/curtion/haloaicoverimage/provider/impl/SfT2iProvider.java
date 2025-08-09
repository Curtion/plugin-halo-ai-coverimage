package io.github.curtion.haloaicoverimage.provider.impl;

import io.github.curtion.haloaicoverimage.provider.T2iProvider;
import io.github.curtion.haloaicoverimage.setting.T2iProviderSetting;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class SfT2iProvider implements T2iProvider {

    @Override
    public Mono<String> generate(String prompt, T2iProviderSetting setting) {
        // This is a dummy implementation.
        // In a real-world application, you would call a text-to-image API
        // with the given prompt and settings.
        // For now, we'll just return a placeholder image URL.
        return Mono.just("https://via.placeholder.com/1200x800.png?text=" + prompt.replace(" ", "+"));
    }
}