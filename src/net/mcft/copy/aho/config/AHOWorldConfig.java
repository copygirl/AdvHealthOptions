package net.mcft.copy.aho.config;

import java.io.File;

import net.mcft.copy.core.config.Config;
import net.mcft.copy.core.config.SyncedSetting;
import net.mcft.copy.core.config.setting.DoubleSetting;
import net.mcft.copy.core.config.setting.EnumSetting;
import net.mcft.copy.core.config.setting.IntegerSetting;
import net.mcft.copy.core.config.setting.Setting;

public class AHOWorldConfig extends Config {
	
	// General
	public static final Setting generalPreset =
			new EnumSetting("general.preset", EnumPreset.NORMAL).setComment(
					"Choose a preset you want to go with, or CUSTOM if you want to build your own.\n" +
					"WARNING: If you select anything other than CUSTOM, all settings will be overwritten!\n" +
					"Valid values are PEACEFUL, EASY, NORMAL, HARD, HARDCORE, ULTRAHARDCORE, CUSTOM.");
	
	// Regeneration
	public static final Setting regenHealTime =
			new DoubleSetting("regeneration.healTime", EnumPreset.NORMAL.regenHealTime).setValidRange(0.0, Double.MAX_VALUE).setComment(
					"Minimum time in seconds between healing half a heart. Use 0 to disable.");
	
	public static final Setting regenHungerMinimum =
			new IntegerSetting("regeneration.hungerMinimum", EnumPreset.NORMAL.regenHungerMinimum).setValidRange(0, 20).setComment(
					"Natural regeneration starts at this hunger level. Valid values: 0 - 20.");
	
	public static final Setting regenHungerMaximum =
		new IntegerSetting("regeneration.hungerMaximum", EnumPreset.NORMAL.regenHungerMaximum).setValidRange(0, 20).setComment(
				"Natural regeneration is at its maximum at this hunger level. Valid values: 0 - 20.");
	
	public static final Setting regenExhaustion =
			new DoubleSetting("regeneration.exhaustion", EnumPreset.NORMAL.regenExhaustion).setValidRange(0.0, Double.MAX_VALUE).setComment(
					"Exhaustion added when healing naturally (higher = more food needed).");
	
	public static final Setting regenHungerPoisonFactor =
			new DoubleSetting("regeneration.hungerPoisonFactor", EnumPreset.NORMAL.regenHungerFactor).setValidRange(0.0, 1.0).setComment(
					"Regeneration speed is multiplied by this if food poisoned (for example from rotten flesh).");
	
	// Hurt penalty
	public static final Setting hurtPenaltyTime =
			new DoubleSetting("hurtPenalty.time", EnumPreset.NORMAL.hurtTime).setValidRange(0.0, Double.MAX_VALUE).setComment(
					"Penalty time in seconds added per point of damage (= half a heart).\n" +
					"When no damage is taken from a hit, half of this value is added instead.");
	
	public static final Setting hurtPenaltyTimeMaximum =
			new DoubleSetting("hurtPenalty.timeMaximum", EnumPreset.NORMAL.hurtTimeMaximum).setValidRange(0.0, Double.MAX_VALUE).setComment(
					"Maximum penalty time added at once when taking damage.");
	
	public static final Setting hurtPenaltyMaximum =
			new DoubleSetting("hurtPenalty.maximum", EnumPreset.NORMAL.hurtMaximum).setValidRange(0.0, Double.MAX_VALUE).setComment(
					"Maximum penalty time that can be accumulated.");
	
	public static final Setting hurtPenaltyBuffer =
			new DoubleSetting("hurtPenalty.buffer", EnumPreset.NORMAL.hurtBuffer).setValidRange(0.0, Double.MAX_VALUE).setComment(
					"Penalty time where regeneration speed decreases linearly.\n" +
					"When the penalty time is larger than this amount, regeneration is inactive.");
	
	// Respawn
	public static final Setting respawnHealth =
			new IntegerSetting("respawn.health", EnumPreset.NORMAL.respawnHealth).setValidRange(1, 20).setComment(
					"Health players respawn with after death. Valid values: 1 - 20.");
	
	public static final Setting respawnFood =
			new IntegerSetting("respawn.food", EnumPreset.NORMAL.respawnFood).setValidRange(0, 20).setComment(
					"Food players respawn with after death. Valid values: 0 - 20.");
	
