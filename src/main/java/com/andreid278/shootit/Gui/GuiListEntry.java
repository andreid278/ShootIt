package com.andreid278.shootit.Gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;

public class GuiListEntry implements GuiListExtended.IGuiListEntry {
	public final Minecraft mc;
	public final String text;
	public final int color;
	
	public GuiListEntry(String text, int color) {
		this.mc = Minecraft.getMinecraft();
		this.text = text;
		this.color = color;
	}
	
	@Override
	public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {
		
	}

	@Override
	public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY,
			boolean isSelected) {
		mc.fontRendererObj.drawString(text, x, y, color, false);
	}

	@Override
	public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
		return true;
	}

	@Override
	public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
		
	}
}
