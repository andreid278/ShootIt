package com.andreid278.shootit.common.network;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import com.andreid278.shootit.common.MCData;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PhotoLoaderToServer implements IMessage {
	public int photoID;
	public int messageID;
	public byte[] image;
	public int partLength;
	public int imageLength;
	
	public PhotoLoaderToServer() {
		
	}
	
	public PhotoLoaderToServer(int photoID, int messageID, int partLength, int imageLength) {
		this.photoID = photoID;
		this.messageID = messageID;
		this.partLength = partLength;
		this.imageLength = imageLength;
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
		buf.writeBytes(MCData.imageToLoadToServer, messageID * MCData.partSize, partLength);
	}
	
	public static class Handler implements IMessageHandler<PhotoLoaderToServer, IMessage> {
		@Override
		public IMessage onMessage(PhotoLoaderToServer message, MessageContext ctx) {
			if(!MCData.imagesFromClients.containsKey(message.photoID))
				MCData.imagesFromClients.put(message.photoID, MCData.instance.new ImageInfo(message.imageLength));
			for(int i = 0; i < message.partLength; i++)
				MCData.imagesFromClients.get(message.photoID).byteBuffer[message.messageID * MCData.partSize + i] = message.image[i];
			MCData.imagesFromClients.get(message.photoID).partLengthSum += message.partLength;
			if(MCData.imagesFromClients.get(message.photoID).partLengthSum == message.imageLength) {
				Date date = new Date();
				if(MCData.lastLoadings.containsKey(message.photoID))
					MCData.lastLoadings.get(message.photoID).setTime(date.getTime());
				else MCData.lastLoadings.put(message.photoID, date);
				//WorldDataDates data = WorldDataDates.getForWorld(ctx.getServerHandler().player.world);
				//data.writeDates();
				try {
					File file = new File(MCData.photosFolderPathServer);
					if(!file.isDirectory())
						file.mkdirs();
					FileUtils.writeByteArrayToFile(new File(MCData.photosFolderPathServer + "/" + message.photoID + ".png"), MCData.imagesFromClients.get(message.photoID).byteBuffer);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				MCData.imagesFromClients.remove(message.photoID);
			}
			return null;
		}
	}
}
