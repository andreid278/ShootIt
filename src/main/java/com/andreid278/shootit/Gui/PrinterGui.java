package com.andreid278.shootit.Gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.andreid278.shootit.Main;
import com.andreid278.shootit.Containers.PrinterContainer;
import com.andreid278.shootit.Items.MemoryCard;
import com.andreid278.shootit.Misc.Photos;
import com.andreid278.shootit.Misc.Statics;
import com.andreid278.shootit.Network.MessagePrinterToServer;
import com.andreid278.shootit.Network.MessageRequestForPhoto;
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

public class PrinterGui extends GuiContainer {
	List<String> slotsText = Arrays.asList("Memory card", "Black dye", "Blue dye", "Green dye", "Red dye", "Paper");
	public List<ResourceLocation> backRL = new ArrayList<>();
	public List<ResourceLocation> framesRL = new ArrayList<>();
	public int curBack = 0;
	public int curFrames = 0;

	protected List<Checkbox> checkboxList = Lists.<Checkbox>newArrayList();

	public PrinterGui(Container inventorySlotsIn) {
		super(inventorySlotsIn);
		// TODO Auto-generated constructor stub
	}

	public PrinterGui(InventoryPlayer inventory, TEPrinter te) {
		super(new PrinterContainer(inventory, te));
		this.xSize = 352;
		this.ySize = 210;
	}

