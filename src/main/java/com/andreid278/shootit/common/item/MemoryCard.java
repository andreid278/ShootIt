package com.andreid278.shootit.common.item;

import java.util.List;

import javax.annotation.Nullable;

import com.andreid278.shootit.ShootIt;
import com.andreid278.shootit.common.ShootItCreativeTab;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MemoryCard extends Item {
	
	public static final int maxPhoto = 16;
	public MemoryCard() {
		this.setUnlocalizedName("memorycard");
		this.setRegistryName(ShootIt.modId + ":memorycard");
		this.setCreativeTab(ShootItCreativeTab.tab);
		this.setMaxStackSize(1);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if(stack.hasTagCompound()) {
			int[] seq = stack.getTagCompound().getIntArray("indexes");
			tooltip.add(seq.length + "/" + maxPhoto + " photos");
		}
		else tooltip.add("0/" + maxPhoto + " photos");
	}
}
