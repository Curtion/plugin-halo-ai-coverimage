package io.github.curtion.haloaicoverimage.setting;

import io.github.curtion.haloaicoverimage.model.enums.ProviderEngine;

public record LlmProviderSetting(
        ProviderEngine engine,
        String model,
        String apiKey,
        String prompt) {
    public static final String GROUP_NAME = "llmProvider";
}
