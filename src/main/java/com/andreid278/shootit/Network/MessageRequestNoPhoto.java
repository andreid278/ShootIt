package com.andreid278.shootit.Network;

import com.andreid278.shootit.Misc.Photos;
import com.andreid278.shootit.Misc.Statics;

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
			Photos.addEmptyPhoto(message.index);
			if(Statics.imageIDToLoadFromServer == message.index) {
				Statics.imageToLoadFromServer = null;
				Statics.imageIDToLoadFromServer = 0;
				Statics.partLengthSum = 0;
			}
			return null;
		}
	}
}
