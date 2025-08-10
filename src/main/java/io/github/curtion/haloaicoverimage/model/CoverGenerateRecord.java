package io.github.curtion.haloaicoverimage.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@GVK(group = "io.github.curtion", version = "v1alpha1", kind = "CoverGenerateRecord", plural = "covergeneraterecords", singular = "covergeneraterecord")
public class CoverGenerateRecord extends AbstractExtension {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Spec spec;

    @Data
    @Schema(name = "CoverGenerateRecordSpec")
    public static class Spec {

        @Schema(description = "处理状态")
        private Status status;

        @Schema(description = "提示词")
        private String prompt;

        @Schema(description = "服务提供商")
        private String provider;

        @Schema(description = "模型")
        private String modelId;
    }

    public enum Status {
        PROCESSING,
        SUCCESS,
        FAILED
    }
}