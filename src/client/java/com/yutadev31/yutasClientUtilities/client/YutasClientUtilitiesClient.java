package com.yutadev31.yutasClientUtilities.client;

import net.fabricmc.api.ClientModInitializer;

public class YutasClientUtilitiesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CoordinateCopyFeature.initialize();
    }
}
