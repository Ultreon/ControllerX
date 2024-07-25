package io.github.ultreon.controllerx.mixin.accessors;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {
    @Accessor(value = "leftPos", remap = true)
    int getLeftPos();

    @Accessor(value = "topPos", remap = true)
    int getTopPos();

    @Accessor(value = "lastClickSlot", remap = true)
    Slot getLastClickSlot();

    @Accessor(value = "lastClickSlot", remap = true)
    void setLastClickSlot(Slot slot);

    @Accessor(value = "hoveredSlot", remap = true)
    Slot getClickedSlot();

    @Accessor(value = "hoveredSlot", remap = true)
    void setClickedSlot(Slot slot);

    @Accessor(value = "quickdropSlot", remap = true)
    Slot getQuickdropSlot();

    @Accessor(value = "quickdropSlot", remap = true)
    void setQuickdropSlot(Slot slot);
}
