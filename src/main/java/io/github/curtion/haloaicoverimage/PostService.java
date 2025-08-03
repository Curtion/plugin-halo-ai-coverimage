package io.github.curtion.haloaicoverimage;

import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;
import run.halo.app.core.extension.content.Post;
import run.halo.app.extension.ReactiveExtensionClient;

@Component
public class PostService {
    private final ReactiveExtensionClient client;

    public PostService(ReactiveExtensionClient client) {
        this.client = client;
    }

    public Mono<Post> setCover(String name, String coverUrl) {
        Mono<Post> updateOperation = this.client.fetch(Post.class, name)
                .doOnNext(post -> {
                    post.getSpec().setCover(coverUrl);
                })
                .flatMap(this.client::update);
        return updateOperation;
    }
}