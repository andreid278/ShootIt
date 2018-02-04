package com.andreid278.shootit.common.network;

import java.util.Date;

import com.andreid278.shootit.common.MCData;
import com.andreid278.shootit.common.MCData.ImageInfoToClient;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRequestForPhoto implements IMessage {
	public int index;

	public MessageRequestForPhoto() {

	}

	public MessageRequestForPhoto(int index) {
		this.index = index;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		index = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(index);
	}

	public static class Handler implements IMessageHandler<MessageRequestForPhoto, IMessage> {
		@Override
		public IMessage onMessage(MessageRequestForPhoto message, MessageContext ctx) {
			if(!MCData.imagesToClients.containsKey(ctx.getServerHandler().player)) {
				ImageInfoToClient imageInfo = MCData.instance.new ImageInfoToClient(message.index);
				if(imageInfo.byteBuffer != null) {
					/*Date date = new Date();
					if(MCData.lastLoadings.containsKey(imageInfo.photoID))
						MCData.lastLoadings.get(imageInfo.photoID).setTime(date.getTime());
					else MCData.lastLoadings.put(imageInfo.photoID, date);
					WorldDataDates data = WorldDataDates.getForWorld(ctx.getServerHandler().playerEntity.worldObj);
					data.writeDates();*/
					MCData.imagesToClients.put(ctx.getServerHandler().player, imageInfo);
				}
				else return new MessageRequestNoPhoto(message.index);
			}
			return null;
		}
	}
}
