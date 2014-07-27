package net.mcft.copy.aho.client.gui;

import java.util.Set;

import net.mcft.copy.aho.AdvHealthOptions;
import net.mcft.copy.aho.config.AHOWorldConfig;
import net.mcft.copy.core.client.gui.GuiConfigBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AHOGuiFactory implements IModGuiFactory {
	
	public static class AHOConfigGuiScreen extends GuiConfigBase {
		
		public AHOConfigGuiScreen(GuiScreen parentScreen) {
			super(parentScreen, AdvHealthOptions.MOD_ID, AdvHealthOptions.MOD_NAME, AdvHealthOptions.globalConfig);
		}
		
		@Override
		public void initGui() {
			super.initGui();
			updateCategoryEnabled();
		}
		
		@Override
		protected void mouseClicked(int x, int y, int mouseEvent) {
			super.mouseClicked(x, y, mouseEvent);
			updateCategoryEnabled();
		}
		
		public void updateCategoryEnabled() {
			boolean custom = "CUSTOM".equals(getEntry(AHOWorldConfig.generalPreset).getCurrentValue());
			getCategoryEntry("regeneration").enabled = custom;
			getCategoryEntry("hurtPenalty").enabled = custom;
			getCategoryEntry("respawn").enabled = custom;
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
