package net.mcft.copy.aho;

import java.lang.reflect.Field;

import net.mcft.copy.aho.config.AHOWorldConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.FoodStats;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class PlayerHealthProperties implements IExtendedEntityProperties {
	
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
		
		FoodStats stats = player.getFoodStats();
		// Reset the "food timer" to disable Vanilla natural regeneration.
		resetFoodTimer(stats);
		
		// If the heal time is set to 0, disable all regeneration.
		double regenHealTime = AdvHealthOptions.worldConfig.getDouble(AHOWorldConfig.regenHealTime);
		if (regenHealTime <= 0)
			return;
		
		// If player was hurt, increase the penalty timer.
		if (hurt) {
			float damageTaken = (healthBefore - player.getHealth());
			if (damageTaken >= 0) {
				double hurtTime = AdvHealthOptions.worldConfig.getDouble(AHOWorldConfig.hurtPenaltyTime);
				double hurtTimeMaximum = AdvHealthOptions.worldConfig.getDouble(AHOWorldConfig.hurtPenaltyTimeMaximum);
				double penaltyIncrease = Math.min(hurtTimeMaximum, Math.max(0.5, damageTaken) * hurtTime);
				
				double hurtMaximum = AdvHealthOptions.worldConfig.getDouble(AHOWorldConfig.hurtPenaltyMaximum);
				penaltyTimer = Math.min(hurtMaximum, penaltyTimer + penaltyIncrease);
			}
			hurt = false;
		}
		
		// Decrease penalty timer.
		penaltyTimer = Math.max(0, penaltyTimer - 1 / 20.0);
		
		// If player has full or no health, reset regen timer and return.
		if (!player.shouldHeal()) {
			regenTimer = 0;
			return;
		}
		
		// Check if food level in in range where it allows health regeneration.
		int regenHungerMinimum = AdvHealthOptions.worldConfig.getInteger(AHOWorldConfig.regenHungerMinimum);
		if (stats.getFoodLevel() < regenHungerMinimum) return;
		
		double penaltyFactor = 1.0;
		if (penaltyTimer > 0) {
			// Check if penalty timer is in range where it allows health regeneration.
			double hurtBuffer = AdvHealthOptions.worldConfig.getDouble(AHOWorldConfig.hurtPenaltyBuffer);
			if (penaltyTimer >= hurtBuffer) return;
			penaltyFactor = 1 - (penaltyTimer / hurtBuffer);
		}
		
		int regenHungerMaximum = AdvHealthOptions.worldConfig.getInteger(AHOWorldConfig.regenHungerMaximum);
		double foodFactor = ((stats.getFoodLevel() < regenHungerMaximum)
			? ((stats.getFoodLevel() - regenHungerMinimum + 1.0) /
			   (regenHungerMaximum - regenHungerMinimum + 1.0))
			: 1.0); // ALL the regeneration!
		
		if ((regenTimer += (penaltyFactor * foodFactor) / 20.0) > regenHealTime) {
			player.heal(1);
			double regenExhaustion = AdvHealthOptions.worldConfig.getDouble(AHOWorldConfig.regenExhaustion);
			stats.addExhaustion((float)regenExhaustion);
			regenTimer -= regenHealTime;
		}
		
	}
	
	public void hurt(EntityPlayer player) {
		hurt = true;
		healthBefore = player.getHealth();
	}
	
	
	private static Field foodTimerField = null;
	private static void resetFoodTimer(FoodStats stats) {
		if (foodTimerField == null)
			foodTimerField = ReflectionHelper.findField(FoodStats.class, "field_75123_d", "foodTimer");
		if (stats.getFoodLevel() > 0) {
			try { foodTimerField.set(stats, 0); }
			catch (Exception ex) { throw new RuntimeException(ex); }
		}
	}
	
}
