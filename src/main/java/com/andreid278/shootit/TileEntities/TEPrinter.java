package com.andreid278.shootit.TileEntities;

import java.lang.reflect.Field;
import java.util.List;

import com.andreid278.shootit.Main;
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

public class TEPrinter extends TileEntity implements IInventory {
	public ItemStack[] inventory;
	public byte width;
	public byte height;
	public int curPhoto;
	public boolean checkboxCustom;
	public PrinterContainer parent;

	public TEPrinter() {
		inventory = new ItemStack[9];
		width = 1;
		height = 1;
		curPhoto = 0;
		checkboxCustom = false;
	}

	public TEPrinter(PrinterContainer container) {
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
		compound.setTag("printer", tagList);
		compound.setByte("width", width);
		compound.setByte("height", height);
		compound.setInteger("curPhoto", curPhoto);
		compound.setBoolean("custom", checkboxCustom);
		return compound;
	}

	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		NBTTagList tagList = compound.getTagList("printer", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tagCompound = tagList.getCompoundTagAt(i);
			byte slot = tagCompound.getByte("slot");
			if(slot >= 0 && slot < inventory.length)
				inventory[slot] = ItemStack.loadItemStackFromNBT(tagCompound);
		}
		width = compound.getByte("width");
		height = compound.getByte("height");
		curPhoto = compound.getInteger("curPhoto");
		checkboxCustom = compound.getBoolean("custom");
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
		return "container.printer";
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
			if(index == 7|| index == 8) {
				Class container = parent.getClass();
				Field listenersField = null;
				try {
					listenersField = container.getSuperclass().getDeclaredField("listeners");
				} catch (NoSuchFieldException | SecurityException e) {
					try {
						listenersField = container.getSuperclass().getDeclaredField("field_75149_d");
					} catch (NoSuchFieldException | SecurityException e1) {
						e1.printStackTrace();
					}
				}
				if(listenersField != null) {
					try {
						listenersField.setAccessible(true);
						List<IContainerListener> listeners = (List<IContainerListener>) listenersField.get(parent);

						if(stack == null)
							for(IContainerListener listener : listeners) {
								if(listener instanceof EntityPlayerMP)
									Main.network.sendTo(new MessagePrinterToClient(getPos(), (byte)(index), null), (EntityPlayerMP)listener);
							}
						else
							for(IContainerListener listener : listeners)
								if(listener instanceof EntityPlayerMP)
									Main.network.sendTo(new MessagePrinterToClient(getPos(), (byte)(index - 2), stack), (EntityPlayerMP)listener);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			inventory[index] = stack;
			markDirty();
		}
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
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
		String dstOd = "";
		switch(index) {
		case 1:
			dstOd = "dyeBlack";
			break;
		case 2:
			dstOd = "dyeBlue";
			break;
		case 3:
			dstOd = "dyeGreen";
			break;
		case 4:
			dstOd = "dyeRed";
			break;
		case 5:
			dstOd = "paper";
			break;
		}
		if(index == 6)
			return false;
		if(index == 0) {
			if(stack.getItem() instanceof MemoryCard)
				return true;
		}
		else if(index > 0 && index < 6) {
			int [] od = OreDictionary.getOreIDs(stack);
			for(int i = 0; i < od.length; i++) {
				if(OreDictionary.getOreName(od[i]).compareTo(dstOd) == 0)
					return true;
			}
		}
		if(index == 7 || index == 8)
			return (stack.getItem() instanceof ItemBlock)
					//					&& !(Block.getBlockFromItem(stack.getItem()) instanceof ITileEntityProvider)
					&& Block.getBlockFromItem(stack.getItem()).isFullCube(Block.getBlockFromItem(stack.getItem()).getStateFromMeta(stack.getMetadata()));
		return false;
	}

	@Override
	public int getField(int id) {
		switch(id) {
		case 0:
			return width;
		case 1:
			return height;
		case 2:
			return curPhoto;
		}
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		switch(id) {
		case 0:
			width = (byte)value;
			break;
		case 1:
			height = (byte)value;
			break;
		case 2:
			curPhoto = value;
			break;
		}
		markDirty();
	}

	@Override
	public int getFieldCount() {
		return 3;
	}

	@Override
	public void clear() {
		for(int i = 0; i < inventory.length; i++)
			setInventorySlotContents(i, null);
	}
}
