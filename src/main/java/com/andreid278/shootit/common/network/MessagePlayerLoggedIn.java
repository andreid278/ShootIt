package com.andreid278.shootit.common.network;

import com.andreid278.shootit.ShootIt;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessagePlayerLoggedIn implements IMessage {

	@Override
	public void fromBytes(ByteBuf buf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		// TODO Auto-generated method stub
		
	}
	
	public static class Handler implements IMessageHandler<MessagePlayerLoggedIn, IMessage> {
		@Override
		public IMessage onMessage(MessagePlayerLoggedIn message, MessageContext ctx) {
			ShootIt.proxy.onMessage(message, ctx);
			return null;
		}
	}

}
