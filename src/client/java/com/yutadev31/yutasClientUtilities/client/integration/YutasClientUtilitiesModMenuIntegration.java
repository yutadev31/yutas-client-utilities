package com.yutadev31.yutasClientUtilities.client.integration;

import com.yutadev31.yutasClientUtilities.client.config.YutasClientUtilitiesConfigScreen;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public final class YutasClientUtilitiesModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return YutasClientUtilitiesConfigScreen::create;
    }
}
