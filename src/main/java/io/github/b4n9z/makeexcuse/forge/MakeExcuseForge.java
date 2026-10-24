package io.github.b4n9z.makeexcuse.forge;

import io.github.b4n9z.makeexcuse.MakeExcuse;
import io.github.b4n9z.makeexcuse.client.MakeExcuseClient;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Forge platform entry point for MakeExcuse.
 */
@Mod(MakeExcuse.MOD_ID)
public class MakeExcuseForge {

    public MakeExcuseForge() {
        MakeExcuse.init();

        RegisterKeyMappingsEvent.BUS.addListener(event -> event.register(MakeExcuseClient.DROP_KEY));
        TickEvent.ClientTickEvent.Post.BUS.addListener(event -> MakeExcuseClient.onClientTick(Minecraft.getInstance()));

        MakeExcuse.LOGGER.info("[{}] Forge platform initialized with emergency drop key registered.", MakeExcuse.MOD_NAME);
    }
}
