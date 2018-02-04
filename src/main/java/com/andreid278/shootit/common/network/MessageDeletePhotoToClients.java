package com.andreid278.shootit.common.network;

import com.andreid278.shootit.ShootIt;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageDeletePhotoToClients implements IMessage {
	public int photoID;

	public MessageDeletePhotoToClients() {

	}

	public MessageDeletePhotoToClients(int photoID) {
		this.photoID = photoID;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		photoID = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(photoID);
	}

	public static class Handler implements IMessageHandler<MessageDeletePhotoToClients, IMessage> {
		@Override
		public IMessage onMessage(MessageDeletePhotoToClients message, MessageContext ctx) {
			return ShootIt.proxy.onMessage(message, ctx);
		}
	}
}
