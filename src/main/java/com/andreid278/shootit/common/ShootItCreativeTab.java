package com.andreid278.shootit.common;

import com.andreid278.shootit.ShootIt;
import com.andreid278.shootit.common.item.PhotoItem;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ShootItCreativeTab extends CreativeTabs {

	public static final ItemStack stack = new ItemStack(new PhotoItem());
	
	public ShootItCreativeTab(String label) {
		super(label);
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(CommonProxy.photoItem);
	}
	
	public static final ShootItCreativeTab tab = new ShootItCreativeTab("ShootIt");
}
