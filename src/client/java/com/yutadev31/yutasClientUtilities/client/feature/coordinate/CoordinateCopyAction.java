package com.yutadev31.yutasClientUtilities.client.feature.coordinate;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import com.yutadev31.yutasClientUtilities.client.config.YutasClientUtilitiesConfig;

enum CoordinateCopyAction {
    CUSTOM("radial.yutas-client-utilities.coordinate-copy.custom") {
        @Override
        String format(PlayerEntity player) {
            return CoordinateCopyFeature.formatCoordinates(
                    YutasClientUtilitiesConfig.get().getCoordinateCopyFormat(),
                    player);
        }
    },
    BLOCK("radial.yutas-client-utilities.coordinate-copy.block") {
        @Override
        String format(PlayerEntity player) {
            return CoordinateCopyFeature.formatCoordinates("{x} {y} {z}", player);
        }
    },
    PRECISE("radial.yutas-client-utilities.coordinate-copy.precise") {
        @Override
        String format(PlayerEntity player) {
            return CoordinateCopyFeature.formatCoordinates("{fx} {fy} {fz}", player);
        }
    },
    XZ("radial.yutas-client-utilities.coordinate-copy.xz") {
        @Override
        String format(PlayerEntity player) {
            return CoordinateCopyFeature.formatCoordinates("{x} {z}", player);
        }
    },
    CORRESPONDING("radial.yutas-client-utilities.coordinate-copy.corresponding") {
        @Override
        String format(PlayerEntity player) {
            if (player.getEntityWorld().getRegistryKey() == World.OVERWORLD) {
                return CoordinateCopyFeature.formatCoordinates(player.getX() / 8.0D, player.getY(), player.getZ() / 8.0D);
            }

            if (player.getEntityWorld().getRegistryKey() == World.NETHER) {
                return CoordinateCopyFeature.formatCoordinates(player.getX() * 8.0D, player.getY(), player.getZ() * 8.0D);
            }

            return null;
        }
    },
    CHUNK("radial.yutas-client-utilities.coordinate-copy.chunk") {
        @Override
        String format(PlayerEntity player) {
            return player.getChunkPos().x + " " + player.getChunkPos().z;
        }
    };

    private final String translationKey;

    CoordinateCopyAction(String translationKey) {
        this.translationKey = translationKey;
    }

    Text getLabel() {
        return Text.translatable(translationKey);
    }

    Text getUnavailableMessage() {
        return Text.translatable("message.yutas-client-utilities.coordinate-copy.unavailable");
    }

    abstract String format(PlayerEntity player);
}
