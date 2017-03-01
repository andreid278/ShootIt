package com.andreid278.shootit.Containers;

import javax.annotation.Nullable;

import com.andreid278.shootit.Main;
import com.andreid278.shootit.Gui.GuiHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class CameraContainer extends Container {
	public CameraInventory cameraInventory;
	public CameraContainer(InventoryPlayer inventoryPlayer, CameraInventory cameraInventory) {
		this.cameraInventory = cameraInventory;

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 109));
		}

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 51 + i * 18));
			}
		}

		addSlotToContainer(new ModSlot(cameraInventory, 0, 80, 20));
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	@Nullable
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = null;
		Slot slot = (Slot)this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if(index == 36) {
				if(!mergeItemStack(itemstack1, 0, 36, false))
					return null;
			}
			else if(cameraInventory.isItemValidForSlot(0, itemstack1)) {
				if(!mergeItemStack(itemstack1, 36, 37, false))
					return null;
			}
			else if(index >= 0 && index < 9) {
				if(!mergeItemStack(itemstack1, 9, 36, false))
					return null;
			}
			else if(index >= 9 && index < 36) {
				if(!mergeItemStack(itemstack1, 0, 9, false))
					return null;
			}
			if(itemstack1.stackSize == 0)
				slot.putStack(null);
			else slot.onSlotChanged();
			if(itemstack1.stackSize == itemstack.stackSize)
				return null;
			slot.onPickupFromSlot(playerIn, itemstack1);
		}
		return itemstack;
	}
	
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
		if(slotId == player.inventory.currentItem)
			return null;
		return super.slotClick(slotId, dragType, clickTypeIn, player);
	}
}
