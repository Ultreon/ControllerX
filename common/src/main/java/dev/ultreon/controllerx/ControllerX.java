package dev.ultreon.controllerx;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientScreenInputEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.hooks.client.screen.ScreenAccess;
import dev.ultreon.controllerx.api.ICxInternals;
import dev.ultreon.controllerx.api.config.IConfig;
import dev.ultreon.controllerx.api.extension.ICxExtension;
import dev.ultreon.controllerx.api.input.IControllerBackend;
import dev.ultreon.controllerx.impl.ControllerMappings;
import dev.ultreon.controllerx.api.IControllerMappings;
import dev.ultreon.controllerx.api.IControllerX;
import dev.ultreon.controllerx.api.input.IControllerInput;
import dev.ultreon.controllerx.config.gui.BindingsScreen;
import dev.ultreon.controllerx.impl.contexts.VirtKeyboardControllerContext;
import dev.ultreon.controllerx.init.ModSounds;
import dev.ultreon.controllerx.backend.glfw.GLFWControllerBackend;
import dev.ultreon.controllerx.backend.sdl.SdlControllerBackend;
import dev.ultreon.controllerx.api.ControllerContext;
import dev.ultreon.controllerx.config.Config;
import dev.ultreon.controllerx.gui.ControllerHud;
import dev.ultreon.controllerx.gui.KeyboardHud;
import dev.ultreon.controllerx.input.ControllerInput;
import dev.ultreon.controllerx.api.input.InputType;
import dev.ultreon.controllerx.api.input.keyboard.keyboard.KeyboardLayouts;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.controls.ControlsScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public abstract class ControllerX implements IControllerX {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final Logger LOGGER = LoggerFactory.getLogger("ControllerX");
    public static final String BINDINGS_DIRECTORY = "config/controllerx-bindings";

    private static ControllerX instance;

    public ControllerInput input;
    public boolean skippedWarning;
    private ControllerHud controllerHud;
    private KeyboardHud keyboardHud;
    private InputType inputType = InputType.KEYBOARD_AND_MOUSE;
    private int inputCooldown;
    private boolean canChangeInput = true;

    @ApiStatus.Internal
    public VirtualKeyboard virtualKeyboard;
    private final CxInternals cxInternals = new CxInternals();
    private final List<ICxExtension> extensions = new ArrayList<>();
    private static IControllerBackend backend;

    protected ControllerX() {
        instance = this;

        ModSounds.register();

        Util.OS platform = Util.getPlatform();
        backend = platform == Util.OS.WINDOWS || platform == Util.OS.LINUX ? new SdlControllerBackend() : new GLFWControllerBackend();

        ServiceLoader<ICxExtension> load = ServiceLoader.load(ICxExtension.class);
        for (ICxExtension extension : load) {
            extensions.add(extension);
        }

        ClientLifecycleEvent.CLIENT_STARTED.register(this::clientStarted);

        LOGGER.info("ControllerX initialized");
    }

    private EventResult initGui(Screen screen, ScreenAccess screenAccess) {
        if (input.isVirtualKeyboardOpen()) {
            // SCARY!
            virtualKeyboard.getScreen().resize(Minecraft.getInstance(), Minecraft.getInstance().getWindow().getGuiScaledWidth(), Minecraft.getInstance().getWindow().getGuiScaledHeight());
            return EventResult.pass();
        }

        return EventResult.pass();
    }

    private void postInitGui(Screen screen, ScreenAccess screenAccess) {
        if (screen instanceof AbstractContainerScreen<?> containerScreen) {
            Hooks.hookContainerSlots(containerScreen, screenAccess);
        } else if (screen instanceof ControlsScreen controlsScreen) {
            List<? extends GuiEventListener> children = controlsScreen.children();
            for (GuiEventListener child : children) {
                if (child instanceof Button button) {
                    if (button.getMessage().equals(Component.translatable("gui.done"))) {
                        screenAccess.addRenderableWidget(Button.builder(Component.translatable("controllerx.screen.controller_bindings"), btn -> new BindingsScreen(screen).open()).bounds(button.getX(), button.getY(), button.getWidth(), button.getHeight()).build());
                        button.setY(button.getY() + button.getHeight() + 10);
                        break;
                    }
                }
            }
        }
    }

    private void initKeyboardLayout() {
        input.setLayout(KeyboardLayouts.QWERTY);
    }

    private void tickInput(Minecraft minecraft) {
        Screen screen = minecraft.screen;

        if (screen != null) {
            input.updateScreen(screen);
        }

        if (inputCooldown > 0) {
            inputCooldown--;
            if (inputCooldown == 0) {
                canChangeInput = true;
            }
        }
    }

    private void renderGui(Screen screen, GuiGraphics gfx, int mouseX, int mouseY, float partialTicks) {
        if (input.isVirtualKeyboardOpen()) {
            virtualKeyboard.render(gfx, mouseX, mouseY, partialTicks);
            return;
        }
        controllerHud.render(gfx, partialTicks);
    }

    public static ResourceLocation res(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public void initMod() {
        if (Util.getPlatform() != Util.OS.OSX) backend.init();

        ClientLifecycleEvent.CLIENT_STOPPING.register(ControllerX::quitGame);
        input = new ControllerInput(this);

        controllerHud = new ControllerHud();
        keyboardHud = new KeyboardHud();

        ClientGuiEvent.RENDER_HUD.register(this::renderHud);
        ClientGuiEvent.RENDER_POST.register(this::renderGui);
        ClientGuiEvent.INIT_PRE.register(this::initGui);
        ClientGuiEvent.INIT_POST.register(this::postInitGui);

        ClientTickEvent.CLIENT_PRE.register(this::tickInput);

        if (input.isConnected()) {
            inputType = InputType.CONTROLLER;
        }

        initKeyboardLayout();
        virtualKeyboard = new VirtualKeyboard();

        ClientScreenInputEvent.KEY_PRESSED_PRE.register((client, screen, keyCode, scanCode, modifiers) -> {
            setInputType(InputType.KEYBOARD_AND_MOUSE);

            if (input.isVirtualKeyboardOpen()) {
                virtualKeyboard.getScreen().keyPressed(keyCode, scanCode, modifiers);
                return EventResult.interruptFalse();
            }
            return EventResult.pass();
        });
        ClientScreenInputEvent.KEY_RELEASED_PRE.register((client, screen, keyCode, scanCode, modifiers) -> {
            setInputType(InputType.KEYBOARD_AND_MOUSE);
            if (input.isVirtualKeyboardOpen()) {
                virtualKeyboard.getScreen().keyReleased(keyCode, scanCode, modifiers);
                return EventResult.interruptFalse();
            }
            return EventResult.pass();
        });
        ClientScreenInputEvent.CHAR_TYPED_PRE.register((client, screen, character, keyCode) -> {
            setInputType(InputType.KEYBOARD_AND_MOUSE);
            if (input.isVirtualKeyboardOpen()) {
                virtualKeyboard.getScreen().charTyped(character, keyCode);
                return EventResult.interruptFalse();
            }
            return EventResult.pass();
        });
        ClientScreenInputEvent.MOUSE_CLICKED_PRE.register((client, screen, x, y, button) -> {
            setInputType(InputType.KEYBOARD_AND_MOUSE);
            if (input.isVirtualKeyboardOpen()) {
                virtualKeyboard.getScreen().mouseClicked(x, y, button);
                return EventResult.interruptFalse();
            }
            return EventResult.pass();
        });
        ClientScreenInputEvent.MOUSE_DRAGGED_PRE.register((client, screen, mouseX1, mouseY1, button, mouseX2, mouseY2) -> {
            setInputType(InputType.KEYBOARD_AND_MOUSE);
            if (input.isVirtualKeyboardOpen()) {
                virtualKeyboard.getScreen().mouseDragged(mouseX1, mouseY1, button, mouseX2, mouseY2);
                return EventResult.interruptFalse();
            }
            return EventResult.pass();
        });
        ClientScreenInputEvent.MOUSE_SCROLLED_PRE.register((client, screen, mouseX, mouseY, amount) -> {
            setInputType(InputType.KEYBOARD_AND_MOUSE);
            if (input.isVirtualKeyboardOpen()) {
                virtualKeyboard.getScreen().mouseScrolled(mouseX, mouseY, amount);
                return EventResult.interruptFalse();
            }
            return EventResult.pass();
        });
        ClientScreenInputEvent.MOUSE_RELEASED_PRE.register((client, screen, mouseX, mouseY, button) -> {
            setInputType(InputType.KEYBOARD_AND_MOUSE);
            if (input.isVirtualKeyboardOpen()) {
                virtualKeyboard.getScreen().mouseReleased(mouseX, mouseY, button);
                return EventResult.interruptFalse();
            }
            return EventResult.pass();
        });

        Iterable<IConfig> configs = ControllerContext.createConfigs();

        Path dir = Paths.get(BINDINGS_DIRECTORY);
        if (!Files.exists(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                LOGGER.error("Failed to create config directory", e);
            }

            for (IConfig config : configs) {
                config.save();
            }
        } else for (IConfig config : configs) {
            config.load();
        }
    }

    private static boolean isUsingVirtualKeyboard(Minecraft minecraft) {
        return IControllerX.get().getInput().isVirtualKeyboardOpen();
    }

    private void clientStarted(Minecraft instance) {
        ControllerContext.register(VirtKeyboardControllerContext.INSTANCE, ControllerX::isUsingVirtualKeyboard);
        for (ICxExtension extension : extensions) extension.onRegisterContexts();
        ControllerContext.freeze();

        KeyboardHud.addMapping(Minecraft.getInstance().options.keyAttack);
        KeyboardHud.addMapping(Minecraft.getInstance().options.keyUse);
        KeyboardHud.addMapping(Minecraft.getInstance().options.keyJump);
        KeyboardHud.addMapping(Minecraft.getInstance().options.keyShift);
        KeyboardHud.addMapping(Minecraft.getInstance().options.keyChat);
        KeyboardHud.addMapping(Minecraft.getInstance().options.keyCommand);

        if (Util.getPlatform() != Util.OS.OSX) {
            initMod();
        }
    }

    private void renderHud(GuiGraphics gfx, float partialTicks) {
        if (Minecraft.getInstance().screen != null) return;

        controllerHud.render(gfx, partialTicks);
        keyboardHud.render(gfx, partialTicks);
        input.update();
    }

    private static void quitGame(Minecraft instance) {
        backend.quit();
    }

    public static ControllerX get() {
        return instance;
    }

    @Override
    public IControllerMappings createMappings() {
        return new ControllerMappings();
    }

    @Override
    public IControllerInput getInput() {
        return input;
    }

    @Override
    public IConfig createConfig(ResourceLocation id, ControllerContext controllerContext) {
        Config config = new Config(id, controllerContext);
        Config.register(config);
        return config;
    }

    @Override
    public ICxInternals getInternals() {
        return cxInternals;
    }

    public void setInputType(InputType inputType, int cooldown) {
        if (!canChangeInput) return;
        if (inputType == this.inputType) return;

        this.inputType = inputType;
        inputCooldown = cooldown;
        canChangeInput = false;
    }

    @ApiStatus.Experimental
    public void forceSetInputType(InputType inputType, int cooldown) {
        if (inputType == this.inputType) return;

        this.inputType = inputType;
        inputCooldown = cooldown;
        canChangeInput = false;
    }

    public InputType getInputType() {
        return inputType;
    }

    public void setInputType(InputType inputType) {
        setInputType(inputType, 10);
    }

    public IControllerBackend getBackend() {
        return backend;
    }
}
