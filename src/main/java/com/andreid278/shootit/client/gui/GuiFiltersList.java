package com.andreid278.shootit.client.gui;

import java.util.ArrayList;
import java.util.List;

import com.andreid278.shootit.ShootIt;
import com.andreid278.shootit.client.shader.Shader;
import com.andreid278.shootit.client.shader.ShaderManager;
import com.andreid278.shootit.common.MCData;
import com.andreid278.shootit.common.network.MessageCameraToServer;

import net.minecraft.client.Minecraft;

public class GuiFiltersList extends GuiList {
	public CameraFiltersGui owner;
	public List<GuiListEntry> list = new ArrayList<>();

	public GuiFiltersList(CameraFiltersGui owner, Minecraft mcIn, int left, int top, int widthIn, int heightIn, int slotHeightIn, String header) {
		super(mcIn, left, top, widthIn, heightIn, slotHeightIn, header);
		this.owner = owner;
		for(Shader shader : ShaderManager.instance.shaderList)
			list.add(new GuiListEntry(shader.name, 0xffffff, false, 0));
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
		owner.onFilterChange(slot);
	}
}
