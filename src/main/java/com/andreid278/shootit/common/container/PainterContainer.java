package com.andreid278.shootit.common.container;

import javax.annotation.Nullable;

import com.andreid278.shootit.common.tiileentity.TEPainter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class PainterContainer extends Container {
public TEPainter te;
	
	public PainterContainer(InventoryPlayer inventory, TEPainter te) {
		this.te = te;
		this.te.parent = this;
		
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 176));
		}

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 118 + i * 18));
			}
		}

		addSlotToContainer(new ModSlot(te, 0, 8, 94));
		addSlotToContainer(new ModSlot(te, 1, 152, 94));
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}

	@Nullable
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		return ItemStack.EMPTY;
	}
}
