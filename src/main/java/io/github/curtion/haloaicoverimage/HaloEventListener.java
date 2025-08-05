package io.github.curtion.haloaicoverimage;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;
import run.halo.app.core.extension.content.Post;
import run.halo.app.event.post.PostPublishedEvent;
import run.halo.app.extension.ReactiveExtensionClient;

@Component
public class HaloEventListener {

    private final ReactiveExtensionClient client;

    public HaloEventListener(ReactiveExtensionClient client) {
        this.client = client;
    }

    @EventListener
    public void handlePostPublishedEvent(PostPublishedEvent event) {
        this.client.fetch(Post.class, event.getName()).subscribe(post -> {
            System.out.println("Post title: " + post.getSpec().getTitle());
            System.out.println("Post cover: " + post.getSpec().getCover());
        }, error -> {
            System.err.println("Error fetching post: " + error.getMessage());
        });
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