package com.andreid278.shootit.client.gui;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;

public abstract class GuiList extends GuiListExtended {
	public int selectedSlot = -1;
	public String headerName;

	public GuiList(Minecraft mcIn, int left, int top, int widthIn, int heightIn, int slotHeightIn, String header) {
		super(mcIn, widthIn, heightIn, top, top + heightIn, slotHeightIn);
		selectedSlot = -1;
		this.left = left;
		this.right = left + width;
		initialClickY = -1;
		headerName = header;
		if(header == "")
			setHasListHeader(false, 0);
		else
			setHasListHeader(true, slotHeightIn);
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public boolean getVisible() {
		return this.visible;
	}

	@Override
	public IGuiListEntry getListEntry(int index) {
		return null;
	}

	@Override
	protected int getSize() {
		return 0;
	}

	@Override
	public int getListWidth() {
		return width;
	}

	@Override
	protected boolean isSelected(int slotIndex) {
		return slotIndex == selectedSlot;
	}

	@Override
	protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
		selectedSlot = slotIndex;
	}

	@Override
	protected int getScrollBarX() {
		return width + left;
	}

	@Override
	public int getMaxScroll() {
		return Math.max(0, getSize() - height / slotHeight);
	}

	public abstract void slotClicked(int slot);

	@Override
	public void handleMouseInput() {
		if(this.isMouseYWithinSlotBounds(this.mouseY) || mouseX >= getScrollBarX() && mouseX <= getScrollBarX() + 6) {
			if(Mouse.getEventButton() == 0 && Mouse.getEventButtonState() && this.mouseY >= this.top && this.mouseY <= this.bottom) {
				int i = (this.width - this.getListWidth()) / 2 + left;
				int j = (this.width + this.getListWidth()) / 2 + left;
				int k = this.mouseY - this.top;
				int l = k / this.slotHeight + (int)amountScrolled;

				if (l < this.getSize() && this.mouseX >= i && this.mouseX < j && l >= 0 && k >= 0) {
					this.elementClicked(l, false, this.mouseX, this.mouseY);
					this.selectedElement = l;
					slotClicked(selectedElement);
				}
			}

			if(Mouse.isButtonDown(0) && this.getEnabled()) {
				boolean flag1 = true;
				int j2 = (this.width - this.getListWidth()) / 2 + left;
				int k2 = (this.width + this.getListWidth()) / 2 + left;
				int l2 = this.mouseY - this.top;
				int i1 = l2 / this.slotHeight + (int)amountScrolled;

				if (i1 < this.getSize() && this.mouseX >= j2 && this.mouseX < k2 && i1 >= 0 && l2 >= 0 && initialClickY == -1) {
					boolean flag = i1 == this.selectedElement && Minecraft.getSystemTime() - this.lastClicked < 250L;
					this.elementClicked(i1, flag, this.mouseX, this.mouseY);
					this.selectedElement = i1;
					slotClicked(selectedElement);
					this.lastClicked = Minecraft.getSystemTime();
				}
				else if(this.mouseX >= j2 && this.mouseX <= k2 && l2 < 0 && initialClickY == -1) {
					flag1 = false;
				}

				int i3 = this.getScrollBarX();
				int j1 = i3 + 6;

				if((this.mouseX >= i3 && this.mouseX <= j1 && l2 >= 0 && flag1 || initialClickY != -1) && getMaxScroll() > 0) {
					if(initialClickY == -1)
						initialClickY = mouseY;
					if((mouseY - initialClickY) * (getMaxScroll() + 1) / height != 0) {
						this.amountScrolled += (mouseY - initialClickY) * (getMaxScroll() + 1) / height;
						this.bindAmountScrolled();
						initialClickY = mouseY;
					}
				}
				else {
					this.initialClickY = -1;
				}
			}
			else {
				this.initialClickY = -1;
			}

			int i2 = Mouse.getEventDWheel();

			if (i2 != 0 && getMaxScroll() > 0) {
				if (i2 > 0) {
					i2 = -1;
				}
				else if (i2 < 0) {
					i2 = 1;
				}

				this.amountScrolled += i2;
			}
		}
	}

	@Override
	protected void drawContainerBackground(Tessellator tessellator) {
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableTexture2D();
		GlStateManager.glLineWidth(2);
		vertexbuffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
		vertexbuffer.pos((double)right, (double)top, 0).color(128, 128, 128, 255).endVertex();
		vertexbuffer.pos((double)left, (double)top, 0).color(128, 128, 128, 255).endVertex();
		vertexbuffer.pos((double)left, (double)bottom, 0).color(128, 128, 128, 255).endVertex();
		vertexbuffer.pos((double)right, (double)bottom, 0).color(128, 128, 128, 255).endVertex();
		tessellator.draw();
		GlStateManager.glLineWidth(1);
		GlStateManager.enableTexture2D();
	}

