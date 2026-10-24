package io.github.b4n9z.makeexcuse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common entry point and constant definitions for MakeExcuse.
 * Shared across Fabric, Forge, and NeoForge loaders.
 */
public class MakeExcuse {
    public static final String MOD_ID = "makeexcuse";
    public static final String MOD_NAME = "MakeExcuse";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    /**
     * Common initialization logic called by each platform loader entry point.
     */
    public static void init() {
        LOGGER.info("[{}] Initializing MakeExcuse client PvP utility mod...", MOD_NAME);
    }
}
