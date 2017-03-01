package com.andreid278.shootit.Items;

import java.util.List;

import com.andreid278.shootit.CommonProxy;
import com.andreid278.shootit.Main;
import com.andreid278.shootit.Gui.CameraGui;
import com.andreid278.shootit.Gui.GuiHandler;
import com.andreid278.shootit.Misc.Statics;
import com.andreid278.shootit.WorldData.WorldData;

import net.minecraft.client.Minecraft;
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
	public boolean isShooting;

	public Camera() {
		this.setUnlocalizedName("camera");
		this.setRegistryName("camera");
		this.setCreativeTab(CommonProxy.modTab);
		this.setMaxStackSize(1);
	}

	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		if(!itemStackIn.hasTagCompound())
			itemStackIn.setTagCompound(new NBTTagCompound());
		if(playerIn.isSneaking()) {
			if(worldIn.isRemote)
				playerIn.openGui(Main.instance, GuiHandler.CAMERA_GUI, worldIn, 0, 0, 0);
			return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
		}
		if(worldIn.isRemote) {
			NBTTagCompound compound = itemStackIn.getTagCompound();
			if(compound.getBoolean("hasMemoryCard")) {
				if(Statics.imageIDToLoadToServer == 0)
					if(!Minecraft.getMinecraft().gameSettings.hideGUI)
						Statics.isShooting = true;
			}
			else playerIn.addChatMessage(new TextComponentString("No memory card!!!"));
		}
		return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
	}

	@Override
	public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		return EnumActionResult.FAIL;
	}

	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		if(stack.hasTagCompound()) {
			if(!stack.getTagCompound().getBoolean("hasMemoryCard"))
				tooltip.add("No Memory Card");
			else {
				int[] seq = stack.getTagCompound().getIntArray("indexes");
				tooltip.add(seq.length + "/16 photos");
			}
		}
		else tooltip.add("No Memory Card");
	}
}
