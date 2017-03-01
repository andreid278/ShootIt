package com.andreid278.shootit.WorldData;

import java.util.Date;
import java.util.Map;

import com.andreid278.shootit.Misc.Statics;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.util.Constants;

public class WorldDataDates extends WorldSavedData {
	public static String name = "shootit_2";
	public NBTTagCompound data = new NBTTagCompound();

	public WorldDataDates() {
		super(name);
	}

	public WorldDataDates(String name) {
		super(name);
	}

	public static WorldDataDates getForWorld(World world) {
		MapStorage storage = world.getMapStorage();
		WorldDataDates result = (WorldDataDates)storage.getOrLoadData(WorldDataDates.class, name);
		if(result == null) {
			result = new WorldDataDates();
			storage.setData(name, result);
		}
		return result;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		data = nbt.getCompoundTag("tagNameDates");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setTag("tagNameDates", data);
		return nbt;
	}
	
	public void writeDates() {
		NBTTagList tagList = new NBTTagList();
		for(Map.Entry<Integer, Date> date : Statics.lastLoadings.entrySet()) {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setInteger("photoID", date.getKey());
			compound.setLong("date", date.getValue().getTime());
			tagList.appendTag(compound);
		}
		data.setTag("lastLoadings", tagList);
		this.markDirty();
	}

	public void readDates() {
		NBTTagList tagList = data.getTagList("lastLoadings", Constants.NBT.TAG_COMPOUND);
		NBTTagCompound compound;
		int index;
		for(int i = 0; i < tagList.tagCount(); i++) {
			compound = tagList.getCompoundTagAt(i);
			index = compound.getInteger("photoID");
			Date date = new Date(compound.getLong("date"));
			Statics.lastLoadings.put(index, date);
		}
	}
}
