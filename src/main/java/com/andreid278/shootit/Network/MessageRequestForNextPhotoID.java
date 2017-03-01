package com.andreid278.shootit.Network;

import com.andreid278.shootit.Main;
import com.andreid278.shootit.Items.Camera;
import com.andreid278.shootit.WorldData.WorldData;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRequestForNextPhotoID implements IMessage {
	public MessageRequestForNextPhotoID() {

	}

	@Override
	public void fromBytes(ByteBuf buf) {

	}

	@Override
	public void toBytes(ByteBuf buf) {

	}

	public static class Handler implements IMessageHandler<MessageRequestForNextPhotoID, IMessage> {
		@Override
		public IMessage onMessage(MessageRequestForNextPhotoID message, MessageContext ctx) {
			WorldData data = WorldData.getForWorld(ctx.getServerHandler().playerEntity.worldObj);
			int photoID = data.increasePhotoID();
			ItemStack camera = ctx.getServerHandler().playerEntity.getHeldItemMainhand();
			if(camera == null || !(camera.getItem() instanceof Camera))
				return new MessageReplyForNextPhotoID((byte)1, photoID);
			NBTTagCompound compound = null;
			if(camera.hasTagCompound())
				compound = camera.getTagCompound();
			else compound = new NBTTagCompound();
			int[] oldIndexes = compound.getIntArray("indexes");
			if(oldIndexes.length == 16)
				return new MessageReplyForNextPhotoID((byte)1, photoID);
			int[] newIndexes = new int[oldIndexes.length + 1];
			for(int i = 0; i < oldIndexes.length; i++)
				newIndexes[i] = oldIndexes[i];
			newIndexes[oldIndexes.length] = photoID;
			compound.setIntArray("indexes", newIndexes);
			camera.setTagCompound(compound);
			return new MessageReplyForNextPhotoID((byte)0, photoID);
		}
	}
}
