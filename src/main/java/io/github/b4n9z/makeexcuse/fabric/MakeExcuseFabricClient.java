package io.github.b4n9z.makeexcuse.fabric;

import io.github.b4n9z.makeexcuse.MakeExcuse;
import io.github.b4n9z.makeexcuse.client.MakeExcuseClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

/**
 * Fabric client-side entry point for MakeExcuse.
 */
public class MakeExcuseFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MakeExcuse.init();

        KeyBindingHelper.registerKeyBinding(MakeExcuseClient.DROP_KEY);
        ClientTickEvents.END_CLIENT_TICK.register(MakeExcuseClient::onClientTick);

        MakeExcuse.LOGGER.info("[{}] Fabric client initialized with emergency drop key registered.", MakeExcuse.MOD_NAME);
    }
}
