package io.github.curtion.haloaicoverimage.provider;

import io.github.curtion.haloaicoverimage.LlmProviderSetting;
import run.halo.app.core.extension.content.Post;

/**
 * LLM provider extension point.
 *
 * @author curtion
 * @date 2024/04/27
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