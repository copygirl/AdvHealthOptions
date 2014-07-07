package net.mcft.copy.aho.proxy;

import net.mcft.copy.aho.AdvHealthOptions;
import net.mcft.copy.aho.client.GuiCreateWorldCustom;
import net.mcft.copy.aho.config.AHOGlobalConfig;
import net.mcft.copy.aho.config.AHOWorldConfig;
import net.mcft.copy.aho.config.EnumControl;
import net.mcft.copy.aho.config.EnumHunger;
import net.mcft.copy.aho.entity.AHOProperties;
import net.mcft.copy.core.client.GuiTextureResource;
import net.mcft.copy.core.util.ClientUtils;
import net.mcft.copy.core.util.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
	
	private static final GuiTextureResource icons =
			new GuiTextureResource(AdvHealthOptions.MOD_ID, "icons", 64, 64);
	
	private boolean renderHealth = false;
	
	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent event) {
		EnumControl regenControl = AdvHealthOptions.config.get(AHOGlobalConfig.generalControl);
		if ((regenControl == EnumControl.HIDDEN) ||
		    !(event.gui instanceof GuiCreateWorld) ||
		     (event.gui instanceof GuiCreateWorldCustom)) return;
		// Replace default create world GUI with custom one. This might be
		// bad if other mods do the same, or try to modify the default one.
		Minecraft mc = Minecraft.getMinecraft();
		mc.displayGuiScreen(new GuiCreateWorldCustom(mc.currentScreen));
		event.setCanceled(true);
	}
	
	@SubscribeEvent
	public void onRenderGameOverlayPre(RenderGameOverlayEvent.Pre event) {
		if (event.type == ElementType.ALL) renderHealth = false;
		else if (event.type != ElementType.FOOD) return;
		EnumHunger hunger = AdvHealthOptions.config.get(AHOWorldConfig.hunger);
		if (hunger != EnumHunger.ENABLE)
			event.setCanceled(true);
	}
	
	@SubscribeEvent
	public void onRenderGameOverlayPost(RenderGameOverlayEvent.Post event) {
		if (event.type == ElementType.HEALTH) renderHealth = true;
		else if ((event.type != ElementType.ALL) || !renderHealth) return;
		
		EntityPlayer player = ClientUtils.getLocalPlayer();
		AHOProperties properties = EntityUtils.getProperties(player, AHOProperties.class);
		if (properties.shieldAmount.get() <= 0) return;
		
		int shieldMaximum = AdvHealthOptions.config.get(AHOWorldConfig.shieldMaximum);
		int hearts = (int)(player.getMaxHealth() + 0.5F) / 2;
		int shieldHalfHearts = (int)(Math.min(1, properties.shieldAmount.get() / shieldMaximum) * hearts * 2);
		
		int rows = shieldHalfHearts / 20;
		int rowHeight = Math.max(10 - (rows - 2), 3);
		
		int left = event.resolution.getScaledWidth() / 2 - 91;
		int top = event.resolution.getScaledHeight() - 39;
		
		GL11.glPushAttrib(GL11.GL_TEXTURE_BIT);
		GL11.glEnable(GL11.GL_BLEND);
		Minecraft.getMinecraft().renderEngine.bindTexture(icons);
		for (int i = 0; i < shieldHalfHearts / 2; i++) {
			int row = MathHelper.ceiling_float_int((i + 1) / 10.0F) - 1;
			int x = left + i % 10 * 8;
			int y = top - row * rowHeight;
			icons.drawQuad(x, y, 0, 0, 9, 9);
		}
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopAttrib();
	}
	
}
