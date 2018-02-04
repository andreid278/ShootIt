package com.andreid278.shootit.common.item;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.List;

import javax.annotation.Nullable;

import com.andreid278.shootit.ShootIt;
import com.andreid278.shootit.common.CommonProxy;
import com.andreid278.shootit.common.ShootItCreativeTab;
import com.andreid278.shootit.common.entity.EntityPainting;

import net.minecraft.client.util.ITooltipFlag;
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
		this.setRegistryName(ShootIt.modId + ":photoitem");
		this.setCreativeTab(ShootItCreativeTab.tab);
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(!worldIn.isRemote) {
			ItemStack stack = player.getHeldItem(hand);
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
				EntityPainting ep = new EntityPainting(worldIn, pos, facing, width, height, index, ((player.getHorizontalFacing().getHorizontalIndex() + 1) * 3) % 4, framesRL, backRL, textureCoords[0], textureCoords[1], textureCoords[2], textureCoords[3]);
				worldIn.spawnEntity(ep);
				if(!player.isCreative()) {
					stack.setCount(stack.getCount() - 1);
				}
			}
		}
		return EnumActionResult.SUCCESS;
	}
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if(stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();
			tooltip.add("Image " + nbt.getInteger("index"));
			tooltip.add("Size " + nbt.getByte("width") + "x" + nbt.getByte("height"));
		}
	}
}