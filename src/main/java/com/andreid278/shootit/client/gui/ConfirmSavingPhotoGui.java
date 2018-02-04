package com.andreid278.shootit.client.gui;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

import com.andreid278.shootit.ShootIt;
import com.andreid278.shootit.common.MCData;
import com.andreid278.shootit.common.network.MessageRequestForNextPhotoID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ConfirmSavingPhotoGui extends GuiScreen {
	public ItemStack camera;
	public ResourceLocation rl;
	boolean isClosed;

	public ConfirmSavingPhotoGui(ItemStack heldItemMainhand) {
		camera = heldItemMainhand;
	}
	
	public void initGui() {
		isClosed = false;
		MCData.imageIDToLoadToServer = -1;
		this.buttonList.clear();
		this.buttonList.add(new TrueButtonGui(0, (int)(width * 224 / 256.0), (int)(height * 50 / 150.0), (int)(width * 20 / 256.0), (int)(height * 15 / 150.0), "Yes"));
		this.buttonList.add(new TrueButtonGui(1, (int)(width * 224 / 256.0), (int)(height * 70 / 150.0), (int)(width * 20 / 256.0), (int)(height * 15 / 150.0), "No"));
		rl = new ResourceLocation("photos", MCData.resourceLocationPath + "/0.png");
		Class tm = Minecraft.getMinecraft().getTextureManager().getClass();
		Field field = null;
		try {
			field = tm.getDeclaredField("mapTextureObjects");
		}
		catch (NoSuchFieldException e) {
			try {
				field = tm.getDeclaredField("field_110585_a");
			} catch (NoSuchFieldException | SecurityException e1) {
				e1.printStackTrace();
			}
		}
		if(field != null) {
			field.setAccessible(true);
			Map<ResourceLocation, ITextureObject> mapTextureObjects;
			try {
				mapTextureObjects = (Map<ResourceLocation, ITextureObject>) field.get(Minecraft.getMinecraft().getTextureManager());
				if(mapTextureObjects.containsKey(rl)) {
					mapTextureObjects.remove(rl);
					field.set(Minecraft.getMinecraft().getTextureManager(), mapTextureObjects);
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(rl);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		double bx1 = MCData.x1 * width / 256.0;
		double bx2 = MCData.x2 * width / 256.0;
		double by1 = MCData.y1 * height / 150.0;
		double by2 = MCData.y2 * height / 150.0;
		vertexbuffer.pos(bx1, by2, 0).tex(0.0D, 1.0D).endVertex();
		vertexbuffer.pos(bx2, by2, 0).tex(1.0D, 1.0D).endVertex();
		vertexbuffer.pos(bx2, by1, 0).tex(1.0D, 0.0D).endVertex();
		vertexbuffer.pos(bx1, by1, 0).tex(0.0D, 0.0D).endVertex();
		tessellator.draw();

		drawCenteredStringWithoutShadow(mc.fontRenderer, "Save?", (int)(width * 234 / 256.0), (int)(height * 40 / 150.0), 16777215);

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	public void drawCenteredStringWithoutShadow(FontRenderer fontRendererIn, String text, int x, int y, int color) {
		fontRendererIn.drawString(text, (float)(x - fontRendererIn.getStringWidth(text) / 2), (float)y, color, false);
	}

	protected void actionPerformed(GuiButton button) throws IOException {
		switch (button.id) {
		case 0:
			isClosed = true;
			File file = new File(MCData.photosFolderPathClient + "/0.png");
			if(file.exists()) {
				ShootIt.network.sendToServer(new MessageRequestForNextPhotoID());
			}
			else {
				System.out.println("Can't save this photo!!! File " + MCData.photosFolderPathClient + "/0.png" + " does not exist");
				MCData.imageIDToLoadToServer = 0;
				this.mc.displayGuiScreen((GuiScreen)null);
			}
			break;
		case 1:
			isClosed = true;
			MCData.imageIDToLoadToServer = 0;
			this.mc.displayGuiScreen((GuiScreen)null);
			break;
		}
	}

	public void onGuiClosed()
	{
		if(!isClosed)
			MCData.imageIDToLoadToServer = 0;
	}
}
