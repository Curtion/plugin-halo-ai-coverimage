package io.github.curtion.haloaicoverimage.setting;

import io.github.curtion.haloaicoverimage.model.enums.ProviderEngine;

public record T2iProviderSetting(
        ProviderEngine engine,
        String model,
        String apiKey) {
    public static final String GROUP_NAME = "t2iProvider";
}
