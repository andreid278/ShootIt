package com.andreid278.shootit.common.worlddata;

import java.util.Date;
import java.util.Map;

import com.andreid278.shootit.common.MCData;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class WorldData extends WorldSavedData {
	public static String name = "shootit_1";
	public NBTTagCompound data = new NBTTagCompound();

	public WorldData() {
		super(name);
	}

	public WorldData(String name) {
		super(name);
	}

	public static WorldData getForWorld(World world) {
		MapStorage storage = world.getMapStorage();
		WorldData result = (WorldData)storage.getOrLoadData(WorldData.class, name);
		if(result == null) {
			result = new WorldData();
			storage.setData(name, result);
		}
		return result;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		data = nbt.getCompoundTag("tagName");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setTag("tagName", data);
		return nbt;
	}

	public int increasePhotoID() {
		data.setInteger("photoID", data.getInteger("photoID") + 1);
		this.markDirty();
		return data.getInteger("photoID");
	}
	
	public boolean needChecking(Date date) {
		if(!data.getBoolean("needChecking"))
			return false;
		Date prevDate = new Date(data.getLong("date"));
		if(prevDate.getTime() == 0) {
			data.setLong("date", date.getTime());
			this.markDirty();
			return false;
		}
		if(date.getTime() - prevDate.getTime() > 1000 * 60 * 60 * 24 * MCData.timeBetweenChecksInDays) {
			data.setLong("date", date.getTime());
			this.markDirty();
			return true;
		}
		return false;
	}
	
	public void toggleChecking() {
		data.setBoolean("needChecking", !data.getBoolean("needChecking"));
		this.markDirty();
		Date date = new Date();
		for(Map.Entry<Integer, Date> a : MCData.lastLoadings.entrySet()) {
			a.setValue(date);
		}
	}
}
