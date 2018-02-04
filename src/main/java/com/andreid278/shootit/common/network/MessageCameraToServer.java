package com.andreid278.shootit.common.network;

import com.andreid278.shootit.common.item.Camera;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageCameraToServer implements IMessage {
	public byte messageID;
	public boolean increase;
	public int curShader;

	public MessageCameraToServer() {

	}

	public MessageCameraToServer(byte messageID, boolean increase) {
		this.messageID = messageID;
		this.increase = increase;
	}

	public MessageCameraToServer(byte messageID) {
		this.messageID = messageID;
	}

	public MessageCameraToServer(byte messageID, int curShader) {
		this.messageID = messageID;
		this.curShader = curShader;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		messageID = buf.readByte();
		if(messageID == 0)
			increase = buf.readBoolean();
		else if(messageID == 3)
			curShader = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeByte(messageID);
		if(messageID == 0)
			buf.writeBoolean(increase);
		else if(messageID == 3)
			buf.writeInt(curShader);
	}

	public static class Handler implements IMessageHandler<MessageCameraToServer, IMessage> {
		@Override
		public IMessage onMessage(MessageCameraToServer message, MessageContext ctx) {
			ItemStack item = ctx.getServerHandler().player.getHeldItemMainhand();
			if(item.getItem() instanceof Camera) {
				NBTTagCompound nbt = null;
				if(item.hasTagCompound())
					nbt = item.getTagCompound();
				else nbt = new NBTTagCompound();
				switch(message.messageID) {
				case 0:
					if(nbt.getBoolean("hasMemoryCard")) {
						int curPhoto = nbt.getInteger("curPhoto");
						int[] indexes = nbt.getIntArray("indexes");
						if(indexes.length > 0) {
							if(message.increase)
								curPhoto = curPhoto == indexes.length - 1 ? 0 : curPhoto + 1;
							else curPhoto = curPhoto == 0 ? indexes.length - 1 : curPhoto - 1;
							nbt.setInteger("curPhoto", curPhoto);
							item.setTagCompound(nbt);
							return new MessageCameraToClient((byte)0, curPhoto);
						}
					}
					break;
				case 2:
					nbt.setInteger("curPhoto", 0);
					item.setTagCompound(nbt);
					break;
				case 3:
					nbt.setInteger("shader", message.curShader);
					item.setTagCompound(nbt);
					return new MessageCameraToClient((byte)2, message.curShader);
				}
			}
			return null;
		}
	}
}
