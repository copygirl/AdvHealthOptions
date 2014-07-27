package net.mcft.copy.aho;

import java.io.File;

import net.mcft.copy.aho.client.gui.AHOGuiCreateWorld;
import net.mcft.copy.aho.config.AHOGlobalConfig;
import net.mcft.copy.aho.config.AHOWorldConfig;
import net.mcft.copy.aho.config.EnumPreset;
import net.mcft.copy.aho.proxy.CommonProxy;
import net.mcft.copy.core.config.PriorityConfig;
import net.mcft.copy.core.config.PriorityConfig.Priority;
import net.mcft.copy.core.config.SyncedConfig;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;

@Mod(modid = AdvHealthOptions.MOD_ID, version = "@VERSION@",
     useMetadata = false, dependencies = "required-after:copycore",
     guiFactory = "net.mcft.copy.aho.client.gui.AHOGuiFactory")
public class AdvHealthOptions {
	
	public static final String MOD_ID = "AdvHealthOptions";
	public static final String MOD_NAME = "Advanced Health Options";
	
	@SidedProxy(clientSide = "net.mcft.copy.aho.proxy.ClientProxy",
	            serverSide = "net.mcft.copy.aho.proxy.CommonProxy")
	private static CommonProxy proxy;
	
	public static PriorityConfig config = new PriorityConfig();
	
	public static AHOGlobalConfig globalConfig;
	public static AHOWorldConfig worldConfig;
	public static SyncedConfig syncedConfig;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		
		globalConfig = new AHOGlobalConfig(event.getSuggestedConfigurationFile());
		globalConfig.load();
		proxy.init();
		globalConfig.update();
		globalConfig.save();
		
		syncedConfig = new SyncedConfig(MOD_ID);
		syncedConfig.addFromReflection(config, globalConfig);
		
		config.add(Priority.GLOBAL, globalConfig);
		config.add(Priority.SYNCED, syncedConfig);
		
	}
	
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		if (event.getServer().isDedicatedServer()) return;
		
		File worldConfigDir = new File(DimensionManager.getCurrentSaveRootDirectory(), "config");
		File worldConfigFile = new File(worldConfigDir, MOD_ID + ".cfg");
		
		worldConfig = new AHOWorldConfig(worldConfigFile);
		worldConfig.load(globalConfig);
		
		// If the world was just created using the	
		// create world screen, use the selected preset.
		EnumPreset preset = AHOGuiCreateWorld.getAndResetPreset();
		if (preset != EnumPreset.CUSTOM) {
			worldConfig.usePreset(preset);
			worldConfig.save();
		// Save the world config file if it's already created, in case the
		// preset setting was changed or the mod updated with new settings.
		} else if (worldConfigFile.exists())
			worldConfig.save();
		
		if (worldConfig.exists())
			config.add(Priority.WORLD, worldConfig);
	}
	
	@EventHandler
	public void serverStopping(FMLServerStoppedEvent event) {
		if (worldConfig == null) return;
		config.remove(worldConfig);
		worldConfig = null;
	}
	
}
