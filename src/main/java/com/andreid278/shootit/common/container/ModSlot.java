package com.andreid278.shootit.common.container;

import javax.annotation.Nullable;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ModSlot extends Slot {
	public ModSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	}
	
	public boolean isItemValid(@Nullable ItemStack stack) {
		return inventory.isItemValidForSlot(getSlotIndex(), stack);
	}
}
