package com.andreid278.shootit.Gui;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.andreid278.shootit.Misc.Photos;
import com.andreid278.shootit.Misc.Statics;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class GuiEditor extends Gui {
	public int photoID;
	public boolean isEnabled;
	public int x;
	public int y;
	public int width;
	public int height;
	public int photoWidth;
	public int photoHeight;
	public double x1;
	public double y1;
	public double blockSize;
	public int lastMouseX;
	public int lastMouseY;
	public int curMouseX;
	public int curMouseY;

	public GuiEditor(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		isEnabled = false;
	}

	public void setPhotoSize(int photoWidth, int photoHeight) {
		if(this.photoWidth != photoWidth || this.photoHeight != photoHeight) {
			this.photoWidth = photoWidth;
			this.photoHeight = photoHeight;
			blockSize = width / photoWidth;
			if(blockSize * photoHeight >= height) {
				blockSize = height / photoHeight;
			}
			x1 = x + width / 2 - blockSize * photoWidth / 2;
			y1 = y + height / 2 - blockSize * photoHeight / 2;
		}
	}

	public void draw(Minecraft mc, int mouseX, int mouseY) {
		if(photoID == -2)
			mc.renderEngine.bindTexture(Statics.NO_MEMORY_CARD);
		else if(photoID == -1)
			mc.renderEngine.bindTexture(Statics.NO_PHOTOS);
		else if(photoID == 0)
			mc.renderEngine.bindTexture(Statics.LOADING);
		else mc.renderEngine.bindTexture(Photos.photos.get(photoID));
		if(!isEnabled) {
			drawTexturedModalRect(x, y, 0, 0, width, height, 1, 1);
		}
		else {
			curMouseX = mouseX;
			curMouseY = mouseY;
			//			double xl = x1 >= x ? x1 >= x + width ? x + width : x1 : x;
			//			double yl = y1 >= y ? y1 >= y + height ? y + height : y1 : y;
			//			double xw = x2 < x + width ? x2 < x ? 0 : x2 - xl : x + width - xl;
			//			double yh = y2 < y + height ? y2 < y ? 0 : y2 - yl : y + height - yl;
			//			double txl = x1 < x ? x2 < x ? 0 : (double)(x - x1) / (x2 - x1) : 0;
			//			double tyl = y1 < y ? y2 < y ? 0 : (double)(y - y1) / (y2 - y1) : 0;
			//			double txw = (double)xw / (x2 - x1);
			//			double tyh = (double)yh / (y2 - y1);
			//			drawTexturedModalRect((int)xl, (int)yl, txl, tyl, (int)xw, (int)yh, txw, tyh);
			drawTexturedModalRect(x, y, 0, 0, width, height, 1, 1);
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
			GlStateManager.disableTexture2D();
			for(int i = 0; i < photoWidth; i++)
				for(int j = 0; j < photoHeight; j++) {
					Tessellator tessellator = Tessellator.getInstance();
					VertexBuffer vertexBuffer = tessellator.getBuffer();
					vertexBuffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
					vertexBuffer.pos((double)(x1 + i * blockSize), (double)(y1 + j * blockSize), 0.0).color(255, 255, 255, 255).endVertex();
					vertexBuffer.pos((double)(x1 + (i + 1) * blockSize), (double)(y1 + j * blockSize), 0.0).color(255, 255, 255, 255).endVertex();
					vertexBuffer.pos((double)(x1 + (i + 1) * blockSize), (double)(y1 + (j + 1) * blockSize), 0.0).color(255, 255, 255, 255).endVertex();
					vertexBuffer.pos((double)(x1 + i * blockSize), (double)(y1 + (j + 1) * blockSize), 0.0).color(255, 255, 255, 255).endVertex();
					tessellator.draw();
				}
			GlStateManager.enableTexture2D();
		}
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

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if(isEnabled)
			if(mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height) {
				lastMouseX = mouseX;
				lastMouseY = mouseY;
			}
	}

	public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		if(isEnabled)
			if(mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height) {
				x1 += mouseX - lastMouseX;
				y1 += mouseY - lastMouseY;
				if(x1 < x || x1 + photoWidth * blockSize > x + width)
					x1 -= mouseX - lastMouseX;
				if(y1 < y || y1 + photoHeight * blockSize > y + height)
					y1 -= mouseY - lastMouseY;
				lastMouseX = mouseX;
				lastMouseY = mouseY;
			}
	}

	public void handleMouseInput() {
		if(isEnabled)
			if(curMouseX >= x && curMouseX < x + width && curMouseY >= y && curMouseY < y + height) {
				double dWheel = Mouse.getEventDWheel();
				if(dWheel != 0) {
					if(dWheel > 0)
						dWheel = 1;
					else dWheel = -1;
					double xc = x1 + blockSize * photoWidth / 2.0;
					double yc = y1 + blockSize * photoHeight / 2.0;
					blockSize += dWheel;
					if(xc - blockSize * photoWidth / 2 < x || xc + blockSize * photoWidth / 2 > x + width || yc - blockSize * photoHeight / 2 < y || yc + blockSize * photoHeight / 2 > y + height || blockSize <= 0)
						blockSize -= dWheel;
					else {
						x1 = xc - blockSize * photoWidth / 2.0;
						y1 = yc - blockSize * photoHeight / 2.0;
					}
				}
			}
	}
	
	public double[] getTextureCoords() {
		double result[] = new double[4];
		if(!isEnabled) {
			result[0] = 0;
			result[1] = 0;
			result[2] = 1;
			result[3] = 1;
		}
		else {
			result[0] = (x1 - x) / width;
			result[1] = (y1 - y) / height;
			result[2] = (x1 + photoWidth * blockSize - x) / width;
			result[3] = (y1 + photoHeight * blockSize - y) / height;
			if(result[0] < 0)
				result[0] = 0;
			if(result[1] < 0)
				result[1] = 0;
			if(result[2] > 1)
				result[2] = 1;
			if(result[3] > 1)
				result[3] = 1;
		}
		return result;
	}
}
