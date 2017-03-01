package com.andreid278.shootit.Items;

import java.util.List;

import com.andreid278.shootit.CommonProxy;
import com.andreid278.shootit.Entity.EntityPainting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PhotoItem extends Item {
	public PhotoItem() {
		this.setUnlocalizedName("photoitem");
		this.setRegistryName("photoitem");
		this.setCreativeTab(CommonProxy.modTab);
	}

	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(!worldIn.isRemote) {
			if(stack.hasTagCompound()) {
				NBTTagCompound compound = stack.getTagCompound();
				int index = compound.getInteger("index");
				byte width = compound.getByte("width");
				byte height = compound.getByte("height");
				EntityPainting ep = new EntityPainting(worldIn, pos, facing, width, height, index);
				worldIn.spawnEntityInWorld(ep);
				if(!playerIn.isCreative()) {
					stack.stackSize--;
				}
			}
		}
		return EnumActionResult.SUCCESS;
	}
	
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		if(stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();
			tooltip.add("Image " + nbt.getInteger("index"));
			tooltip.add("Size " + nbt.getByte("width") + "x" + nbt.getByte("height"));
		}
	}
}
