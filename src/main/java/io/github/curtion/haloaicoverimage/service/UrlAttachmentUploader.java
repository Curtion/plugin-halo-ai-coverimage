package io.github.curtion.haloaicoverimage.service;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import run.halo.app.core.extension.attachment.Attachment;
import run.halo.app.core.extension.service.AttachmentService;

/**
 * 基于 Halo AttachmentService 的 URL 上传封装。
 *
 */
@Service
public class UrlAttachmentUploader {

    private final AttachmentService attachmentService;

    public UrlAttachmentUploader(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    /**
     * 通过外部 URL 上传附件。
     *
     * 契约：
     * - 输入：url 字符串、策略名 policyName（必填）、分组名 groupName（可空）、文件名 filename（可空）
     * - 输出：Mono<Attachment>，成功时包含创建的附件对象
     * - 错误：当 url 无效或 policyName 为空将返回 Mono.error(IllegalArgumentException)
     */
    public Mono<Attachment> uploadFromUrl(@NonNull String url,
            String groupName,
            String filename) {
        Objects.requireNonNull(url, "url must not be null");

        System.out.println("Uploading attachment from URL: " + url);

        final URL externalUrl;
        try {
            externalUrl = new URI(url).toURL();
        } catch (MalformedURLException e) {
            return Mono.error(new IllegalArgumentException("Invalid url: " + url, e));
        } catch (URISyntaxException e) {
            return Mono.error(new IllegalArgumentException("Invalid url: " + url, e));
        }

        // 如果未提供文件名，则尝试从 URL 路径中解析一个
        String effectiveFilename = filename;
        if (effectiveFilename == null || effectiveFilename.isBlank()) {
            String path = externalUrl.getPath();
            int idx = path.lastIndexOf('/') + 1;
            effectiveFilename = idx >= 0 && idx < path.length() ? path.substring(idx) : null;
            if (effectiveFilename == null || effectiveFilename.isBlank()) {
                // 兜底一个简单的文件名
                effectiveFilename = "attachment";
            }
        }

        return attachmentService.uploadFromUrl(externalUrl, "default-policy", groupName, effectiveFilename);
    }
}
