package io.github.curtion.haloaicoverimage.provider.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.curtion.haloaicoverimage.model.enums.ProviderEngine;
import io.github.curtion.haloaicoverimage.provider.T2iProvider;
import io.github.curtion.haloaicoverimage.setting.T2iProviderSetting;
import io.github.curtion.haloaicoverimage.service.UrlAttachmentUploader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class SfT2iProvider implements T2iProvider {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UrlAttachmentUploader urlAttachmentUploader;

    public SfT2iProvider(UrlAttachmentUploader urlAttachmentUploader) {
        this.urlAttachmentUploader = urlAttachmentUploader;
    }

    @Override
    public ProviderEngine getProviderEngine() {
        return ProviderEngine.SILICONFLOW;
    }

    @Override
    public Mono<String> generate(String prompt, T2iProviderSetting setting) {
        try {
            Map<String, Object> body = Map.of(
                "model", setting.model(),
                "prompt", prompt,
                "image_size", "1024x1024",
                "batch_size", 1,
                "num_inference_steps", 20,
                "guidance_scale", 7.5
            );

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.siliconflow.cn/v1/images/generations"))
                .header("Authorization", "Bearer " + setting.apiKey())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                    objectMapper.writeValueAsString(body)))
                .build();

            return Mono.fromFuture(
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()))
                .map(HttpResponse::body)
                .flatMap(responseBody -> {
                    try {
                        Map<String, Object> responseMap =
                            objectMapper.readValue(responseBody, Map.class);
                        List<Map<String, Object>> images =
                            (List<Map<String, Object>>) responseMap.get("images");
                        if (images != null && !images.isEmpty()) {
                            String imageUrl = (String) images.get(0).get("url");
                            String filename = UUID.randomUUID().toString();
                            // 上传到 Halo 并返回存储 URI
                            return urlAttachmentUploader
                                .uploadFromUrl(imageUrl, "", filename+".jpeg")
                                .flatMap(attachment -> {
                                    Map<String, String> annotations = attachment
                                        .getMetadata()
                                        .getAnnotations();
                                    String uri = annotations != null
                                        ? annotations.get("storage.halo.run/uri")
                                        : null;
                                    if (uri == null || uri.isBlank()) {
                                        return Mono.error(new IllegalStateException(
                                            "Attachment missing storage uri annotation"));
                                    }
                                    return Mono.just(uri);
                                });
                        }
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                    return Mono.error(new IllegalStateException("No images returned by provider"));
                });
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}