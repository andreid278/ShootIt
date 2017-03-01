package com.andreid278.shootit.Network;

import java.io.IOException;

import com.andreid278.shootit.Entity.EntityPainting;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageBreakEntityOnServer implements IMessage {
	AxisAlignedBB boundingBox;
	
	public MessageBreakEntityOnServer() {
		
	}
	
	public MessageBreakEntityOnServer(AxisAlignedBB boundingBox) {
		this.boundingBox = boundingBox;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		double minX, minY, minZ, maxX, maxY, maxZ;
		minX = buf.readDouble();
		minY = buf.readDouble();
		minZ = buf.readDouble();
		maxX = buf.readDouble();
		maxY = buf.readDouble();
		maxZ = buf.readDouble();
		boundingBox = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeDouble(boundingBox.minX);
		buf.writeDouble(boundingBox.minY);
		buf.writeDouble(boundingBox.minZ);
		buf.writeDouble(boundingBox.maxX);
		buf.writeDouble(boundingBox.maxY);
		buf.writeDouble(boundingBox.maxZ);
	}
	
	public static class Handler implements IMessageHandler<MessageBreakEntityOnServer, IMessage> {
		@Override
		public IMessage onMessage(MessageBreakEntityOnServer message, MessageContext ctx) {
			return null;
		}
	}
}
