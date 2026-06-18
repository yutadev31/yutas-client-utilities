package com.yutadev31.yutasClientUtilities.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

final class CoordinateRadialMenuScreen extends Screen {
    private static final CoordinateCopyAction[] ACTIONS = CoordinateCopyAction.values();
    private static final double INNER_DEADZONE = 24.0D;
    private static final double OPTION_DISTANCE = 88.0D;
    private static final int OPTION_HEIGHT = 24;
    private static final int OPTION_HORIZONTAL_PADDING = 8;
    private static final int LABEL_GAP = 6;
    private static final int NORMAL_COLOR = 0xAA1F1F1F;
    private static final int HIGHLIGHT_COLOR = 0xE070C070;
    private static final int PRIMARY_TEXT_COLOR = 0xFFFFFFFF;
    private final KeyBinding selectorKey;
    private boolean comboSeenPressed;

    CoordinateRadialMenuScreen(KeyBinding selectorKey) {
        super(Text.translatable("radial.yutas-client-utilities.coordinate-copy.title"));
        this.selectorKey = selectorKey;
    }

    @Override
    public void tick() {
        MinecraftClient client = this.client;
        if (client == null) {
            return;
        }

        if (client.player == null) {
            close();
            return;
        }

        if (CoordinateCopyFeature.isMenuComboPressed(client, selectorKey)) {
            comboSeenPressed = true;
            return;
        }

        if (comboSeenPressed) {
            finishSelection();
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        CoordinateCopyAction selected = getSelectedAction(mouseX, mouseY);

        context.fill(0, 0, this.width, this.height, 0x55000000);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, centerX, centerY - 10, PRIMARY_TEXT_COLOR);
        if (selected != null) {
            context.drawCenteredTextWithShadow(this.textRenderer, selected.getLabel(), centerX, centerY + 22, PRIMARY_TEXT_COLOR);
        }

        for (int i = 0; i < ACTIONS.length; i++) {
            CoordinateCopyAction action = ACTIONS[i];
            double angle = (Math.PI * 2.0D * i / ACTIONS.length) - (Math.PI / 2.0D);
            int optionCenterX = centerX + MathHelper.floor(Math.cos(angle) * OPTION_DISTANCE);
            int optionCenterY = centerY + MathHelper.floor(Math.sin(angle) * OPTION_DISTANCE);
            boolean highlighted = action == selected;
            int color = highlighted ? HIGHLIGHT_COLOR : NORMAL_COLOR;
            int textWidth = this.textRenderer.getWidth(action.getLabel());
            int optionWidth = textWidth + (OPTION_HORIZONTAL_PADDING * 2);
            int left = optionCenterX - (optionWidth / 2);
            int top = optionCenterY - (OPTION_HEIGHT / 2);
            int labelY = top + OPTION_HEIGHT + LABEL_GAP;

            context.fill(
                    left,
                    top,
                    left + optionWidth,
                    top + OPTION_HEIGHT,
                    color);
            drawBorder(context, left, top, optionWidth, OPTION_HEIGHT, 0xCCFFFFFF);
            context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    action.getLabel(),
                    optionCenterX,
                    top + 8,
                    PRIMARY_TEXT_COLOR);
            context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    action.getLabel(),
                    optionCenterX,
                    labelY,
                    PRIMARY_TEXT_COLOR);
        }

        super.render(context, mouseX, mouseY, deltaTicks);
    }

    private CoordinateCopyAction getSelectedAction(double mouseX, double mouseY) {
        double centerX = this.width / 2.0D;
        double centerY = this.height / 2.0D;
        double deltaX = mouseX - centerX;
        double deltaY = mouseY - centerY;

        if ((deltaX * deltaX) + (deltaY * deltaY) < INNER_DEADZONE * INNER_DEADZONE) {
            return null;
        }

        double angle = Math.atan2(deltaY, deltaX) + (Math.PI / 2.0D);
        if (angle < 0.0D) {
            angle += Math.PI * 2.0D;
        }

        int index = Math.floorMod((int) Math.round(angle / ((Math.PI * 2.0D) / ACTIONS.length)), ACTIONS.length);
        return ACTIONS[index];
    }

    private double getMouseX() {
        MinecraftClient client = this.client;
        if (client == null) {
            return this.width / 2.0D;
        }
        return client.mouse.getX() * this.width / client.getWindow().getWidth();
    }

    private double getMouseY() {
        MinecraftClient client = this.client;
        if (client == null) {
            return this.height / 2.0D;
        }
        return client.mouse.getY() * this.height / client.getWindow().getHeight();
    }

    private void drawBorder(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + 1, color);
        context.fill(x, y + height - 1, x + width, y + height, color);
        context.fill(x, y, x + 1, y + height, color);
        context.fill(x + width - 1, y, x + width, y + height, color);
    }

    private void finishSelection() {
        if (!comboSeenPressed || this.client == null) {
            close();
            return;
        }

        CoordinateCopyFeature.copyCoordinates(this.client, getSelectedAction(getMouseX(), getMouseY()));
        close();
    }
}
