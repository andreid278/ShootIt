package com.andreid278.shootit.common.network;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.andreid278.shootit.common.MCData;
import com.andreid278.shootit.common.PhotosData;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PhotoLoaderToClient implements IMessage {
	public int photoID;
	public int messageID;
	public byte[] image;
	public int partLength;
	public int imageLength;
	public EntityPlayer player;

	public PhotoLoaderToClient() {

	}

	public PhotoLoaderToClient(int photoID, int messageID, int partLength, int imageLength, EntityPlayer player) {
		this.photoID = photoID;
		this.messageID = messageID;
		this.partLength = partLength;
		this.imageLength = imageLength;
		this.player = player;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		photoID = buf.readInt();
		messageID = buf.readInt();
		partLength = buf.readInt();
		imageLength = buf.readInt();
		image = new byte[partLength];
		buf.readBytes(image);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(photoID);
		buf.writeInt(messageID);
		buf.writeInt(partLength);
		buf.writeInt(imageLength);
		buf.writeBytes(MCData.imagesToClients.get(player).byteBuffer, messageID * MCData.partSize, partLength);
	}

	public static class Handler implements IMessageHandler<PhotoLoaderToClient, IMessage> {
		@Override
		public IMessage onMessage(PhotoLoaderToClient message, MessageContext ctx) {
			if(MCData.imageToLoadFromServer == null)
				MCData.imageToLoadFromServer = new byte[message.imageLength];
			for(int i = 0; i < message.partLength; i++)
				MCData.imageToLoadFromServer[message.messageID * MCData.partSize + i] = message.image[i];
			MCData.partLengthSum += message.partLength;
			if(MCData.partLengthSum == message.imageLength) {
				try {
					File file = new File(MCData.photosFolderPathClient);
					if(!file.isDirectory())
						file.mkdirs();
					FileUtils.writeByteArrayToFile(new File(MCData.photosFolderPathClient + "/" + message.photoID + ".png"), MCData.imageToLoadFromServer);
				} catch (IOException e) {
					e.printStackTrace();
				}
				PhotosData.addPhoto(MCData.imageIDToLoadFromServer);
				MCData.imageToLoadFromServer = null;
				MCData.partLengthSum = 0;
				MCData.imageIDToLoadFromServer = 0;
			}
			return null;
		}
	}
}
