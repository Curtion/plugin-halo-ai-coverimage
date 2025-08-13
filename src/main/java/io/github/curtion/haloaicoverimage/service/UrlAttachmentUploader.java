package io.github.curtion.haloaicoverimage.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import run.halo.app.core.extension.attachment.Attachment;
import run.halo.app.core.extension.service.AttachmentService;
import run.halo.app.core.user.service.UserService;
import run.halo.app.extension.ReactiveExtensionClient;

@Service
public class UrlAttachmentUploader {

    private final AttachmentService attachmentService;
    private final UserService userService;
    private final ReactiveExtensionClient client;

    // 轮询配置
    private static final int MAX_RETRIES = 10;
    private static final Duration RETRY_DELAY = Duration.ofMillis(500);

    public UrlAttachmentUploader(AttachmentService attachmentService, UserService userService,
            ReactiveExtensionClient client) {
        this.attachmentService = attachmentService;
        this.userService = userService;
        this.client = client;
    }

    /**
     * 从 URL 上传文件并获取指定尺寸的缩略图链接。
     *
     * @param url               要上传的图片URL
     * @param groupName         附件分组名
     * @param thumbnailSizeName 缩略图尺寸的名称 (例如 "S", "M", "L")
     * @return 包含缩略图URL的 Mono
     */
    public Mono<String> uploadFromUrl(@NonNull String url, String groupName, @NonNull String thumbnailSizeName) {
        Objects.requireNonNull(url, "url must not be null");
        Objects.requireNonNull(thumbnailSizeName, "thumbnailSizeName must not be null");

        final URL externalUrl;
        try {
            externalUrl = UriComponentsBuilder.fromUriString(url).build(true).toUri().toURL();
        } catch (MalformedURLException e) {
            return Mono.error(new IllegalArgumentException("Invalid url: " + url, e));
        }

        Mono<Attachment> initialAttachmentMono = userService.getUserOrGhost("admin")
                .flatMap(user -> {
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            user.getMetadata().getName(),
                            null,
                            Collections.emptyList());
                    SecurityContext securityContext = new SecurityContextImpl(authentication);
                    return attachmentService.uploadFromUrl(externalUrl, "default-policy", groupName, "")
                            .contextWrite(
                                    ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
                });

        return initialAttachmentMono
                .flatMap(attachment -> pollForThumbnail(attachment.getMetadata().getName(), thumbnailSizeName));
    }

    private Mono<String> pollForThumbnail(String attachmentName, String thumbnailSizeName) {
        Mono<String> pollingMono = client.get(Attachment.class, attachmentName)
                .flatMap(attachment -> {
                    if (attachment.getStatus() != null && attachment.getStatus().getThumbnails() != null) {
                        String thumbnailUrl = attachment.getStatus().getThumbnails().get(thumbnailSizeName);
                        if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
                            return Mono.just(thumbnailUrl);
                        }
                    }
                    return Mono.error(new IllegalStateException("Thumbnail not ready yet"));
                });

        return pollingMono.retryWhen(Retry.fixedDelay(MAX_RETRIES, RETRY_DELAY)
                .filter(ex -> ex instanceof IllegalStateException)
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> new TimeoutException(
                        "Failed to get thumbnail [" + thumbnailSizeName + "] for attachment [" + attachmentName
                                + "] after " + MAX_RETRIES + " retries.")))
                .onErrorResume(TimeoutException.class, ex -> client.get(Attachment.class, attachmentName)
                        .map(att -> att.getStatus().getPermalink()));
    }
}
