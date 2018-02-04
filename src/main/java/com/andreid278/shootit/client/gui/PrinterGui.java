package com.andreid278.shootit.client.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.andreid278.shootit.ShootIt;
import com.andreid278.shootit.client.Resources;
import com.andreid278.shootit.common.MCData;
import com.andreid278.shootit.common.PhotosData;
import com.andreid278.shootit.common.container.PrinterContainer;
import com.andreid278.shootit.common.item.MemoryCard;
import com.andreid278.shootit.common.network.MessagePrinterToServer;
import com.andreid278.shootit.common.network.MessageRequestForPhoto;
import com.andreid278.shootit.common.tiileentity.TEPrinter;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
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
	
	public int curPhoto;
	public int maxPhoto;

	public Checkbox checkbox;

	public GuiEditor guiEditor;

	public PrinterGui(Container inventorySlotsIn) {
		super(inventorySlotsIn);
		// TODO Auto-generated constructor stub
	}

	public PrinterGui(InventoryPlayer inventory, TEPrinter te) {
		super(new PrinterContainer(inventory, te));
		this.xSize = 352;
		this.ySize = 210;
	}
	
	@Override
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
		checkbox = new Checkbox(0, 180 + guiLeft, 145 + guiTop, 10, 10, te.checkboxCustom, "Custom area");
		guiEditor = new GuiEditor(181 + guiLeft, 11 + guiTop, 158, 108);

		curFrames = 0;
		framesRL.clear();
		ItemStack itemFrames = te.getStackInSlot(7);
		if(!itemFrames.isEmpty()) {
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
		if(!itemBack.isEmpty()) {
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
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		TEPrinter te = ((PrinterContainer)inventorySlots).te;
		mc.getTextureManager().bindTexture(Resources.PRINTER);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize, 1, 1);
		ItemStack itemStack = inventorySlots.getSlot(36).getStack();
		curPhoto = 0;
		maxPhoto = 0;
		if(!itemStack.isEmpty()) {
			if(itemStack.getItem() instanceof MemoryCard)
				if(itemStack.hasTagCompound()) {
					int[] array = itemStack.getTagCompound().getIntArray("indexes");
					if(array.length > 0) {
						curPhoto = te.curPhoto;
						maxPhoto = array.length;
						if(curPhoto < array.length) {
							if(PhotosData.photos.containsKey(array[curPhoto]))
								guiEditor.photoID = array[curPhoto];
							else {
								if(MCData.imageIDToLoadFromServer == 0 && !mc.isSingleplayer()) {
									MCData.imageIDToLoadFromServer = array[curPhoto];
									ShootIt.network.sendToServer(new MessageRequestForPhoto(array[curPhoto]));
								}
								else if(Minecraft.getMinecraft().isSingleplayer()) {
									File file = new File(MCData.photosFolderPathClient + "/" + array[curPhoto] + ".png");
									if(file.exists())
										PhotosData.addPhoto(array[curPhoto]);
									else PhotosData.addEmptyPhoto(array[curPhoto]);
								}
								guiEditor.photoID = 0;
							}
						}
					}
					else {
						guiEditor.photoID = -1;
					}
				}
		}
		else {
			guiEditor.photoID = -2;
		}
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		if(checkbox.check) {
			guiEditor.isEnabled = true;
			guiEditor.setPhotoSize(te.width, te.height);
		}
		else guiEditor.isEnabled = false;
		guiEditor.draw(mc, mouseX, mouseY);
		drawCenteredStringWithoutShadow(fontRenderer, "Width = " + te.width, 100 + guiLeft, 12 + guiTop, 16777215);
		drawCenteredStringWithoutShadow(fontRenderer, "Height = " + te.height, 100 + guiLeft, 27 + guiTop, 16777215);
		drawCenteredStringWithoutShadow(fontRenderer, "Frame", 60 + guiLeft, 89 + guiTop, 0);
		drawCenteredStringWithoutShadow(fontRenderer, "Back", 60 + guiLeft, 110 + guiTop, 0);
		if(maxPhoto > 0) {
			drawCenteredStringWithoutShadow(mc.fontRenderer, (curPhoto + 1) + "/" + maxPhoto, (int)(guiLeft + 260), (int)(guiTop + 127), 16777215);
		}
		checkbox.drawCheckbox(mc, mouseX, mouseY);
	}
	
	@Override
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
		if(getSlotUnderMouse() == null)
			return;
		int slot = getSlotUnderMouse().slotNumber;
		if(slot == 36 && !getSlotUnderMouse().getHasStack()) {
			drawHoveringText(Arrays.asList(TextFormatting.ITALIC + slotsText.get(slot - 36)), mouseX - guiLeft, mouseY - guiTop + 25);
		}
		if(slot > 36 && slot < 42) {
			TEPrinter te = ((PrinterContainer)inventorySlots).te;
			int curCount = getSlotUnderMouse().getHasStack() ? getSlotUnderMouse().getStack().getCount() : 0;
			int reqCount = (int)Math.sqrt(te.width * te.height);
			drawHoveringText(Arrays.asList(TextFormatting.ITALIC + slotsText.get(slot - 36), TextFormatting.GRAY + "Required " + TextFormatting.BOLD + (curCount < reqCount ? TextFormatting.RED : TextFormatting.GREEN) + reqCount), mouseX - guiLeft, mouseY - guiTop + 25);
		}
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

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		switch (button.id) {
		case 0: {
			ItemStack itemStack = inventorySlots.getSlot(36).getStack();
			if(itemStack.getItem() instanceof MemoryCard)
					if(itemStack.hasTagCompound()) {
						int[] array = itemStack.getTagCompound().getIntArray("indexes");
						if(array.length > 0) {
							int index = itemStack.getTagCompound().getIntArray("indexes")[((PrinterContainer)inventorySlots).te.curPhoto];
							double textureCoords[] = guiEditor.getTextureCoords();
							ShootIt.network.sendToServer(new MessagePrinterToServer((byte)0,
									index,
									((PrinterContainer)inventorySlots).te.width,
									((PrinterContainer)inventorySlots).te.height,
									((PrinterContainer)inventorySlots).te.getPos(),
									framesRL.size() == 0 ? null : (curFrames < framesRL.size() ? framesRL.get(curFrames) : null),
									backRL.size() == 0 ? null : (curBack < backRL.size() ? backRL.get(curBack) : null),
									textureCoords));
						}
					}
			break;
		}
		case 1: {
			ItemStack itemStack = inventorySlots.getSlot(36).getStack();
			if(itemStack.getItem() instanceof MemoryCard)
					if(itemStack.hasTagCompound()) {
						ShootIt.network.sendToServer(new MessagePrinterToServer((byte)1, false, ((PrinterContainer)inventorySlots).te.getPos()));
					}
			break;
		}
		case 2: {
			ItemStack itemStack = inventorySlots.getSlot(36).getStack();
			if(itemStack.getItem() instanceof MemoryCard)
					if(itemStack.hasTagCompound()) {
						ShootIt.network.sendToServer(new MessagePrinterToServer((byte)1, true, ((PrinterContainer)inventorySlots).te.getPos()));
					}
			break;
		}
		case 3:
			ShootIt.network.sendToServer(new MessagePrinterToServer((byte)2, false, ((PrinterContainer)inventorySlots).te.getPos()));
			break;
		case 4:
			ShootIt.network.sendToServer(new MessagePrinterToServer((byte)2, true, ((PrinterContainer)inventorySlots).te.getPos()));
			break;
		case 5:
			ShootIt.network.sendToServer(new MessagePrinterToServer((byte)3, false, ((PrinterContainer)inventorySlots).te.getPos()));
			break;
		case 6:
			ShootIt.network.sendToServer(new MessagePrinterToServer((byte)3, true, ((PrinterContainer)inventorySlots).te.getPos()));
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
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		guiEditor.mouseClicked(mouseX, mouseY, mouseButton);
		if(checkbox.mousePressed(mc, mouseX, mouseY)) {
			ShootIt.network.sendToServer(new MessagePrinterToServer((byte)(4 + checkbox.id), ((PrinterContainer)inventorySlots).te.getPos()));
		}
	}
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		guiEditor.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
	}
	
	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		guiEditor.handleMouseInput();
	}
}
