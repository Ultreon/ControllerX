package io.github.ultreon.controllerx.gui.screen;

import com.ultreon.mods.lib.client.gui.Theme;
import com.ultreon.mods.lib.client.gui.screen.PanoramaScreen;
import com.ultreon.mods.lib.client.gui.widget.BaseWidget;
import com.ultreon.mods.lib.client.gui.widget.ListWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class BaseConfigScreen extends PanoramaScreen {
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
        this.list = new ListWidget(this, 0, 0, 0, getCount(), hasSearch(), title, Theme.NORMAL);
    }

    protected abstract boolean hasSearch();

    protected abstract int getCount();

    public final void addConfigRow(Component title, BaseWidget widget) {

    }

    @Override
    public @Nullable Vec2 getCloseButtonPos() {
        return new Vec2(width - 20, 20);
    }

    public ListWidget getList() {
        return list;
    }
}
