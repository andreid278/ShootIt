package com.andreid278.shootit.Network;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.andreid278.shootit.Misc.Photos;
import com.andreid278.shootit.Misc.Statics;

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
		buf.writeBytes(Statics.imagesToClients.get(player).byteBuffer, messageID * Statics.partSize, partLength);
	}

	public static class Handler implements IMessageHandler<PhotoLoaderToClient, IMessage> {
		@Override
		public IMessage onMessage(PhotoLoaderToClient message, MessageContext ctx) {
			if(Statics.imageToLoadFromServer == null)
				Statics.imageToLoadFromServer = new byte[message.imageLength];
			for(int i = 0; i < message.partLength; i++)
				Statics.imageToLoadFromServer[message.messageID * Statics.partSize + i] = message.image[i];
			Statics.partLengthSum += message.partLength;
			if(Statics.partLengthSum == message.imageLength) {
				try {
					File file = new File(Statics.photosFolderPathClient);
					if(!file.isDirectory())
						file.mkdirs();
					FileUtils.writeByteArrayToFile(new File(Statics.photosFolderPathClient + Statics.slash + message.photoID + ".png"), Statics.imageToLoadFromServer);
				} catch (IOException e) {
					e.printStackTrace();
				}
				Photos.addPhoto(Statics.imageIDToLoadFromServer);
				Statics.imageToLoadFromServer = null;
				Statics.partLengthSum = 0;
				Statics.imageIDToLoadFromServer = 0;
			}
			return null;
		}
	}
}
