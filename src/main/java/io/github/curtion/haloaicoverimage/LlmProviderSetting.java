package io.github.curtion.haloaicoverimage;

public record LlmProviderSetting(
        String engine,
        String model,
        String apiKey,
        String prompt) {
    public static final String GROUP_NAME = "llmProvider";
}
