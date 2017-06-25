package com.andreid278.shootit.Gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.vecmath.Point3i;
import javax.vecmath.Point4i;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.andreid278.shootit.Misc.Photos;
import com.andreid278.shootit.Misc.Statics;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class GuiPhotoEditor extends Gui {
	public enum Tools {Brush, Eraser, Rectangle};
	private int x;
	private int y;
	private int width;
	private int height;
	private int screenWidth;
	private int screenHeight;
	private int photoWidth;
	private int photoHeight;
	public int photoID;
	private ByteBuffer imageBuffer;
	private int restoreBuffersNum;
	private ByteBuffer restoreBuffers[];
	private int curRestoreBuffer;
	private int maxRestoreBuffer;
	private boolean isEnabled;
	private int prevMouseX;
	private int prevMouseY;
	public Tools curTool;
	public int size;
	public int red;
	public int green;
	public int blue;

	public GuiPhotoEditor(int x, int y, int width, int height, int screenWidth, int screenHeight) {
		photoID = -1;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		isEnabled = false;
		prevMouseX = -1;
		prevMouseY = -1;
		curTool = Tools.Brush;
		size = 1;
		red = 0;
		green = 0;
		blue = 0;
		restoreBuffersNum = 10;
		restoreBuffers = new ByteBuffer[restoreBuffersNum];
		curRestoreBuffer = 0;
		maxRestoreBuffer = restoreBuffersNum - 1;
	}

	public void draw(Minecraft mc, int mouseX, int mouseY) {
		mc.renderEngine.bindTexture(Statics.TRANSPARENCY);
		drawTexturedModalRect(x, y, 0, 0, width, height, 1, 1);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		if(photoID != -1) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			if(!isEnabled) {
				mc.renderEngine.bindTexture(Photos.photos.get(photoID));
				drawTexturedModalRect(x, y, 0, 0, width, height, 1, 1);
				return;
			}
			GL11.glDisable(GL11.GL_BLEND);
		}
		if(photoWidth > 0 && photoHeight > 0) {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glRasterPos2d(x, y + height);
			GL11.glPixelZoom((float)width / photoWidth * mc.displayWidth / screenWidth, (float)height / photoHeight * mc.displayHeight / screenHeight);
			//				GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
			//				GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
			GL11.glDrawPixels(photoWidth, photoHeight, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imageBuffer);
			GL11.glPixelZoom(1, 1);
			GL11.glRasterPos2d(0, 0);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			switch(curTool) {
			case Brush:
			case Eraser:
				int curSizeX = (int) ((double) size / photoWidth * width);
				int curSizeY = (int) ((double) size / photoHeight * height);
				drawRectangle(mouseX - curSizeX, mouseY - curSizeY, mouseX + curSizeX, mouseY + curSizeY);
				break;
			case Rectangle:
				if(prevMouseX != -1 && prevMouseY != -1)
					drawRectangle(prevMouseX, prevMouseY, mouseX, mouseY);
				break;
			}
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

	public void drawRectangle(int x1, int y1, int x2, int y2) {
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
		vertexbuffer.pos((double)x1, (double)y2, (double)this.zLevel).color(0, 0, 0, 255).endVertex();
		vertexbuffer.pos((double)x2, (double)y2, (double)this.zLevel).color(0, 0, 0, 255).endVertex();
		vertexbuffer.pos((double)x2, (double)y1, (double)this.zLevel).color(0, 0, 0, 255).endVertex();
		vertexbuffer.pos((double)x1, (double)y1, (double)this.zLevel).color(0, 0, 0, 255).endVertex();
		tessellator.draw();
	}

	public void setEnabled(boolean isEnabled) {
		if(photoID == -1)
			return;
		this.isEnabled = isEnabled;
		curRestoreBuffer = 0;
		maxRestoreBuffer = restoreBuffersNum - 1;
		if(isEnabled && photoID != -1) {
			try {
				File file = new File(Statics.photosFolderPathClient + Statics.slash + photoID + ".png");
				if(!file.exists()) {
					this.isEnabled = false;
					return;
				}
				BufferedImage bufferedImage = ImageIO.read(file);
				photoWidth = bufferedImage.getWidth();
				photoHeight = bufferedImage.getHeight();
				imageBuffer = ByteBuffer.allocateDirect(photoWidth * photoHeight * 4);
				for(int i = 0; i < restoreBuffersNum; i++)
					restoreBuffers[i] = ByteBuffer.allocateDirect(photoWidth * photoHeight * 4);
				for(int j = photoHeight - 1; j >= 0; j--)
					for(int i = 0; i < photoWidth; i++) {
						int color = bufferedImage.getRGB(i, j);
						Color c = new Color(bufferedImage.getRGB(i, j), true);
						byte r = (byte)c.getRed();
						byte g = (byte)c.getGreen();
						byte b = (byte)c.getBlue();
						byte a = (byte)c.getAlpha();
						imageBuffer.put((byte)c.getRed());
						imageBuffer.put((byte)c.getGreen());
						imageBuffer.put((byte)c.getBlue());
						imageBuffer.put((byte)c.getAlpha());
					}
				imageBuffer.rewind();
			} catch (IOException e) {
				this.isEnabled = false;
				return;
			}
		}
		else if(!isEnabled) {
			photoWidth = 0;
			photoHeight = 0;
		}
	}

	public void save() {
		if(!isEnabled)
			return;
		BufferedImage bufferedImage = new BufferedImage(photoWidth, photoHeight, BufferedImage.TYPE_INT_ARGB);
		for(int j = photoHeight - 1; j >= 0; j--)
			for(int i = 0; i < photoWidth; i++) {
				int color;
				color = (imageBuffer.get() & 0x000000ff) << 16;
				color += (imageBuffer.get() & 0x000000ff) << 8;
				color += (imageBuffer.get() & 0x000000ff);
				color += (imageBuffer.get() & 0x000000ff) << 24;
				bufferedImage.setRGB(i, j, color);
			}
		imageBuffer.rewind();
		try {
			ImageIO.write(bufferedImage, "png", new File(Statics.photosFolderPathClient + Statics.slash + "0.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void restore() {
		if(!isEnabled)
			return;
		int a = curRestoreBuffer - 1;
		if(a < 0)
			a = restoreBuffersNum - 1;
		if(a == maxRestoreBuffer)
			return;
		imageBuffer.put(restoreBuffers[a]);
		imageBuffer.rewind();
		restoreBuffers[a].rewind();
		curRestoreBuffer = a;
	}

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if(!isEnabled)
			return;
		if(mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height) {
			prevMouseX = mouseX;
			prevMouseY = mouseY;
			restoreBuffers[curRestoreBuffer].put(imageBuffer);
			restoreBuffers[curRestoreBuffer].rewind();
			imageBuffer.rewind();
			if(curRestoreBuffer == maxRestoreBuffer)
				maxRestoreBuffer = (maxRestoreBuffer + 1) % restoreBuffersNum;
			curRestoreBuffer = (curRestoreBuffer + 1) % restoreBuffersNum;
			applyTool(mouseX - x, height - mouseY + y, mouseButton);
		}
	}

	public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		if(!isEnabled)
			return;
		if(prevMouseX != mouseX || prevMouseY != mouseY)
			if(mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height)
				applyTool(mouseX - x, height - mouseY + y, clickedMouseButton);
	}

	public void mouseReleased(int mouseX, int mouseY, int state) {
		if(!isEnabled)
			return;
		if(prevMouseX >= x && prevMouseX < x + width && prevMouseY >= y && prevMouseY < y + height) {
			prevMouseX -= x;
			prevMouseY = height - prevMouseY + y;
			int curMouseX = mouseX >= x ? mouseX < x + width ? mouseX - x : width - 1 : 0;
			int curMouseY = mouseY >= y ? mouseY < y + height ? height - mouseY + y : 0 : height - 1;
			if(curTool == Tools.Rectangle) {
				if(prevMouseX > curMouseX) {
					prevMouseX = prevMouseX + curMouseX;
					curMouseX = prevMouseX - curMouseX;
					prevMouseX = prevMouseX - curMouseX;
				}
				if(prevMouseY > curMouseY) {
					prevMouseY = prevMouseY + curMouseY;
					curMouseY = prevMouseY - curMouseY;
					prevMouseY = prevMouseY - curMouseY;
				}
				int normalX1 = (int) ((float)prevMouseX / width * photoWidth);
				int normalY1 = (int) ((float)prevMouseY / height * photoHeight);
				int normalX2 = (int) ((float)curMouseX / width * photoWidth);
				if(curMouseX == width - 1)
					normalX2 = photoWidth - 1;
				int normalY2 = (int) ((float)curMouseY / height * photoHeight);
				if(curMouseY == height - 1)
					normalY2 = photoHeight - 1;
				for(int i = normalX1; i <= normalX2; i++)
					for(int j = normalY1; j <= normalY2; j++) {
						imageBuffer.put(j * photoWidth * 4 + i * 4, (byte)red);
						imageBuffer.put(j * photoWidth * 4 + i * 4 + 1, (byte)green);
						imageBuffer.put(j * photoWidth * 4 + i * 4 + 2, (byte)blue);
						imageBuffer.put(j * photoWidth * 4 + i * 4 + 3, (byte)255);
					}
			}
		}
		prevMouseX = -1;
		prevMouseY = -1;
	}

	public void applyTool(int x, int y, int clickedMouseButton) {
		int normalX = (int) ((float)x / width * photoWidth);
		int normalY = (int) ((float)y / height * photoHeight);
		switch(curTool) {
		case Brush:
			for(int i = (normalX >= size ? normalX - size : 0); i <= ((normalX + size < photoWidth) ? normalX + size : photoWidth - 1); i++)
				for(int j = (normalY >= size ? normalY - size : 0); j <= ((normalY + size < photoHeight) ? normalY + size : photoHeight - 1); j++) {
					imageBuffer.put(j * photoWidth * 4 + i * 4, (byte)red);
					imageBuffer.put(j * photoWidth * 4 + i * 4 + 1, (byte)green);
					imageBuffer.put(j * photoWidth * 4 + i * 4 + 2, (byte)blue);
					imageBuffer.put(j * photoWidth * 4 + i * 4 + 3, (byte)255);
				}
			break;
		case Eraser:
			for(int i = (normalX >= size ? normalX - size : 0); i <= ((normalX + size < photoWidth) ? normalX + size : photoWidth - 1); i++)
				for(int j = (normalY >= size ? normalY - size : 0); j <= ((normalY + size < photoHeight) ? normalY + size : photoHeight - 1); j++) {
					imageBuffer.put(j * photoWidth * 4 + i * 4, (byte)0);
					imageBuffer.put(j * photoWidth * 4 + i * 4 + 1, (byte)0);
					imageBuffer.put(j * photoWidth * 4 + i * 4 + 2, (byte)0);
					imageBuffer.put(j * photoWidth * 4 + i * 4 + 3, (byte)0);
				}
			break;
		}
	}

	public boolean isMouseOver(int mouseX, int mouseY) {
		return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
	}

	public boolean isEnabled() {
		return isEnabled;
	}
}
