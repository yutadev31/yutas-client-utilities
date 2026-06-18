package com.yutadev31.yutasClientUtilities.client.feature.coordinate;

import java.util.Locale;
import java.util.function.Function;

import org.lwjgl.glfw.GLFW;

import com.yutadev31.yutasClientUtilities.client.config.YutasClientUtilitiesConfig;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class CoordinateCopyFeature {
    private static final String DEFAULT_FORMAT = "{x} {y} {z}";
    private static final KeyBinding.Category KEY_CATEGORY = KeyBinding.Category.create(Identifier.of("yutas-client-utilities", "general"));
    private static final KeyBinding COPY_COORDINATES_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.yutas-client-utilities.copy_coordinates",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            KEY_CATEGORY));
    private static final KeyBinding OPEN_RADIAL_MENU_PREFIX_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.yutas-client-utilities.open_radial_menu_prefix",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            KEY_CATEGORY));
    private static final KeyBinding OPEN_COORDINATE_MENU_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.yutas-client-utilities.open_coordinate_menu",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            KEY_CATEGORY));
    private static final RadialMenuBinding[] RADIAL_MENU_BINDINGS = {
            new RadialMenuBinding(
                    OPEN_COORDINATE_MENU_KEY,
                    client -> new CoordinateRadialMenuScreen(OPEN_COORDINATE_MENU_KEY))
    };
    private static RadialMenuBinding previouslyPressedMenu;

    private CoordinateCopyFeature() {
    }

    public static void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (COPY_COORDINATES_KEY.wasPressed()) {
                copyCoordinates(client, CoordinateCopyAction.CUSTOM);
            }

            RadialMenuBinding activeMenu = getActiveRadialMenu(client);
            if (activeMenu != null && activeMenu != previouslyPressedMenu) {
                openRadialMenu(client, activeMenu);
            }
            previouslyPressedMenu = activeMenu;
        });
    }

    static void copyCoordinates(MinecraftClient client, CoordinateCopyAction action) {
        PlayerEntity player = client.player;
        if (player == null || action == null) {
            return;
        }

        String copied = action.format(player);
        if (copied == null || copied.isBlank()) {
            player.sendMessage(action.getUnavailableMessage(), true);
            return;
        }

        client.keyboard.setClipboard(copied);
        player.sendMessage(Text.translatable("message.yutas-client-utilities.coordinate-copy.success", copied), true);
    }

    static String formatCoordinates(String format, PlayerEntity player) {
        String resolvedFormat = format == null || format.isBlank() ? DEFAULT_FORMAT : format;
        return resolvedFormat
                .replace("{x}", Integer.toString(player.getBlockX()))
                .replace("{y}", Integer.toString(player.getBlockY()))
                .replace("{z}", Integer.toString(player.getBlockZ()))
                .replace("{fx}", formatDouble(player.getX()))
                .replace("{fy}", formatDouble(player.getY()))
                .replace("{fz}", formatDouble(player.getZ()));
    }

    static String formatCoordinates(double x, double y, double z) {
        return formatCoordinates(
                formatDouble(x),
                formatDouble(y),
                formatDouble(z));
    }

    static String formatCoordinates(String x, String y, String z) {
        return DEFAULT_FORMAT
                .replace("{x}", x)
                .replace("{y}", y)
                .replace("{z}", z);
    }

    private static String formatDouble(double value) {
        return String.format(Locale.ROOT, "%.3f", value);
    }

    static boolean isMenuComboPressed(MinecraftClient client, KeyBinding selectorKey) {
        if (client == null) {
            return false;
        }

        Key prefixKey = InputUtil.fromTranslationKey(OPEN_RADIAL_MENU_PREFIX_KEY.getBoundKeyTranslationKey());
        Key menuKey = InputUtil.fromTranslationKey(selectorKey.getBoundKeyTranslationKey());
        if (prefixKey == InputUtil.UNKNOWN_KEY || menuKey == InputUtil.UNKNOWN_KEY) {
            return false;
        }

        long handle = client.getWindow().getHandle();
        return isBindingPressed(client, handle, prefixKey) && isBindingPressed(client, handle, menuKey);
    }

    private static void openRadialMenu(MinecraftClient client, RadialMenuBinding menuBinding) {
        if (client.player == null || client.currentScreen != null) {
            return;
        }

        client.setScreen(menuBinding.createScreen(client));
    }

    private static RadialMenuBinding getActiveRadialMenu(MinecraftClient client) {
        for (RadialMenuBinding menuBinding : RADIAL_MENU_BINDINGS) {
            if (isMenuComboPressed(client, menuBinding.selectorKey())) {
                return menuBinding;
            }
        }
        return null;
    }

    private static boolean isBindingPressed(MinecraftClient client, long handle, Key boundKey) {
        if (boundKey == InputUtil.UNKNOWN_KEY) {
            return false;
        }

        return switch (boundKey.getCategory()) {
            case KEYSYM, SCANCODE -> InputUtil.isKeyPressed(client.getWindow(), boundKey.getCode());
            case MOUSE -> GLFW.glfwGetMouseButton(handle, boundKey.getCode()) == GLFW.GLFW_PRESS;
        };
    }

    private record RadialMenuBinding(KeyBinding selectorKey, Function<MinecraftClient, Screen> screenFactory) {
        private Screen createScreen(MinecraftClient client) {
            return screenFactory.apply(client);
        }
    }
}
