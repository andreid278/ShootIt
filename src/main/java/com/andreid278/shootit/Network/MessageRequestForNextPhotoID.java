package com.andreid278.shootit.Network;

import com.andreid278.shootit.Main;
import com.andreid278.shootit.Items.Camera;
import com.andreid278.shootit.TileEntities.TEPainter;
import com.andreid278.shootit.WorldData.WorldData;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRequestForNextPhotoID implements IMessage {
	public int messageID;
	public BlockPos pos;

	public MessageRequestForNextPhotoID() {
		messageID = 0;
	}

	public MessageRequestForNextPhotoID(int messageID, BlockPos pos) {
		this.messageID = messageID;
		this.pos = pos;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		messageID = buf.readInt();
		if(messageID > 0) {
			int x = buf.readInt();
			int y = buf.readInt();
			int z = buf.readInt();
			pos = new BlockPos(x, y, z);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(messageID);
		if(messageID > 0) {
			buf.writeInt(pos.getX());
			buf.writeInt(pos.getY());
			buf.writeInt(pos.getZ());
		}
	}

	public static class Handler implements IMessageHandler<MessageRequestForNextPhotoID, IMessage> {
		@Override
		public IMessage onMessage(MessageRequestForNextPhotoID message, MessageContext ctx) {
			WorldData data = WorldData.getForWorld(ctx.getServerHandler().playerEntity.worldObj);
			int photoID = data.increasePhotoID();
			NBTTagCompound compound = null;
			if(message.messageID == 0) {
				ItemStack camera = ctx.getServerHandler().playerEntity.getHeldItemMainhand();
				if(camera == null || !(camera.getItem() instanceof Camera))
					return new MessageReplyForNextPhotoID((byte)1, photoID);
				if(camera.hasTagCompound())
					compound = camera.getTagCompound();
				else compound = new NBTTagCompound();
			}
			else if(message.messageID == 1) {
				TileEntity te = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.pos);
				if(te instanceof TEPainter)
					if(((TEPainter)te).inventory[1] != null) {
						compound = ((TEPainter)te).inventory[1].getTagCompound();
						if(compound == null)
							compound = new NBTTagCompound();
					}
			}
			int[] oldIndexes = compound.getIntArray("indexes");
			if(oldIndexes.length == 16)
				return new MessageReplyForNextPhotoID((byte)1, photoID);
			int[] newIndexes = new int[oldIndexes.length + 1];
			for(int i = 0; i < oldIndexes.length; i++)
				newIndexes[i] = oldIndexes[i];
			newIndexes[oldIndexes.length] = photoID;
			compound.setIntArray("indexes", newIndexes);
			if(message.messageID == 0)
				ctx.getServerHandler().playerEntity.getHeldItemMainhand().setTagCompound(compound);
			else if(message.messageID == 1) {
				TileEntity te = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.pos);
				if(te instanceof TEPainter)
					if(((TEPainter)te).inventory[1] != null)
						((TEPainter)te).inventory[1].setTagCompound(compound);
			}
			return new MessageReplyForNextPhotoID((byte)0, photoID);
		}
	}
}
