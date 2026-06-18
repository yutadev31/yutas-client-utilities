package com.yutadev31.yutasClientUtilities.client;

import com.yutadev31.yutasClientUtilities.client.feature.coordinate.CoordinateCopyFeature;

import net.fabricmc.api.ClientModInitializer;

public class YutasClientUtilitiesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CoordinateCopyFeature.initialize();
    }
}
