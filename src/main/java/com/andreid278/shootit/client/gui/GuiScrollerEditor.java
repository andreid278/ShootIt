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
	int min;
	int max;
	int step;
	int curValue;
	int curMouseX;
	int curMouseY;

	public GuiScrollerEditor(int x, int y, int width, int height, int min, int max, int step) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.min = min;
		this.max = max;
		this.step = step;
		this.curValue = min;
		curMouseX = 0;
		curMouseY = 0;
	}

	public void draw(Minecraft mc, int mouseX, int mouseY) {
		curMouseX = mouseX;
		curMouseY = mouseY;
		double scrollerMin = (double)height * (curValue - min) / (max - min);
		double scrollerMax = (double)height * (curValue - min + step) / (max - min);
		if(scrollerMax - scrollerMin < 3) {
			scrollerMin = (scrollerMax + scrollerMin) / 2 - 1;
			scrollerMax = scrollerMin + 2;
		}
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		GlStateManager.disableTexture2D();
		vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		vertexbuffer.pos(x + width / 4, y, 0).color(128, 128, 128, 255).endVertex();
		vertexbuffer.pos(x - width / 4, y, 0).color(128, 128, 128, 255).endVertex();
		vertexbuffer.pos(x - width / 4, y + height, 0).color(128, 128, 128, 255).endVertex();
		vertexbuffer.pos(x + width / 4, y + height, 0).color(128, 128, 128, 255).endVertex();
		vertexbuffer.pos(x + width / 2, y + scrollerMin, 0).color(0, 0, 0, 255).endVertex();
		vertexbuffer.pos(x - width / 2, y + scrollerMin, 0).color(0, 0, 0, 255).endVertex();
		vertexbuffer.pos(x - width / 2, y + scrollerMax, 0).color(0, 0, 0, 255).endVertex();
		vertexbuffer.pos(x + width / 2, y + scrollerMax, 0).color(0, 0, 0, 255).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();

	}

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		
	}

	public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		if(!isMouseOverGui(mouseX, mouseY))
			return;
		curValue = (int) ((double)(mouseY - y) / height * (max - min) + min);
		if(curValue < min)
			curValue = min;
		if(curValue >= max)
			curValue = max - step;
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
			if(curValue >= max)
				curValue = max - step;
		}
	}
	
	public boolean isMouseOverGui(int mouseX, int mouseY) {
		return mouseX >= x - width / 2 && mouseX <= x + width / 2 && mouseY >= y - height && mouseY <= y + height;
	}
}
