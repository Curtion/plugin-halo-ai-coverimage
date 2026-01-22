package io.github.curtion.haloaicoverimage;

import io.github.curtion.haloaicoverimage.model.CoverGenerateRecord;
import org.springframework.stereotype.Component;

import run.halo.app.extension.Scheme;
import run.halo.app.extension.SchemeManager;
import run.halo.app.extension.index.IndexSpec;
import run.halo.app.plugin.BasePlugin;
import run.halo.app.plugin.PluginContext;

import static run.halo.app.extension.index.IndexAttributeFactory.simpleAttribute;

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
            indexSpecs.add(new IndexSpec()
                .setName("spec.status")
                .setIndexFunc(simpleAttribute(CoverGenerateRecord.class, 
                    record -> {
                        var status = record.getSpec().getStatus();
                        return status != null ? status.name() : null;
                    }))
            );
            indexSpecs.add(new IndexSpec()
                .setName("spec.postTitle")
                .setIndexFunc(simpleAttribute(CoverGenerateRecord.class,
                    record -> record.getSpec().getPostTitle()))
            );
        });
        System.out.println("HaloAiCoverimagePlugin 插件启动了!");
    }

    @Override
    public void stop() {
        Scheme coverGenerateRecordScheme = schemeManager.get(CoverGenerateRecord.class);
        schemeManager.unregister(coverGenerateRecordScheme);
        System.out.println("HaloAiCoverimagePlugin 被停止!");
    }
}
