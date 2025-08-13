package io.github.curtion.haloaicoverimage.provider;

import io.github.curtion.haloaicoverimage.model.enums.ProviderEngine;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class T2iProviderManager {

    private final Map<ProviderEngine, It2iProvider> providers;

    /**
     * Injects all T2iProvider beans and maps them by their engine type.
     *
     * @param providerList A list of all T2iProvider implementations found by Spring.
     */
    public T2iProviderManager(List<It2iProvider> providerList) {
        this.providers = providerList.stream()
            .collect(Collectors.toMap(It2iProvider::getProviderEngine, Function.identity()));
    }

    /**
     * Gets a provider instance based on the engine enum.
     *
     * @param engine The engine selected by the user.
     * @return An Optional containing the T2iProvider if found, otherwise empty.
     */
    public Optional<It2iProvider> getProvider(ProviderEngine engine) {
        return Optional.ofNullable(providers.get(engine));
    }
}