package net.mcft.copy.aho.proxy;

import net.mcft.copy.aho.AdvHealthOptions;
import net.mcft.copy.aho.client.GuiCreateWorldCustom;
import net.mcft.copy.aho.config.AHOGlobalConfig;
import net.mcft.copy.aho.config.EnumControl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraftforge.client.event.GuiOpenEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
	
	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent event) {
		EnumControl regenControl = AdvHealthOptions.globalConfig.getEnum(
				AHOGlobalConfig.control);
		if ((regenControl == EnumControl.HIDDEN) ||
		    !(event.gui instanceof GuiCreateWorld) ||
		     (event.gui instanceof GuiCreateWorldCustom)) return;
		// Replace default create world GUI with custom one. This might be
		// bad if other mods do the same, or try to modify the default one.
		Minecraft mc = Minecraft.getMinecraft();
		mc.displayGuiScreen(new GuiCreateWorldCustom(mc.currentScreen));
		event.setCanceled(true);
	}
	
}
