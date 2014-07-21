package net.mcft.copy.aho.entity;

import java.lang.reflect.Field;

import net.mcft.copy.aho.AdvHealthOptions;
import net.mcft.copy.aho.config.AHOWorldConfig;
import net.mcft.copy.aho.config.EnumHunger;
import net.mcft.copy.aho.config.EnumShieldMode;
import net.mcft.copy.aho.config.EnumShieldModifier;
import net.mcft.copy.aho.config.EnumShieldReq;
import net.mcft.copy.core.entity.EntityPropertiesBase;
import net.mcft.copy.core.entity.EntityProperty;
import net.mcft.copy.core.entity.EntityPropertyPrimitive;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class AHOProperties extends EntityPropertiesBase {
	
	/** If enabled, prints debug messages about all players' regeneration progress. */
	public static final boolean DEBUG = false;
	
	public static final String IDENTIFIER = AdvHealthOptions.MOD_ID;
	
	
	public final EntityProperty<Double> shieldAmount;
	public final EntityProperty<Double> regenTimer;
	public final EntityProperty<Double> penaltyTimer;
	public final EntityProperty<Double> shieldTimer;
	
	private boolean hurt = false;
	private float healthBefore = 0;
	private int previousShieldMaximum = 0;
	
	public AHOProperties() {
		add(shieldAmount = new EntityPropertyPrimitive<Double>("shieldAmount", 0.0).setSaved().setSynced(false));
		add(regenTimer = new EntityPropertyPrimitive<Double>("regenTimer", 0.0).setSaved());
		add(penaltyTimer = new EntityPropertyPrimitive<Double>("penaltyTimer", 0.0).setSaved());
		add(shieldTimer = new EntityPropertyPrimitive<Double>("shieldTimer", 0.0).setSaved());
	}
	
	// EntityPropertiesBase methods
	
	@Override
	public boolean isSynced() { return true; }
	
	@Override
	public void update() {
		super.update();
		
		EntityPlayer player = (EntityPlayer)getEntity();
		boolean wasHurt = hurt;
		hurt = false;
		
		FoodStats foodStats = player.getFoodStats();
		boolean hungerEnabled = handleHunger(player, foodStats);
		boolean shieldPreventHeal = handleShield(player, wasHurt);
		resetFoodTimer(foodStats);
		
		// If the heal time is set to 0, disable all regeneration.
		double regenHealTime = AdvHealthOptions.config.<Double>get(AHOWorldConfig.regenHealTime);
		if ((regenHealTime <= 0) && !DEBUG) return;
		
		if (wasHurt) increasePenaltyTimer(player);
		// Decrease penalty timer.
		penaltyTimer.set(Math.max(0, penaltyTimer.get() - 1 / 20.0));
		
		// If player has full or no health, reset regeneration timer and return.
		if (!player.shouldHeal()) {
			regenTimer.set(0.0);
			if (!DEBUG) return;
		}
		
		double foodFactor = (hungerEnabled ? calculateFoodFactor(foodStats.getFoodLevel()) : 1.0);
		// If player has the hunger potion effect (food poisoning?), apply hunger poison factor.
		if (player.getActivePotionEffect(Potion.hunger) != null)
			foodFactor *= AdvHealthOptions.config.<Double>get(AHOWorldConfig.regenHungerPoisonFactor);
		double penaltyFactor = calculatePenaltyFactor(penaltyTimer.get());
		
		if (!shieldPreventHeal && (regenHealTime > 0) &&
		    ((regenTimer.set(regenTimer.get() + (penaltyFactor * foodFactor) / 20.0)) > regenHealTime)) {
			player.heal(1);
			double regenExhaustion = AdvHealthOptions.config.<Double>get(AHOWorldConfig.regenExhaustion);
			foodStats.addExhaustion((float)regenExhaustion);
			regenTimer.set(regenTimer.get() - regenHealTime);
		}
		
		if (DEBUG)
			System.out.println(String.format("%s: Regen=%d/%d+%.2f Penalty=%d/%d/%d+%.2f Shield=%d/%d",
					player.getCommandSenderName(), regenTimer.get().intValue(), (int)regenHealTime, foodFactor,
					penaltyTimer.get().intValue(), AdvHealthOptions.config.<Double>get(AHOWorldConfig.hurtPenaltyBuffer).intValue(),
					AdvHealthOptions.config.<Double>get(AHOWorldConfig.hurtPenaltyMaximum).intValue(), penaltyFactor,
					shieldTimer.get().intValue(), AdvHealthOptions.config.<Double>get(AHOWorldConfig.shieldRechargeTime).intValue()));
		
	}
	
	// Utility methods
	
	/** Called when the player is hurt. */
	public double hurt(EntityPlayer player, double damage) {
		if (!hurt)
			healthBefore = player.getHealth();
		hurt = true;
		// Apply SUBTRACTION mode shielding.
		double reduction = Math.min(damage, shieldAmount.get());
		shieldAmount.set(shieldAmount.get() - reduction);
		return (damage - reduction);
	}
	
	/** Called when the player respawns. */
	public void respawn(EntityPlayer player) {
		int health = AdvHealthOptions.config.<Integer>get(AHOWorldConfig.respawnHealth);
		int food   = AdvHealthOptions.config.<Integer>get(AHOWorldConfig.respawnFood);
		int shield = AdvHealthOptions.config.<Integer>get(AHOWorldConfig.respawnShield);
		double penalty = AdvHealthOptions.config.<Double>get(AHOWorldConfig.respawnHurtPenalty);
		
		if (health < 20) player.setHealth(health);
		if (food < 20) setFoodLevel(player, food);
		if (shield > 0) {
			EnumShieldMode mode = AdvHealthOptions.config.<EnumShieldMode>get(AHOWorldConfig.shieldMode);
			setShieldAmount(player, mode, shield);
		}
		if (penalty > 0) penaltyTimer.set(penalty);
	}
	
	private boolean foodLevelSet = false;
	/** Handles the hunger setting, returns if hunger is enabled. */
	private boolean handleHunger(EntityPlayer player, FoodStats foodStats) {
		EnumHunger hunger = AdvHealthOptions.config.<EnumHunger>get(AHOWorldConfig.miscHunger);
		if (hunger == EnumHunger.ENABLE) return true;
		
		// Make direct changes to the food meter affect health directly.
		if ((hunger == EnumHunger.HEALTH) && foodLevelSet) {
			int change = (foodStats.getFoodLevel() - 8);
			if (change > 0) player.heal(change);
			else if (change < 0) player.attackEntityFrom(DamageSource.starve, -change);
		}
		
		// Reset the food level and saturation.
		setFoodLevel(player, 0);
		foodStats.addStats(8, 20.0F);
		foodLevelSet = true;
		
		return false;
	}
	
	private static Field foodLevelField = null;
	private void setFoodLevel(EntityPlayer player, int level) {
		if (foodLevelField == null)
			foodLevelField = ReflectionHelper.findField(FoodStats.class, "field_75127_a", "foodLevel");
		try { foodLevelField.set(player.getFoodStats(), level); }
		catch (Exception ex) { throw new RuntimeException(ex); }
	}
	
	/** Handles the shield settings, returns if healing is paused. */
	private boolean handleShield(EntityPlayer player, boolean wasHurt) {
		EnumShieldMode mode = AdvHealthOptions.config.<EnumShieldMode>get(AHOWorldConfig.shieldMode);
		double shieldAmount = getShieldAmount(player, mode);
		int armorPoints = player.getTotalArmorValue();
		
		int maximum = AdvHealthOptions.config.<Integer>get(AHOWorldConfig.shieldMaximum);
		EnumShieldModifier modifier = AdvHealthOptions.config.<EnumShieldModifier>get(AHOWorldConfig.shieldModifier);
		if (modifier == EnumShieldModifier.ARMOR) {
			maximum = (maximum * armorPoints) / 20;
			// If total armor points got decreased, remove some of the shielding.
			if ((maximum < previousShieldMaximum) && (shieldAmount > maximum))
				shieldAmount = setShieldAmount(player, mode, Math.max(maximum, shieldAmount - (previousShieldMaximum - maximum)));
		} else if (modifier == EnumShieldModifier.HEALTH)
			maximum = (maximum * (int)(player.getHealth() + 0.5)) / (int)player.getMaxHealth();
		
		EnumShieldReq req = AdvHealthOptions.config.<EnumShieldReq>get(AHOWorldConfig.shieldRequirement);
		boolean atMaximum = (shieldAmount >= maximum);
		
		if (!wasHurt && !atMaximum && !((req == EnumShieldReq.SHIELD_REQ_HEALTH) && player.shouldHeal())) {
			double shieldRechargeTime = AdvHealthOptions.config.<Double>get(AHOWorldConfig.shieldRechargeTime);
			if ((shieldTimer.set(shieldTimer.get() + 1 / 20.0)) >= shieldRechargeTime) {
				setShieldAmount(player, mode, Math.min(maximum, shieldAmount + 1));
				shieldTimer.set(shieldTimer.get() - shieldRechargeTime);
			}
		} else shieldTimer.set((wasHurt ? -AdvHealthOptions.config.<Double>get(AHOWorldConfig.shieldTimeout) : 0.0));
		previousShieldMaximum = maximum;
		return ((req == EnumShieldReq.HEALTH_REQ_SHIELD) && !atMaximum);
	}
	
	private double getShieldAmount(EntityPlayer player, EnumShieldMode mode) {
		return ((mode == EnumShieldMode.ABSORPTION) ? player.getAbsorptionAmount() : shieldAmount.get());
	}
	private double setShieldAmount(EntityPlayer player, EnumShieldMode mode, double amount) {
		if (mode == EnumShieldMode.ABSORPTION)
			player.setAbsorptionAmount((float)amount);
		else shieldAmount.set(amount);
		return amount;
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
		float damageTaken = (healthBefore - player.getHealth());
		
		double hurtTime = AdvHealthOptions.config.<Double>get(AHOWorldConfig.hurtPenaltyTime);
		double hurtTimeMaximum = AdvHealthOptions.config.<Double>get(AHOWorldConfig.hurtPenaltyTimeMaximum);
		double penaltyIncrease = Math.min(hurtTimeMaximum, Math.max(0.5, damageTaken) * hurtTime);
		
		double hurtMaximum = AdvHealthOptions.config.<Double>get(AHOWorldConfig.hurtPenaltyMaximum);
		if (penaltyTimer.get() < hurtMaximum)
			penaltyTimer.set(Math.min(hurtMaximum, penaltyTimer.get() + penaltyIncrease));
	}
	 
	private double calculateFoodFactor(int foodLevel) {
		int regenHungerMinimum = AdvHealthOptions.config.<Integer>get(AHOWorldConfig.regenHungerMinimum);
		if (foodLevel < regenHungerMinimum) return 0.0;
		
		int regenHungerMaximum = AdvHealthOptions.config.<Integer>get(AHOWorldConfig.regenHungerMaximum);
		if (foodLevel >= regenHungerMaximum) return 1.0;
		
		return ((foodLevel - regenHungerMinimum + 1.0) /
		        (regenHungerMaximum - regenHungerMinimum + 1.0));
	}
	
	private double calculatePenaltyFactor(double penaltyTimer) {
		if (penaltyTimer <= 0) return 1.0;
		double hurtBuffer = AdvHealthOptions.config.<Double>get(AHOWorldConfig.hurtPenaltyBuffer);
		return ((penaltyTimer < hurtBuffer) ? (1 - (penaltyTimer / hurtBuffer)) : 0.0);
	}
	
}
