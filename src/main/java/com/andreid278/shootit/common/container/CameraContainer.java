package com.andreid278.shootit.common.container;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class CameraContainer extends Container {
	public CameraInventory cameraInventory;
	public CameraContainer(InventoryPlayer inventoryPlayer, CameraInventory cameraInventory) {
		this.cameraInventory = cameraInventory;

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventoryPlayer, i, 12, 37 + i * 18));
		}

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 34 + i * 18, 37 + j * 18));
			}
		}

		addSlotToContainer(new ModSlot(cameraInventory, 0, 12, 12));
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	@Nullable
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = (Slot)this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if(index == 36) {
				if(!mergeItemStack(itemstack1, 0, 36, false))
					return ItemStack.EMPTY;
//				for (int j = 0; j < this.listeners.size(); ++j)
//				{
//					((IContainerListener)this.listeners.get(j)).sendSlotContents(this, playerIn.inventory.currentItem, ((Slot)this.inventorySlots.get(playerIn.inventory.currentItem)).getStack());
//				}
			}
			else if(cameraInventory.isItemValidForSlot(0, itemstack1)) {
				if(!mergeItemStack(itemstack1, 36, 37, false))
					return ItemStack.EMPTY;
				for (int j = 0; j < this.listeners.size(); ++j)
				{
					((IContainerListener)this.listeners.get(j)).sendSlotContents(this, playerIn.inventory.currentItem, ((Slot)this.inventorySlots.get(playerIn.inventory.currentItem)).getStack());
				}
			}
			else if(index >= 0 && index < 9) {
				if(!mergeItemStack(itemstack1, 9, 36, false))
					return ItemStack.EMPTY;
			}
			else if(index >= 9 && index < 36) {
				if(!mergeItemStack(itemstack1, 0, 9, false))
					return ItemStack.EMPTY;
			}
			if(itemstack1.getCount() == 0)
				slot.putStack(ItemStack.EMPTY);
			else slot.onSlotChanged();
			if(itemstack1.getCount() == itemstack.getCount())
				return ItemStack.EMPTY;
			slot.onTake(playerIn, itemstack1);
		}
		return itemstack;
	}

	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
		if(slotId == player.inventory.currentItem)
			return ItemStack.EMPTY;
		ItemStack res = super.slotClick(slotId, dragType, clickTypeIn, player);

		if(slotId == 36) {
			for (int j = 0; j < this.listeners.size(); ++j)
			{
				((IContainerListener)this.listeners.get(j)).sendSlotContents(this, player.inventory.currentItem, ((Slot)this.inventorySlots.get(player.inventory.currentItem)).getStack());
			}
		}

		return res;
	}
}
