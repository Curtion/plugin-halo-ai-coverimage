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

        @Schema(description = "LLM提示词")
        private String llmPrompt;

        @Schema(description = "T2I提示词")
        private String t2iPrompt;

        @Schema(description = "LLM服务提供商")
        private String llmProvider;

        @Schema(description = "T2I服务提供商")
        private String t2iProvider;

        @Schema(description = "LLM模型")
        private String llmModelId;

        @Schema(description = "T2I模型")
        private String t2iModelId;

        @Schema(description = "任务执行结果, 成功时为图片URL, 失败时为错误信息")
        private String result;

        @Schema(description = "文章标题")
        private String postTitle;
    }

    public enum Status {
        PROCESSING,
        SUCCESS,
        FAILED
    }
}