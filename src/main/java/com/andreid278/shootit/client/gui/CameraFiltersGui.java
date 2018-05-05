package com.andreid278.shootit.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.andreid278.shootit.client.shader.Shader;
import com.andreid278.shootit.client.shader.ShaderManager;
import com.andreid278.shootit.common.MCData;
import com.andreid278.shootit.common.item.Camera;
import com.andreid278.shootit.client.shader.Shader.UniformInfo;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class CameraFiltersGui extends GuiScreen {

	public GuiFiltersList filtersList;
	public List<GuiScrollerEditor> scrollersList;
	public List<String> uniformNames;

	public ItemStack camera;

	public CameraFiltersGui(ItemStack camera) {
		this.camera = camera;
	}

	@Override
	public void initGui() {
		filtersList = new GuiFiltersList(this, mc, 5, MCData.y1 * this.height / 150, MCData.x1 * this.width / 256 - 10, 75, 15, "");
		scrollersList = new ArrayList<>();
		uniformNames = new ArrayList<>();
		
		NBTTagCompound nbt = camera.getTagCompound();
		if(nbt != null) {
			int curShader = nbt.getInteger("shader");
			if(curShader >= 0 && curShader < filtersList.getSize()) {
				filtersList.selectedSlot = curShader;
				filtersList.scrollTo(curShader);
				
				nbt = nbt.getCompoundTag("shaderInfo");
				
				Shader shader = ShaderManager.instance.shaderList.get(curShader);

				int i = 0;
				
				for(UniformInfo uniform : shader.uniformList) {
					uniformNames.add(uniform.name);
					scrollersList.add(new GuiScrollerEditor(filtersList.left + 5, filtersList.bottom + 10 + i * 40 + 20, filtersList.right - filtersList.left - 5, 10, uniform.min, uniform.max, uniform.step, nbt.getFloat(uniform.name), false, 0xeeeeee, 0x808080));
					i++;
				}
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		filtersList.drawScreen(mouseX, mouseY, partialTicks);

		for(GuiScrollerEditor scroller : scrollersList) {
			scroller.draw(mc, mouseX, mouseY);
		}
		
		int i = 0;
		for(String uniform : uniformNames) {
			drawString(fontRenderer, uniform, filtersList.left + 10, filtersList.bottom + 10 + (i++) * 40, 0xffffffff);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		//filtersList.mouseClicked(mouseX, mouseY, mouseButton);

		for(GuiScrollerEditor scroller : scrollersList) {
			scroller.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);

		//filtersList.mouseReleased(mouseX, mouseY, state);
		
		for(GuiScrollerEditor scroller : scrollersList) {
			scroller.mouseReleased(mouseX, mouseY, state);
		}
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);

		for(GuiScrollerEditor scroller : scrollersList) {
			scroller.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		}
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();

		filtersList.handleMouseInput();

		for(GuiScrollerEditor scroller : scrollersList) {
			scroller.handleMouseInput();
		}
	}

	public void onFilterChange(int index) {
		if(index < 0 || index >= filtersList.getSize()) {
			return;
		}

//		NBTTagCompound nbt = camera.getTagCompound();
//		if(nbt != null && nbt.getInteger("shader") == index) {
//			return;
//		}

		scrollersList.clear();
		uniformNames.clear();

		Shader shader = ShaderManager.instance.shaderList.get(index);

		int i = 0;

		for(UniformInfo uniform : shader.uniformList) {
			uniformNames.add(uniform.name);
			scrollersList.add(new GuiScrollerEditor(filtersList.left + 5, filtersList.bottom + 10 + i * 40 + 20, filtersList.right - filtersList.left - 5, 10, uniform.min, uniform.max, uniform.step, uniform.initValue, false, 0xeeeeee, 0x808080));
			i++;
		}

		updateShader();
	}

	public NBTTagCompound createShaderInfo(int index) {
		if(index < 0 || index >= filtersList.getSize()) {
			return new NBTTagCompound();
		}

		Shader shader = ShaderManager.instance.shaderList.get(index);

		NBTTagCompound nbt = new NBTTagCompound();

		int i = 0;

		for(UniformInfo uniform : shader.uniformList) {
			nbt.setFloat(uniform.name, scrollersList.get(i++).curValue);
		}

		return nbt;
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	public void updateScreen() {
		if(isShaderParametersChanged()) {
			updateShader();
		}
	}

	public boolean isShaderParametersChanged() {
		for(GuiScrollerEditor scroller : scrollersList) {
			if(scroller.isChanged) {
				for(GuiScrollerEditor scroller2 : scrollersList) {
					scroller2.isChanged = false;
				}

				return true;
			}
		}

		return false;
	}

	public void updateShader() {
		ShaderManager.instance.updateCameraShaderInfo(filtersList.selectedSlot, createShaderInfo(filtersList.selectedSlot));
	}
}
