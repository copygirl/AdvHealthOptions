package net.mcft.copy.aho.proxy;

import net.mcft.copy.aho.AdvHealthOptions;
import net.mcft.copy.aho.client.GuiCreateWorldCustom;
import net.mcft.copy.aho.config.AHOGlobalConfig;
import net.mcft.copy.aho.config.AHOWorldConfig;
import net.mcft.copy.aho.config.EnumControl;
import net.mcft.copy.aho.config.EnumHunger;
import net.mcft.copy.aho.config.EnumShieldMode;
import net.mcft.copy.aho.config.EnumShieldModifier;
import net.mcft.copy.aho.entity.AHOProperties;
import net.mcft.copy.core.client.GuiTextureResource;
import net.mcft.copy.core.util.ClientUtils;
import net.mcft.copy.core.util.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
	
	private static final GuiTextureResource icons =
			new GuiTextureResource(AdvHealthOptions.MOD_ID, "icons", 32, 32);
	
	private int renderTop = 0;
	private boolean renderShield = false;
	
	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent event) {
		EnumControl regenControl = AdvHealthOptions.config.<EnumControl>get(AHOGlobalConfig.generalControl);
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
		EnumShieldModifier modifier = AdvHealthOptions.config.<EnumShieldModifier>get(AHOWorldConfig.shieldModifier);
		switch (event.type) {
			case ALL:
				renderShield = false;
				break;
			case HEALTH:
				if (modifier == EnumShieldModifier.HEALTH)
					renderTop = GuiIngameForge.left_height;
				break;
			case ARMOR:
				if (modifier == EnumShieldModifier.ARMOR)
					renderTop = GuiIngameForge.left_height;
				break;
			case FOOD:
				// Don't render food meter if hunger is disabled.
				if (AdvHealthOptions.config.<EnumHunger>get(AHOWorldConfig.hunger) != EnumHunger.ENABLE)
					event.setCanceled(true);
				break;
			default: break;
		}
	}
	
	@SubscribeEvent
	public void onRenderGameOverlayPost(RenderGameOverlayEvent.Post event) {
		EnumShieldModifier modifier = AdvHealthOptions.config.<EnumShieldModifier>get(AHOWorldConfig.shieldModifier);
		switch (event.type) {
			case HEALTH:
				if (modifier != EnumShieldModifier.ARMOR)
					renderShield = true;
				break;
			case ARMOR:
				if (modifier == EnumShieldModifier.ARMOR)
					renderShield = true;
				break;
			case ALL:
				if (!renderShield) break;
				else if (modifier == EnumShieldModifier.NONE) {
					renderTop = GuiIngameForge.left_height;
					GuiIngameForge.left_height += 10;
				} 
				
				EntityPlayer player = ClientUtils.getLocalPlayer();
				EnumShieldMode mode = AdvHealthOptions.config.<EnumShieldMode>get(AHOWorldConfig.shieldMode);
				AHOProperties properties = EntityUtils.getProperties(player, AHOProperties.class);
				
				int shieldMaximum = AdvHealthOptions.config.<Integer>get(AHOWorldConfig.shieldMaximum);
				
				if ((mode != EnumShieldMode.SUBTRACTION) || (shieldMaximum <= 0) ||
				    ((properties.shieldAmount.get() <= 0) && (modifier != EnumShieldModifier.NONE))) break;
				
				int maxHalfHearts = ((modifier == EnumShieldModifier.HEALTH) ? (int)(player.getMaxHealth() + 0.5) : 20);
				int shieldHalfHearts = (int)Math.floor(properties.shieldAmount.get() / shieldMaximum * maxHalfHearts + 0.5);
				shieldHalfHearts = Math.min(shieldHalfHearts, maxHalfHearts);
				
				int rows = shieldHalfHearts / 20;
				int rowHeight = Math.max(10 - (rows - 2), 3);
				
				int left = event.resolution.getScaledWidth() / 2 - 91;
				int top = event.resolution.getScaledHeight() - renderTop;
				
				Minecraft.getMinecraft().renderEngine.bindTexture(icons);
				int v = ((modifier == EnumShieldModifier.NONE) ? 0 : ((modifier == EnumShieldModifier.ARMOR) ? 9 : 18));
				int max = ((modifier == EnumShieldModifier.NONE) ? 10 : (int)Math.floor(shieldHalfHearts / 2.0 + 0.5));
				for (int i = 0; i < max; i++) {
					int row = MathHelper.ceiling_float_int((i + 1) / 10.0F) - 1;
					int x = left + i % 10 * 8;
					int y = top - row * rowHeight;
					if (i * 2 == shieldHalfHearts - 1)
						icons.drawQuad(x, y, 9, v, 9, 9);
					else if (i * 2 < shieldHalfHearts)
						icons.drawQuad(x, y, 0, v, 9, 9);
					else icons.drawQuad(x, y, 18, v, 9, 9);
				}
				break;
			default: break;
		}
	}
	
}
