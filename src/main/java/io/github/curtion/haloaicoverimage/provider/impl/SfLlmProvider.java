package io.github.curtion.haloaicoverimage.provider.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.curtion.haloaicoverimage.provider.LlmProvider;
import io.github.curtion.haloaicoverimage.setting.LlmProviderSetting;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import run.halo.app.content.ContentWrapper;
import run.halo.app.content.PostContentService;
import run.halo.app.core.extension.content.Post;
import reactor.core.publisher.Mono;

@Component
public class SfLlmProvider implements LlmProvider {

    private final PostContentService postContentService;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SfLlmProvider(PostContentService postContentService) {
        this.postContentService = postContentService;
    }

    @Override
    public String generatePrompt(Post post, LlmProviderSetting setting) {
        String postName = post.getMetadata().getName();
        String title = post.getSpec().getTitle();
        List<String> tags = post.getSpec().getTags();
        List<String> categories = post.getSpec().getCategories();

        return this.postContentService.getReleaseContent(postName)
            .map(ContentWrapper::getRaw)
            .flatMap(content -> {
                try {
                    String prompt =
                        "The following is the basic information of a blog post. Please generate a suitable prompt for generating a cover image for this article based on this information. The prompt should be in English and as detailed as possible. You only need to return the prompt itself, without any other content.\n"
                            + "Title: " + title + "\n"
                            + "Tags: " + String.join(", ", tags) + "\n"
                            + "Categories: " + String.join(", ", categories) + "\n"
                            + "Content: " + content;

                    Map<String, Object> message = Map.of("role", "user", "content", prompt);
                    Map<String, Object> body =
                        Map.of("model", setting.model(), "messages", List.of(message));

                    HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.siliconflow.cn/v1/chat/completions"))
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
                                List<Map<String, Object>> choices =
                                    (List<Map<String, Object>>) responseMap.get("choices");
                                if (choices != null && !choices.isEmpty()) {
                                    Map<String, Object> firstChoice = choices.get(0);
                                    Map<String, String> messageMap =
                                        (Map<String, String>) firstChoice.get("message");
                                    return Mono.just(messageMap.get("content"));
                                }
                            } catch (Exception e) {
                                return Mono.error(e);
                            }
                            return Mono.empty();
                        });
                } catch (Exception e) {
                    return Mono.error(e);
                }
            })
            .block();
    }
}