package io.github.curtion.haloaicoverimage;

import io.github.curtion.haloaicoverimage.model.CoverGenerateRecord;
import org.springframework.stereotype.Component;

import run.halo.app.extension.Scheme;
import run.halo.app.extension.SchemeManager;
import run.halo.app.extension.exception.SchemeNotFoundException;
import run.halo.app.extension.index.IndexSpecs;
import run.halo.app.plugin.BasePlugin;
import run.halo.app.plugin.PluginContext;

/**
 * <p>
 * Plugin main class to manage the lifecycle of the plugin.
 * </p>
 * <p>
 * This class must be public and have a public constructor.
 * </p>
 * <p>
 * Only one main class extending {@link BasePlugin} is allowed per plugin.
 * </p>
 *
 * @author Curtion
 * @since 1.0.0
 */
@Component
public class HaloAiCoverimagePlugin extends BasePlugin {

    private final SchemeManager schemeManager;

    public HaloAiCoverimagePlugin(PluginContext pluginContext, SchemeManager schemeManager) {
        super(pluginContext);
        this.schemeManager = schemeManager;
    }

    @Override
    public void start() {
        schemeManager.register(CoverGenerateRecord.class, indexSpecs -> {
            indexSpecs.add(
                    IndexSpecs.<CoverGenerateRecord, String>single("spec.status", String.class)
                            .indexFunc(record -> {
                                var status = record.getSpec().getStatus();
                                return status != null ? status.name() : null;
                            }));
            indexSpecs.add(
                    IndexSpecs.<CoverGenerateRecord, String>single("spec.postTitle", String.class)
                            .indexFunc(record -> record.getSpec().getPostTitle()));
        });
        System.out.println("HaloAiCoverimagePlugin 插件启动了!");
    }

    @Override
    public void stop() {
        try {
            Scheme coverGenerateRecordScheme = schemeManager.get(CoverGenerateRecord.class);
            schemeManager.unregister(coverGenerateRecordScheme);
        } catch (SchemeNotFoundException e) {
        }
        System.out.println("HaloAiCoverimagePlugin 被停止!");
    }
}
