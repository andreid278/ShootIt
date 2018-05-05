package com.andreid278.shootit.common.network;

import java.io.File;

import org.apache.commons.lang3.ArrayUtils;

import com.andreid278.shootit.ShootIt;
import com.andreid278.shootit.common.MCData;
import com.andreid278.shootit.common.container.CameraContainer;
import com.andreid278.shootit.common.item.Camera;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageDeletePhotoRequest implements IMessage {
	public int photoID;
	public boolean deleteForever;

	public MessageDeletePhotoRequest() {

	}

	public MessageDeletePhotoRequest(int photoID, boolean deleteForever) {
		this.photoID = photoID;
		this.deleteForever = deleteForever;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		photoID = buf.readInt();
		deleteForever = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(photoID);
		buf.writeBoolean(deleteForever);
	}

	public static class Handler implements IMessageHandler<MessageDeletePhotoRequest, IMessage> {
		@Override
		public IMessage onMessage(MessageDeletePhotoRequest message, MessageContext ctx) {
			ItemStack camera = ctx.getServerHandler().player.getHeldItemMainhand();
			if(camera.getItem() instanceof Camera)
				if(camera.hasTagCompound()) {
					NBTTagCompound nbt = camera.getTagCompound();
					int[] indexes = nbt.getIntArray("indexes");
					indexes = ArrayUtils.removeElement(indexes, message.photoID);
					nbt.setInteger("curPhoto", 0);
					nbt.setIntArray("indexes", indexes);
					camera.setTagCompound(nbt);
					if(message.deleteForever) {
						File file = new File(MCData.photosFolderPathServer + "/" + message.photoID + ".png");
						if(file.exists())
							file.delete();
						ShootIt.network.sendToAll(new MessageDeletePhotoToClients(message.photoID));
					}
					if(ctx.getServerHandler().player.openContainer instanceof CameraContainer) {
						CameraContainer container = (CameraContainer) ctx.getServerHandler().player.openContainer;
						container.cameraInventory.readFromNBT(nbt);
					}
					return new MessageCameraToClient((byte)1, message.photoID);
				}
			return null;
		}
	}
}
