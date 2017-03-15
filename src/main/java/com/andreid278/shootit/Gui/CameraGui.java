package com.andreid278.shootit.Gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.andreid278.shootit.Main;
import com.andreid278.shootit.Items.Camera;
import com.andreid278.shootit.Misc.Photos;
import com.andreid278.shootit.Misc.Statics;
import com.andreid278.shootit.Network.MessageCameraToServer;
import com.andreid278.shootit.Network.MessageDeletePhotoRequest;
import com.andreid278.shootit.Network.MessageRequestForPhoto;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

public class CameraGui extends GuiScreen {
	public int xSize;
	public int ySize;
	public static List<String> deleteButtonText = Arrays.asList(TextFormatting.BOLD + "Ctrl + LMB" + TextFormatting.RESET + " - Delete only from this card", TextFormatting.BOLD + "Ctrl + Alt + LMB" + TextFormatting.RESET + " - Delete from everywhere");
	public GuiShadersList guiShadersList;
	public int curShader;
	
	public CameraGui() {
		super();
//		xSize = 100;
//		ySize = 162;
	}

	public void initGui() {
		super.initGui();
		buttonList.add(new TrueButtonGui(0, (int)(width * 221 / 256.0), (int)(height * 20 / 150.0), (int)(width * 10 / 256.0), (int)(height * 10 / 150.0), "<-"));
		buttonList.add(new TrueButtonGui(1, (int)(width * 233 / 256.0), (int)(height * 20 / 150.0), (int)(width * 10 / 256.0), (int)(height * 10 / 150.0), "->"));
		buttonList.add(new TrueButtonGui(2, (int)(width * 215 / 256.0), (int)(height * 115 / 150.0), (int)(width * 35 / 256.0), (int)(height * 25 / 150.0), Arrays.asList("Open", "inventory")));
		buttonList.add(new TrueButtonGui(3, (int)(width * 215 / 256.0), (int)(height * 85 / 150.0), (int)(width * 35 / 256.0), (int)(height * 25 / 150.0), Arrays.asList("Delete", "photo")));
		curShader = 0;
		ItemStack item = mc.thePlayer.getHeldItemMainhand();
		if(item != null)
			if(item.getItem() instanceof Camera)
				if(item.hasTagCompound())
					curShader = item.getTagCompound().getInteger("shader");
		guiShadersList = new GuiShadersList(this, mc, (int)(width * 5 / 256.0), (int)(width * 35 / 256.0), (int)(height * 70 / 150.0), (int)(height * 20 / 150.0), (int)(height * 90 / 150.0), 15);
	}
	
	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		guiShadersList.handleMouseInput();
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		guiShadersList.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		guiShadersList.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		ItemStack item = mc.thePlayer.getHeldItemMainhand();
		if(item != null)
			if(item.getItem() instanceof Camera)
				if(item.hasTagCompound()) {
					NBTTagCompound nbt = item.getTagCompound();
					int shader = nbt.getInteger("shader");
					if(Statics.lastShader != shader) {
						if(shader > 0)
							Minecraft.getMinecraft().entityRenderer.loadShader(Statics.shaders.get(shader).rl);
						else Minecraft.getMinecraft().entityRenderer.stopUseShader();
						Statics.lastShader = shader;
					}
					if(nbt.getBoolean("hasMemoryCard")) {
						int[] indexes = nbt.getIntArray("indexes");
						int curPhoto = nbt.getInteger("curPhoto");
						if(indexes.length > 0) {
							if(curPhoto < indexes.length) {
								drawCenteredStringWithoutShadow(mc.fontRendererObj, (curPhoto + 1) + "/" + indexes.length, (int)(width * 232 / 256.0), (int)(height * 15 / 150.0), 16777215);
								if(Photos.photos.containsKey(indexes[curPhoto]))
									mc.renderEngine.bindTexture(Photos.photos.get(indexes[curPhoto]));
								else {
									if(Statics.imageIDToLoadFromServer == 0 && !Minecraft.getMinecraft().isSingleplayer()) {
										Statics.imageIDToLoadFromServer = indexes[curPhoto];
										Main.network.sendToServer(new MessageRequestForPhoto(indexes[curPhoto]));
									}
									else if(Minecraft.getMinecraft().isSingleplayer()) {
										File file = new File(Statics.photosFolderPathClient + Statics.slash + indexes[curPhoto] + ".png");
										if(file.exists())
											Photos.addPhoto(indexes[curPhoto]);
										else Photos.addEmptyPhoto(indexes[curPhoto]);
									}
									Minecraft.getMinecraft().getTextureManager().bindTexture(Statics.LOADING);
								}
							}
							else {
								nbt.setInteger("curPhoto", 0);
								item.setTagCompound(nbt);
								Main.network.sendToServer(new MessageCameraToServer((byte)2));
							}
						}
						else {
							drawCenteredStringWithoutShadow(mc.fontRendererObj, "0/0", (int)(width * 232 / 256.0), (int)(height * 15 / 150.0), 16777215);
							Minecraft.getMinecraft().getTextureManager().bindTexture(Statics.NO_PHOTOS);
						}
					}
					else {
						drawCenteredStringWithoutShadow(mc.fontRendererObj, "0/0", (int)(width * 232 / 256.0), (int)(height * 15 / 150.0), 16777215);
						Minecraft.getMinecraft().getTextureManager().bindTexture(Statics.NO_MEMORY_CARD);
					}
					int bx1 = (int)(Statics.x1 * width / 256.0);
					int bx2 = (int)(Statics.x2 * width / 256.0);
					int by1 = (int)(Statics.y1 * height / 150.0);
					int by2 = (int)(Statics.y2 * height / 150.0);
					drawTexturedModalRect(bx1, by1, 0, 0, bx2 - bx1 + 1, by2 - by1 + 1, 1, 1);
				}
		guiShadersList.drawScreen(mouseX, mouseY, partialTicks);
		super.drawScreen(mouseX, mouseY, partialTicks);
		if(buttonList.get(3).isMouseOver())
			drawHoveringText(deleteButtonText, mouseX - fontRendererObj.getStringWidth(deleteButtonText.get(1)) - 15, mouseY - 15);
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
			Main.network.sendToServer(new MessageCameraToServer((byte)0, false));
			break;
		case 1:
			Main.network.sendToServer(new MessageCameraToServer((byte)0, true));
			break;
		case 2:
			mc.displayGuiScreen((GuiScreen)null);
			Main.network.sendToServer(new MessageCameraToServer((byte)1));
			break;
		case 3:
			ItemStack item = mc.thePlayer.getHeldItemMainhand();
			if(item != null)
				if(item.getItem() instanceof Camera)
					if(item.hasTagCompound()) {
						NBTTagCompound nbt = item.getTagCompound();
						if(nbt.getBoolean("hasMemoryCard")) {
							int[] indexes = nbt.getIntArray("indexes");
							int curPhoto = nbt.getInteger("curPhoto");
							if(indexes.length > 0 && curPhoto < indexes.length) {
								if(isCtrlKeyDown() && isAltKeyDown())
									Main.network.sendToServer(new MessageDeletePhotoRequest(indexes[curPhoto], true));
								else if(isCtrlKeyDown())
									Main.network.sendToServer(new MessageDeletePhotoRequest(indexes[curPhoto], false));
							}
						}
					}
			break;
		}
	}
}
