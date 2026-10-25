package io.github.b4n9z.makeexcuse.client;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.b4n9z.makeexcuse.MakeExcuse;
import io.github.b4n9z.makeexcuse.network.TcpDropHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.resources.Identifier;

/**
 * Common client logic and key mapping definitions for MakeExcuse.
 */
public class MakeExcuseClient {

    public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(MakeExcuse.MOD_ID, "main")
    );

    public static final KeyMapping DROP_KEY = new KeyMapping(
            "key.makeexcuse.drop_connection",
            InputConstants.KEY_U,
            CATEGORY
    );

    /**
     * Checks keypress state during client tick and triggers emergency TCP drop if activated.
     *
     * @param client the active Minecraft client instance
     */
    public static void onClientTick(Minecraft client) {
        if (client == null) {
            return;
        }

        // Avoid dropping connection while the player is typing messages into the chat screen
        if (client.gui != null && client.gui.screen() instanceof ChatScreen) {
            return;
        }

        while (DROP_KEY.consumeClick()) {
            TcpDropHandler.dropCurrentConnection();
        }
    }
}
