package io.github.curtion.haloaicoverimage;

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

    public Mono<LlmProviderSetting> getLlmSettings() {
        return settingFetcher.fetch(LlmProviderSetting.GROUP_NAME, LlmProviderSetting.class);
    }

    public Mono<T2iProviderSetting> getT2iSettings() {
        return settingFetcher.fetch(T2iProviderSetting.GROUP_NAME, T2iProviderSetting.class);
    }

    public HaloEventListener(ReactiveExtensionClient client, ReactiveSettingFetcher settingFetcher) {
        this.client = client;
        this.settingFetcher = settingFetcher;
    }

    @EventListener
    Mono<Void> handlePostPublishedEvent(PostPublishedEvent event) {
        return this.client.fetch(Post.class, event.getName())
                .filter(post -> post.getSpec().getCover() == null || post.getSpec().getCover().isEmpty())
                .doOnNext(post -> {
                    post.getSpec().setCover("https://github.com/Curtion/douyu-keep/raw/master/doc/home.png");
                })
                .flatMap(this.client::update)
                .then();
    }
}