package com.andreid278.shootit.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.andreid278.shootit.client.Resources;

import net.minecraft.util.ResourceLocation;

public class PhotosData {
	public static Map<Integer, ResourceLocation> photos = new HashMap<Integer, ResourceLocation>();
	public static List<Integer> photosToLoad = new ArrayList<Integer>();
	
	public static void addPhoto(String name) {
		photos.put(Integer.parseInt(name.substring(0, name.lastIndexOf('.'))), new ResourceLocation("photos", MCData.resourceLocationPath + "/" + name));
	}

	public static void addPhoto(int index) {
		photos.put(index, new ResourceLocation("photos", MCData.resourceLocationPath + "/" + index + ".png"));
	}
	
	public static void addEmptyPhoto(int index) {
		photos.put(index, Resources.PHOTO_ERROR_RL);
	}

	public static boolean isPhotoLoading(int index) {
		return photosToLoad.contains(index);
	}
}
