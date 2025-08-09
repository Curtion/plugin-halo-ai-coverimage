package io.github.curtion.haloaicoverimage.listener;

import io.github.curtion.haloaicoverimage.provider.LlmProviderManager;
import io.github.curtion.haloaicoverimage.provider.T2iProviderManager;
import io.github.curtion.haloaicoverimage.setting.LlmProviderSetting;
import io.github.curtion.haloaicoverimage.setting.T2iProviderSetting;
import java.time.Duration;
import org.springframework.dao.OptimisticLockingFailureException;
import reactor.util.retry.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.content.Post;
import run.halo.app.event.post.PostPublishedEvent;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.plugin.ReactiveSettingFetcher;

@Component
public class HaloEventListener {

    private static final Logger log = LoggerFactory.getLogger(HaloEventListener.class);

    private final ReactiveExtensionClient client;
    private final ReactiveSettingFetcher settingFetcher;
    private final LlmProviderManager llmProviderManager;
    private final T2iProviderManager t2iProviderManager;

    public HaloEventListener(ReactiveExtensionClient client, ReactiveSettingFetcher settingFetcher,
        LlmProviderManager llmProviderManager, T2iProviderManager t2iProviderManager) {
        this.client = client;
        this.settingFetcher = settingFetcher;
        this.llmProviderManager = llmProviderManager;
        this.t2iProviderManager = t2iProviderManager;
    }

    @EventListener
    Mono<Void> handlePostPublishedEvent(PostPublishedEvent event) {
        return this.client.fetch(Post.class, event.getName())
                .filter(post -> post.getSpec().getCover() == null || post.getSpec().getCover().isEmpty())
                .flatMap(post -> {
                    var llmSettingsMono = settingFetcher.fetch(LlmProviderSetting.GROUP_NAME, LlmProviderSetting.class);
                    var t2iSettingsMono = settingFetcher.fetch(T2iProviderSetting.GROUP_NAME, T2iProviderSetting.class);

                    return Mono.zip(llmSettingsMono, t2iSettingsMono)
                            .flatMap(tuple -> {
                                var llmSetting = tuple.getT1();
                                var t2iSetting = tuple.getT2();

                                if (llmSetting == null || t2iSetting == null) {
                                    log.warn("LLM or T2I setting is not configured, skipping.");
                                    return Mono.empty();
                                }

                                return llmProviderManager.getProvider(llmSetting.engine())
                                   .map(provider -> provider.generatePrompt(post, llmSetting))
                                   .orElseGet(() -> {
                                       log.warn("No LLM provider found for engine: {}",
                                           llmSetting.engine());
                                       return Mono.empty();
                                   })
                                   .flatMap(prompt ->
                                       t2iProviderManager.getProvider(t2iSetting.engine())
                                           .map(t2iProvider -> t2iProvider.generate(prompt,
                                               t2iSetting))
                                           .orElseGet(() -> {
                                               log.warn("No T2I provider found for engine: {}",
                                                   t2iSetting.engine());
                                               return Mono.empty();
                                           })
                                   )
                                   .flatMap(imageUrl -> Mono.defer(
                                       () -> this.client.fetch(Post.class,
                                           post.getMetadata().getName())
                                                        .flatMap(latestPost -> {
                                                            latestPost.getSpec().setCover(imageUrl);
                                                            return this.client.update(latestPost);
                                                        }))
                                                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                                                        .filter(throwable -> throwable instanceof OptimisticLockingFailureException)
                                                        .doBeforeRetry(retrySignal -> log.warn(
                                                                "Failed to update post cover due to conflict, retry attempt: {}",
                                                                retrySignal.totalRetries() + 1)))
                                        );
                            });
                })
                .then();
    }
}