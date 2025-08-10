package io.github.curtion.haloaicoverimage.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;

import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@GVK(group = "io.github.curtion",
        version = "v1alpha1",
        kind = "CoverGenerateRecord",
        plural = "covergeneraterecords",
        singular = "covergeneraterecord")
public class CoverGenerateRecord extends AbstractExtension {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Spec spec;

    @Data
    @Schema(name = "CoverGenerateRecordSpec")
    public static class Spec {

        @Schema(description = "The time of the event")
        private Instant eventTime;

        @Schema(description = "The status of the process")
        private String status;

        @Schema(description = "The prompt used for image generation")
        private String prompt;

        @Schema(description = "The provider of the model")
        private String provider;

        @Schema(description = "The ID of the model")
        private String modelId;
    }
}