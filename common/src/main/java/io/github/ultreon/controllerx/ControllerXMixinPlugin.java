package io.github.ultreon.controllerx;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class ControllerXMixinPlugin implements IMixinConfigPlugin {
    private boolean disabled = false;

    @Override
    public void onLoad(String mixinPackage) {
        if (System.getProperty("os.name").toLowerCase().startsWith("mac")) {
            this.disabled = true;
        }

        if (mixinPackage.startsWith("io.github.ultreon.controllerx.")) {
            if (getClass().getClassLoader().getResource("com/ultreon/mods/lib/UltreonLib.class") == null) {
                this.disabled = true;
            }
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return !disabled && mixinClassName.startsWith("io.github.ultreon.controllerx.");
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        if (!disabled) {
            return List.of();
        }
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
