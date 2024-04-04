package io.github.ultreon.controllerx.event;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import io.github.ultreon.controllerx.gui.widget.ItemSlot;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;

@FunctionalInterface
public interface ItemSlotGuiEvent {
    Event<ItemSlotGuiEvent> EVENT = EventFactory.createCompoundEventResult();

    CompoundEventResult<ItemSlot> onSlot(int x, int y, AbstractContainerScreen<?> screen, Slot slot);
}
