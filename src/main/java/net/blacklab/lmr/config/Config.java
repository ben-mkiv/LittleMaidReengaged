package net.blacklab.lmr.config;

/**
 * @author ben_mkiv, based on MinecraftByExample Templates
 */
import java.io.File;
import java.util.HashMap;

import net.blacklab.lmr.LittleMaidReengaged;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.server.permission.PermissionAPI;


//todo: check what config values have to be sent by a server so that client methods are aware of server configuration
public class Config extends PermissionAPI {
    private static Configuration config = null;

    static HashMap<String, Property> configOptions = new HashMap<>();

    public static void preInit(){
        File configFile = new File(Loader.instance().getConfigDir(), LittleMaidReengaged.MODID + ".cfg");
        config = new Configuration(configFile);

        syncConfig(true);
    }

    public static void clientPreInit() {
        MinecraftForge.EVENT_BUS.register(new ConfigEventHandler());
    }

    public static Configuration getConfig() {
        return config;
    }

    private static void syncConfig(boolean loadConfigFromFile) {
        if (loadConfigFromFile)
            config.load();

        boolean isClient = FMLCommonHandler.instance().getEffectiveSide().isClient();

        /*
        Property enableplaySoundAt = config.get("general", "enableplaySoundAt", false);
        enableplaySoundAt.setLanguageKey("gui.config.general.enableplaySoundAt");
        enableplaySoundAt.setComment("Enable/Disable the playSoundAt feature of alarm blocks, this allows any user to play any sound at any location in a world, and is exploitable, disabled by default.");

        Property rfidMaxRange = config.get("general", "rfidMaxRange", 16);
        rfidMaxRange.setMinValue(1);
        rfidMaxRange.setMaxValue(64);
        rfidMaxRange.setLanguageKey("gui.config.general.rfidMaxRange");
        rfidMaxRange.setComment("The maximum range of the RFID Reader in blocks");
        rfidMaxRange.setRequiresMcRestart(true);
        if(isClient)
            rfidMaxRange.setConfigEntryClass(GuiConfigEntries.NumberSliderEntry.class);

        */

        Property Aggressive = config.get("general", "Aggressive", true);
        Aggressive.setLanguageKey("gui.config.general.Aggressive");

        Property antiDoppelganger = config.get("general", "antiDoppelganger", true);
        antiDoppelganger.setLanguageKey("gui.config.general.antiDoppelganger");

        Property canDespawn = config.get("general", "canDespawn", false);
        canDespawn.setLanguageKey("gui.config.general.canDespawn");
        canDespawn.setComment("Whether a LittleMaid(no-contract) can despawn.");

        Property checkOwnerName = config.get("general", "checkOwnerName", true);
        checkOwnerName.setLanguageKey("gui.config.general.checkOwnerName");
        checkOwnerName.setComment("Recommended to keep 'true'. If 'true', on SMP, each player can tame his/her own maids.");

        Property DeathMessage = config.get("general", "DeathMessage", true);
        DeathMessage.setLanguageKey("gui.config.general.DeathMessage");
        DeathMessage.setComment("Print chat message when your maid dies.");

        Property VoiceDistortion = config.get("general", "VoiceDistortion", true);
        VoiceDistortion.setLanguageKey("gui.config.general.VoiceDistortion");
        VoiceDistortion.setComment("If 'true', voices distorts like as vanila mobs.");

        Property cfg_Dominant = config.get("general", "Dominant", false);
        cfg_Dominant.setLanguageKey("gui.config.general.Dominant");

        Property enableSpawnEgg = config.get("general", "enableSpawnEgg", true);
        enableSpawnEgg.setLanguageKey("gui.config.general.enableSpawnEgg");
        enableSpawnEgg.setComment("If 'true', you can use a recipe of LittleMaid SpawnEgg.");

        Property maxGroupSize = config.get("general", "maxGroupSize", 3);
        maxGroupSize.setMinValue(0);
        maxGroupSize.setMaxValue(8);
        maxGroupSize.setLanguageKey("gui.config.general.maxGroupSize");
        maxGroupSize.setComment("This config adjusts LittleMaids spawning.");

        Property minGroupSize = config.get("general", "minGroupSize", 1);
        minGroupSize.setMinValue(0);
        minGroupSize.setMaxValue(1);
        minGroupSize.setLanguageKey("gui.config.general.minGroupSize");
        minGroupSize.setComment("This config adjusts LittleMaids spawning.");

        Property spawnLimit = config.get("general", "spawnLimit", 20);
        spawnLimit.setMinValue(0);
        spawnLimit.setMaxValue(20);
        spawnLimit.setLanguageKey("gui.config.general.spawnLimit");
        spawnLimit.setComment("This config adjusts LittleMaids spawning.");

        Property spawnWeight = config.get("general", "spawnWeight", 5);
        spawnWeight.setMinValue(0);
        spawnWeight.setMaxValue(20);
        spawnWeight.setLanguageKey("gui.config.general.spawnWeight");
        spawnWeight.setComment("This config adjusts LittleMaids spawning.");

        Property maidOverdriveDelay = config.get("general", "maidOverdriveDelay", 32);
        maidOverdriveDelay.setMinValue(1);
        maidOverdriveDelay.setMaxValue(128);
        maidOverdriveDelay.setLanguageKey("gui.config.general.maidOverdriveDelay");
        maidOverdriveDelay.setComment("This config adjusts LittleMaids spawning.");

        Property PrintDebugMessage = config.get("general", "PrintDebugMessage", false);
        PrintDebugMessage.setLanguageKey("gui.config.general.PrintDebugMessage");
        PrintDebugMessage.setComment("Output messages for debugging to log. Usually this should be 'false'.");

        Property isModelAlphaBlend = config.get("general", "isModelAlphaBlend", true);
        isModelAlphaBlend.setLanguageKey("gui.config.general.isModelAlphaBlend");
        isModelAlphaBlend.setComment("If 'false', alpha-blend of textures is disabled.");

        Property isFixedWildMaid = config.get("general", "isFixedWildMaid", false);
        isFixedWildMaid.setLanguageKey("gui.config.general.isFixedWildMaid");
        isFixedWildMaid.setComment("If 'true', additional textures of LittleMaid(no-contract) will never used.");

        Property voiceRate = config.get("general", "voiceRate", 0.2f);
        voiceRate.setMinValue(1);
        voiceRate.setMaxValue(128);
        voiceRate.setLanguageKey("gui.config.general.voiceRate");
        voiceRate.setComment("Ratio of playing non-force sound");

        if (config.hasChanged())
            config.save();
    }

    public static class ConfigEventHandler{
        @SubscribeEvent(priority = EventPriority.NORMAL)
        public void onEvent(ConfigChangedEvent.OnConfigChangedEvent event){
            if (!event.getModID().equals(LittleMaidReengaged.MODID))
                return;

            syncConfig(false);
        }
    }
}