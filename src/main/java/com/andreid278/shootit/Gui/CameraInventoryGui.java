package com.andreid278.shootit.Gui;

import java.io.IOException;
import java.util.Arrays;

import com.andreid278.shootit.Main;
import com.andreid278.shootit.Misc.Statics;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.Container;
import net.minecraft.util.text.TextFormatting;

public class CameraInventoryGui extends GuiContainer {
	public CameraInventoryGui(Container inventorySlotsIn) {
		super(inventorySlotsIn);
		xSize = 176;
		ySize = 133;
	}

	public void initGui() {
		super.initGui();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		this.mc.renderEngine.bindTexture(Statics.CAMERA_INVENTORY_RL);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize, 1, 1);
	}

	public void drawTexturedModalRect(int x, int y, double textureX, double textureY, int width, int height, double textureWidth, double textureHeight) {
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos((double)(x + 0), (double)(y + height), (double)this.zLevel).tex(textureX, textureY + textureHeight).endVertex();
		vertexbuffer.pos((double)(x + width), (double)(y + height), (double)this.zLevel).tex(textureX + textureWidth, textureY + textureHeight).endVertex();
		vertexbuffer.pos((double)(x + width), (double)(y + 0), (double)this.zLevel).tex(textureX + textureWidth, textureY).endVertex();
		vertexbuffer.pos((double)(x + 0), (double)(y + 0), (double)this.zLevel).tex(textureX, textureY).endVertex();
		tessellator.draw();
	}

	public void onGuiClosed() {
		super.onGuiClosed();
	}
	
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		if(getSlotUnderMouse() == null)
			return;
		int slot = getSlotUnderMouse().slotNumber;
		if(slot == 36)
			drawHoveringText(Arrays.asList(TextFormatting.ITALIC + "Memory card"), mouseX - guiLeft, mouseY - guiTop + 25);
	}
	
	protected boolean checkHotbarKeys(int keyCode) {
		return false;
	}
}
