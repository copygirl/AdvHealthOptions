package net.mcft.copy.aho.config;

import java.io.File;

import net.mcft.copy.aho.client.AHOLocalization;
import net.mcft.copy.core.config.setting.EnumSetting;
import net.mcft.copy.core.config.setting.StringSetting;

public class AHOGlobalConfig extends AHOWorldConfig {
	
	// General
	public static final String preset      = "general.preset";
	public static final String control     = "general.control";
	public static final String description = "general.description";
	
	
	public AHOGlobalConfig(File file) {
		super(file);
		
		// General
		
		new EnumSetting(this, preset, EnumPreset.NORMAL)
			.setComment("Choose a preset you want to go with, or CUSTOM if you want to build your own.\n" +
			            "To see the settings for a specific preset, select it and start the game once.\n" +
			            "WARNING: If you select anything other than CUSTOM, all settings will be overwritten!\n" +
			            "Valid values are PEACEFUL, EASY, NORMAL, HARD, HARDCORE, ULTRAHARDCORE, CUSTOM.");
		new EnumSetting(this, control, EnumControl.DEFAULT)
			.setComment("DEFAULT = Settings are used as default in the world creation screen.\n" +
			            "LOCK    = Settings are locked in the world creation screen.\n" +
			            "HIDDEN  = Settings are used automatically and world creation screen is not modified.\n" +
			            "When using the DEFAULT setting, new worlds will generate with a world-specific config file.\n" +
			            "Once a world has a config file, changing the global settings will have no effect on that world.");
		new StringSetting(this, description, AHOLocalization.REGEN_MODE + ".custom.desc")
			.setComment("The description shown when CUSTOM regen mode is selected.\n" +
			            "Can be a string to be translated, for example '" + AHOLocalization.REGEN_MODE + ".hard.desc'.");
		
	}
	
}
