package io.github.curtion.haloaicoverimage.provider;

import io.github.curtion.haloaicoverimage.model.enums.ProviderEngine;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class LlmProviderManager {

    private final Map<ProviderEngine, LlmProvider> providers;

    /**
     * Injects all LlmProvider beans and maps them by their engine type.
     *
     * @param providerList A list of all LlmProvider implementations found by Spring.
     */
    public LlmProviderManager(List<LlmProvider> providerList) {
        this.providers = providerList.stream()
            .collect(Collectors.toMap(LlmProvider::getProviderEngine, Function.identity()));
    }

    /**
     * Gets a provider instance based on the engine enum.
     *
     * @param engine The engine selected by the user.
     * @return An Optional containing the LlmProvider if found, otherwise empty.
     */
    public Optional<LlmProvider> getProvider(ProviderEngine engine) {
        return Optional.ofNullable(providers.get(engine));
    }
}