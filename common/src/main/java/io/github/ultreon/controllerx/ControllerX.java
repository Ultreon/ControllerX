package io.github.ultreon.controllerx;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.injectables.annotations.ExpectPlatform;
import io.github.libsdl4j.api.SdlSubSystemConst;
import io.github.ultreon.controllerx.api.ControllerContext;
import io.github.ultreon.controllerx.gui.ControllerHud;
import io.github.ultreon.controllerx.gui.KeyboardHud;
import io.github.ultreon.controllerx.input.ControllerInput;
import io.github.ultreon.controllerx.input.InputType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
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

    public final ControllerInput controllerInput;
    private final ControllerHud controllerHud;
    private final KeyboardHud keyboardHud;
    private InputType inputType = InputType.KEYBOARD_AND_MOUSE;
    private int inputCooldown;
    private boolean canChangeInput = true;

    private ControllerX() {
        instance = this;

        SDL_Init(SdlSubSystemConst.SDL_INIT_EVENTS | SdlSubSystemConst.SDL_INIT_GAMECONTROLLER | SdlSubSystemConst.SDL_INIT_JOYSTICK);
        ClientLifecycleEvent.CLIENT_STOPPING.register(ControllerX::quitGame);
        controllerInput = new ControllerInput(this);

        controllerHud = new ControllerHud();
        keyboardHud = new KeyboardHud();

        ClientGuiEvent.RENDER_HUD.register(this::renderHud);
        ClientGuiEvent.RENDER_POST.register(this::renderGui);

        ClientTickEvent.CLIENT_PRE.register(this::tickInput);

        ClientLifecycleEvent.CLIENT_STARTED.register(ControllerX::clientStarted);

        if (controllerInput.isConnected()) {
            inputType = InputType.CONTROLLER;
        }

        LOGGER.info("ControllerX initialized");
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

    private static void clientStarted(Minecraft instance) {
        ControllerContext.freeze();

        KeyboardHud.addMapping(Minecraft.getInstance().options.keyAttack);
        KeyboardHud.addMapping(Minecraft.getInstance().options.keyUse);
        KeyboardHud.addMapping(Minecraft.getInstance().options.keyJump);
        KeyboardHud.addMapping(Minecraft.getInstance().options.keyShift);
        KeyboardHud.addMapping(Minecraft.getInstance().options.keyChat);
        KeyboardHud.addMapping(Minecraft.getInstance().options.keyCommand);
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
