package com.andreid278.shootit.common.item;

import java.util.List;

import javax.annotation.Nullable;

import com.andreid278.shootit.ShootIt;
import com.andreid278.shootit.client.gui.GuiHandler;
import com.andreid278.shootit.common.MCData;
import com.andreid278.shootit.common.ShootItCreativeTab;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class Camera extends Item {
	public Camera() {
		this.setUnlocalizedName("camera");
		this.setRegistryName(ShootIt.modId + ":camera");
		this.setCreativeTab(ShootItCreativeTab.tab);
		this.setMaxStackSize(1);
	}
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if(stack.hasTagCompound()) {
			if(!stack.getTagCompound().getBoolean("hasMemoryCard"))
				tooltip.add("No Memory Card");
			else {
				int[] seq = stack.getTagCompound().getIntArray("indexes");
				tooltip.add(seq.length + "/" + MemoryCard.maxPhoto + " photos");
			}
		}
		else tooltip.add("No Memory Card");
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemStack = playerIn.getHeldItem(handIn);
		if(!itemStack.hasTagCompound())
			itemStack.setTagCompound(new NBTTagCompound());
		if(playerIn.isSneaking()) {
			if(!worldIn.isRemote)
				playerIn.openGui(ShootIt.instance, GuiHandler.CAMERA_GUI, worldIn, 0, 0, 0);
			return new ActionResult(EnumActionResult.SUCCESS, itemStack);
		}
		if(worldIn.isRemote) {
			NBTTagCompound compound = itemStack.getTagCompound();
			if(compound.getBoolean("hasMemoryCard")) {
				if(MCData.imageIDToLoadToServer == 0)
					if(!Minecraft.getMinecraft().gameSettings.hideGUI)
						MCData.isShooting = true;
			}
			else playerIn.sendMessage(new TextComponentString("No memory card!!!"));
			}
		return new ActionResult(EnumActionResult.SUCCESS, itemStack);
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		return EnumActionResult.FAIL;
	}
}
