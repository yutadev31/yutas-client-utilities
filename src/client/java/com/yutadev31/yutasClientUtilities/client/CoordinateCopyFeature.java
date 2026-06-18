package com.yutadev31.yutasClientUtilities.client;

import java.util.Locale;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.input.MouseInput;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

final class CoordinateCopyFeature {
    private static final String DEFAULT_FORMAT = "{x} {y} {z}";
    private static final KeyBinding.Category KEY_CATEGORY = KeyBinding.Category.create(Identifier.of("yutas-client-utilities", "general"));
    private static final KeyBinding COPY_COORDINATES_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.yutas-client-utilities.copy_coordinates",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            KEY_CATEGORY));
    private static final KeyBinding OPEN_COORDINATE_MENU_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.yutas-client-utilities.open_coordinate_menu",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            KEY_CATEGORY));

    private CoordinateCopyFeature() {
    }

    static void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (COPY_COORDINATES_KEY.wasPressed()) {
                copyCoordinates(client, CoordinateCopyAction.CUSTOM);
            }

            while (OPEN_COORDINATE_MENU_KEY.wasPressed()) {
                openRadialMenu(client);
            }
        });
    }

    static boolean matchesMenuKey(int keyCode, int scanCode, int modifiers) {
        return OPEN_COORDINATE_MENU_KEY.matchesKey(new KeyInput(keyCode, scanCode, modifiers));
    }

    static boolean matchesMenuMouse(double mouseX, double mouseY, int button, int modifiers) {
        return OPEN_COORDINATE_MENU_KEY.matchesMouse(new Click(mouseX, mouseY, new MouseInput(button, modifiers)));
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

    private static void openRadialMenu(MinecraftClient client) {
        if (client.player == null || client.currentScreen != null) {
            return;
        }

        client.setScreen(new CoordinateRadialMenuScreen());
    }
}
