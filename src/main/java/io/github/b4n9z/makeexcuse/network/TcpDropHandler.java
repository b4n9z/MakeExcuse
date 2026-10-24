package io.github.b4n9z.makeexcuse.network;

import io.github.b4n9z.makeexcuse.MakeExcuse;
import io.github.b4n9z.makeexcuse.mixin.ConnectionAccessor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import java.io.IOException;
import java.lang.reflect.Field;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;

/**
 * Handles the instant TCP connection drop action to simulate network disconnect errors.
 */
public class TcpDropHandler {

    /**
     * Executes the instant connection drop on the active client network session.
     */
    public static void dropCurrentConnection() {
        Minecraft minecraft = Minecraft.getInstance();
        ClientPacketListener listener = minecraft.getConnection();
        if (listener == null) {
            MakeExcuse.LOGGER.warn("[{}] Cannot drop connection: no active ClientPacketListener.", MakeExcuse.MOD_NAME);
            return;
        }

        Connection connection = listener.getConnection();
        if (connection == null) {
            MakeExcuse.LOGGER.warn("[{}] Cannot drop connection: Connection instance is null.", MakeExcuse.MOD_NAME);
            return;
        }

        drop(connection);
    }

    /**
     * Forcibly closes the Netty socket channel immediately without sending a graceful disconnect packet.
     * Sets SO_LINGER to 0 to trigger an instant TCP RST and fires a natural network exception.
     *
     * @param connection the active network connection to terminate
     */
    public static void drop(Connection connection) {
        MakeExcuse.LOGGER.info("[{}] Emergency TCP drop initiated by player!", MakeExcuse.MOD_NAME);

        Channel channel = getChannel(connection);

        if (channel != null && channel.isOpen()) {
            try {
                // Configure SO_LINGER to 0 so the OS socket stack sends an immediate TCP RST
                // rather than performing the graceful 4-way FIN-ACK handshake.
                channel.config().setOption(ChannelOption.SO_LINGER, 0);
            } catch (Throwable t) {
                MakeExcuse.LOGGER.debug("[{}] Could not set SO_LINGER=0: {}", MakeExcuse.MOD_NAME, t.getMessage());
            }

            try {
                // Fire an IOException onto the pipeline so Minecraft records an authentic network failure reason
                channel.pipeline().fireExceptionCaught(new IOException("Connection reset by peer"));
            } catch (Throwable t) {
                MakeExcuse.LOGGER.debug("[{}] Could not fire exceptionCaught: {}", MakeExcuse.MOD_NAME, t.getMessage());
            }

            try {
                // Forcibly close the socket channel immediately
                channel.close();
            } catch (Throwable t) {
                MakeExcuse.LOGGER.error("[{}] Error closing Netty channel: {}", MakeExcuse.MOD_NAME, t.getMessage());
            }

            try {
                // Trigger client-side disconnection handling to transition to the standard DisconnectedScreen
                connection.handleDisconnection();
            } catch (Throwable t) {
                MakeExcuse.LOGGER.debug("[{}] Error calling handleDisconnection: {}", MakeExcuse.MOD_NAME, t.getMessage());
            }
        } else {
            // Fallback for non-Netty / memory connections (e.g., local integrated server)
            try {
                connection.disconnect(Component.literal("Internal Exception: java.io.IOException: Connection reset by peer"));
                connection.handleDisconnection();
            } catch (Throwable t) {
                MakeExcuse.LOGGER.error("[{}] Fallback disconnection failed: {}", MakeExcuse.MOD_NAME, t.getMessage());
            }
        }

        MakeExcuse.LOGGER.info("[{}] Instant TCP drop successfully executed.", MakeExcuse.MOD_NAME);
    }

    /**
     * Safely retrieves the underlying Netty Channel using Mixin Accessor or fallback reflection.
     */
    private static Channel getChannel(Connection connection) {
        // Primary path: Mixin accessor
        if (connection instanceof ConnectionAccessor accessor) {
            try {
                Channel ch = accessor.makeexcuse$getChannel();
                if (ch != null) {
                    return ch;
                }
            } catch (Throwable ignored) {
            }
        }

        // Secondary path: Direct named field reflection
        try {
            Field field = Connection.class.getDeclaredField("channel");
            field.setAccessible(true);
            return (Channel) field.get(connection);
        } catch (Throwable ignored) {
        }

        // Tertiary fallback: Inspect all fields matching Channel type
        for (Field f : Connection.class.getDeclaredFields()) {
            if (Channel.class.isAssignableFrom(f.getType())) {
                try {
                    f.setAccessible(true);
                    return (Channel) f.get(connection);
                } catch (Throwable ignored) {
                }
            }
        }

        return null;
    }
}
