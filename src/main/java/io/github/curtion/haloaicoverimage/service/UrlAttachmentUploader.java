package io.github.curtion.haloaicoverimage.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import reactor.core.publisher.Mono;
import run.halo.app.core.extension.attachment.Attachment;
import run.halo.app.core.extension.service.AttachmentService;
import run.halo.app.core.user.service.UserService;

/**
 * 基于 Halo AttachmentService 的 URL 上传封装。
 *
 */
@Service
public class UrlAttachmentUploader {

    private final AttachmentService attachmentService;
    private final UserService userService;

    public UrlAttachmentUploader(AttachmentService attachmentService, UserService userService) {
        this.attachmentService = attachmentService;
        this.userService = userService;
    }

    public Mono<String> uploadFromUrl(@NonNull String url, String groupName) {
        Objects.requireNonNull(url, "url must not be null");

        final URL externalUrl;
        try {
            externalUrl = UriComponentsBuilder.fromUriString(url).build(true).toUri().toURL();
        } catch (MalformedURLException e) {
            return Mono.error(new IllegalArgumentException("Invalid url: " + url, e));
        }

        Mono<Attachment> attachmentMono = userService.getUser("admin")
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
        return attachmentMono
                .flatMap(attachment -> {
                    if (attachment == null || attachment.getStatus() == null) {
                        return attachmentService.getPermalink(attachment);
                    }
                    String thumbnailUrl = Optional.ofNullable(attachment.getStatus().getThumbnails())
                            .map(thumbnails -> thumbnails.get("S"))
                            .orElse(null);
                    if (thumbnailUrl == null || thumbnailUrl.isEmpty()) {
                        return attachmentService.getPermalink(attachment);
                    }
                    return Mono.just(thumbnailUrl);
                })
                .flatMap(uri -> Mono.just(uri.toString()));
    }
}
