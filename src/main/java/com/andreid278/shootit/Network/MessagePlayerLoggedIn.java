package com.andreid278.shootit.Network;

import com.andreid278.shootit.Main;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessagePlayerLoggedIn implements IMessage {
	public MessagePlayerLoggedIn() {
		
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {

	}

	@Override
	public void toBytes(ByteBuf buf) {

	}

	public static class Handler implements IMessageHandler<MessagePlayerLoggedIn, IMessage> {
		@Override
		public IMessage onMessage(MessagePlayerLoggedIn message, MessageContext ctx) {
			Main.proxy.onMessage(message, ctx);
			return null;
		}
	}
}
