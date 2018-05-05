package com.andreid278.shootit.common.network;

import com.andreid278.shootit.ShootIt;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageCameraToClient implements IMessage {
	public byte messageID;
	public int curPhoto;
	public int curShader;
	public NBTTagCompound shaderNbt;

	public MessageCameraToClient() {

	}

	public MessageCameraToClient(byte messageID, int curPhoto) {
		this.messageID = messageID;
		this.curPhoto = curPhoto;
	}

	public MessageCameraToClient(byte messageID) {
		this.messageID = messageID;
	}
	
	public MessageCameraToClient(byte messageID, int curShader, NBTTagCompound shaderNbt) {
		this.messageID = messageID;
		this.curShader = curShader;
		this.shaderNbt = shaderNbt;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		messageID = buf.readByte();
		if(messageID == 0 || messageID == 1)
			curPhoto = buf.readInt();
		else if(messageID == 2) {
			curShader = buf.readInt();
			shaderNbt = ByteBufUtils.readTag(buf);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeByte(messageID);
		if(messageID == 0 || messageID == 1)
			buf.writeInt(curPhoto);
		else if(messageID == 2) {
			buf.writeInt(curShader);
			ByteBufUtils.writeTag(buf, shaderNbt);
		}
	}

	public static class Handler implements IMessageHandler<MessageCameraToClient, IMessage> {
		@Override
		public IMessage onMessage(MessageCameraToClient message, MessageContext ctx) {
			return ShootIt.proxy.onMessage(message, ctx);
		}
	}
}
