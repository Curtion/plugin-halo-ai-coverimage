package io.github.curtion.haloaicoverimage.provider;

import io.github.curtion.haloaicoverimage.model.enums.ProviderEngine;
import io.github.curtion.haloaicoverimage.setting.T2iProviderSetting;
import reactor.core.publisher.Mono;

/**
 * Text to image provider.
 */
public interface T2iProvider {

    /**
     * Returns the engine type of this provider.
     *
     * @return provider engine enum.
     */
    ProviderEngine getProviderEngine();

    /**
     * Generate image by prompt.
     *
     * @param prompt prompt must not be blank
     * @param setting setting must not be null
     * @return image url
     */
    Mono<String> generate(String prompt, T2iProviderSetting setting);
}