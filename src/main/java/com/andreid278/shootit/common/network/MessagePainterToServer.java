package com.andreid278.shootit.common.network;

import com.andreid278.shootit.ShootIt;
import com.andreid278.shootit.common.tiileentity.TEPainter;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessagePainterToServer implements IMessage {
	public byte messageID;
	BlockPos pos;
	public boolean increase;

	public MessagePainterToServer() {

	}

	public MessagePainterToServer(byte messageID, boolean increase, BlockPos pos) {
		this.messageID = messageID;
		this.increase = increase;
		this.pos = pos;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		messageID = buf.readByte();
		if(messageID == 0) {
			increase = buf.readBoolean();
		}
		int posX = buf.readInt();
		int posY = buf.readInt();
		int posZ = buf.readInt();
		pos = new BlockPos(posX, posY, posZ);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeByte(messageID);
		if(messageID == 0) {
			buf.writeBoolean(increase);
		}
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
	}

	public static class Handler implements IMessageHandler<MessagePainterToServer, IMessage> {
		@Override
		public IMessage onMessage(MessagePainterToServer message, MessageContext ctx) {
			TileEntity tileEntity = ctx.getServerHandler().player.world.getTileEntity(message.pos);
			if(tileEntity != null)
				if(tileEntity instanceof TEPainter) {
					switch(message.messageID) {
					case 0:
						ItemStack memoryCard = ((TEPainter)tileEntity).inventory[0];
						if(memoryCard.hasTagCompound()) {
							int length = memoryCard.getTagCompound().getIntArray("indexes").length;
							if(length > 0) {
								int curPhoto = ((TEPainter)tileEntity).curPhoto;
								if(message.increase) {
									curPhoto = (curPhoto + 1) % length;
								}
								else {
									curPhoto = curPhoto > 0 ? curPhoto - 1 : length - 1;
								}
								((TEPainter) tileEntity).curPhoto = curPhoto;
								ShootIt.network.sendTo(new MessagePainterToClient((byte)0, message.pos, curPhoto), ctx.getServerHandler().player);
							}
						}
						break;
					}
				}
			return null;
		}
	}
}
