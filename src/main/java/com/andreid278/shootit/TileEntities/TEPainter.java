package com.andreid278.shootit.TileEntities;

import java.lang.reflect.Field;
import java.util.List;

import com.andreid278.shootit.Main;
import com.andreid278.shootit.Containers.PainterContainer;
import com.andreid278.shootit.Containers.PrinterContainer;
import com.andreid278.shootit.Items.MemoryCard;
import com.andreid278.shootit.Network.MessagePrinterToClient;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.oredict.OreDictionary;

public class TEPainter extends TileEntity implements IInventory {
	public ItemStack[] inventory;
	public int curPhoto;
	public PainterContainer parent;

	public TEPainter() {
		inventory = new ItemStack[2];
		curPhoto = 0;
	}

	public TEPainter(PainterContainer container) {
		super();
		this.parent = container;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		NBTTagList tagList = new NBTTagList();
		for(int i = 0; i < inventory.length; i++) {
			if(inventory[i] != null) {
				NBTTagCompound tagCompound = new NBTTagCompound();
				tagCompound.setByte("slot", (byte)i);
				inventory[i].writeToNBT(tagCompound);
				tagList.appendTag(tagCompound);
			}
		}
		compound.setTag("painter", tagList);
		compound.setInteger("curPhoto", curPhoto);
		return compound;
	}

	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		NBTTagList tagList = compound.getTagList("painter", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tagCompound = tagList.getCompoundTagAt(i);
			byte slot = tagCompound.getByte("slot");
			if(slot >= 0 && slot < inventory.length)
				inventory[slot] = ItemStack.loadItemStackFromNBT(tagCompound);
		}
		curPhoto = compound.getInteger("curPhoto");
	}

	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
	}

	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	public void onDataPacket(net.minecraft.network.NetworkManager net, net.minecraft.network.play.server.SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public String getName() {
		return "container.painter";
	}

	@Override
	public boolean hasCustomName() {
		return getName() != null && !getName().equals("");
	}

	@Override
	public ITextComponent getDisplayName() {
		return (ITextComponent)(this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName(), new Object[0]));
	}

	@Override
	public int getSizeInventory() {
		return inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return index < inventory.length ? inventory[index] : null;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		if(getStackInSlot(index) != null) {
			if (index >= 0 && index < inventory.length && inventory[index] != null && count > 0) {
				ItemStack itemstack = inventory[index].splitStack(count);

				if (inventory[index].stackSize == 0)
					setInventorySlotContents(index, null);
				else markDirty();
				return itemstack;
			}
			else return null;
		}
		else return null;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack itemStack = getStackInSlot(index);
		if(index == 0)
			curPhoto = 0;
		if(index < inventory.length) {
			setInventorySlotContents(index, null);
			return itemStack;
		}
		return null;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		if(index < inventory.length) {
			if(index == 0) {
				if(stack != null) {
					if(!stack.hasTagCompound())
						stack.setTagCompound(new NBTTagCompound());
					if(inventory[index] == null)
						curPhoto = 0;
					else if(stack.getTagCompound().getIntArray("indexes").length < inventory[index].getTagCompound().getIntArray("indexes").length)
						curPhoto = 0;
				}
				else curPhoto = 0;
			}
			inventory[index] = stack;
			markDirty();
		}
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public void markDirty() {
		super.markDirty();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.worldObj.getTileEntity(this.pos) != this ? false : player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		if(index == 0 || index == 1) {
			if(stack.getItem() instanceof MemoryCard)
				return true;
		}
		return false;
	}

	@Override
	public int getField(int id) {
		switch(id) {
		case 0:
			return curPhoto;
		}
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		switch(id) {
		case 0:
			curPhoto = value;
			break;
		}
		markDirty();
	}

	@Override
	public int getFieldCount() {
		return 1;
	}

	@Override
	public void clear() {
		for(int i = 0; i < inventory.length; i++)
			setInventorySlotContents(i, null);
	}
}
