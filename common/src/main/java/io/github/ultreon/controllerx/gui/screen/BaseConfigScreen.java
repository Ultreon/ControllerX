package io.github.ultreon.controllerx.gui.screen;

import dev.ultreon.mods.lib.client.gui.screen.BaseScreen;
import dev.ultreon.mods.lib.client.gui.widget.ListWidget;
import dev.ultreon.mods.lib.client.theme.GlobalTheme;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;

public abstract class BaseConfigScreen extends BaseScreen {
    private ListWidget list;

    /**
     * Panorama screen constructor.
     *
     * @param title screen title.
     */
    protected BaseConfigScreen(Component title) {
        super(title);
    }

    @Override
    protected final void init() {
        this.list = new ListWidget(this, 0, 0, 0, getCount(), hasSearch(), title, GlobalTheme.VANILLA);
    }

    protected abstract boolean hasSearch();

    protected abstract int getCount();

    @Override
    public @Nullable Vec2 getCloseButtonPos() {
        return new Vec2(width - 20, 20);
    }

    public ListWidget getList() {
        return list;
    }
}
