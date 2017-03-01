package com.andreid278.shootit.Gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class TrueButtonGui extends GuiButton {
	public List<String> text;

	public TrueButtonGui(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
		super(buttonId, x, y, widthIn, heightIn, buttonText);
		text = new ArrayList<String>();
		text.add(buttonText);
	}

	public TrueButtonGui(int buttonId, int x, int y, int widthIn, int heightIn, List<String> text) {
		super(buttonId, x, y, widthIn, heightIn, "");
		this.text = new ArrayList<String>(text);
	}

	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if(this.visible) {
			FontRenderer fontrenderer = mc.fontRendererObj;
			mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
			int i = this.getHoverState(this.hovered);
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + i * 20, this.width, this.height, 200, 20);
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
				this.drawCenteredString(fontrenderer, s, this.xPosition + this.width / 2, this.yPosition + (k++) * this.height / (text.size() + 1) - 4, j);
		}
	}

	public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height, int textureWidth, int textureHeight) {
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos((double)(x + 0), (double)(y + height), (double)this.zLevel).tex((textureX + 0) * 0.00390625, (double)((textureY + textureHeight) * 0.00390625)).endVertex();
		vertexbuffer.pos((double)(x + width), (double)(y + height), (double)this.zLevel).tex((textureX + textureWidth) * 0.00390625, (textureY + textureHeight) * 0.00390625).endVertex();
		vertexbuffer.pos((double)(x + width), (double)(y + 0), (double)this.zLevel).tex((textureX + textureWidth) * 0.00390625, (textureY + 0) * 0.00390625).endVertex();
		vertexbuffer.pos((double)(x + 0), (double)(y + 0), (double)this.zLevel).tex((textureX + 0) * 0.00390625, (textureY + 0) * 0.00390625).endVertex();
		tessellator.draw();
	}
}