	@Override
	protected void drawSelectionBox(int insideLeft, int insideTop, int mouseXIn, int mouseYIn, float partialTicks)
	{
		int i = this.getSize();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();

		for (int j = (int)amountScrolled; j < (int)amountScrolled + height / slotHeight && j < getSize(); ++j)
		{
			int k = insideTop + (j - (int)amountScrolled) * this.slotHeight;
			int l = this.slotHeight - 4;

			if (k > this.bottom || k + l < this.top)
			{
				this.updateItemPos(j, insideLeft, k, partialTicks);
			}

			this.drawSlot(j, insideLeft + 2, k - 4, this.slotHeight, mouseXIn, mouseYIn, partialTicks);

			if (this.showSelectionBox && this.isSelected(j))
			{
				int i1 = this.left + (this.width / 2 - this.getListWidth() / 2);
				int j1 = this.left + this.width / 2 + this.getListWidth() / 2;
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.disableTexture2D();
				GlStateManager.glLineWidth(2);
				vertexbuffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
				vertexbuffer.pos((double)i1 + 1, (double)(k + l - 1), 0.0D).color(128, 128, 128, 255).endVertex();
				vertexbuffer.pos((double)j1 - 1, (double)(k + l - 1), 0.0D).color(128, 128, 128, 255).endVertex();
				vertexbuffer.pos((double)j1 - 1, (double)(k - 4 + 1), 0.0D).color(128, 128, 128, 255).endVertex();
				vertexbuffer.pos((double)i1 + 1, (double)(k - 4 + 1), 0.0D).color(128, 128, 128, 255).endVertex();
				tessellator.draw();
				GlStateManager.glLineWidth(1);
				GlStateManager.enableTexture2D();
			}
		}
	}

	@Override
	public void drawScreen(int mouseXIn, int mouseYIn, float partialTicks)
	{
		if (this.visible)
		{
			this.mouseX = mouseXIn;
			this.mouseY = mouseYIn;
			this.drawBackground();
			int i = this.getScrollBarX();
			int j = i + 6;
			this.bindAmountScrolled();
			GlStateManager.disableLighting();
			GlStateManager.disableFog();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder vertexbuffer = tessellator.getBuffer();
			// Forge: background rendering moved into separate method.
			this.drawContainerBackground(tessellator);
			int k = this.left + this.width / 2 - this.getListWidth() / 2;
			int l = this.top + 4;

			if (this.hasListHeader)
			{
				this.drawListHeader(k, l, tessellator);
			}

			this.drawSelectionBox(k, l, mouseXIn, mouseYIn, partialTicks);

			GlStateManager.disableDepth();
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
			GlStateManager.disableAlpha();
			GlStateManager.shadeModel(7425);
			GlStateManager.disableTexture2D();

			int j1 = this.getMaxScroll();

			if (j1 > 0)
			{
				double l1 = amountScrolled / (getMaxScroll() + 1) * height + top;
				double k1 = height / (getMaxScroll() + 1);

				vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
				vertexbuffer.pos((double)i, (double)this.bottom, 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
				vertexbuffer.pos((double)j, (double)this.bottom, 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
				vertexbuffer.pos((double)j, (double)this.top, 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 255).endVertex();
				vertexbuffer.pos((double)i, (double)this.top, 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
				tessellator.draw();
				vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
				vertexbuffer.pos((double)i, (double)(l1 + k1), 0.0D).tex(0.0D, 1.0D).color(128, 128, 128, 255).endVertex();
				vertexbuffer.pos((double)j, (double)(l1 + k1), 0.0D).tex(1.0D, 1.0D).color(128, 128, 128, 255).endVertex();
				vertexbuffer.pos((double)j, (double)l1, 0.0D).tex(1.0D, 0.0D).color(128, 128, 128, 255).endVertex();
				vertexbuffer.pos((double)i, (double)l1, 0.0D).tex(0.0D, 0.0D).color(128, 128, 128, 255).endVertex();
				tessellator.draw();
				vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
				vertexbuffer.pos((double)i, (double)(l1 + k1 - 1), 0.0D).tex(0.0D, 1.0D).color(192, 192, 192, 255).endVertex();
				vertexbuffer.pos((double)(j - 1), (double)(l1 + k1 - 1), 0.0D).tex(1.0D, 1.0D).color(192, 192, 192, 255).endVertex();
				vertexbuffer.pos((double)(j - 1), (double)l1, 0.0D).tex(1.0D, 0.0D).color(192, 192, 192, 255).endVertex();
				vertexbuffer.pos((double)i, (double)l1, 0.0D).tex(0.0D, 0.0D).color(192, 192, 192, 255).endVertex();
				tessellator.draw();
			}

			this.renderDecorations(mouseXIn, mouseYIn);
			GlStateManager.enableTexture2D();
			GlStateManager.shadeModel(7424);
			GlStateManager.enableAlpha();
			GlStateManager.disableBlend();
		}
	}

	@Override
	protected void drawListHeader(int insideLeft, int insideTop, Tessellator tessellatorIn) {
		mc.fontRenderer.drawString(headerName, insideLeft, insideTop - headerPadding, 16711680, false);
	}
}
