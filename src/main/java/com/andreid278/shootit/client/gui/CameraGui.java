package com.andreid278.shootit.client.gui;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import com.andreid278.shootit.ShootIt;
import com.andreid278.shootit.client.Resources;
import com.andreid278.shootit.common.MCData;
import com.andreid278.shootit.common.PhotosData;
import com.andreid278.shootit.common.item.Camera;
import com.andreid278.shootit.common.network.MessageCameraToServer;
import com.andreid278.shootit.common.network.MessageDeletePhotoRequest;
import com.andreid278.shootit.common.network.MessageRequestForPhoto;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;

public class CameraGui extends GuiContainer implements IContainerListener {
	public GuiFiltersList filters;
	public int curShader;
	
	public int curPhoto;
	public int maxPhoto;

	public CameraGui(Container inventorySlotsIn) {
		super(inventorySlotsIn);
		xSize = 352;
		ySize = 210;
	}

	public void initGui() {
		super.initGui();

		this.inventorySlots.removeListener(this);
		this.inventorySlots.addListener(this);

		buttonList.add(new TrueButtonGui(0, width / 2 - 72, height / 2 - 10, 10, 20, "<"));
		buttonList.add(new TrueButtonGui(1, width / 2 + 150, height / 2 - 10, 10, 20, ">"));
		buttonList.add(new TrueButtonGui(2, width / 2 - 72, height / 2 - 90, 20, 20, Resources.FILTERS, 64, 192));
		buttonList.add(new TrueButtonGui(3, width / 2 + 140, height / 2 - 90, 20, 20, Resources.DELETE, 64, 192));

		curShader = 0;
		ItemStack item = mc.player.getHeldItemMainhand();
		if(item.getItem() instanceof Camera)
			if(item.hasTagCompound())
				curShader = item.getTagCompound().getInteger("shader");

		filters = new GuiFiltersList(this, mc, width / 2 - 50, height / 2 - 90, 100, 75, 15, "");
		filters.setVisible(false);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		this.mc.renderEngine.bindTexture(Resources.CAMERA_GUI);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize, 1, 1);

