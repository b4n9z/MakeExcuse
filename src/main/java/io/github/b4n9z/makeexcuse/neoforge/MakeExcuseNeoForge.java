package io.github.b4n9z.makeexcuse.neoforge;

import io.github.b4n9z.makeexcuse.MakeExcuse;
import io.github.b4n9z.makeexcuse.client.MakeExcuseClient;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;

/**
 * NeoForge platform entry point for MakeExcuse.
 */
@Mod(MakeExcuse.MOD_ID)
public class MakeExcuseNeoForge {

    public MakeExcuseNeoForge(IEventBus modBus) {
        MakeExcuse.init();

        modBus.addListener((RegisterKeyMappingsEvent event) -> event.register(MakeExcuseClient.DROP_KEY));
        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post event) -> MakeExcuseClient.onClientTick(Minecraft.getInstance()));

        MakeExcuse.LOGGER.info("[{}] NeoForge platform initialized with emergency drop key registered.", MakeExcuse.MOD_NAME);
    }
}
