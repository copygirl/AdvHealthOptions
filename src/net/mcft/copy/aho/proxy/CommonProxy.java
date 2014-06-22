package net.mcft.copy.aho.proxy;

import net.mcft.copy.aho.AdvHealthOptions;
import net.mcft.copy.aho.PlayerHealthProperties;
import net.mcft.copy.aho.config.AHOWorldConfig;
import net.mcft.copy.core.util.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

public class CommonProxy {
	
	public void init() {
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
	}
	
	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event) {
		if (event.entity instanceof EntityPlayerMP)
			EntityUtils.createProperties(event.entity, PlayerHealthProperties.class);
	}
	
	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event) {
		if (event.entity instanceof EntityPlayerMP)
			getProperties(event.entity).update((EntityPlayer)event.entity);
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onLivingHurt(LivingHurtEvent event) {
		if (event.entity instanceof EntityPlayerMP)
			getProperties(event.entity).hurt((EntityPlayer)event.entity);
	}
	
	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		int health = AdvHealthOptions.worldConfig.getInteger(AHOWorldConfig.respawnHealth);
		int food   = AdvHealthOptions.worldConfig.getInteger(AHOWorldConfig.respawnFood);
		int shield = AdvHealthOptions.worldConfig.getInteger(AHOWorldConfig.respawnShield);
		double penalty = AdvHealthOptions.worldConfig.getDouble(AHOWorldConfig.respawnHurtPenalty);
		
		if (health < 20) event.player.setHealth(health);
		if (food < 20) event.player.getFoodStats().setFoodLevel(food);
		if (shield > 0) event.player.setAbsorptionAmount(shield);
		if (penalty > 0)
			getProperties(event.player).penaltyTimer = penalty;
	}
	
	
	private static PlayerHealthProperties getProperties(Entity entity) {
		return EntityUtils.getProperties(entity, PlayerHealthProperties.class);
	}
	
}
