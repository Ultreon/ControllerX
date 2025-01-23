package dev.ultreon.controllerx.impl.contexts;

import dev.architectury.platform.Platform;
import dev.ultreon.controllerx.ControllerX;
import dev.ultreon.controllerx.Hooks;
import dev.ultreon.controllerx.api.ControllerContext;
import dev.ultreon.controllerx.api.IControllerMapping;
import dev.ultreon.controllerx.config.gui.tabs.Tabs;
import dev.ultreon.controllerx.gui.widget.ItemSlot;
import dev.ultreon.controllerx.impl.ControllerAction;
import dev.ultreon.controllerx.impl.ControllerMapping;
import dev.ultreon.controllerx.injection.CreativeModeInventoryScreenInjection;
import dev.ultreon.controllerx.api.input.ControllerSignedFloat;
import dev.ultreon.controllerx.api.input.ControllerBoolean;
import dev.ultreon.controllerx.api.input.ControllerVec2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class MenuControllerContext extends ControllerContext {
    public static final MenuControllerContext INSTANCE = new MenuControllerContext(ControllerX.res("menu"));
    public final ControllerMapping<?> joystickMove;
    public final ControllerMapping<?> dpadMove;
    public final ControllerMapping<?> activate;
    public final ControllerMapping<?> scrollY;

    public final ControllerMapping<?> close;
    public final ControllerMapping<?> back;
    public final ControllerMapping<?> closeInventory;

    public final ControllerMapping<?> pickup;
    public final ControllerMapping<?> place;
    public final ControllerMapping<?> split;
    public final ControllerMapping<?> putSingle;
    public final ControllerMapping<?> drop;
    public final ControllerMapping<?> prevPage;
    public final ControllerMapping<?> nextPage;

    protected MenuControllerContext(ResourceLocation id) {
        super(id);

        joystickMove = mappings.register(new ControllerMapping<>(new ControllerAction.Joystick(ControllerVec2.LeftStick), IControllerMapping.Side.LEFT, Component.translatable("controllerx.action.menu.joystick_move"), "joystick_move"));
        dpadMove = mappings.register(new ControllerMapping<>(new ControllerAction.Joystick(ControllerVec2.Dpad), IControllerMapping.Side.LEFT, Component.translatable("controllerx.action.menu.dpad_move"), "dpad_move"));
        activate = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.A), IControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.menu.activate"), "activate", this::canActivate));
        scrollY = mappings.register(new ControllerMapping<>(new ControllerAction.Axis(ControllerSignedFloat.RightStickY), IControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.menu.scroll_y"), "scroll_y"));

        closeInventory = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.Y), IControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.menu.closeInventory"), "close_inventory", MenuControllerContext::isInventory));
        back = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.B), IControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.menu.back"), "back", this::isCloseableInGame));
        close = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.B), IControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.menu.close"), "close", this::isCloseableInMenu));

        pickup = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.A), IControllerMapping.Side.LEFT, Component.translatable("controllerx.action.menu.pickup"), "pickup", this::canPickup));
        place = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.A), IControllerMapping.Side.LEFT, Component.translatable("controllerx.action.menu.place"), "place", this::canPlace));
        split = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.X), IControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.menu.split"), "split", this::canSplit));
        putSingle = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.X), IControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.menu.putSingle"), "put_single", this::canPutSingle));
        drop = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.RightStickClick), IControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.menu.drop"), "drop", this::canDrop));

        prevPage = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.LeftShoulder), IControllerMapping.Side.LEFT, Component.translatable("controllerx.action.menu.prevPage"), "prev_page", MenuControllerContext::hasPrevPage));
        nextPage = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.RightShoulder), IControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.menu.nextPage"), "next_page", MenuControllerContext::hasNextPage));
    }

    private boolean canActivate(Minecraft minecraft) {
        Screen screen = minecraft.screen;
        if (screen instanceof AbstractContainerScreen<?> containerScreen
                && Hooks.isOnSlot(containerScreen)) return false;

        if (screen == null) return false;
        GuiEventListener focused = screen.getFocused();
        if (focused == null || !focused.isFocused()) {
            return false;
        }

        if (focused instanceof AbstractWidget widget) {
            if (!widget.visible) return false;
            return widget.active;
        }

        return true;
    }

    private static boolean isInventory(Minecraft mc) {
        Screen screen = Minecraft.getInstance().screen;
        return screen instanceof InventoryScreen || screen instanceof CreativeModeInventoryScreen;
    }

    private static boolean hasNextPage(Minecraft mc) {
        if (mc.screen != null && mc.screen.getFocused() instanceof Tabs) {
            return true;
        }
        return mc.screen instanceof CreativeModeInventoryScreen screen && ((CreativeModeInventoryScreenInjection) screen).controllerX$getNextPage() != null;
    }

    private static boolean hasPrevPage(Minecraft mc) {
        if (mc.screen != null && mc.screen.getFocused() instanceof Tabs) {
            return true;
        }
        return mc.screen instanceof CreativeModeInventoryScreen screen && ((CreativeModeInventoryScreenInjection) screen).controllerX$getPrevPage() != null;
    }

    private boolean canPickup(Minecraft minecraft) {
        if (minecraft.player == null) return false;
        if (!(minecraft.screen instanceof AbstractContainerScreen<?> containerScreen)) return false;
        if (!(containerScreen.getFocused() instanceof ItemSlot slot)) return false;

        ItemStack carried = minecraft.player.containerMenu.getCarried();
        if (!carried.isEmpty()) return false;
        return slot.getSlot().mayPickup(minecraft.player);
    }

    private boolean canPlace(Minecraft minecraft) {
        if (minecraft.player == null) return false;
        if (!(minecraft.screen instanceof AbstractContainerScreen<?> containerScreen)) return false;
        if (!(containerScreen.getFocused() instanceof ItemSlot slot)) return false;

        ItemStack carried = minecraft.player.containerMenu.getCarried();
        if (carried.isEmpty()) return false;
        return slot.getSlot().mayPlace(carried);
    }

    private boolean canSplit(Minecraft minecraft) {
        if (minecraft.player == null) return false;
        if (!(minecraft.screen instanceof AbstractContainerScreen<?> containerScreen)) return false;
        if (!(containerScreen.getFocused() instanceof ItemSlot slot)) return false;

        ItemStack carried = minecraft.player.containerMenu.getCarried();
        if (carried.isEmpty()) return false;
        return slot.getSlot().mayPickup(minecraft.player);
    }

    private boolean canPutSingle(Minecraft minecraft) {
        if (minecraft.player == null) return false;
        if (!(minecraft.screen instanceof AbstractContainerScreen<?> containerScreen)) return false;
        if (!(containerScreen.getFocused() instanceof ItemSlot slot)) return false;

        ItemStack carried = minecraft.player.containerMenu.getCarried();
        if (!carried.isEmpty()) return false;
        return slot.getSlot().mayPlace(carried);
    }

    private boolean canDrop(Minecraft minecraft) {
        if (minecraft.player == null) return false;
        if (!(minecraft.screen instanceof AbstractContainerScreen<?> containerScreen)) return false;
        if (!(containerScreen.getFocused() instanceof ItemSlot slot)) return false;
        return slot.getSlot().mayPickup(minecraft.player);
    }

    private boolean isCloseableInMenu(Minecraft mc) {
        return mc.player == null && mc.level == null && mc.screen != null && mc.screen.shouldCloseOnEsc() && !isInventory(mc);
    }

    private boolean isCloseableInGame(Minecraft mc) {
        return mc.player != null && mc.level != null && mc.screen != null && mc.screen.shouldCloseOnEsc() && !isInventory(mc);
    }

    @Override
    public int getYOffset() {
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof ChatScreen) {
            return 32;
        }

        if (screen instanceof TitleScreen) {
            if (Platform.isForge()) return 36;
            return 12;
        }

        return super.getYOffset();
    }

    @Override
    public boolean shouldShowHUD() {
        return false;
    }
}
