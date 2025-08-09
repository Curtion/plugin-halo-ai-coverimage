package io.github.curtion.haloaicoverimage.provider;

import io.github.curtion.haloaicoverimage.T2iProviderSetting;

/**
 * Text to image provider.
 *
 * @author curtion
 * @date 2024/04/27
 */
public interface T2iProvider {

    /**
     * Generate image by prompt.
     *
     * @param prompt prompt must not be blank
     * @param setting setting must not be null
     * @return image url
     */
    String generate(String prompt, T2iProviderSetting setting);
}