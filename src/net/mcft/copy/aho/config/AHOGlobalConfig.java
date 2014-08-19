package net.mcft.copy.aho.config;

import java.io.File;

import net.mcft.copy.aho.client.AHOLocalization;
import net.mcft.copy.core.config.setting.EnumSetting;
import net.mcft.copy.core.config.setting.Setting;
import net.mcft.copy.core.config.setting.StringSetting;

public class AHOGlobalConfig extends AHOWorldConfig {
	
	// General
	public static final Setting generalControl =
			new EnumSetting("general.control", EnumControl.DEFAULT).setComment(
					"DEFAULT = Settings are used as default in the world creation screen.\n" +
					"LOCK    = Settings are locked in the world creation screen.\n" +
					"HIDDEN  = Settings are used automatically and world creation screen is not modified.\n" +
					"When using the DEFAULT setting, new worlds will generate with a world-specific config file.\n" +
					"Once a world has a config file, changing the global settings will have no effect on that world.");
	
	public static final Setting generalDescription =
			new StringSetting("general.description", AHOLocalization.REGEN_MODE + ".custom.desc").setComment(
					"The description shown when CUSTOM regen mode is selected.\n" +
					"Can be a string to be translated, for example '" + AHOLocalization.REGEN_MODE + ".hard.desc'.");
	
	
	public AHOGlobalConfig(File file) {
		super(file);
	}
	
}
