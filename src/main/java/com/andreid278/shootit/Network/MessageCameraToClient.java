package com.andreid278.shootit.Network;

import com.andreid278.shootit.Main;
import com.andreid278.shootit.Items.Camera;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageCameraToClient implements IMessage {
	public byte messageID;
	public int curPhoto;

	public MessageCameraToClient() {

	}

	public MessageCameraToClient(byte messageID, int curPhoto) {
		this.messageID = messageID;
		this.curPhoto = curPhoto;
	}

	public MessageCameraToClient(byte messageID) {
		this.messageID = messageID;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		messageID = buf.readByte();
		if(messageID == 0 || messageID == 1)
			curPhoto = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeByte(messageID);
		if(messageID == 0 || messageID == 1)
			buf.writeInt(curPhoto);
	}

	public static class Handler implements IMessageHandler<MessageCameraToClient, IMessage> {
		@Override
		public IMessage onMessage(MessageCameraToClient message, MessageContext ctx) {
			return Main.proxy.onMessage(message, ctx);
		}
	}

}
