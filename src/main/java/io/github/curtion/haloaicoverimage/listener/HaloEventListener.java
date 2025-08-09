package io.github.curtion.haloaicoverimage.listener;

import io.github.curtion.haloaicoverimage.provider.LlmProvider;
import io.github.curtion.haloaicoverimage.provider.T2iProvider;
import io.github.curtion.haloaicoverimage.setting.LlmProviderSetting;
import io.github.curtion.haloaicoverimage.setting.T2iProviderSetting;
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
    private final LlmProvider llmProvider;
    private final T2iProvider t2iProvider;

    public HaloEventListener(ReactiveExtensionClient client, ReactiveSettingFetcher settingFetcher,
            LlmProvider llmProvider, T2iProvider t2iProvider) {
        this.client = client;
        this.settingFetcher = settingFetcher;
        this.llmProvider = llmProvider;
        this.t2iProvider = t2iProvider;
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

                                return llmProvider.generatePrompt(post, llmSetting)
                                        .flatMap(prompt -> t2iProvider.generate(prompt, t2iSetting))
                                        .flatMap(imageUrl -> {
                                            post.getSpec().setCover(imageUrl);
                                            return client.update(post);
                                        });
                            });
                })
                .then();
    }
}