package com.yutadev31.yutasClientUtilities.client;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

final class YutasClientUtilitiesConfigScreen {
    private YutasClientUtilitiesConfigScreen() {
    }

    static Screen create(Screen parent) {
        YutasClientUtilitiesConfig config = YutasClientUtilitiesConfig.get();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("title.yutas-client-utilities.config"));

        ConfigCategory general = builder.getOrCreateCategory(Text.literal("General"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        general.addEntry(entryBuilder
                .startStrField(Text.literal("座標コピーのフォーマット"), config.getCoordinateCopyFormat())
                .setDefaultValue("{x} {y} {z}")
                .setTooltip(
                        Text.literal("円形メニューの「カスタム」で使う文字列です。"),
                        Text.literal("使用可能: {x} {y} {z} は整数座標、{fx} {fy} {fz} は小数座標です。"),
                        Text.literal("キー割り当ては操作設定から「座標をコピー」と「座標メニューを開く」を設定してください。"),
                        Text.literal("メニューキーを押すと開き、マウス移動で選択、キーを離すとコピーします。"))
                .setSaveConsumer(config::setCoordinateCopyFormat)
                .build());

        builder.setSavingRunnable(() -> {
            YutasClientUtilitiesConfig.save();
        });

        return builder.build();
    }
}
