package com.andreid278.shootit.Containers;

import javax.annotation.Nullable;

import com.andreid278.shootit.TileEntities.TEPrinter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class PrinterContainer extends Container {
	public TEPrinter te;
	public PrinterContainer(InventoryPlayer inventory, TEPrinter te) {
		this.te = te;
		
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142));
		}
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		addSlotToContainer(new ModSlot(te, 0, 8, 8));
		
		for(int i = 0; i < 4; i++)
			addSlotToContainer(new ModSlot(te, i + 1, 8 + i * 18, 60));
		
		addSlotToContainer(new ModSlot(te, 5, 88, 60));
		
		addSlotToContainer(new ModSlot(te, 6, 152, 60));
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUseableByPlayer(playerIn);
	}

	@Nullable
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = null;
		Slot slot = (Slot)this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if(index >= 36 && index < 43) {
				if(!mergeItemStack(itemstack1, 0, 36, false))
					return null;
			}
			else if(te.isItemValidForSlot(0, itemstack1)) {
				if(!mergeItemStack(itemstack1, 36, 37, false))
					return null;
			}
			else if(te.isItemValidForSlot(1, itemstack1)) {
				if(!mergeItemStack(itemstack1, 37, 38, false))
					return null;
			}
			else if(te.isItemValidForSlot(2, itemstack1)) {
				if(!mergeItemStack(itemstack1, 38, 39, false))
					return null;
			}
			else if(te.isItemValidForSlot(3, itemstack1)) {
				if(!mergeItemStack(itemstack1, 39, 40, false))
					return null;
			}
			else if(te.isItemValidForSlot(4, itemstack1)) {
				if(!mergeItemStack(itemstack1, 40, 41, false))
					return null;
			}
			else if(te.isItemValidForSlot(5, itemstack1)) {
				if(!mergeItemStack(itemstack1, 41, 42, false))
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
}