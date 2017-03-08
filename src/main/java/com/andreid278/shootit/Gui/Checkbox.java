package com.andreid278.shootit.Gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class Checkbox extends Gui {
	public static final ResourceLocation texture = new ResourceLocation("shootit", "textures/checkbox.png");

	public int x;
	public int y;
	public int width;
	public int height;
	public boolean check;
	public int id;
	public String text;

	public Checkbox(int id, int x, int y, int width, int height, boolean check, String text) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.check = check;
		this.text = text;
	}

	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if(mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height) {
			check = !check;
			return true;
		}
		return false;
	}
	
	public void drawCheckbox(Minecraft mc, int mouseX, int mouseY) {
		mc.getTextureManager().bindTexture(texture);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(x, y, 0, check ? 0.5 : 0, width, height, 1, 0.5);
		mc.fontRendererObj.drawString(text, x + width + 5, y, 0, false);
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
}