	public void initGui() {
		super.initGui();
		this.buttonList.clear();
		this.buttonList.add(new TrueButtonGui(0, 110 + guiLeft, 57 + guiTop, 35, 21, "Print"));
		this.buttonList.add(new TrueButtonGui(1, 230 + guiLeft, 125 + guiTop, 15, 10, "<-"));
		this.buttonList.add(new TrueButtonGui(2, 275 + guiLeft, 125 + guiTop, 15, 10, "->"));
		this.buttonList.add(new TrueButtonGui(3, 133 + guiLeft, 11 + guiTop, 10, 10, "-"));
		this.buttonList.add(new TrueButtonGui(4, 145 + guiLeft, 11 + guiTop, 10, 10, "+"));
		this.buttonList.add(new TrueButtonGui(5, 133 + guiLeft, 26 + guiTop, 10, 10, "-"));
		this.buttonList.add(new TrueButtonGui(6, 145 + guiLeft, 26 + guiTop, 10, 10, "+"));
		this.buttonList.add(new TrueButtonGui(7, 130 + guiLeft, 83 + guiTop, 10, 18, ">"));
		this.buttonList.add(new TrueButtonGui(8, 130 + guiLeft, 105 + guiTop, 10, 18, ">"));

		TEPrinter te = ((PrinterContainer)inventorySlots).te;
//		checkboxList.add(new Checkbox(0, 10, 85, 10, 10, te.checkboxFrames, "Frames"));
//		checkboxList.add(new Checkbox(1, 10, 110, 10, 10, te.checkboxBack, "Back"));

		curFrames = 0;
		framesRL.clear();
		ItemStack itemFrames = te.getStackInSlot(7);
		if(itemFrames != null) {
			Block block = Block.getBlockFromItem(itemFrames.getItem());
			BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
			for(int i = 0; i < 6; i++) {
				IBlockState state = block.getStateFromMeta(itemFrames.getMetadata());
				List<BakedQuad> quads = blockrendererdispatcher.getModelForState(state).getQuads(state, EnumFacing.getFront(i), 1);
				for(BakedQuad quad : quads) {
					ResourceLocation trl = new ResourceLocation(quad.getSprite().getIconName());
					trl = new ResourceLocation(trl.getResourceDomain(), String.format("%s/%s%s", new Object[] {"textures", trl.getResourcePath(), ".png"}));
					if(!framesRL.contains(trl))
						framesRL.add(trl);
				}
			}
		}
		else framesRL.clear();

		curBack = 0;
		backRL.clear();
		ItemStack itemBack = te.getStackInSlot(8);
		if(itemBack != null) {
			Block block = Block.getBlockFromItem(itemBack.getItem());
			BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
			for(int i = 0; i < 6; i++) {
				IBlockState state = block.getStateFromMeta(itemBack.getMetadata());
				List<BakedQuad> quads = blockrendererdispatcher.getModelForState(state).getQuads(state, EnumFacing.getFront(i), 1);
				for(BakedQuad quad : quads) {
					ResourceLocation trl = new ResourceLocation(quad.getSprite().getIconName());
					trl = new ResourceLocation(trl.getResourceDomain(), String.format("%s/%s%s", new Object[] {"textures", trl.getResourcePath(), ".png"}));
					if(!backRL.contains(trl))
						backRL.add(trl);
				}
			}
		}
		else backRL.clear();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		TEPrinter te = ((PrinterContainer)inventorySlots).te;
		mc.getTextureManager().bindTexture(Statics.PRINTER);
		int x = (width - xSize) / 2;
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
							drawCenteredStringWithoutShadow(mc.fontRendererObj, (curIndex + 1) + "/" + array.length, 260 + guiLeft, 127 + guiTop, 0);
							if(Photos.photos.containsKey(array[curIndex]))
								mc.getTextureManager().bindTexture(Photos.photos.get(array[curIndex]));
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
								mc.getTextureManager().bindTexture(Statics.LOADING);
							}
						}
					}
					else {
						drawCenteredStringWithoutShadow(mc.fontRendererObj, "0/0", 260 + guiLeft, 127 + guiTop, 0);
						mc.getTextureManager().bindTexture(Statics.NO_PHOTOS);
					}
				}
		}
		else {
			drawCenteredStringWithoutShadow(mc.fontRendererObj, "0/0", 260 + guiLeft, 127 + guiTop, 0);
			mc.getTextureManager().bindTexture(Statics.NO_MEMORY_CARD);
		}
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(181 + guiLeft, 11 + guiTop, 0, 0, 159, 109, 1, 1);
		drawCenteredStringWithoutShadow(fontRendererObj, "Width = " + te.width, 100 + guiLeft, 12 + guiTop, 16777215);
		drawCenteredStringWithoutShadow(fontRendererObj, "Height = " + te.height, 100 + guiLeft, 27 + guiTop, 16777215);
		drawCenteredStringWithoutShadow(fontRendererObj, "Frame", 60 + guiLeft, 89 + guiTop, 0);
		drawCenteredStringWithoutShadow(fontRendererObj, "Back", 60 + guiLeft, 110 + guiTop, 0);
	}

	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		if(backRL.size() > 0) {
			if(curBack >= backRL.size())
				curBack = 0;
			mc.getTextureManager().bindTexture(backRL.get(curBack));
			drawTexturedModalRect(111, 106, 0, 0, 16, 16, 1, 1);
		}
		if(framesRL.size() > 0) {
			if(curFrames >= framesRL.size())
				curFrames = 0;
			mc.getTextureManager().bindTexture(framesRL.get(curFrames));
			drawTexturedModalRect(111, 84, 0, 0, 16, 16, 1, 1);
		}
		for(Checkbox checkbox : checkboxList)
			checkbox.drawCheckbox(mc, mouseX, mouseY);
		if(getSlotUnderMouse() == null)
			return;
		int slot = getSlotUnderMouse().slotNumber;
		if(slot >= 36 && slot < 42) {
			TEPrinter te = ((PrinterContainer)inventorySlots).te;
			int curCount = getSlotUnderMouse().getHasStack() ? getSlotUnderMouse().getStack().stackSize : 0;
			int reqCount = (int)Math.sqrt(te.width * te.height);
			drawHoveringText(Arrays.asList(TextFormatting.ITALIC + slotsText.get(slot - 36), slot == 36 ? "" : TextFormatting.GRAY + "Required " + TextFormatting.BOLD + (curCount < reqCount ? TextFormatting.RED : TextFormatting.GREEN) + reqCount), mouseX - guiLeft, mouseY - guiTop + 25);
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

	public void drawCenteredStringWithoutShadow(FontRenderer fontRendererIn, String text, int x, int y, int color) {
		fontRendererIn.drawString(text, (float)(x - fontRendererIn.getStringWidth(text) / 2), (float)y, color, false);
	}

	protected void actionPerformed(GuiButton button) throws IOException {
		switch (button.id) {
		case 0: {
			ItemStack itemStack = inventorySlots.getSlot(36).getStack();
			if(itemStack != null)
				if(itemStack.getItem() instanceof MemoryCard)
					if(itemStack.hasTagCompound()) {
						int[] array = itemStack.getTagCompound().getIntArray("indexes");
						if(array.length > 0) {
							int index = itemStack.getTagCompound().getIntArray("indexes")[((PrinterContainer)inventorySlots).te.curPhoto];
							Main.network.sendToServer(new MessagePrinterToServer((byte)0,
																				index,
																				((PrinterContainer)inventorySlots).te.width,
																				((PrinterContainer)inventorySlots).te.height,
																				((PrinterContainer)inventorySlots).te.getPos(),
																				framesRL.size() == 0 ? null : (curFrames < framesRL.size() ? framesRL.get(curFrames) : null),
																				backRL.size() == 0 ? null : (curBack < backRL.size() ? backRL.get(curBack) : null)));
						}
					}
			break;
		}
		case 1: {
			ItemStack itemStack = inventorySlots.getSlot(36).getStack();
			if(itemStack != null)
				if(itemStack.getItem() instanceof MemoryCard)
					if(itemStack.hasTagCompound()) {
						Main.network.sendToServer(new MessagePrinterToServer((byte)1, false, ((PrinterContainer)inventorySlots).te.getPos()));
					}
			break;
		}
		case 2: {
			ItemStack itemStack = inventorySlots.getSlot(36).getStack();
			if(itemStack != null)
				if(itemStack.getItem() instanceof MemoryCard)
					if(itemStack.hasTagCompound()) {
						Main.network.sendToServer(new MessagePrinterToServer((byte)1, true, ((PrinterContainer)inventorySlots).te.getPos()));
					}
			break;
		}
		case 3:
			Main.network.sendToServer(new MessagePrinterToServer((byte)2, false, ((PrinterContainer)inventorySlots).te.getPos()));
			break;
		case 4:
			Main.network.sendToServer(new MessagePrinterToServer((byte)2, true, ((PrinterContainer)inventorySlots).te.getPos()));
			break;
		case 5:
			Main.network.sendToServer(new MessagePrinterToServer((byte)3, false, ((PrinterContainer)inventorySlots).te.getPos()));
			break;
		case 6:
			Main.network.sendToServer(new MessagePrinterToServer((byte)3, true, ((PrinterContainer)inventorySlots).te.getPos()));
			break;
		case 7:
			if(framesRL.size() > 0)
				curFrames = (curFrames + 1) % framesRL.size();
			break;
		case 8:
			if(backRL.size() > 0)
				curBack = (curBack + 1) % backRL.size();
			break;
		}
	}

	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		for(Checkbox checkbox : checkboxList)
			if(checkbox.mousePressed(mc, mouseX - guiLeft, mouseY - guiTop)) {
				Main.network.sendToServer(new MessagePrinterToServer((byte)(4 + checkbox.id), ((PrinterContainer)inventorySlots).te.getPos()));
			}
	}
}
