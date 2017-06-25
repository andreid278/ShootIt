package com.andreid278.shootit.Network;

import com.andreid278.shootit.Main;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessagePainterToClient implements IMessage {
	public BlockPos pos;
	public byte id;
	public int value;
	
	public MessagePainterToClient() {

	}

	public MessagePainterToClient(byte id, BlockPos pos, int value) {
		this.pos = pos;
		this.id = id;
		this.value = value;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		int x = buf.readInt();
		int y = buf.readInt();
		int z = buf.readInt();
		pos = new BlockPos(x, y, z);
		id = buf.readByte();
		if(id == 0)
			value = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
		buf.writeByte(id);
		if(id == 0)
			buf.writeInt(value);
	}

	public static class Handler implements IMessageHandler<MessagePainterToClient, IMessage> {
		@Override
		public IMessage onMessage(MessagePainterToClient message, MessageContext ctx) {
			return Main.proxy.onMessage(message, ctx);
		}
	}
}
