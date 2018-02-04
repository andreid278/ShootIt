package com.andreid278.shootit.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class GuiListEntry implements GuiListExtended.IGuiListEntry {
	public final Minecraft mc;
	public final String text;
	public final int color;
	public final boolean drawBckgnd;
	public final float bckgndColorR;
	public final float bckgndColorG;
	public final float bckgndColorB;
	
	public GuiListEntry(String text, int color, boolean drawBckgnd, int bckgndColor) {
		this.mc = Minecraft.getMinecraft();
		this.text = text;
		this.color = color;
		this.drawBckgnd = drawBckgnd;
		this.bckgndColorR = (float)(bckgndColor >> 16 & 255) / 255.0F;
        this.bckgndColorG = (float)(bckgndColor >> 8 & 255) / 255.0F;
        this.bckgndColorB = (float)(bckgndColor & 255) / 255.0F;
	}
	
	@Override
	public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
		if(this.drawBckgnd) {
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder vertexbuffer = tessellator.getBuffer();
			GlStateManager.color(bckgndColorR, bckgndColorG, bckgndColorB, 1.0F);
			GlStateManager.disableTexture2D();
			vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
			vertexbuffer.pos((double)(x + listWidth), (double)y, 0).endVertex();
			vertexbuffer.pos((double)(x - 2), (double)y, 0).endVertex();
			vertexbuffer.pos((double)(x - 2), (double)(y + slotHeight), 0).endVertex();
			vertexbuffer.pos((double)(x + listWidth), (double)(y + slotHeight), 0).endVertex();
			tessellator.draw();
			GlStateManager.enableTexture2D();
		}
		mc.fontRenderer.drawString(text, x, y + 4, color, false);
	}

	@Override
	public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
		return true;
	}

	@Override
	public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
		
	}

	@Override
	public void updatePosition(int slotIndex, int x, int y, float partialTicks) {
		// TODO Auto-generated method stub
		
	}
}
