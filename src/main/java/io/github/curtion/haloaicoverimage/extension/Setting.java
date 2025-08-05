package io.github.curtion.haloaicoverimage.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@GVK(group = "io.github.curtion.haloaicoverimage",
        version = "v1alpha1",
        kind = "Setting",
        plural = "settings",
        singular = "setting")
public class Setting extends AbstractExtension {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Spec spec;

    @Data
    @Schema(name = "SettingSpec")
    public static class Spec {
      @Schema(description = "LLM provider settings")
      private LlmProvider llmProvider;
      
      @Schema(description = "Text-to-image provider settings")
      private T2iProvider t2iProvider;
    }

    @Data
    @Schema(name = "LlmProvider")
    public static class LlmProvider {
      @Schema(description = "Service provider engine", requiredMode = Schema.RequiredMode.REQUIRED)
      private String engine;
      
      @Schema(description = "Model ID", requiredMode = Schema.RequiredMode.REQUIRED)
      private String model;
      
      @Schema(description = "API key", requiredMode = Schema.RequiredMode.REQUIRED)
      private String apiKey;
      
      @Schema(description = "Prompt template", requiredMode = Schema.RequiredMode.REQUIRED)
      private String prompt;
    }

    @Data
    @Schema(name = "T2iProvider")
    public static class T2iProvider {
      @Schema(description = "Service provider engine", requiredMode = Schema.RequiredMode.REQUIRED)
      private String engine;
      
      @Schema(description = "Model ID", requiredMode = Schema.RequiredMode.REQUIRED)
      private String model;
      
      @Schema(description = "API key", requiredMode = Schema.RequiredMode.REQUIRED)
      private String apiKey;
    }
}
