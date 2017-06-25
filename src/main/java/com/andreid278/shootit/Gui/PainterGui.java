package com.andreid278.shootit.Gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.andreid278.shootit.Main;
import com.andreid278.shootit.Containers.PainterContainer;
import com.andreid278.shootit.Containers.PrinterContainer;
import com.andreid278.shootit.Items.MemoryCard;
import com.andreid278.shootit.Misc.Photos;
import com.andreid278.shootit.Misc.Statics;
import com.andreid278.shootit.Network.MessagePainterToServer;
import com.andreid278.shootit.Network.MessagePrinterToServer;
import com.andreid278.shootit.Network.MessageRequestForNextPhotoID;
import com.andreid278.shootit.Network.MessageRequestForPhoto;
import com.andreid278.shootit.TileEntities.TEPainter;
import com.andreid278.shootit.TileEntities.TEPrinter;
import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class PainterGui extends GuiContainer {
	List<String> slotsText = Arrays.asList();
	GuiPhotoEditor photoEditor;
	GuiToolsList toolsList;
	GuiScrollerEditor size;
	GuiScrollerEditor red;
	GuiScrollerEditor green;
	GuiScrollerEditor blue;

	public PainterGui(Container inventorySlotsIn) {
		super(inventorySlotsIn);
	}

	public PainterGui(InventoryPlayer inventory, TEPainter te) {
		super(new PainterContainer(inventory, te));
		this.xSize = 176;
		this.ySize = 200;
	}

	public void initGui() {
		super.initGui();
		this.guiLeft = 10;
		this.guiTop = (height - ySize) / 2;
		this.buttonList.clear();
		this.buttonList.add(new TrueButtonGui(0, 27 + guiLeft, 90 + guiTop, 38, 22, "<-Load"));
		this.buttonList.add(new TrueButtonGui(1, 110 + guiLeft, 90 + guiTop, 38, 22, "Save->"));
		this.buttonList.add(new TrueButtonGui(2, 70 + guiLeft, 90 + guiTop, 15, 22, "<-"));
		this.buttonList.add(new TrueButtonGui(3, 90 + guiLeft, 90 + guiTop, 15, 22, "->"));
		this.buttonList.add(new TrueButtonGui(4, 160 + guiLeft, 10 + guiTop, 10, 10, "<"));
		int w = width - 30 - xSize;
		int h = height - 20;
		if(w < h * 800 / 600)
			h = w * 600 / 800;
		else w = h * 800 / 600;
		photoEditor = new GuiPhotoEditor(20 + xSize + (width - 30 - xSize) / 2 - w / 2, height / 2 - h / 2, w, h, width, height);
		toolsList = new GuiToolsList(this, mc, guiLeft + 8, 72, 60, guiTop + 23, guiTop + 83, 15);
		size = new GuiScrollerEditor(guiLeft + 100, guiTop + 23, 8, 60, 1, 50, 1);
		red = new GuiScrollerEditor(guiLeft + 120, guiTop + 23, 6, 60, 0, 256, 1);
		green = new GuiScrollerEditor(guiLeft + 134, guiTop + 23, 6, 60, 0, 256, 1);
		blue = new GuiScrollerEditor(guiLeft + 148, guiTop + 23, 6, 60, 0, 256, 1);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		TEPainter te = ((PainterContainer)inventorySlots).te;
		mc.getTextureManager().bindTexture(Statics.PAINTER_GUI);
		int x = 10;
		int y = (height - ySize) / 2;
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize, 1, 1);
		ItemStack itemStack = inventorySlots.getSlot(36).getStack();
		if(itemStack != null) {
			if(itemStack.getItem() instanceof MemoryCard)
				if(itemStack.hasTagCompound()) {
					int[] array = itemStack.getTagCompound().getIntArray("indexes");
					if(array.length > 0) {
						int curIndex = te.curPhoto;
						if(curIndex < array.length) {
							if(Photos.photos.containsKey(array[curIndex]))
								photoEditor.photoID = array[curIndex];
							else {
								if(Statics.imageIDToLoadFromServer == 0 && !mc.isSingleplayer()) {
									Statics.imageIDToLoadFromServer = array[curIndex];
									Main.network.sendToServer(new MessageRequestForPhoto(array[curIndex]));
								}
								else if(Minecraft.getMinecraft().isSingleplayer()) {
									File file = new File(Statics.photosFolderPathClient + Statics.slash + array[curIndex] + ".png");
									if(file.exists())
										Photos.addPhoto(array[curIndex]);
									else Photos.addEmptyPhoto(array[curIndex]);
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
		int r = red.curValue << 16;
		int g = green.curValue << 8;
		int b = blue.curValue;
		drawCenteredStringWithoutShadow(mc.fontRendererObj, "Color", guiLeft + 134, guiTop + 10, r + g + b);
		drawCenteredStringWithoutShadow(mc.fontRendererObj, "Size", guiLeft + 100, guiTop + 10, 0);
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
		VertexBuffer vertexbuffer = tessellator.getBuffer();
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
			if(Statics.imageIDToLoadToServer == 0 && (((PainterContainer)inventorySlots).te).inventory[1] != null) {
				photoEditor.save();
				File file = new File(Statics.photosFolderPathClient + "/0.png");
				if(file.exists()) {
					Main.network.sendToServer(new MessageRequestForNextPhotoID(1, ((PainterContainer)inventorySlots).te.getPos()));
				}
			}
			break;
		case 2: {
			photoEditor.setEnabled(false);
			ItemStack itemStack = inventorySlots.getSlot(36).getStack();
			if(itemStack != null)
				if(itemStack.getItem() instanceof MemoryCard)
					if(itemStack.hasTagCompound()) {
						Main.network.sendToServer(new MessagePainterToServer((byte)0, false, ((PainterContainer)inventorySlots).te.getPos()));
					}
			break;
		}
		case 3: {
			photoEditor.setEnabled(false);
			ItemStack itemStack = inventorySlots.getSlot(36).getStack();
			if(itemStack != null)
				if(itemStack.getItem() instanceof MemoryCard)
					if(itemStack.hasTagCompound()) {
						Main.network.sendToServer(new MessagePainterToServer((byte)0, true, ((PainterContainer)inventorySlots).te.getPos()));
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
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		photoEditor.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		size.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		red.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		green.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		blue.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		photoEditor.size = size.curValue;
		photoEditor.red = red.curValue;
		photoEditor.green = green.curValue;
		photoEditor.blue = blue.curValue;
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		toolsList.mouseReleased(mouseX, mouseY, state);
		photoEditor.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		toolsList.handleMouseInput();
		size.handleMouseInput();
		red.handleMouseInput();
		green.handleMouseInput();
		blue.handleMouseInput();
		photoEditor.red = red.curValue;
		photoEditor.green = green.curValue;
		photoEditor.blue = blue.curValue;
	}
}
