package io.github.curtion.haloaicoverimage;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents the provider engine for LLM and T2I services.
 * This enum is designed to be safely used in settings and can be
 * serialized/deserialized from YAML configuration files.
 */
public enum ProviderEngine {

    /**
     * Represents the SiliconFlow provider.
     * The value "siliconflow" corresponds to the configuration in settings.yaml.
     */
    SILICONFLOW("siliconflow");

    private final String value;

    ProviderEngine(String value) {
        this.value = value;
    }

    /**
     * Gets the string value of the enum constant, used by Jackson for serialization.
     *
     * @return The lower-case string representation of the engine.
     */
    @JsonValue
    public String getValue() {
        return value;
    }
}