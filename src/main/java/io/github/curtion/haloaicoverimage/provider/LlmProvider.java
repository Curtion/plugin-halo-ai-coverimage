package io.github.curtion.haloaicoverimage.provider;

import io.github.curtion.haloaicoverimage.LlmProviderSetting;
import run.halo.app.core.extension.content.Post;

/**
 * LLM provider extension point.
 */
public interface LlmProvider {

    /**
     * Generate prompt by post.
     *
     * @param post post must not be null
     * @param setting setting must not be null
     * @return prompt
     */
    String generatePrompt(Post post, LlmProviderSetting setting);
}