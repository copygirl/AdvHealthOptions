package net.mcft.copy.aho.client.gui;

import java.util.Set;

import net.mcft.copy.aho.AdvHealthOptions;
import net.mcft.copy.aho.config.AHOGlobalConfig;
import net.mcft.copy.core.client.gui.GuiConfigHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AHOGuiFactory implements IModGuiFactory {
	
	public static class AHOConfigGuiScreen extends GuiConfig {
		
		public AHOConfigGuiScreen(GuiScreen parentScreen) {
			super(parentScreen, GuiConfigHelper.getElementsFor(AHOGlobalConfig.class, AdvHealthOptions.MOD_ID),
					AdvHealthOptions.MOD_ID, false, false, AdvHealthOptions.MOD_NAME);
		}
		
	}
	
	@Override
	public void initialize(Minecraft minecraftInstance) {  }
	
	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass() { return AHOConfigGuiScreen.class; }
	
	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() { return null; }
	
	@Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) { return null; }
	
}
