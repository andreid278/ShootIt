package com.andreid278.shootit.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;

public class GuiToolsList extends GuiList {
	public PainterGui owner;
	public List<GuiListEntry> list = new ArrayList<>();
	
	public GuiToolsList(PainterGui owner, Minecraft mcIn, int left, int top, int widthIn, int heightIn, int slotHeightIn, String header) {
		super(mcIn, left, top, widthIn, heightIn, slotHeightIn, header);
		this.owner = owner;
		for(GuiPhotoEditor.Tools tool : GuiPhotoEditor.Tools.values())
			list.add(new GuiListEntry(tool.toString(), 0, false, 0));
		selectedSlot = 0;
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
	public void slotClicked(int slot) {
//		owner.curShader = slot;
//		Main.network.sendToServer(new MessageCameraToServer((byte)3, slot));
		owner.photoEditor.curTool = GuiPhotoEditor.Tools.valueOf(((GuiListEntry)this.getListEntry(selectedSlot)).text);
	}
}
