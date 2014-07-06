package net.mcft.copy.aho.client;

import net.mcft.copy.aho.AdvHealthOptions;
import net.mcft.copy.aho.config.AHOGlobalConfig;
import net.mcft.copy.aho.config.AHOWorldConfig;
import net.mcft.copy.aho.config.EnumControl;
import net.mcft.copy.aho.config.EnumPreset;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiCreateWorldCustom extends GuiCreateWorld {
	
	private static EnumPreset preset = EnumPreset.CUSTOM;
	
	public static EnumPreset getAndResetPreset() {
		EnumPreset p = preset;
		preset = EnumPreset.CUSTOM;
		return p;
	}
	
	private GuiButton buttonRegenMode;
	private static EnumPreset regenMode;
	
	public GuiCreateWorldCustom(GuiScreen gui) {
		super(gui);
		regenMode = AdvHealthOptions.config.get(AHOWorldConfig.generalPreset);
	}
	
	private String getRegenButtonString() {
		String regenModeString = I18n.format(AHOLocalization.REGEN_MODE + "." + regenMode.toString().toLowerCase());
		return I18n.format(AHOLocalization.REGEN_MODE, regenModeString);
	}
	
	@Override
	public void initGui() {
		super.initGui();
		((GuiButton)buttonList.get(2)).yPosition = 100;
		buttonList.add(buttonRegenMode = new GuiButton(9, width / 2 - 75, 151, 150, 20, getRegenButtonString()));
		EnumControl regenControl = AdvHealthOptions.config.get(AHOGlobalConfig.generalControl);
		buttonRegenMode.enabled = (regenControl != EnumControl.LOCK);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) {
		if (!button.enabled) return;
		switch (button.id) {
			case 0:
				if (buttonRegenMode.enabled)
					preset = regenMode;
				break;
			case 3:
				// If going to the more options screen, hide the button.
				// If going back to the main screen, show the button again.
				boolean inMoreOptions = ReflectionHelper.getPrivateValue(GuiCreateWorld.class, this, "field_146344_y");
				buttonRegenMode.visible = inMoreOptions;
				break;
			case 9:
				regenMode = EnumPreset.values()[(regenMode.ordinal() + 1) % EnumPreset.values().length];
				buttonRegenMode.displayString = getRegenButtonString();
				break;
		}
		super.actionPerformed(button);
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		boolean inMoreOptions = ReflectionHelper.getPrivateValue(GuiCreateWorld.class, this, "field_146344_y");
		if (!inMoreOptions) {
			GuiTextField textField = ReflectionHelper.getPrivateValue(GuiCreateWorld.class, this, "field_146333_g");
			String resultFolder = ReflectionHelper.getPrivateValue(GuiCreateWorld.class, this, "field_146336_i");
			String gameModeLine1 = ReflectionHelper.getPrivateValue(GuiCreateWorld.class, this, "field_146323_G");
			String gameModeLine2 = ReflectionHelper.getPrivateValue(GuiCreateWorld.class, this, "field_146328_H");
			
	        drawDefaultBackground();
	        drawCenteredString(fontRendererObj, I18n.format("selectWorld.create"), width / 2, 20, -1);
	        
			drawString(fontRendererObj, I18n.format("selectWorld.enterName"), width / 2 - 100, 47, 0xFFA0A0A0);
			drawString(fontRendererObj, I18n.format("selectWorld.resultFolder") + " " + resultFolder, width / 2 - 100, 85, 0xFFA0A0A0);
			textField.drawTextBox();
			drawString(fontRendererObj, gameModeLine1, width / 2 - 100, 122, 0xFFA0A0A0);
			drawString(fontRendererObj, gameModeLine2, width / 2 - 100, 134, 0xFFA0A0A0);
			
			String regenModeDesc = ((regenMode == EnumPreset.CUSTOM)
					? AdvHealthOptions.config.<String>get(AHOGlobalConfig.generalDescription)
					: (AHOLocalization.REGEN_MODE + "." + regenMode.toString().toLowerCase() + ".desc"));
			regenModeDesc = I18n.format(regenModeDesc);
			drawString(fontRendererObj, regenModeDesc, width / 2 - 100, 172, 0xFFA0A0A0);
			
		    for (int i = 0; i < buttonList.size(); ++i)
		        ((GuiButton)buttonList.get(i)).drawButton(mc, par1, par2);
		    for (int i = 0; i < labelList.size(); ++i)
		        ((GuiLabel)labelList.get(i)).func_146159_a(mc, par1, par2);
		} else super.drawScreen(par1, par2, par3);
	}
	
}
