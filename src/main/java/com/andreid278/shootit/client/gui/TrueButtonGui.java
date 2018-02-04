package com.andreid278.shootit.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.util.ResourceLocation;

public class TrueButtonGui extends GuiButton {
	public List<String> text;
	public int textureButtonX;
	public int textureButtonY;
	public int textureButtonWidth;
	public int textureButtonHeight;
	public int textureWidth;
	public int textureHeight;
	public ResourceLocation buttonTexture;

	public TrueButtonGui(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
		super(buttonId, x, y, widthIn, heightIn, buttonText);
		text = new ArrayList<String>();
		text.add(buttonText);
		textureButtonX = 0;
		textureButtonY = 46;
		textureButtonWidth = 200;
		textureButtonHeight = 20;
		textureWidth = 256;
		textureHeight = 256;
		buttonTexture = BUTTON_TEXTURES;
	}

	public TrueButtonGui(int buttonId, int x, int y, int widthIn, int heightIn, List<String> text) {
		super(buttonId, x, y, widthIn, heightIn, "");
		this.text = new ArrayList<String>(text);
	}
	
	public TrueButtonGui(int buttonId, int x, int y, int widthIn, int heightIn, ResourceLocation rl, int textureW, int textureH) {
		super(buttonId, x, y, widthIn, heightIn, "");
		text = new ArrayList<String>();
		textureButtonX = 0;
		textureButtonY = 0;
		textureButtonWidth = textureW;
		textureButtonHeight = textureH / 3;
		textureWidth = textureW;
		textureHeight = textureH;
		buttonTexture = rl;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if(this.visible) {
			FontRenderer fontrenderer = mc.fontRenderer;
			this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			int i = this.getHoverState(this.hovered);
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			mc.getTextureManager().bindTexture(buttonTexture);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.drawTexturedModalRect(this.x,
					this.y,
					(double)textureButtonX / (textureWidth),
					(double)(textureButtonY + i * textureButtonHeight) / (textureHeight),
					this.width,
					this.height,
					(double)(textureButtonWidth) / (textureWidth),
					(double)(textureButtonHeight) / (textureHeight));
			this.mouseDragged(mc, mouseX, mouseY);
			int j = 14737632;

			if(packedFGColour != 0)
				j = packedFGColour;
			else if(!this.enabled)
				j = 10526880;
			else if(this.hovered)
				j = 16777120;
			
			int k = 1;
			for(String s : text)
				this.drawCenteredString(fontrenderer, s, this.x + this.width / 2, this.y + (k++) * this.height / (text.size() + 1) - 4, j);
		}
	}

	public void drawTexturedModalRect(int x, int y, double textureX, double textureY, int width, int height, double textureW, double textureH) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos((double)(x + 0), (double)(y + height), (double)this.zLevel).tex(textureX, textureY + textureH).endVertex();
		vertexbuffer.pos((double)(x + width), (double)(y + height), (double)this.zLevel).tex(textureX + textureW, textureY + textureH).endVertex();
		vertexbuffer.pos((double)(x + width), (double)(y + 0), (double)this.zLevel).tex(textureX + textureW, textureY).endVertex();
		vertexbuffer.pos((double)(x + 0), (double)(y + 0), (double)this.zLevel).tex(textureX, textureY).endVertex();
		tessellator.draw();
	}
}