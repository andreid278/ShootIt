package com.andreid278.shootit.common.event;

import java.io.File;
import java.io.IOException;

import com.andreid278.shootit.ShootIt;
import com.andreid278.shootit.common.MCData;
import com.andreid278.shootit.common.MCData.ImageInfoToClient;
import com.andreid278.shootit.common.network.MessagePlayerLoggedIn;
import com.andreid278.shootit.common.network.PhotoLoaderToClient;
import com.andreid278.shootit.common.network.PhotoLoaderToServer;
import com.google.common.io.Files;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class PlayerEvents {
	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if(!event.player.world.getMinecraftServer().isDedicatedServer()) {
			try {
				String s = event.player.world.getSaveHandler().getWorldDirectory().getCanonicalPath();
				s = s.replace("\\", "/");
				MCData.photosFolderPathServer = event.player.world.getMinecraftServer().getDataDirectory().getCanonicalPath().replace("\\", "/") + "/photos/assets/photos/Singleplayer";
				MCData.photosFolderPathServer += s.substring(s.lastIndexOf("/"));
				MCData.photosFolderPathClient = MCData.photosFolderPathServer;
				MCData.resourceLocationPath = "Singleplayer/" + s.substring(s.lastIndexOf("/") + 1);
				File file = new File(MCData.photosFolderPathServer);
				if(!file.isDirectory())
					file.mkdirs();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			if(MCData.photosFolderPathServer == "") {
				try {
					MCData.photosFolderPathServer = event.player.world.getMinecraftServer().getDataDirectory().getCanonicalPath().replace("\\", "/") + "/photos";
					File file = new File(MCData.photosFolderPathServer);
					if(!file.isDirectory())
						file.mkdirs();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		ShootIt.network.sendTo(new MessagePlayerLoggedIn(), (EntityPlayerMP) event.player);
	}
	
	@SubscribeEvent
	public void updatePlayer(PlayerTickEvent event) {
		if(event.player.world.isRemote) {
			if(MCData.imageIDToLoadToServer > 0) {
				if(event.player.ticksExisted % 10 == 0) {
					if(MCData.imageToLoadToServer == null) {
						File imageFile = new File(MCData.photosFolderPathClient + "/" + MCData.imageIDToLoadToServer + ".png");
						if(!imageFile.exists()) {
							MCData.imageIDToLoadToServer = 0;
							MCData.imageToLoadToServer = null;
							return;
						} else
							try {
								MCData.imageToLoadToServer = Files.toByteArray(imageFile);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					}
					int length = MCData.imageToLoadToServer.length - MCData.partSize * MCData.startIndex;
					if(length > MCData.partSize)
						length = MCData.partSize;
					ShootIt.network.sendToServer(new PhotoLoaderToServer(MCData.imageIDToLoadToServer, MCData.startIndex, length, MCData.imageToLoadToServer.length));
					if(MCData.partSize * MCData.startIndex + length == MCData.imageToLoadToServer.length) {
						MCData.imageIDToLoadToServer = 0;
						MCData.imageToLoadToServer = null;
						MCData.startIndex = 0;
					}
					else MCData.startIndex++;
				}
			}
		}
		else if(event.player.ticksExisted % 10 == 0)
			if(!MCData.imagesToClients.isEmpty())
				if(MCData.imagesToClients.containsKey(event.player)) {
					ImageInfoToClient imageInfo = MCData.imagesToClients.get(event.player);
					int length = imageInfo.byteBuffer.length - MCData.partSize * MCData.imagesToClients.get(event.player).index;
					if(length > MCData.partSize)
						length = MCData.partSize;
					ShootIt.network.sendTo(new PhotoLoaderToClient(imageInfo.photoID, imageInfo.index, length, imageInfo.byteBuffer.length, event.player), (EntityPlayerMP) event.player);
					if(MCData.partSize * imageInfo.index + length == imageInfo.byteBuffer.length) {
						MCData.imagesToClients.remove(event.player);
					}
					else MCData.imagesToClients.get(event.player).index++;
				}
	}
}
