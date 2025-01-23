package dev.ultreon.controllerx.compat.advdbg;

import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.mods.advanceddebug.AdvancedDebug;
import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext;
import com.ultreon.mods.advanceddebug.api.client.menu.Formatter;
import com.ultreon.mods.advanceddebug.api.client.registry.IFormatterRegistry;
import com.ultreon.mods.advanceddebug.api.events.IInitPagesEvent;
import com.ultreon.mods.advanceddebug.api.extension.Extension;
import com.ultreon.mods.advanceddebug.api.extension.ExtensionInfo;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2f;

@ExtensionInfo(AdvancedDebug.MOD_ID)
public class AdvancedDebugExtension implements Extension {
    @Override
    public void initPages(IInitPagesEvent initEvent) {
        initEvent.register(new Identifier("controllerx", "controllerx"), new ControllerDebugPage());
    }

    @Override
    public void initFormatters(IFormatterRegistry formatterRegistry) {
        formatterRegistry.register(new Formatter<>(Vector2f.class, ResourceLocation.tryBuild("controllerx", "vec2")) {
            @Override
            public void format(Vector2f vector2f, IFormatterContext iFormatterContext) {
                float x = vector2f.x;
                float y = vector2f.y;

                if (x == 0 && y == 0) {
                    iFormatterContext.number("zero");
                } else {
                    iFormatterContext.parameter("x: ", x).separator().parameter("y: ", y);
                }
            }
        });
    }
}
