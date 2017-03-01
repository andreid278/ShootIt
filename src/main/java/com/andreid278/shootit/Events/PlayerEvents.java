package com.andreid278.shootit.Events;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import com.andreid278.shootit.Items.Camera;
import com.andreid278.shootit.Misc.Photos;
import com.andreid278.shootit.Misc.Statics;
import com.andreid278.shootit.Misc.Statics.ImageInfo;
import com.andreid278.shootit.Misc.Statics.ImageInfoToClient;
import com.andreid278.shootit.WorldData.WorldData;
import com.andreid278.shootit.WorldData.WorldDataDates;
import com.andreid278.shootit.Main;
import com.andreid278.shootit.Network.MessagePlayerLoggedIn;
import com.andreid278.shootit.Network.PhotoLoaderToClient;
import com.andreid278.shootit.Network.PhotoLoaderToServer;
import com.andreid278.shootit.Renderer.ArmModelRenderer;
import com.google.common.io.Files;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class PlayerEvents {
	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if(!event.player.worldObj.isRemote) {
			if(!event.player.worldObj.getMinecraftServer().isDedicatedServer()) {
				try {
					String s = event.player.worldObj.getSaveHandler().getWorldDirectory().getCanonicalPath();
					Statics.photosFolderPathServer = event.player.worldObj.getMinecraftServer().getDataDirectory().getCanonicalPath() + Statics.slash + "photos" + Statics.slash + "assets" + Statics.slash + "photos" + Statics.slash + "Singleplayer";
					Statics.photosFolderPathServer += s.substring(s.lastIndexOf(Statics.slash));
					Statics.photosFolderPathClient = Statics.photosFolderPathServer;
					Statics.resourceLocationPath = "Singleplayer/" + s.substring(s.lastIndexOf(Statics.slash) + 1);
					File file = new File(Statics.photosFolderPathServer);
					if(!file.isDirectory())
						file.mkdirs();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				if(Statics.photosFolderPathServer == "") {
					try {
						Statics.photosFolderPathServer = event.player.worldObj.getMinecraftServer().getDataDirectory().getCanonicalPath() + Statics.slash + "photos";
						File file = new File(Statics.photosFolderPathServer);
						if(!file.isDirectory())
							file.mkdirs();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					WorldDataDates data = WorldDataDates.getForWorld(event.player.worldObj);
					data.readDates();
					if(Statics.imagesToClients.containsKey(event.player))
						Statics.imagesToClients.remove(event.player);
					if(Statics.imagesFromClients.containsKey(event.player))
						Statics.imagesFromClients.remove(event.player);
					WorldData worldData = WorldData.getForWorld(event.player.worldObj);
					if(worldData.data.getLong("date") == 0) {
						worldData.data.setLong("date", new Date().getTime());
						worldData.markDirty();
					}
				}
			}
			Main.network.sendTo(new MessagePlayerLoggedIn(), (EntityPlayerMP) event.player);
		}
	}

	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
		if(!event.player.worldObj.isRemote) {
			if(Statics.imagesToClients.containsKey(event.player))
				Statics.imagesToClients.remove(event.player);
			if(event.player.worldObj.getMinecraftServer().isDedicatedServer()) {
				Date date = new Date();
				if(WorldData.getForWorld(event.player.worldObj).needChecking(date)) {
					System.out.println("Checking...");
					int i = 0;
					File file = new File(Statics.photosFolderPathServer);
					Iterator<Entry<Integer, Date>> it = Statics.lastLoadings.entrySet().iterator();
					while(it.hasNext()) {
						Entry<Integer, Date> entry = it.next();
						if(date.getTime() - entry.getValue().getTime() > 1000 * 60 * 60 * 24 * Statics.timeBetweenChecksInDays) {
							File file1 = new File(file, entry.getKey() + ".png");
							if(file1.exists())
								file1.delete();
							it.remove();
							i++;
						}
					}
					System.out.println("Removed " + i + " elements");
				}
			}
		}
	}

	@SubscribeEvent
	public void updatePlayer(PlayerTickEvent event) {
		if(event.player.worldObj.isRemote) {
			if(Statics.imageIDToLoadToServer > 0) {
				if(event.player.ticksExisted % 10 == 0) {
					if(Statics.imageToLoadToServer == null) {
						File imageFile = new File(Statics.photosFolderPathClient + Statics.slash + Statics.imageIDToLoadToServer + ".png");
						if(!imageFile.exists()) {
							Statics.imageIDToLoadToServer = 0;
							Statics.imageToLoadToServer = null;
							return;
						} else
							try {
								Statics.imageToLoadToServer = Files.toByteArray(imageFile);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					}
					int length = Statics.imageToLoadToServer.length - Statics.partSize * Statics.startIndex;
					if(length > Statics.partSize)
						length = Statics.partSize;
					Main.network.sendToServer(new PhotoLoaderToServer(Statics.imageIDToLoadToServer, Statics.startIndex, length, Statics.imageToLoadToServer.length));
					if(Statics.partSize * Statics.startIndex + length == Statics.imageToLoadToServer.length) {
						Statics.imageIDToLoadToServer = 0;
						Statics.imageToLoadToServer = null;
						Statics.startIndex = 0;
					}
					else Statics.startIndex++;
				}
			}
		}
		else if(event.player.ticksExisted % 10 == 0)
			if(!Statics.imagesToClients.isEmpty())
				if(Statics.imagesToClients.containsKey(event.player)) {
					ImageInfoToClient imageInfo = Statics.imagesToClients.get(event.player);
					int length = imageInfo.byteBuffer.length - Statics.partSize * Statics.imagesToClients.get(event.player).index;
					if(length > Statics.partSize)
						length = Statics.partSize;
					Main.network.sendTo(new PhotoLoaderToClient(imageInfo.photoID, imageInfo.index, length, imageInfo.byteBuffer.length, event.player), (EntityPlayerMP) event.player);
					if(Statics.partSize * imageInfo.index + length == imageInfo.byteBuffer.length) {
						Statics.imagesToClients.remove(event.player);
					}
					else Statics.imagesToClients.get(event.player).index++;
				}
	}
}
