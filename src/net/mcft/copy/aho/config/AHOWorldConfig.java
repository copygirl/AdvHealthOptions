package net.mcft.copy.aho.config;

import java.io.File;

import net.mcft.copy.core.config.Config;
import net.mcft.copy.core.config.setting.DoubleSetting;
import net.mcft.copy.core.config.setting.EnumSetting;
import net.mcft.copy.core.config.setting.IntegerSetting;
import net.mcft.copy.core.config.setting.Setting;

public class AHOWorldConfig extends Config {
	
	// General
	public static final String generalPreset = "general.preset";
	
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
	
	// Respawn
	public static final String respawnHealth = "respawn.health";
	public static final String respawnFood   = "respawn.food";
	public static final String respawnShield = "respawn.shield";
	public static final String respawnHurtPenalty = "respawn.hurtPenalty";
	
	// Shield
	public static final String shieldMaximum      = "shield.maximum";
	public static final String shieldTimeout      = "shield.timeout";
	public static final String shieldRechargeTime = "shield.rechargeTime";
	public static final String shieldRequirement  = "shield.Requirement";
	
	// Miscellaneous
	public static final String hunger = "misc.hunger";
	
	
	public final File file;
	
	public AHOWorldConfig(File file) {
		super(file);
		this.file = file;
		
		// General
		
		new EnumSetting(this, generalPreset, EnumPreset.NORMAL)
			.setComment("Choose a preset you want to go with, or CUSTOM if you want to build your own.\n" +
			            "WARNING: If you select anything other than CUSTOM, all settings will be overwritten!\n" +
			            "Valid values are PEACEFUL, EASY, NORMAL, HARD, HARDCORE, ULTRAHARDCORE, CUSTOM.");
		
		// Regeneration
		
		new DoubleSetting(this, regenHealTime, EnumPreset.NORMAL.regenHealTime).setValidRange(0.0, Double.MAX_VALUE)
			.setComment("Minimum time in seconds between healing half a heart. Use 0 to disable.");
		new IntegerSetting(this, regenHungerMinimum, EnumPreset.NORMAL.regenHungerMinimum).setValidRange(0, 20)
			.setComment("Natural regeneration starts at this hunger level. Valid values: 0 - 20.");
		new IntegerSetting(this, regenHungerMaximum, EnumPreset.NORMAL.regenHungerMaximum).setValidRange(0, 20)
			.setComment("Natural regeneration is at its maximum at this hunger level. Valid values: 0 - 20.");
		new DoubleSetting(this, regenExhaustion, EnumPreset.NORMAL.regenExhaustion).setValidRange(0.0, Double.MAX_VALUE)
			.setComment("Exhaustion added when healing naturally (higher = more food needed).");
		
		
		// Hurt penalty
		addCategoryComment("hurtPenalty",
				"When taking damage, a variable amount of 'penalty time' is added.\n" +
				"During this time, health regeneration is decreased or completely inactive.");
		
		
		new DoubleSetting(this, hurtPenaltyTime, EnumPreset.NORMAL.hurtTime).setValidRange(0.0, Double.MAX_VALUE)
			.setComment("Penalty time in seconds added per point of damage (= half a heart).\n" +
			            "When no damage is taken from a hit, half of this value is added instead.");
		new DoubleSetting(this, hurtPenaltyTimeMaximum, EnumPreset.NORMAL.hurtTimeMaximum).setValidRange(0.0, Double.MAX_VALUE)
			.setComment("Maximum penalty time added at once when taking damage.");
		new DoubleSetting(this, hurtPenaltyMaximum, EnumPreset.NORMAL.hurtMaximum).setValidRange(0.0, Double.MAX_VALUE)
			.setComment("Maximum penalty time that can be accumulated.");
		new DoubleSetting(this, hurtPenaltyBuffer, EnumPreset.NORMAL.hurtBuffer).setValidRange(0.0, Double.MAX_VALUE)
			.setComment("Penalty time where regeneration speed decreases linearly.\n" +
			            "When the penalty time is larger than this amount, regeneration is inactive.");
		
		// Respawn
		
		new IntegerSetting(this, respawnHealth, EnumPreset.NORMAL.respawnHealth).setValidRange(1, 20)
			.setComment("Health players respawn with after death. Valid values: 1 - 20.");
		new IntegerSetting(this, respawnFood, EnumPreset.NORMAL.respawnFood).setValidRange(0, 20)
			.setComment("Food players respawn with after death. Valid values: 0 - 20.");
		new IntegerSetting(this, respawnShield, EnumPreset.NORMAL.respawnShield).setValidRange(0, 40)
			.setComment("Absorption points players respawn with after death.\n" +
			            "The shield remains until it's used up. Valid values: 0 - 40.");
		new DoubleSetting(this, respawnHurtPenalty, EnumPreset.NORMAL.respawnPenalty).setValidRange(0, Double.MAX_VALUE)
			.setComment("Penalty time players respawn with after death.\n" +
			            "Can be larger than the maximum hurt penalty time.");
		
		// Shield
		
		new IntegerSetting(this, shieldMaximum).setValidRange(0, 40)
			.setComment("Maximum absorption points that can be recharged. Valid values: 0 - 40.");
		new DoubleSetting(this, shieldTimeout).setValidRange(0, Double.MAX_VALUE)
			.setComment("Time for which the shield doesn't recharge after being hit.");
		new DoubleSetting(this, shieldRechargeTime).setValidRange(0, Double.MAX_VALUE)
			.setComment("Time it takes to recharge one absorption point.");
		new EnumSetting(this, shieldRequirement, EnumShieldReq.NONE)
			.setComment("NONE = Both shield and health can regenerate at the same time.\n" +
			            "SHIELD_REQ_HEALTH = Full health is required for the shield to recharge.\n" +
			            "HEALTH_REQ_SHIELD = Health regeneration is paused while shield is not full.");
		
		// Miscellaneous
		
		new EnumSetting(this, hunger, EnumHunger.ENABLE).setSynced()
			.setComment("ENABLE  = Hunger functions like normal and affects regeneration speed.\n" +
			            "DISABLE = Hunger is completely disabled, food can be eaten but is ignored.\n" +
			            "HEALTH  = Hunger is disabled, eating food directly translates to health.\n" +
			            "When hunger is disabled the food meter will internally be locked at 8, enough to sprint.\n" +
			            "Changes in the food meter (for example from eating food) will directly affect health.");
		
	}
	
