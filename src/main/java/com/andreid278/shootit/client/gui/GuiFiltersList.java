package com.andreid278.shootit.client.gui;

import java.util.ArrayList;
import java.util.List;

import com.andreid278.shootit.ShootIt;
import com.andreid278.shootit.common.MCData;
import com.andreid278.shootit.common.MCData.ShaderInfo;
import com.andreid278.shootit.common.network.MessageCameraToServer;

import net.minecraft.client.Minecraft;

public class GuiFiltersList extends GuiList {
	public CameraGui owner;
	public List<GuiListEntry> list = new ArrayList<>();

	public GuiFiltersList(CameraGui owner, Minecraft mcIn, int left, int top, int widthIn, int heightIn, int slotHeightIn, String header) {
		super(mcIn, left, top, widthIn, heightIn, slotHeightIn, header);
		this.owner = owner;
		for(ShaderInfo shader : MCData.shaders)
			list.add(new GuiListEntry(shader.name, 0xffffff, true, 0));
		selectedSlot = owner.curShader;
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
		owner.curShader = slot;
		ShootIt.network.sendToServer(new MessageCameraToServer((byte)3, slot));
	}
}
