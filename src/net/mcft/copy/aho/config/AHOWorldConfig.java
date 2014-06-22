package net.mcft.copy.aho.config;

import java.io.File;

import net.mcft.copy.core.config.Config;
import net.mcft.copy.core.config.setting.DoubleSetting;
import net.mcft.copy.core.config.setting.IntegerSetting;
import net.mcft.copy.core.config.setting.Setting;

public class AHOWorldConfig extends Config {
	
	// Regeneration
	public static final String regenHealTime      = "regeneration.healTime";
	public static final String regenHungerMinimum = "regeneration.hungerMinimum";
	public static final String regenHungerMaximum = "regeneration.hungerMaximum";
	public static final String regenExhaustion    = "regeneration.Exhaustion";
	
	// Hurt penalty
	public static final String hurtPenaltyTime        = "hurtPenalty.time";
	public static final String hurtPenaltyTimeMaximum = "hurtPenalty.timeMaximum";
	public static final String hurtPenaltyMaximum     = "hurtPenalty.maximum";
	public static final String hurtPenaltyBuffer      = "hurtPenalty.buffer";
	
	
	public final File file;
	
	public AHOWorldConfig(File file) {
		super(file);
		this.file = file;
		
		// Regeneration
		
		new DoubleSetting(this, regenHealTime).setValidRange(0.0, Double.MAX_VALUE)
			.setComment("Minimum time in seconds between healing half a heart. Set to 0 to disable.");
		new IntegerSetting(this, regenHungerMinimum).setValidRange(0, 20)
			.setComment("Natural regeneration starts at this hunger level. Valid values: 0 - 20.");
		new IntegerSetting(this, regenHungerMaximum).setValidRange(0, 20)
			.setComment("Natural regeneration is at its maximum at this hunger level. Valid values: 0 - 20.");
		new DoubleSetting(this, regenExhaustion).setValidRange(0.0, Double.MAX_VALUE)
			.setComment("Amount of exhaustion added when healing naturally.");
		
		
		// Hurt penalty
		addCategoryComment("hurtPenalty",
				"When taking damage, a variable amount of 'penalty time' is added.\n" +
				"During this time, health regeneration is decreased or completely inactive.");
		
		
		new DoubleSetting(this, hurtPenaltyTime).setValidRange(0.0, Double.MAX_VALUE)
			.setComment("The amount of penalty time in seconds added per point of damage (= half a heart).\n" +
			            "When no damage is taken from a hit, half of this value is added instead.");
		new DoubleSetting(this, hurtPenaltyTimeMaximum).setValidRange(0.0, Double.MAX_VALUE)
			.setComment("The maximum amount of penalty time added at once when taking damage.");
		new DoubleSetting(this, hurtPenaltyMaximum).setValidRange(0.0, Double.MAX_VALUE)
			.setComment("The maximum amount of penalty time that can be accumulated.");
		new DoubleSetting(this, hurtPenaltyBuffer).setValidRange(0.0, Double.MAX_VALUE)
			.setComment("The amount of penalty time where regeneration speed decreases linearly.\n" +
			            "When the penalty time is larger than this amount, regeneration is inactive.");
		
	}
	
	/** Loads settings from the config file or uses settings from the global config. */
	public void load(AHOWorldConfig config) {
		if (file.exists()) super.load();
		else for (Setting setting : settings.values())
			setting.setValue(config.get(setting.fullName).getValue()); 
	}
	
	/** Changes the config settings to use the preset's values. */
	public void usePreset(EnumPreset preset) {
		
		get(regenHealTime).setValue(preset.regenHealTime);
		get(regenHungerMinimum).setValue(preset.regenHungerMinimum);
		get(regenHungerMaximum).setValue(preset.regenHungerMaximum);
		get(regenExhaustion).setValue(preset.regenExhaustion);
		
		get(hurtPenaltyTime).setValue(preset.hurtTime);
		get(hurtPenaltyTimeMaximum).setValue(preset.hurtTimeMaximum);
		get(hurtPenaltyMaximum).setValue(preset.hurtMaximum);
		get(hurtPenaltyBuffer).setValue(preset.hurtBuffer);
		
	}
	
}
