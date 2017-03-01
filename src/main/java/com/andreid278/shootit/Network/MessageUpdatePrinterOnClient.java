package com.andreid278.shootit.Network;

import com.andreid278.shootit.Main;
import com.andreid278.shootit.TileEntities.TEPrinter;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageUpdatePrinterOnClient implements IMessage {
	public BlockPos pos;
	public byte id;
	public int value;
	
	public MessageUpdatePrinterOnClient() {
		
	}
	
	public MessageUpdatePrinterOnClient(BlockPos pos, byte id, int value) {
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
		value = buf.readInt();
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
		buf.writeByte(id);
		buf.writeInt(value);
	}
	
	public static class Handler implements IMessageHandler<MessageUpdatePrinterOnClient, IMessage> {
		@Override
		public IMessage onMessage(MessageUpdatePrinterOnClient message, MessageContext ctx) {
			return Main.proxy.onMessage(message, ctx);
		}
	}
}
