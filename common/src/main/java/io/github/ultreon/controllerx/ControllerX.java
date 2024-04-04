package io.github.ultreon.controllerx;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientScreenInputEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.hooks.client.screen.ScreenAccess;
import dev.architectury.injectables.annotations.ExpectPlatform;
import io.github.libsdl4j.api.SdlSubSystemConst;
import io.github.ultreon.controllerx.api.ControllerContext;
import io.github.ultreon.controllerx.gui.ControllerHud;
import io.github.ultreon.controllerx.gui.KeyboardHud;
import io.github.ultreon.controllerx.input.ControllerInput;
import io.github.ultreon.controllerx.input.InputType;
import io.github.ultreon.controllerx.input.keyboard.KeyboardLayouts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.libsdl4j.api.Sdl.SDL_Init;
import static io.github.libsdl4j.api.Sdl.SDL_Quit;

public class ControllerX {
    public static final String MOD_ID = "controllerx";
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final Logger LOGGER = LoggerFactory.getLogger("ControllerX");
    public static final byte MAX_CONTROLLERS = 1;

    private static ControllerX instance;

    public ControllerInput controllerInput;
    private ControllerHud controllerHud;
    private KeyboardHud keyboardHud;
    private InputType inputType = InputType.KEYBOARD_AND_MOUSE;
    private int inputCooldown;
    private boolean canChangeInput = true;

    @ApiStatus.Internal
    public VirtualKeyboard virtualKeyboard;

    private ControllerX() {
        instance = this;

        ClientLifecycleEvent.CLIENT_STARTED.register(this::clientStarted);

        LOGGER.info("ControllerX initialized");
    }

    private EventResult initGui(Screen screen, ScreenAccess screenAccess) {
        if (controllerInput.isVirtualKeyboardOpen()) {
            // SCARY!
            virtualKeyboard.getScreen().resize(Minecraft.getInstance(), Minecraft.getInstance().getWindow().getGuiScaledWidth(), Minecraft.getInstance().getWindow().getGuiScaledHeight());
            return EventResult.pass();
        }

        return EventResult.pass();
    }

    private void postInitGui(Screen screen, ScreenAccess screenAccess) {
        if (screen instanceof AbstractContainerScreen<?> containerScreen) {
            Hooks.hookContainerSlots(containerScreen, screenAccess);
        }
    }

    private void initKeyboardLayout() {
        this.controllerInput.setLayout(KeyboardLayouts.QWERTY);
    }

    private void tickInput(Minecraft minecraft) {
        Screen screen = minecraft.screen;

        if (screen != null) {
            controllerInput.updateScreen(screen);
        }

        if (inputCooldown > 0) {
            inputCooldown--;
            if (inputCooldown == 0) {
                canChangeInput = true;
            }
        }
    }

    private void renderGui(Screen screen, GuiGraphics gfx, int mouseX, int mouseY, float partialTicks) {
        if (controllerInput.isVirtualKeyboardOpen()) {
            virtualKeyboard.render(gfx, mouseX, mouseY, partialTicks);
            return;
        }
        controllerHud.render(gfx, partialTicks);
    }