	@Override
	public void load() {
		super.load();
		// If the preset setting is set to anything other
		// than CUSTOM, change all settings to the preset's.
		EnumPreset preset = getEnum(AHOWorldConfig.generalPreset);
		if (preset != EnumPreset.CUSTOM)
			usePreset(preset);
	}
	
	/** Loads settings from the config file or uses settings from the global config. */
	public void load(AHOGlobalConfig config) {
		if (file.exists()) this.load();
		else for (Setting setting : settings.values())
			setting.setValue(config.get(setting.fullName).getValue()); 
	}
	
	/** Changes the config settings to use the preset's values. */
	public void usePreset(EnumPreset preset) {
		
		get(generalPreset).setValue(preset);
		
		get(regenHealTime).setValue(preset.regenHealTime);
		get(regenHungerMinimum).setValue(preset.regenHungerMinimum);
		get(regenHungerMaximum).setValue(preset.regenHungerMaximum);
		get(regenExhaustion).setValue(preset.regenExhaustion);
		
		get(hurtPenaltyTime).setValue(preset.hurtTime);
		get(hurtPenaltyTimeMaximum).setValue(preset.hurtTimeMaximum);
		get(hurtPenaltyMaximum).setValue(preset.hurtMaximum);
		get(hurtPenaltyBuffer).setValue(preset.hurtBuffer);
		
		get(respawnHealth).setValue(preset.respawnHealth);
		get(respawnFood).setValue(preset.respawnFood);
		get(respawnHurtPenalty).setValue(preset.respawnPenalty);
		get(respawnShield).setValue(preset.respawnShield);
		
	}
	
}
