package dev.ultreon.controllerx.compat.advdbg;

import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.mods.advanceddebug.AdvancedDebug;
import com.ultreon.mods.advanceddebug.api.events.IInitPagesEvent;
import com.ultreon.mods.advanceddebug.api.extension.Extension;
import com.ultreon.mods.advanceddebug.api.extension.ExtensionInfo;

@ExtensionInfo(AdvancedDebug.MOD_ID)
public class AdvancedDebugExtension implements Extension {
    @Override
    public void initPages(IInitPagesEvent initEvent) {
        initEvent.register(new Identifier("controllerx", "controllerx"), new ControllerDebugPage());
    }
}
