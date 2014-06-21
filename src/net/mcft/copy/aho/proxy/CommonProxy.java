package net.mcft.copy.aho.proxy;

import net.mcft.copy.aho.PlayerHealthProperties;
import net.mcft.copy.core.util.EntityUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class CommonProxy {
	
	public void init() {
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
	}
	
	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event) {
		if (!(event.entity instanceof EntityPlayerMP)) return;
		EntityUtils.createProperties(event.entity, PlayerHealthProperties.class);
	}
	
	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event) {
		if (!(event.entity instanceof EntityPlayerMP)) return;
		EntityUtils.getProperties(event.entity, PlayerHealthProperties.class)
			.update((EntityPlayer)event.entity);
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onLivingHurt(LivingHurtEvent event) {
		if (!(event.entity instanceof EntityPlayerMP)) return;
		EntityUtils.getProperties(event.entity, PlayerHealthProperties.class)
			.hurt((EntityPlayer)event.entity);
	}
	
}