    public static ResourceLocation res(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    @ExpectPlatform
    public static double getEntityReach(Player player) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static double getBlockReach(Player player) {
        throw new AssertionError();
    }

    private void clientStarted(Minecraft instance) {
        ControllerContext.freeze();

        KeyboardHud.addMapping(Minecraft.getInstance().options.keyAttack);
        KeyboardHud.addMapping(Minecraft.getInstance().options.keyUse);
        KeyboardHud.addMapping(Minecraft.getInstance().options.keyJump);
        KeyboardHud.addMapping(Minecraft.getInstance().options.keyShift);
        KeyboardHud.addMapping(Minecraft.getInstance().options.keyChat);
        KeyboardHud.addMapping(Minecraft.getInstance().options.keyCommand);

        SDL_Init(SdlSubSystemConst.SDL_INIT_EVENTS | SdlSubSystemConst.SDL_INIT_GAMECONTROLLER | SdlSubSystemConst.SDL_INIT_JOYSTICK);
        ClientLifecycleEvent.CLIENT_STOPPING.register(ControllerX::quitGame);
        controllerInput = new ControllerInput(this);

        controllerHud = new ControllerHud();
        keyboardHud = new KeyboardHud();

        ClientGuiEvent.RENDER_HUD.register(this::renderHud);
        ClientGuiEvent.RENDER_POST.register(this::renderGui);
        ClientGuiEvent.INIT_PRE.register(this::initGui);
        ClientGuiEvent.INIT_POST.register(this::postInitGui);

        ClientTickEvent.CLIENT_PRE.register(this::tickInput);

        if (controllerInput.isConnected()) {
            inputType = InputType.CONTROLLER;
        }

        this.initKeyboardLayout();
        virtualKeyboard = new VirtualKeyboard();

        ClientScreenInputEvent.KEY_PRESSED_PRE.register((client, screen, keyCode, scanCode, modifiers) -> {
            setInputType(InputType.KEYBOARD_AND_MOUSE);

            if (controllerInput.isVirtualKeyboardOpen()) {
                virtualKeyboard.getScreen().keyPressed(keyCode, scanCode, modifiers);
                return EventResult.interruptFalse();
            }
            return EventResult.pass();
        });
        ClientScreenInputEvent.KEY_RELEASED_PRE.register((client, screen, keyCode, scanCode, modifiers) -> {
            setInputType(InputType.KEYBOARD_AND_MOUSE);
            if (controllerInput.isVirtualKeyboardOpen()) {
                virtualKeyboard.getScreen().keyReleased(keyCode, scanCode, modifiers);
                return EventResult.interruptFalse();
            }
            return EventResult.pass();
        });
        ClientScreenInputEvent.CHAR_TYPED_PRE.register((client, screen, character, keyCode) -> {
            setInputType(InputType.KEYBOARD_AND_MOUSE);
            if (controllerInput.isVirtualKeyboardOpen()) {
                virtualKeyboard.getScreen().charTyped(character, keyCode);
                return EventResult.interruptFalse();
            }
            return EventResult.pass();
        });
        ClientScreenInputEvent.MOUSE_CLICKED_PRE.register((client, screen, x, y, button) -> {
            setInputType(InputType.KEYBOARD_AND_MOUSE);
            if (controllerInput.isVirtualKeyboardOpen()) {
                virtualKeyboard.getScreen().mouseClicked(x, y, button);
                return EventResult.interruptFalse();
            }
            return EventResult.pass();
        });
        ClientScreenInputEvent.MOUSE_DRAGGED_PRE.register((client, screen, mouseX1, mouseY1, button, mouseX2, mouseY2) -> {
            setInputType(InputType.KEYBOARD_AND_MOUSE);
            if (controllerInput.isVirtualKeyboardOpen()) {
                virtualKeyboard.getScreen().mouseDragged(mouseX1, mouseY1, button, mouseX2, mouseY2);
                return EventResult.interruptFalse();
            }
            return EventResult.pass();
        });
        ClientScreenInputEvent.MOUSE_SCROLLED_PRE.register((client, screen, mouseX, mouseY, amount) -> {
            setInputType(InputType.KEYBOARD_AND_MOUSE);
            if (controllerInput.isVirtualKeyboardOpen()) {
                virtualKeyboard.getScreen().mouseScrolled(mouseX, mouseY, amount);
                return EventResult.interruptFalse();
            }
            return EventResult.pass();
        });
        ClientScreenInputEvent.MOUSE_RELEASED_PRE.register((client, screen, mouseX, mouseY, button) -> {
            setInputType(InputType.KEYBOARD_AND_MOUSE);
            if (controllerInput.isVirtualKeyboardOpen()) {
                virtualKeyboard.getScreen().mouseReleased(mouseX, mouseY, button);
                return EventResult.interruptFalse();
            }
            return EventResult.pass();
        });

    }

    private void renderHud(GuiGraphics gfx, float ignoredPartialTicks) {
        if (Minecraft.getInstance().screen != null) return;

        controllerHud.render(gfx, ignoredPartialTicks);
        keyboardHud.render(gfx, ignoredPartialTicks);
        controllerInput.update();
    }

    private static void quitGame(Minecraft instance) {
        SDL_Quit();
    }

    public static ControllerX get() {
        if (instance == null) instance = new ControllerX();
        return instance;
    }

    public void setInputType(InputType inputType, int cooldown) {
        if (!canChangeInput) return;
        if (inputType == this.inputType) return;

        this.inputType = inputType;
        this.inputCooldown = cooldown;
        this.canChangeInput = false;
    }

    @ApiStatus.Experimental
    public void forceSetInputType(InputType inputType, int cooldown) {
        if (inputType == this.inputType) return;

        this.inputType = inputType;
        this.inputCooldown = cooldown;
        this.canChangeInput = false;
    }

    public InputType getInputType() {
        return inputType;
    }

    public void setInputType(InputType inputType) {
        this.setInputType(inputType, 10);
    }
}
