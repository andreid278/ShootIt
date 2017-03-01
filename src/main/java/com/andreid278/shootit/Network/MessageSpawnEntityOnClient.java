package com.andreid278.shootit.Network;

import java.io.IOException;

import com.andreid278.shootit.Entity.EntityPainting;
import com.andreid278.shootit.Misc.Photos;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSpawnEntityOnClient implements IMessage {
	public int posX;
	public int posY;
	public int posZ;
	public int width;
	public int height;
	public EnumFacing facing;
	public int entityID;
	public int index;
	
	public MessageSpawnEntityOnClient() {
		
	}
	
	public MessageSpawnEntityOnClient(int posX, int posY, int posZ, int width, int height, EnumFacing facing, int entityID, int index) {
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
		this.width = width;
		this.height = height;
		this.facing = facing;
		this.entityID = entityID;
		this.index = index;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		width = buf.readInt();
		height = buf.readInt();
		posX = buf.readInt();
		posY = buf.readInt();
		posZ = buf.readInt();
		facing = EnumFacing.getFront(buf.readByte());
		entityID = buf.readInt();
		index = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(width);
		buf.writeInt(height);
		buf.writeInt(posX);
		buf.writeInt(posY);
		buf.writeInt(posZ);
		buf.writeByte((byte)facing.getIndex());
		buf.writeInt(entityID);
		buf.writeInt(index);
	}
	
	public static class Handler implements IMessageHandler<MessageSpawnEntityOnClient, IMessage> {

		@Override
		public IMessage onMessage(MessageSpawnEntityOnClient message, MessageContext ctx) {
			Photos.addPhoto(message.index + ".jpg");
			return null;
		}
		
	}
}
