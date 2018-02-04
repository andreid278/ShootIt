package com.andreid278.shootit.common.network;

import com.andreid278.shootit.common.MCData;
import com.andreid278.shootit.common.PhotosData;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRequestNoPhoto implements IMessage {
public int index;
	
	public MessageRequestNoPhoto() {
		
	}
	
	public MessageRequestNoPhoto(int index) {
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

	public static class Handler implements IMessageHandler<MessageRequestNoPhoto, IMessage> {
		@Override
		public IMessage onMessage(MessageRequestNoPhoto message, MessageContext ctx) {
			PhotosData.addEmptyPhoto(message.index);
			if(MCData.imageIDToLoadFromServer == message.index) {
				MCData.imageToLoadFromServer = null;
				MCData.imageIDToLoadFromServer = 0;
				MCData.partLengthSum = 0;
			}
			return null;
		}
	}
}
