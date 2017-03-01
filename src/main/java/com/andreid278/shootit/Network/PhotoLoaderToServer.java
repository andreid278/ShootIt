package com.andreid278.shootit.Network;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import com.andreid278.shootit.Misc.Statics;
import com.andreid278.shootit.WorldData.WorldData;
import com.andreid278.shootit.WorldData.WorldDataDates;

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
		buf.writeBytes(Statics.imageToLoadToServer, messageID * Statics.partSize, partLength);
	}
	
	public static class Handler implements IMessageHandler<PhotoLoaderToServer, IMessage> {
		@Override
		public IMessage onMessage(PhotoLoaderToServer message, MessageContext ctx) {
			if(!Statics.imagesFromClients.containsKey(message.photoID))
				Statics.imagesFromClients.put(message.photoID, Statics.instance.new ImageInfo(message.imageLength));
			for(int i = 0; i < message.partLength; i++)
				Statics.imagesFromClients.get(message.photoID).byteBuffer[message.messageID * Statics.partSize + i] = message.image[i];
			Statics.imagesFromClients.get(message.photoID).partLengthSum += message.partLength;
			if(Statics.imagesFromClients.get(message.photoID).partLengthSum == message.imageLength) {
				Date date = new Date();
				if(Statics.lastLoadings.containsKey(message.photoID))
					Statics.lastLoadings.get(message.photoID).setTime(date.getTime());
				else Statics.lastLoadings.put(message.photoID, date);
				WorldDataDates data = WorldDataDates.getForWorld(ctx.getServerHandler().playerEntity.worldObj);
				data.writeDates();
				try {
					File file = new File(Statics.photosFolderPathServer);
					if(!file.isDirectory())
						file.mkdirs();
					FileUtils.writeByteArrayToFile(new File(Statics.photosFolderPathServer + Statics.slash + message.photoID + ".png"), Statics.imagesFromClients.get(message.photoID).byteBuffer);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Statics.imagesFromClients.remove(message.photoID);
			}
			return null;
		}
	}
}
