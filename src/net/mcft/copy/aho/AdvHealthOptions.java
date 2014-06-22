package net.mcft.copy.aho;

import java.io.File;

import net.mcft.copy.aho.client.GuiCreateWorldCustom;
import net.mcft.copy.aho.config.AHOGlobalConfig;
import net.mcft.copy.aho.config.AHOWorldConfig;
import net.mcft.copy.aho.config.EnumPreset;
import net.mcft.copy.aho.proxy.CommonProxy;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid = AdvHealthOptions.MOD_ID, version = "${version}",
     dependencies = "required-after:copycore")
public class AdvHealthOptions {
	
	public static final String MOD_ID = "AdvHealthOptions";
	
	@SidedProxy(clientSide = "net.mcft.copy.aho.proxy.ClientProxy",
	            serverSide = "net.mcft.copy.aho.proxy.CommonProxy")
	private static CommonProxy proxy;
	
	public static AHOGlobalConfig globalConfig;
	public static AHOWorldConfig worldConfig;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		
		globalConfig = new AHOGlobalConfig(event.getSuggestedConfigurationFile());
		globalConfig.load();
		
		// If the preset setting is set to anything other
		// than CUSTOM, change all settings to the preset's.
		EnumPreset preset = globalConfig.getEnum(AHOGlobalConfig.preset);
		if (preset != EnumPreset.CUSTOM)
			globalConfig.usePreset(preset);
		
		proxy.init();
		
		globalConfig.save();
		
	}
	
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		
		File worldConfigDir = new File(DimensionManager.getCurrentSaveRootDirectory(), "config");
		File worldConfigFile = new File(worldConfigDir, MOD_ID + ".cfg");
		
		worldConfig = new AHOWorldConfig(worldConfigFile);
		worldConfig.load(globalConfig);
		
		// If the world was just created using the
		// create world screen, use the selected preset.
		EnumPreset preset = GuiCreateWorldCustom.getAndResetPreset();
		if (preset != EnumPreset.CUSTOM) {
			worldConfig.usePreset(preset);
			worldConfig.save();
		}
		
	}
	
}
