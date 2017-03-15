package com.andreid278.shootit.Gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.andreid278.shootit.Main;
import com.andreid278.shootit.Misc.Statics;
import com.andreid278.shootit.Misc.Statics.ShaderInfo;
import com.andreid278.shootit.Network.MessageCameraToServer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.math.MathHelper;

public class GuiShadersList extends GuiListExtended {
	public CameraGui owner;
	public List<GuiShadersListEntry> list = new ArrayList<>();
	public int selectedSlot = -1;

	public GuiShadersList(CameraGui owner, Minecraft mcIn, int left, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
		super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
		this.owner = owner;
//		list.add(new GuiShadersListEntry(owner, "aaaaaaa"));
//		list.add(new GuiShadersListEntry(owner, "bbbbbbb"));
//		list.add(new GuiShadersListEntry(owner, "ccccccc"));
//		list.add(new GuiShadersListEntry(owner, "ddddddd"));
//		list.add(new GuiShadersListEntry(owner, "eeeeeee"));
//		list.add(new GuiShadersListEntry(owner, "fffffff"));
//		list.add(new GuiShadersListEntry(owner, "ggggggg"));
//		list.add(new GuiShadersListEntry(owner, "hhhhhhh"));
//		list.add(new GuiShadersListEntry(owner, "iiiiiii"));
//		list.add(new GuiShadersListEntry(owner, "jjjjjjj"));
//		list.add(new GuiShadersListEntry(owner, "lllllll"));
//		list.add(new GuiShadersListEntry(owner, "kkkkkkk"));
//		list.add(new GuiShadersListEntry(owner, "mmmmmmm"));
//		list.add(new GuiShadersListEntry(owner, "nnnnnnn"));
//		list.add(new GuiShadersListEntry(owner, "ooooooo"));
//		list.add(new GuiShadersListEntry(owner, "ppppppp"));
		for(ShaderInfo shader : Statics.shaders)
			list.add(new GuiShadersListEntry(owner, shader.name));
		selectedSlot = owner.curShader;
		this.left = left;
		this.right = left + width;
		initialClickY = -1;
		setHasListHeader(true, 15);
	}

	@Override
	public IGuiListEntry getListEntry(int index) {
		return list.get(index);
	}

	@Override
	protected int getSize() {
		return list.size();
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

	@Override
	public void handleMouseInput() {
		if(this.isMouseYWithinSlotBounds(this.mouseY) || mouseX >= getScrollBarX() && mouseX <= getScrollBarX() + 6)
		{
			if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState() && this.mouseY >= this.top && this.mouseY <= this.bottom)
			{
				int i = (this.width - this.getListWidth()) / 2 + left;
				int j = (this.width + this.getListWidth()) / 2 + left;
				int k = this.mouseY - this.top;
				int l = k / this.slotHeight + (int)amountScrolled;

				if (l < this.getSize() && this.mouseX >= i && this.mouseX < j && l >= 0 && k >= 0)
				{
					this.elementClicked(l, false, this.mouseX, this.mouseY);
					this.selectedElement = l;
					owner.curShader = selectedElement;
					Main.network.sendToServer(new MessageCameraToServer((byte)3, selectedElement));
				}
			}

			if (Mouse.isButtonDown(0) && this.getEnabled())
			{
				boolean flag1 = true;
				int j2 = (this.width - this.getListWidth()) / 2 + left;
				int k2 = (this.width + this.getListWidth()) / 2 + left;
				int l2 = this.mouseY - this.top;
				int i1 = l2 / this.slotHeight + (int)amountScrolled;

				if (i1 < this.getSize() && this.mouseX >= j2 && this.mouseX < k2 && i1 >= 0 && l2 >= 0 && initialClickY == -1)
				{
					boolean flag = i1 == this.selectedElement && Minecraft.getSystemTime() - this.lastClicked < 250L;
					this.elementClicked(i1, flag, this.mouseX, this.mouseY);
					this.selectedElement = i1;
					owner.curShader = selectedElement;
					Main.network.sendToServer(new MessageCameraToServer((byte)3, selectedElement));
					this.lastClicked = Minecraft.getSystemTime();
				}
				else if(this.mouseX >= j2 && this.mouseX <= k2 && l2 < 0 && initialClickY == -1)
				{
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
			else
			{
				this.initialClickY = -1;
			}

			int i2 = Mouse.getEventDWheel();

			if (i2 != 0 && getMaxScroll() > 0)
			{
				if (i2 > 0)
				{
					i2 = -1;
				}
				else if (i2 < 0)
				{
					i2 = 1;
				}

				this.amountScrolled += i2;
			}
		}
	}

	@Override
	protected void drawContainerBackground(Tessellator tessellator) {
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
		vertexbuffer.pos((double)right, (double)top, 0).color(128, 128, 128, 255).endVertex();
		vertexbuffer.pos((double)left, (double)top, 0).color(128, 128, 128, 255).endVertex();
		vertexbuffer.pos((double)left, (double)bottom, 0).color(128, 128, 128, 255).endVertex();
		vertexbuffer.pos((double)right, (double)bottom, 0).color(128, 128, 128, 255).endVertex();
		tessellator.draw();
	}
	
	protected void drawSelectionBox(int insideLeft, int insideTop, int mouseXIn, int mouseYIn)
	{
		int i = this.getSize();
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();

		for (int j = (int)amountScrolled; j < (int)amountScrolled + height / slotHeight && j < getSize(); ++j)
		{
			int k = insideTop + (j - (int)amountScrolled) * this.slotHeight;
			int l = this.slotHeight - 4;

			if (k > this.bottom || k + l < this.top)
			{
				this.updateItemPos(j, insideLeft, k);
			}

			if (this.showSelectionBox && this.isSelected(j))
			{
				int i1 = this.left + (this.width / 2 - this.getListWidth() / 2);
				int j1 = this.left + this.width / 2 + this.getListWidth() / 2;
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.disableTexture2D();
				GlStateManager.glLineWidth(2);
				vertexbuffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
				vertexbuffer.pos((double)i1, (double)(k + l), 0.0D).color(128, 128, 128, 255).endVertex();
				vertexbuffer.pos((double)j1 - 1, (double)(k + l), 0.0D).color(128, 128, 128, 255).endVertex();
				vertexbuffer.pos((double)j1 - 1, (double)(k - 4), 0.0D).color(128, 128, 128, 255).endVertex();
				vertexbuffer.pos((double)i1, (double)(k - 4), 0.0D).color(128, 128, 128, 255).endVertex();
				tessellator.draw();
				GlStateManager.glLineWidth(1);
				GlStateManager.enableTexture2D();
			}

			this.drawSlot(j, insideLeft, k, l, mouseXIn, mouseYIn);
		}
	}

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
			VertexBuffer vertexbuffer = tessellator.getBuffer();
			// Forge: background rendering moved into separate method.
			this.drawContainerBackground(tessellator);
			int k = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
			int l = this.top + 4;

			if (this.hasListHeader)
			{
				this.drawListHeader(k, l, tessellator);
			}

			this.drawSelectionBox(k, l, mouseXIn, mouseYIn);
			
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
		mc.fontRendererObj.drawString("Filters", insideLeft, insideTop - headerPadding, 16711680, false);
	}
}
