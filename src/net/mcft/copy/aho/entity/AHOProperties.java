package net.mcft.copy.aho.entity;

import java.lang.reflect.Field;

import net.mcft.copy.aho.AdvHealthOptions;
import net.mcft.copy.aho.config.AHOWorldConfig;
import net.mcft.copy.aho.config.EnumHunger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class AHOProperties implements IExtendedEntityProperties {
	
	/** If enabled, prints debug messages about all players' regeneration progress. */
	public static final boolean DEBUG = false;
	
	
	public static final String IDENTIFIER = AdvHealthOptions.MOD_ID;
	
	public static final String TAG_REGEN_TIMER = "regenTimer";
	public static final String TAG_PENALTY_TIMER = "penaltyTimer";
	
	
	public double regenTimer = 0;
	public double penaltyTimer = 0;
	
	public boolean hurt = false;
	public float healthBefore = 0;
	
	@Override
	public void init(Entity entity, World world) {  }
	
	@Override
	public void saveNBTData(NBTTagCompound compound) {
		compound.setDouble(TAG_REGEN_TIMER, regenTimer);
		compound.setDouble(TAG_PENALTY_TIMER, penaltyTimer);
	}
	
	@Override
	public void loadNBTData(NBTTagCompound compound) {
		regenTimer = compound.getDouble(TAG_REGEN_TIMER);
		penaltyTimer = compound.getDouble(TAG_PENALTY_TIMER);
	}
	
	public void update(EntityPlayer player) {
		
		FoodStats foodStats = player.getFoodStats();
		boolean hungerEnabled = handleHunger(player, foodStats);
		resetFoodTimer(foodStats);
		
		// If the heal time is set to 0, disable all regeneration.
		double regenHealTime = AdvHealthOptions.worldConfig.getDouble(AHOWorldConfig.regenHealTime);
		if (regenHealTime <= 0)
			return;
		
		increasePenaltyTimer(player);
		// Decrease penalty timer.
		penaltyTimer = Math.max(0, penaltyTimer - 1 / 20.0);
		
		// If player has full or no health, reset regeneration timer and return.
		if (!player.shouldHeal()) {
			regenTimer = 0;
			if (!DEBUG) return;
		}
		
		double foodFactor = (hungerEnabled ? calculateFoodFactor(foodStats.getFoodLevel()) : 1.0);
		double penaltyFactor = calculatePenaltyFactor(penaltyTimer);
		
		if ((regenTimer += (penaltyFactor * foodFactor) / 20.0) > regenHealTime) {
			player.heal(1);
			double regenExhaustion = AdvHealthOptions.worldConfig.getDouble(AHOWorldConfig.regenExhaustion);
			foodStats.addExhaustion((float)regenExhaustion);
			regenTimer -= regenHealTime;
		}
		
		if (DEBUG)
			System.out.println(String.format("%s: Regen=%d/%d+%.2f Penalty=%d/%d/%d+%.2f",
					player.getCommandSenderName(), (int)regenTimer, (int)regenHealTime, foodFactor,
					(int)penaltyTimer, (int)AdvHealthOptions.worldConfig.getDouble(AHOWorldConfig.hurtPenaltyBuffer),
					(int)AdvHealthOptions.worldConfig.getDouble(AHOWorldConfig.hurtPenaltyMaximum), penaltyFactor));
		
	}
	
	public void hurt(EntityPlayer player) {
		if (hurt) return;
		hurt = true;
		healthBefore = player.getHealth();
	}
	
	boolean foodLevelSet = false;
	/** Handles the hunger settings DISABLE and HEALTH, returns if hunger is enabled. */
	private boolean handleHunger(EntityPlayer player, FoodStats foodStats) {
		EnumHunger hunger = AdvHealthOptions.worldConfig.getEnum(AHOWorldConfig.hunger);
		if (hunger == EnumHunger.ENABLE) return true;
		
		// Make direct changes to the food meter affect health directly.
		if ((hunger == EnumHunger.HEALTH) && foodLevelSet) {
			int change = (foodStats.getFoodLevel() - 8);
			if (change > 0) player.heal(change);
			else if (change < 0) player.attackEntityFrom(DamageSource.starve, -change);
		}
		
		// Reset the food level and saturation.
		foodStats.setFoodLevel(8);
		foodStats.addStats(0, 20.0F);
		foodLevelSet = true;
		
		return false;
	}
	
	private static Field foodTimerField = null;
	/** Reset the "food timer" to disable Vanilla natural regeneration. */
	private void resetFoodTimer(FoodStats foodStats) {
		if (foodTimerField == null)
			foodTimerField = ReflectionHelper.findField(FoodStats.class, "field_75123_d", "foodTimer");
		if (foodStats.getFoodLevel() > 0) {
			try { foodTimerField.set(foodStats, 0); }
			catch (Exception ex) { throw new RuntimeException(ex); }
		}
	}
	
	/** Increases the "penalty timer" when player was hurt. */
	private void increasePenaltyTimer(EntityPlayer player) {
		if (!hurt) return;
		hurt = false;
		
		float damageTaken = (healthBefore - player.getHealth());
		if (damageTaken < 0) return;
		
		double hurtTime = AdvHealthOptions.worldConfig.getDouble(AHOWorldConfig.hurtPenaltyTime);
		double hurtTimeMaximum = AdvHealthOptions.worldConfig.getDouble(AHOWorldConfig.hurtPenaltyTimeMaximum);
		double penaltyIncrease = Math.min(hurtTimeMaximum, Math.max(0.5, damageTaken) * hurtTime);
		
		double hurtMaximum = AdvHealthOptions.worldConfig.getDouble(AHOWorldConfig.hurtPenaltyMaximum);
		if (penaltyTimer < hurtMaximum)
			penaltyTimer = Math.min(hurtMaximum, penaltyTimer + penaltyIncrease);
	}
	 
	private double calculateFoodFactor(int foodLevel) {
		int regenHungerMinimum = AdvHealthOptions.worldConfig.getInteger(AHOWorldConfig.regenHungerMinimum);
		if (foodLevel < regenHungerMinimum) return 0.0;
		
		int regenHungerMaximum = AdvHealthOptions.worldConfig.getInteger(AHOWorldConfig.regenHungerMaximum);
		if (foodLevel >= regenHungerMaximum) return 1.0;
		
		return ((foodLevel - regenHungerMinimum + 1.0) /
		        (regenHungerMaximum - regenHungerMinimum + 1.0));
	}
	
	private double calculatePenaltyFactor(double penaltyTimer) {
		if (penaltyTimer <= 0) return 1.0;
		double hurtBuffer = AdvHealthOptions.worldConfig.getDouble(AHOWorldConfig.hurtPenaltyBuffer);
		return ((penaltyTimer < hurtBuffer) ? (1 - (penaltyTimer / hurtBuffer)) : 0.0);
	}
	
}
