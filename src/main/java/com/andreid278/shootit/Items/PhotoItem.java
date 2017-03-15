package com.andreid278.shootit.Items;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
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
import net.minecraft.util.ResourceLocation;
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
				String s = compound.getString("frames");
				ResourceLocation framesRL = s.equals("") ? null : new ResourceLocation(s);
				s = compound.getString("back");
				ResourceLocation backRL = s.equals("") ? null : new ResourceLocation(s);
				byte[] byteArray = compound.getByteArray("textureCoords");
				double[] textureCoords = new double[4];
				if(byteArray.length == 0) {
					textureCoords[0] = 0;
					textureCoords[1] = 0;
					textureCoords[2] = 1;
					textureCoords[3] = 1;
				}
				else {
					DoubleBuffer doubleBuffer = ByteBuffer.wrap(byteArray).asDoubleBuffer();
					doubleBuffer.get(textureCoords);
				}
				EntityPainting ep = new EntityPainting(worldIn, pos, facing, width, height, index, ((playerIn.getHorizontalFacing().getHorizontalIndex() + 1) * 3) % 4, framesRL, backRL, textureCoords[0], textureCoords[1], textureCoords[2], textureCoords[3]);
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
