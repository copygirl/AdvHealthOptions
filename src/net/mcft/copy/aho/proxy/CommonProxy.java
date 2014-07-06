package net.mcft.copy.aho.proxy;

import net.mcft.copy.aho.entity.AHOProperties;
import net.mcft.copy.core.util.EntityUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class CommonProxy {
	
	public void init() {
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
	}
	
	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event) {
		if (event.entity instanceof EntityPlayer)
			EntityUtils.createProperties(event.entity, AHOProperties.class);
	}
	
	@SubscribeEvent
	public void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
		if (event.side.isClient() || (event.phase != Phase.END) || event.player.isDead) return;
		getProperties(event.player).update();
	}
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onLivingHurt(LivingHurtEvent event) {
		if (event.entity instanceof EntityPlayerMP)
			event.ammount = (float)getProperties(event.entity).hurt((EntityPlayer)event.entity, event.ammount);
	}
	
	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if (event.player instanceof EntityPlayerMP)
			getProperties(event.player).respawn(event.player);
	}
	
	
	private static AHOProperties getProperties(Entity entity) {
		return EntityUtils.getProperties(entity, AHOProperties.class);
	}
	
}
