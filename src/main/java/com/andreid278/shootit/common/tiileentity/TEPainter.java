package com.andreid278.shootit.common.tiileentity;

import com.andreid278.shootit.common.container.PainterContainer;
import com.andreid278.shootit.common.item.MemoryCard;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.util.Constants;

public class TEPainter extends TileEntity implements IInventory {
	public ItemStack[] inventory;
	public int curPhoto;
	public PainterContainer parent;

	public TEPainter() {
		inventory = new ItemStack[2];
		for(int i = 0; i < 2; i++)
			inventory[i] = ItemStack.EMPTY;
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
			NBTTagCompound tagCompound = new NBTTagCompound();
			tagCompound.setByte("slot", (byte)i);
			inventory[i].writeToNBT(tagCompound);
			tagList.appendTag(tagCompound);
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
				inventory[slot] = new ItemStack(tagCompound);
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
		return index < inventory.length ? inventory[index] : ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		if(!getStackInSlot(index).isEmpty()) {
			if (index >= 0 && index < inventory.length && count > 0) {
				ItemStack itemstack = inventory[index].splitStack(count);

				if (inventory[index].getCount() == 0)
					setInventorySlotContents(index, ItemStack.EMPTY);
				else markDirty();
				return itemstack;
			}
			else return ItemStack.EMPTY;
		}
		else return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack itemStack = getStackInSlot(index);
		if(index == 0)
			curPhoto = 0;
		if(index < inventory.length) {
			setInventorySlotContents(index, ItemStack.EMPTY);
			return itemStack;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		if(index < inventory.length) {
			if(index == 0) {
				if(!stack.isEmpty()) {
					if(!stack.hasTagCompound())
						stack.setTagCompound(new NBTTagCompound());
					if(inventory[index].isEmpty())
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
	public boolean isUsableByPlayer(EntityPlayer player) {
		return this.world.getTileEntity(this.pos) != this ? false : player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
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
			setInventorySlotContents(i, ItemStack.EMPTY);
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}
}
