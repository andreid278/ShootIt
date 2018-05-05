package com.andreid278.shootit.client.gui;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;

public class GuiScrollerEditor extends Gui {
	private int x;
	private int y;
	private int width;
	private int height;
	float min;
	float max;
	float step;
	float curValue;
	int curMouseX;
	int curMouseY;
	boolean isVertical;
	boolean isChanged;
	int sColorX, sColorY, sColorZ;
	int bColorX, bColorY, bColorZ;
	boolean isDragging;

	public GuiScrollerEditor(int x, int y, int width, int height, float min, float max, float step, float initValue, boolean isVertical, int scrollerColor, int backColor) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.min = min;
		this.max = max;
		this.step = step;
		this.curValue = initValue;
		curMouseX = 0;
		curMouseY = 0;
		this.isVertical = isVertical;
		sColorX = (scrollerColor >> 16) & 0xff;
		sColorY = (scrollerColor >> 8) & 0xff;
		sColorZ = (scrollerColor >> 0) & 0xff;
		bColorX = (backColor >> 16) & 0xff;
		bColorY = (backColor >> 8) & 0xff;
		bColorZ = (backColor >> 0) & 0xff;
		isDragging = false;
	}

	public void draw(Minecraft mc, int mouseX, int mouseY) {
		curMouseX = mouseX;
		curMouseY = mouseY;

		double scrollerMin = (double)(isVertical ? height : width) * (curValue - min) / (max - min) - 2;
		double scrollerMax = scrollerMin + 4;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		GlStateManager.disableTexture2D();
		vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		if(isVertical) {
			vertexbuffer.pos(x + width / 4, y, 0).color(bColorX, bColorY, bColorZ, 255).endVertex();
			vertexbuffer.pos(x - width / 4, y, 0).color(bColorX, bColorY, bColorZ, 255).endVertex();
			vertexbuffer.pos(x - width / 4, y + height, 0).color(bColorX, bColorY, bColorZ, 255).endVertex();
			vertexbuffer.pos(x + width / 4, y + height, 0).color(bColorX, bColorY, bColorZ, 255).endVertex();
			vertexbuffer.pos(x + width / 2, y + scrollerMin, 0).color(sColorX, sColorY, sColorZ, 255).endVertex();
			vertexbuffer.pos(x - width / 2, y + scrollerMin, 0).color(sColorX, sColorY, sColorZ, 255).endVertex();
			vertexbuffer.pos(x - width / 2, y + scrollerMax, 0).color(sColorX, sColorY, sColorZ, 255).endVertex();
			vertexbuffer.pos(x + width / 2, y + scrollerMax, 0).color(sColorX, sColorY, sColorZ, 255).endVertex();
		}
		else {
			vertexbuffer.pos(x, y - height / 4, 0).color(bColorX, bColorY, bColorZ, 255).endVertex();
			vertexbuffer.pos(x, y + height / 4, 0).color(bColorX, bColorY, bColorZ, 255).endVertex();
			vertexbuffer.pos(x + width, y + height / 4, 0).color(bColorX, bColorY, bColorZ, 255).endVertex();
			vertexbuffer.pos(x + width, y - height / 4, 0).color(bColorX, bColorY, bColorZ, 255).endVertex();
			vertexbuffer.pos(x + scrollerMin, y - height / 2, 0).color(sColorX, sColorY, sColorZ, 255).endVertex();
			vertexbuffer.pos(x + scrollerMin, y + height / 2, 0).color(sColorX, sColorY, sColorZ, 255).endVertex();
			vertexbuffer.pos(x + scrollerMax, y + height / 2, 0).color(sColorX, sColorY, sColorZ, 255).endVertex();
			vertexbuffer.pos(x + scrollerMax, y - height / 2, 0).color(sColorX, sColorY, sColorZ, 255).endVertex();
		}
		tessellator.draw();
		GlStateManager.enableTexture2D();

	}

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if(isMouseOverGui(mouseX, mouseY)) {
			isDragging = true;
		}
	}

	public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		if(!isDragging)
			return;
		if(isVertical) {
			curValue = ((float)(mouseY - y) / (height) * (max - min) + min);
		}
		else {
			curValue = ((float)(mouseX - x) / (width) * (max - min) + min);
		}
		
		curValue = (int)((curValue + step / 2 - min) / step) * step + min;

		if(curValue < min)
			curValue = min;
		if(curValue > max)
			curValue = max;
		
		markDirty();
	}
	
	public void mouseReleased(int mouseX, int mouseY, int state) {
		isDragging = false;
	}

	public void handleMouseInput() {
		if(!isMouseOverGui(curMouseX, curMouseY))
			return;
		int wheel = Mouse.getEventDWheel();
		if (wheel != 0) {
			if (wheel > 0) {
				wheel = -1;
			}
			else if (wheel < 0) {
				wheel = 1;
			}
			curValue += wheel * step;
			if(curValue < min)
				curValue = min;
			if(curValue > max)
				curValue = max;

			markDirty();
		}
	}

	public boolean isMouseOverGui(int mouseX, int mouseY) {
		return isVertical ? mouseX >= x - width / 2 && mouseX <= x + width / 2 && mouseY >= y - 2 && mouseY <= y + height + 2
				: mouseX >= x - 2 && mouseX <= x + width + 2 && mouseY >= y - height / 2 && mouseY <= y + height / 2;
	}

	public void markDirty() {
		isChanged = true;
	}
}
