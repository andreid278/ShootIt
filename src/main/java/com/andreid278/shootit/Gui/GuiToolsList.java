package com.andreid278.shootit.Gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.andreid278.shootit.Main;
import com.andreid278.shootit.Misc.Statics;
import com.andreid278.shootit.Misc.Statics.ShaderInfo;
import com.andreid278.shootit.Network.MessageCameraToServer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.math.MathHelper;

public class GuiToolsList extends GuiList {
	public PainterGui owner;
	public List<GuiListEntry> list = new ArrayList<>();
	
	public GuiToolsList(PainterGui owner, Minecraft mcIn, int left, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
		super(mcIn, left, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
		this.owner = owner;
		for(GuiPhotoEditor.Tools tool : GuiPhotoEditor.Tools.values())
			list.add(new GuiListEntry(tool.toString(), 0));
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

	@Override
	public String getHeaderName() {
		return "Tools";
	}
}
