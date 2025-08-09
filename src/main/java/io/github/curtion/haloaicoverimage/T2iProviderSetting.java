package io.github.curtion.haloaicoverimage;

public record T2iProviderSetting(
        ProviderEngine engine,
        String model,
        String apiKey) {
    public static final String GROUP_NAME = "t2iProvider";
}
