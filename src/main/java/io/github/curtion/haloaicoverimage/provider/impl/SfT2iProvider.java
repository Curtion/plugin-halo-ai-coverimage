package io.github.curtion.haloaicoverimage.provider.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.curtion.haloaicoverimage.model.enums.ProviderEngine;
import io.github.curtion.haloaicoverimage.provider.It2iProvider;
import io.github.curtion.haloaicoverimage.setting.BasicSetting;
import io.github.curtion.haloaicoverimage.setting.T2iProviderSetting;
import io.github.curtion.haloaicoverimage.service.UrlAttachmentUploader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class SfT2iProvider implements It2iProvider {

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
    public Mono<String> generate(
            String prompt, T2iProviderSetting setting, BasicSetting basicSetting) {
        try {
            Map<String, Object> body = Map.of(
                    "model", setting.model(),
                    "prompt", prompt,
                    "image_size", "1024x1024",
                    "batch_size", 1,
                    "num_inference_steps", 20,
                    "guidance_scale", 7.5);

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
                            JsonNode rootNode = objectMapper.readTree(responseBody);
                            JsonNode imagesNode = rootNode.path("images");
                            if (imagesNode.isArray() && !imagesNode.isEmpty()) {
                                String imageUrl = imagesNode.get(0).path("url").asText();
                                return urlAttachmentUploader.uploadFromUrl(
                                        imageUrl, basicSetting.group(), basicSetting.quality());
                            }
                        } catch (Exception e) {
                            return Mono.error(e);
                        }
                        return Mono.error(
                                new IllegalStateException("No images returned by provider"));
                    });
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}