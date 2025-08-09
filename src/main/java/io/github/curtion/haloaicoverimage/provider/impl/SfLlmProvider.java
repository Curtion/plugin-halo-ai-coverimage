package io.github.curtion.haloaicoverimage.provider.impl;

import io.github.curtion.haloaicoverimage.setting.LlmProviderSetting;
import io.github.curtion.haloaicoverimage.provider.LlmProvider;
import org.springframework.stereotype.Component;
import run.halo.app.core.extension.content.Post;

@Component
public class SfLlmProvider implements LlmProvider {

    @Override
    public String generatePrompt(Post post, LlmProviderSetting setting) {
        // This is a dummy implementation.
        // In a real-world application, you would use the post content and settings
        // to generate a prompt for a text-to-image model.
        return "A beautiful landscape painting of " + post.getSpec().getTitle();
    }
}