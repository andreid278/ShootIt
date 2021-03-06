package com.andreid278.shootit.client.gui;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.andreid278.shootit.ShootIt;
import com.andreid278.shootit.client.Resources;
import com.andreid278.shootit.common.MCData;
import com.andreid278.shootit.common.PhotosData;
import com.andreid278.shootit.common.container.PainterContainer;
import com.andreid278.shootit.common.item.MemoryCard;
import com.andreid278.shootit.common.network.MessagePainterToServer;
import com.andreid278.shootit.common.network.MessageRequestForNextPhotoID;
import com.andreid278.shootit.common.network.MessageRequestForPhoto;
import com.andreid278.shootit.common.tiileentity.TEPainter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class PainterGui extends GuiContainer {
	List<String> slotsText = Arrays.asList();
	GuiPhotoEditor photoEditor;
	GuiToolsList toolsList;
	GuiScrollerEditor size;
	GuiScrollerEditor red;
	GuiScrollerEditor green;
	GuiScrollerEditor blue;
	int xS = 176;
	int yS = 200;

	public PainterGui(Container inventorySlotsIn) {
		super(inventorySlotsIn);
	}

	public PainterGui(InventoryPlayer inventory, TEPainter te) {
		super(new PainterContainer(inventory, te));
		this.xSize = 176;
		this.ySize = 200;
		this.width = Minecraft.getMinecraft().displayWidth;
		this.height = Minecraft.getMinecraft().displayHeight;
	}

	public void initGui() {
		super.initGui();
		this.guiLeft = 10;
		this.guiTop = (height - yS) / 2;
		this.buttonList.clear();
		this.buttonList.add(new TrueButtonGui(0, 27 + guiLeft, 90 + guiTop, 38, 22, "<-Load"));
		this.buttonList.add(new TrueButtonGui(1, 110 + guiLeft, 90 + guiTop, 38, 22, "Save->"));
		this.buttonList.add(new TrueButtonGui(2, 70 + guiLeft, 90 + guiTop, 15, 22, "<-"));
		this.buttonList.add(new TrueButtonGui(3, 90 + guiLeft, 90 + guiTop, 15, 22, "->"));
		this.buttonList.add(new TrueButtonGui(4, 160 + guiLeft, 10 + guiTop, 10, 10, "<"));
		int w = width - 30 - xS;
		int h = height - 20;
		if(w < h * 800 / 600)
			h = w * 600 / 800;
		else w = h * 800 / 600;
		photoEditor = new GuiPhotoEditor(20 + xS + (width - 30 - xS) / 2 - w / 2, height / 2 - h / 2, w, h, width, height);
		this.xSize = 20 + xS + (width - 30 - xS) / 2 - w / 2 + w - guiLeft;
		toolsList = new GuiToolsList(this, mc, guiLeft + 8, guiTop + 23, 72, 60, 15, "Tools");
		size = new GuiScrollerEditor(guiLeft + 100, guiTop + 23, 8, 60, 1, 50, 1, 1, true, 0x000000, 0x808080);
		red = new GuiScrollerEditor(guiLeft + 120, guiTop + 23, 6, 60, 0, 255, 1, 0, true, 0x000000, 0x808080);
		green = new GuiScrollerEditor(guiLeft + 134, guiTop + 23, 6, 60, 0, 255, 1, 0, true, 0x000000, 0x808080);
		blue = new GuiScrollerEditor(guiLeft + 148, guiTop + 23, 6, 60, 0, 255, 1, 0, true, 0x000000, 0x808080);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		TEPainter te = ((PainterContainer)inventorySlots).te;
		mc.getTextureManager().bindTexture(Resources.PAINTER_GUI);
		int x = 10;
		int y = (height - yS) / 2;
		drawTexturedModalRect(x, y, 0, 0, xS, yS, 1, 1);
		ItemStack itemStack = inventorySlots.getSlot(36).getStack();
		if(!itemStack.isEmpty()) {
			if(itemStack.getItem() instanceof MemoryCard)
				if(itemStack.hasTagCompound()) {
					int[] array = itemStack.getTagCompound().getIntArray("indexes");
					if(array.length > 0) {
						int curIndex = te.curPhoto;
						if(curIndex < array.length) {
							if(PhotosData.photos.containsKey(array[curIndex]))
								photoEditor.photoID = array[curIndex];
							else {
								if(MCData.imageIDToLoadFromServer == 0 && !mc.isSingleplayer()) {
									MCData.imageIDToLoadFromServer = array[curIndex];
									ShootIt.network.sendToServer(new MessageRequestForPhoto(array[curIndex]));
								}
								else if(Minecraft.getMinecraft().isSingleplayer()) {
									File file = new File(MCData.photosFolderPathClient + "/" + array[curIndex] + ".png");
									if(file.exists())
										PhotosData.addPhoto(array[curIndex]);
									else PhotosData.addEmptyPhoto(array[curIndex]);
								}
								photoEditor.photoID = -1;
							}
						}
					}
					else {
						photoEditor.photoID = -1;
					}
				}
		}
		else {
			photoEditor.photoID = -1;
		}
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		photoEditor.draw(mc, mouseX, mouseY);
		toolsList.drawScreen(mouseX, mouseY, partialTicks);
		size.draw(mc, mouseX, mouseY);
		red.draw(mc, mouseX, mouseY);
		green.draw(mc, mouseX, mouseY);
		blue.draw(mc, mouseX, mouseY);
		int r = (int)red.curValue << 16;
		int g = (int)green.curValue << 8;
		int b = (int)blue.curValue;
		drawCenteredStringWithoutShadow(mc.fontRenderer, "Color", guiLeft + 134, guiTop + 10, r + g + b);
		drawCenteredStringWithoutShadow(mc.fontRenderer, "Size", guiLeft + 100, guiTop + 10, 0);
	}

	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		for(int i = 0; i < 5; i++)
			if(buttonList.get(i).isMouseOver()) {
				switch(i) {
				case 0:
					drawHoveringText(Arrays.asList("Choose an image from the left memory card", "and press this button to start editing"), mouseX - guiLeft, mouseY - guiTop);
					break;
				case 1:
					drawHoveringText(Arrays.asList("Press this button to save", "an image to the right memory card"), mouseX - guiLeft, mouseY - guiTop);
					break;
				case 2:
					drawHoveringText(Arrays.asList("Previous image"), mouseX - guiLeft, mouseY - guiTop);
					break;
				case 3:
					drawHoveringText(Arrays.asList("Next image"), mouseX - guiLeft, mouseY - guiTop);
					break;
				case 4:
					drawHoveringText(Arrays.asList("Undo"), mouseX - guiLeft, mouseY - guiTop);
					break;
				}
			}
		if(photoEditor.isMouseOver(mouseX, mouseY))
			if(!photoEditor.isEnabled())
				drawHoveringText(Arrays.asList("Choose an image and press the '<-Load' button", TextFormatting.RED + "Warning:" + TextFormatting.RESET + " after closing GUI all unsaved changes will be lost"), mouseX - guiLeft, mouseY - guiTop);
	}

	public void drawTexturedModalRect(int x, int y, double textureX, double textureY, int width, int height, double textureWidth, double textureHeight) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos((double)(x + 0), (double)(y + height), (double)this.zLevel).tex(textureX, textureY + textureHeight).endVertex();
		vertexbuffer.pos((double)(x + width), (double)(y + height), (double)this.zLevel).tex(textureX + textureWidth, textureY + textureHeight).endVertex();
		vertexbuffer.pos((double)(x + width), (double)(y + 0), (double)this.zLevel).tex(textureX + textureWidth, textureY).endVertex();
		vertexbuffer.pos((double)(x + 0), (double)(y + 0), (double)this.zLevel).tex(textureX, textureY).endVertex();
		tessellator.draw();
	}

	public void drawCenteredStringWithoutShadow(FontRenderer fontRendererIn, String text, int x, int y, int color) {
		fontRendererIn.drawString(text, (float)(x - fontRendererIn.getStringWidth(text) / 2), (float)y, color, false);
	}

	protected void actionPerformed(GuiButton button) throws IOException {
		switch (button.id) {
		case 0:
			photoEditor.setEnabled(true);
			break;
		case 1:
			if(MCData.imageIDToLoadToServer == 0 && !(((PainterContainer)inventorySlots).te).inventory[1].isEmpty()) {
				photoEditor.save();
				File file = new File(MCData.photosFolderPathClient + "/0.png");
				if(file.exists()) {
					ShootIt.network.sendToServer(new MessageRequestForNextPhotoID(1, ((PainterContainer)inventorySlots).te.getPos()));
				}
			}
			break;
		case 2: {
			photoEditor.setEnabled(false);
			ItemStack itemStack = inventorySlots.getSlot(36).getStack();
			if(itemStack.getItem() instanceof MemoryCard)
				if(itemStack.hasTagCompound()) {
					ShootIt.network.sendToServer(new MessagePainterToServer((byte)0, false, ((PainterContainer)inventorySlots).te.getPos()));
				}
			break;
		}
		case 3: {
			photoEditor.setEnabled(false);
			ItemStack itemStack = inventorySlots.getSlot(36).getStack();
			if(itemStack.getItem() instanceof MemoryCard)
				if(itemStack.hasTagCompound()) {
					ShootIt.network.sendToServer(new MessagePainterToServer((byte)0, true, ((PainterContainer)inventorySlots).te.getPos()));
				}
			break;
		}
		case 4:
			photoEditor.restore();
			break;
		}
	}

	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		photoEditor.mouseClicked(mouseX, mouseY, mouseButton);
		toolsList.mouseClicked(mouseX, mouseY, mouseButton);
		size.mouseClicked(mouseX, mouseY, mouseButton);
		red.mouseClicked(mouseX, mouseY, mouseButton);
		green.mouseClicked(mouseX, mouseY, mouseButton);
		blue.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		photoEditor.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		size.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		red.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		green.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		blue.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		photoEditor.size = (int)size.curValue;
		photoEditor.red = (int)red.curValue;
		photoEditor.green = (int)green.curValue;
		photoEditor.blue = (int)blue.curValue;
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		toolsList.mouseReleased(mouseX, mouseY, state);
		photoEditor.mouseReleased(mouseX, mouseY, state);
		size.mouseReleased(mouseX, mouseY, state);
		red.mouseReleased(mouseX, mouseY, state);
		green.mouseReleased(mouseX, mouseY, state);
		blue.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		toolsList.handleMouseInput();
		size.handleMouseInput();
		red.handleMouseInput();
		green.handleMouseInput();
		blue.handleMouseInput();
		photoEditor.red = (int)red.curValue;
		photoEditor.green = (int)green.curValue;
		photoEditor.blue = (int)blue.curValue;
	}
}
