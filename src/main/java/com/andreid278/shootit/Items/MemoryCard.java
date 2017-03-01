package com.andreid278.shootit.Items;

import java.util.List;

import com.andreid278.shootit.CommonProxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class MemoryCard extends Item {
	public MemoryCard() {
		this.setUnlocalizedName("memorycard");
		this.setRegistryName("memorycard");
		this.setCreativeTab(CommonProxy.modTab);
		this.setMaxStackSize(1);
	}

	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		if(stack.hasTagCompound()) {
			int[] seq = stack.getTagCompound().getIntArray("indexes");
			tooltip.add(seq.length + "/16 photos");
		}
		else tooltip.add("0/16 photos");
	}
}
