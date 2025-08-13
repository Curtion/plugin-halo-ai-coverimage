package io.github.curtion.haloaicoverimage.provider;

import io.github.curtion.haloaicoverimage.model.enums.ProviderEngine;
import io.github.curtion.haloaicoverimage.setting.LlmProviderSetting;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.content.Post;

/**
 * LLM provider extension point.
 */
public interface IllmProvider {

    /**
     * Returns the engine type of this provider.
     *
     * @return provider engine enum.
     */
    ProviderEngine getProviderEngine();

    /**
     * Generate prompt by post.
     *
     * @param post post must not be null
     * @param setting setting must not be null
     * @return prompt
     */
    Mono<String> generatePrompt(Post post, LlmProviderSetting setting);
}