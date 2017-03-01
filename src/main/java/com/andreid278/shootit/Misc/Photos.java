package com.andreid278.shootit.Misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;

public class Photos {
	public static Map<Integer, ResourceLocation> photos = new HashMap<Integer, ResourceLocation>();
	public static List<Integer> photosToLoad = new ArrayList<Integer>();
	
	public static void addPhoto(String name) {
		photos.put(Integer.parseInt(name.substring(0, name.lastIndexOf('.'))), new ResourceLocation("photos", Statics.resourceLocationPath + "/" + name));
	}

	public static void addPhoto(int index) {
		photos.put(index, new ResourceLocation("photos", Statics.resourceLocationPath + "/" + index + ".png"));
	}
	
	public static void addEmptyPhoto(int index) {
		photos.put(index, Statics.PHOTO_ERROR_RL);
	}

	public static boolean isPhotoLoading(int index) {
		return photosToLoad.contains(index);
	}
}
