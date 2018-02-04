package com.andreid278.shootit.common;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.io.Files;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

public class MCData {
	public static final MCData instance = new MCData();
	
	public static String photosFolderPathClient = "";
	public static String photosFolderPathServer = "";
	public static String resourceLocationPath = "";
	
	public static boolean isShooting = false;
	
	public static final int x1 = 44, y1 = 10, x2 = 211, y2 = 131;
	
	public static Map<Integer, Date> lastLoadings = new HashMap<Integer, Date>();
	
	public static int timeBetweenChecksInDays = 12;
	
	public static final int partSize = 32000;
	
	public static float fov;
	public static float cameraFov;
	
	public static Map<UUID, Boolean> cameraHoldPhase = new HashMap<UUID, Boolean>();
	
	public static int imageIDToLoadToServer = 0;
	public static byte[] imageToLoadToServer = null;
	public static int startIndex = 0;
	public static int startTime = 0;
	public static Map<Integer, ImageInfo> imagesFromClients = new HashMap<Integer, ImageInfo>();
	public class ImageInfo {
		public byte[] byteBuffer;
		public int partLengthSum;
		
		public ImageInfo(int length) {
			byteBuffer = new byte[length];
			partLengthSum = 0;
		}
	}
	
	public static int imageIDToLoadFromServer = 0;
	public static byte[] imageToLoadFromServer = null;
	public static int partLengthSum = 0;
	public static Map<EntityPlayerMP, ImageInfoToClient> imagesToClients = new HashMap<EntityPlayerMP, MCData.ImageInfoToClient>();
	public class ImageInfoToClient {
		public byte[] byteBuffer;
		public int index;
		public int photoID;
		
		public ImageInfoToClient(int photoID) {
			this.photoID = photoID;
			index = 0;
			File imageFile = new File(MCData.photosFolderPathServer + "/" + photoID + ".png");
			if(imageFile.exists())
				try {
					byteBuffer = Files.toByteArray(imageFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	public static int lastShader = 0;
	public static List<ShaderInfo> shaders = new ArrayList<>();
	public class ShaderInfo {
		public String name;
		public ResourceLocation rl;
		
		public ShaderInfo(String name, ResourceLocation rl) {
			this.name = name;
			this.rl = rl;
		}
		
		public ShaderInfo(String name) {
			this.name = name;
			rl = null;
		}
	}
}
