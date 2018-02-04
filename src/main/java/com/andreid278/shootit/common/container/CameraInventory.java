package com.andreid278.shootit.common.container;

import com.andreid278.shootit.common.CommonProxy;
import com.andreid278.shootit.common.item.MemoryCard;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class CameraInventory implements IInventory {
	public final ItemStack invItem;
	public static final int size = 1;
	public ItemStack memoryCard;

	public CameraInventory(ItemStack invItem) {
		this.invItem = invItem;
		if(!invItem.hasTagCompound())
			invItem.setTagCompound(new NBTTagCompound());
		readFromNBT(invItem.getTagCompound());
	}

	public void readFromNBT(NBTTagCompound nbt) {
		if(invItem.hasTagCompound())
			if(invItem.getTagCompound().getBoolean("hasMemoryCard")) {
				memoryCard = new ItemStack(CommonProxy.memoryCardItem);
				NBTTagCompound compound = new NBTTagCompound();
				compound.setIntArray("indexes", nbt.getIntArray("indexes"));
				memoryCard.setTagCompound(compound);
				return;
			}
		memoryCard = ItemStack.EMPTY;
	}
	
	@Override
	public String getName() {
		return "container.camera";
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
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return memoryCard;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		if(!getStackInSlot(index).isEmpty() && count > 0) {
			ItemStack temp = memoryCard.copy();
			setInventorySlotContents(index, ItemStack.EMPTY);
			return temp;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		/*ItemStack itemStack = getStackInSlot(index);
		if(invItem.hasTagCompound()) {
			NBTTagCompound nbt = invItem.getTagCompound();
			nbt.setInteger("curPhoto", 0);
			nbt.setIntArray("indexes", new int[]{});
			nbt.setBoolean("hasMemoryCard", false);
			invItem.setTagCompound(nbt);
		}
		memoryCard = ItemStack.EMPTY;
		markDirty();*/
		setInventorySlotContents(0, ItemStack.EMPTY);
		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		memoryCard = stack;
		NBTTagCompound nbt = null;
		if(invItem.hasTagCompound())
			nbt = invItem.getTagCompound();
		else nbt = new NBTTagCompound();
		if(!stack.isEmpty()) {
			NBTTagCompound itemTag = null;
			if(!stack.hasTagCompound()) {
				itemTag = new NBTTagCompound();
				itemTag.setIntArray("indexes", new int[]{});
				stack.setTagCompound(itemTag);
			}
			else itemTag = stack.getTagCompound();
			nbt.setIntArray("indexes", itemTag.getIntArray("indexes"));
			nbt.setBoolean("hasMemoryCard", true);
			nbt.setInteger("curPhoto", 0);
		}
		else {
			nbt.setInteger("curPhoto", 0);
			nbt.setIntArray("indexes", new int[]{});
			nbt.setBoolean("hasMemoryCard", false);
		}
		invItem.setTagCompound(nbt);
		markDirty();
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public void markDirty() {
		
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		if(index == 0)
			if(!(stack.getItem() instanceof MemoryCard))
				return false;
		return true;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		setInventorySlotContents(0, ItemStack.EMPTY);
	}

	@Override
	public boolean isEmpty() {
		return memoryCard.isEmpty();
	}
}