	public static final Setting respawnShield =
			new IntegerSetting("respawn.shield", EnumPreset.NORMAL.respawnShield).setValidRange(0, 40).setComment(
			"Shield points players respawn with after death.\n" +
			"The shield remains until it's used up. Valid values: 0 - 40.");
	
	public static final Setting respawnHurtPenalty =
			new DoubleSetting("respawn.hurtPenalty", EnumPreset.NORMAL.respawnPenalty).setValidRange(0, Double.MAX_VALUE).setComment(
					"Penalty time players respawn with after death.\n" +
					"Can be larger than the maximum hurt penalty time.");
	
	// Shield
	public static final Setting shieldMode =
			new EnumSetting("shield.mode", EnumShieldMode.SUBTRACTION).setComment(
					"SUBTRACTION = Decreases damage taken before armor calculation.\n" +
					"ABSORPTION  = Uses vanilla absorption hearts, applied after armor.");
	
	@SyncedSetting
	public static final Setting shieldMaximum =
			new IntegerSetting("shield.maximum").setValidRange(0, 40).setComment(
					"Maximum shield points that can be recharged. Use 0 to disable. Valid values: 0 - 40.");
	
	@SyncedSetting
	public static final Setting shieldModifier =
			new EnumSetting("shield.modifier", EnumShieldModifier.NONE).setComment(
					"NONE   = Maximum shield is not affected by anything.\n" +
					"ARMOR  = Maximum shield is affected by armor points.\n" +
					"HEALTH = Maximum shield is affected by current health.");
	
	public static final Setting shieldTimeout =
			new DoubleSetting("shield.timeout").setValidRange(0, Double.MAX_VALUE).setComment(
					"Time for which the shield doesn't recharge after being hit.");
	
	public static final Setting shieldRechargeTime =
			new DoubleSetting("shield.rechargeTime").setValidRange(0, Double.MAX_VALUE).setComment(
					"Time it takes to recharge one shield point.");
	
	public static final Setting shieldRequirement =
			new EnumSetting("shield.requirement", EnumShieldReq.NONE).setComment(
					"NONE = Both shield and health can regenerate at the same time.\n" +
					"SHIELD_REQ_HEALTH = Full health is required for the shield to recharge.\n" +
					"HEALTH_REQ_SHIELD = Health regeneration is paused while shield is not full.");
	
	// Miscellaneous
	@SyncedSetting
	public static final Setting hunger =
			new EnumSetting("misc.hunger", EnumHunger.ENABLE).setComment(
					"ENABLE  = Hunger functions like normal and affects regeneration speed.\n" +
					"DISABLE = Hunger is completely disabled, food can be eaten but is ignored.\n" +
					"HEALTH  = Hunger is disabled, eating food directly translates to health.\n" +
					"When hunger is disabled the food meter will internally be locked at 8, enough to sprint.");
	
	
	public AHOWorldConfig(File file) {
		super(file);
		addAllViaReflection();
		addCategoryComment("hurtPenalty",
				"When taking damage, a variable amount of 'penalty time' is added.\n" +
				"During this time, health regeneration is decreased or completely inactive.");
	}
	
	@Override
	public void load() {
		super.load();
		// If the preset setting is set to anything other
		// than CUSTOM, change all settings to the preset's.
		EnumPreset preset = this.<EnumPreset>get(AHOWorldConfig.generalPreset);
		if (preset != EnumPreset.CUSTOM) usePreset(preset);
	}
	
	/** Loads settings from the config file or uses settings from the global config. */
	public void load(AHOGlobalConfig config) {
		if (exists()) this.load();
		else for (Setting setting : getSettings())
			set(setting, config.get(setting)); 
	}
	
	/** Changes the config settings to use the preset's values. */
	public void usePreset(EnumPreset preset) {
		
		set(generalPreset, preset);
		
		set(regenHealTime, preset.regenHealTime);
		set(regenHungerMinimum, preset.regenHungerMinimum);
		set(regenHungerMaximum, preset.regenHungerMaximum);
		set(regenExhaustion, preset.regenExhaustion);
		
		set(hurtPenaltyTime, preset.hurtTime);
		set(hurtPenaltyTimeMaximum, preset.hurtTimeMaximum);
		set(hurtPenaltyMaximum, preset.hurtMaximum);
		set(hurtPenaltyBuffer, preset.hurtBuffer);
		
		set(respawnHealth, preset.respawnHealth);
		set(respawnFood, preset.respawnFood);
		set(respawnHurtPenalty, preset.respawnPenalty);
		set(respawnShield, preset.respawnShield);
		
	}
	
}