		ItemStack item = mc.player.getHeldItemMainhand();
		if(item.getItem() instanceof Camera) {
			if(item.hasTagCompound()) {
				curPhoto = 0;
				maxPhoto = 0;
				NBTTagCompound nbt = item.getTagCompound();

				if(nbt.getBoolean("hasMemoryCard")) {
					int[] indexes = nbt.getIntArray("indexes");
					curPhoto = nbt.getInteger("curPhoto");
					maxPhoto = indexes.length;
					if(indexes.length > 0) {
						if(curPhoto < indexes.length) {
							if(PhotosData.photos.containsKey(indexes[curPhoto]))
								mc.renderEngine.bindTexture(PhotosData.photos.get(indexes[curPhoto]));
							else {
								if(MCData.imageIDToLoadFromServer == 0 && !Minecraft.getMinecraft().isSingleplayer()) {
									MCData.imageIDToLoadFromServer = indexes[curPhoto];
									ShootIt.network.sendToServer(new MessageRequestForPhoto(indexes[curPhoto]));
								}
								else if(Minecraft.getMinecraft().isSingleplayer()) {
									File file = new File(MCData.photosFolderPathClient + "/" + indexes[curPhoto] + ".png");
									if(file.exists())
										PhotosData.addPhoto(indexes[curPhoto]);
									else PhotosData.addEmptyPhoto(indexes[curPhoto]);
								}
								Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.LOADING);
							}
						}
						else {
							nbt.setInteger("curPhoto", 0);
							item.setTagCompound(nbt);
							ShootIt.network.sendToServer(new MessageCameraToServer((byte)2));
						}
					}
					else {
						Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.NO_PHOTOS);
					}
				}
				else {
					Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.NO_MEMORY_CARD);
				}
				int bx1 = (int)(100 * width / xSize);
				int bx2 = (int)(339 * width / xSize);
				int by1 = (int)(12 * height / ySize);
				int by2 = (int)(196 * height / ySize);
				drawTexturedModalRect(width / 2 - 76, height / 2 - 93, 0, 0, 240, 185, 1, 1);
				if(maxPhoto > 0) {
					drawCenteredStringWithoutShadow(mc.fontRenderer, (curPhoto + 1) + "/" + maxPhoto, (int)(width / 2 + 44), (int)(height / 2 - 90), 16777215);
				}
			}
		}

		filters.drawScreen(mouseX, mouseY, partialTicks);
	}

	public void drawTexturedModalRect(double x, double y, double textureX, double textureY, double width, double height, double textureWidth, double textureHeight) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(x + 0, y + height, (double)this.zLevel).tex(textureX, textureY + textureHeight).endVertex();
		vertexbuffer.pos(x + width, y + height, (double)this.zLevel).tex(textureX + textureWidth, textureY + textureHeight).endVertex();
		vertexbuffer.pos(x + width, y + 0, (double)this.zLevel).tex(textureX + textureWidth, textureY).endVertex();
		vertexbuffer.pos(x + 0, y + 0, (double)this.zLevel).tex(textureX, textureY).endVertex();
		tessellator.draw();
	}
	
	public void drawCenteredStringWithoutShadow(FontRenderer fontRendererIn, String text, int x, int y, int color) {
		fontRendererIn.drawString(text, (float)(x - fontRendererIn.getStringWidth(text) / 2), (float)y, color, false);
	}

	public void onGuiClosed() {
		super.onGuiClosed();
	}

	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		if(getSlotUnderMouse() != null) {
			int slot = getSlotUnderMouse().slotNumber;
			if(slot == 36 && !getSlotUnderMouse().getHasStack())
				drawHoveringText(Arrays.asList(TextFormatting.ITALIC + "Memory card"), mouseX - guiLeft, mouseY - guiTop);
		}
		
		if(buttonList.get(3).isMouseOver()) {
			drawHoveringText(Arrays.asList("Ctrl + Click to delete only from this memory card", "Ctrl + Alt + Click to delete from everywhere (forever)"), mouseX - guiLeft, mouseY - guiTop);
		}
	}

	protected boolean checkHotbarKeys(int keyCode) {
		return false;
	}

	@Override
	public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList) {
		// TODO Auto-generated method stub
		//this.sendSlotContents(containerToSend, 36, containerToSend.getSlot(36).getStack());

	}

	@Override
	public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) {
		// TODO Auto-generated method stub
		if(slotInd == mc.player.inventory.currentItem) {
			ItemStack item = mc.player.getHeldItemMainhand();
			if(item.getItem() instanceof Camera) {
				item.setTagCompound(stack.getTagCompound());
			}
		}
	}

	@Override
	public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendAllWindowProperties(Container containerIn, IInventory inventory) {
		// TODO Auto-generated method stub

	}

	protected void actionPerformed(GuiButton button) throws IOException {
		switch (button.id) {
		case 0:
			ShootIt.network.sendToServer(new MessageCameraToServer((byte)0, false));
			break;
		case 1:
			ShootIt.network.sendToServer(new MessageCameraToServer((byte)0, true));
			break;
		case 2:
			filters.setVisible(!filters.getVisible());
			break;
		case 3:
			ItemStack item = mc.player.getHeldItemMainhand();
			if(item.getItem() instanceof Camera)
				if(item.hasTagCompound()) {
					NBTTagCompound nbt = item.getTagCompound();
					if(nbt.getBoolean("hasMemoryCard")) {
						int[] indexes = nbt.getIntArray("indexes");
						int curPhoto = nbt.getInteger("curPhoto");
						if(indexes.length > 0 && curPhoto < indexes.length) {
							if(isCtrlKeyDown() && isAltKeyDown())
								ShootIt.network.sendToServer(new MessageDeletePhotoRequest(indexes[curPhoto], true));
							else if(isCtrlKeyDown())
								ShootIt.network.sendToServer(new MessageDeletePhotoRequest(indexes[curPhoto], false));
						}
					}
				}
			break;
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		filters.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		filters.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		filters.handleMouseInput();
	}
}
