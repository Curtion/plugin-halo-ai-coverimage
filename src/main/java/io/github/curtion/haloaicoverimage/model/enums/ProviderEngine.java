package io.github.curtion.haloaicoverimage.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProviderEngine {

    SILICONFLOW("siliconflow");

    private final String value;

    ProviderEngine(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}