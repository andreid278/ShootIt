package com.andreid278.shootit.Network;

import java.util.Date;
import java.util.UUID;

import com.andreid278.shootit.Misc.Statics;
import com.andreid278.shootit.Misc.Statics.ImageInfoToClient;
import com.andreid278.shootit.WorldData.WorldDataDates;

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
			if(!Statics.imagesToClients.containsKey(ctx.getServerHandler().playerEntity)) {
				ImageInfoToClient imageInfo = Statics.instance.new ImageInfoToClient(message.index);
				if(imageInfo.byteBuffer != null) {
					Date date = new Date();
					if(Statics.lastLoadings.containsKey(imageInfo.photoID))
						Statics.lastLoadings.get(imageInfo.photoID).setTime(date.getTime());
					else Statics.lastLoadings.put(imageInfo.photoID, date);
					WorldDataDates data = WorldDataDates.getForWorld(ctx.getServerHandler().playerEntity.worldObj);
					data.writeDates();
					Statics.imagesToClients.put(ctx.getServerHandler().playerEntity, imageInfo);
				}
				else return new MessageRequestNoPhoto(message.index);
			}
			return null;
		}
	}
}
