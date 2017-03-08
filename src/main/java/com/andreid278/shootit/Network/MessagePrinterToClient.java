package com.andreid278.shootit.Network;

import com.andreid278.shootit.Main;
import com.andreid278.shootit.TileEntities.TEPrinter;

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

public class MessagePrinterToClient implements IMessage {
	public BlockPos pos;
	public byte id;
	public int value;
	public boolean checkbox;
	public ItemStack itemStack;

	public MessagePrinterToClient() {

	}

	public MessagePrinterToClient(BlockPos pos, byte id, int value) {
		this.pos = pos;
		this.id = id;
		this.value = value;
	}

	public MessagePrinterToClient(BlockPos pos, byte id, boolean checkbox) {
		this.pos = pos;
		this.id = id;
		this.checkbox = checkbox;
	}

	public MessagePrinterToClient(BlockPos pos, byte id, ItemStack itemStack) {
		this.pos = pos;
		this.id = id;
		this.itemStack = itemStack;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		int x = buf.readInt();
		int y = buf.readInt();
		int z = buf.readInt();
		pos = new BlockPos(x, y, z);
		id = buf.readByte();
		if(id < 3)
			value = buf.readInt();
		else if(id < 5)
			checkbox = buf.readBoolean();
		else if(id < 7) {
			int itemID = buf.readInt();
			int meta = buf.readInt();
			itemStack = new ItemStack(Item.getItemById(itemID), 1, meta);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
		buf.writeByte(id);
		if(id < 3)
			buf.writeInt(value);
		else if(id < 5)
			buf.writeBoolean(checkbox);
		else if(id < 7) {
			buf.writeInt(Item.getIdFromItem(itemStack.getItem()));
			buf.writeInt(itemStack.getMetadata());
		}
	}

	public static class Handler implements IMessageHandler<MessagePrinterToClient, IMessage> {
		@Override
		public IMessage onMessage(MessagePrinterToClient message, MessageContext ctx) {
			return Main.proxy.onMessage(message, ctx);
		}
	}
}
