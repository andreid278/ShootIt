package com.andreid278.shootit.common.network;

import java.io.File;

import com.andreid278.shootit.client.gui.ConfirmSavingPhotoGui;
import com.andreid278.shootit.common.MCData;
import com.andreid278.shootit.common.PhotosData;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageReplyForNextPhotoID implements IMessage {
	public byte messageID;
	public int photoID;

	public MessageReplyForNextPhotoID() {

	}

	public MessageReplyForNextPhotoID(byte messageID, int photoID) {
		this.messageID = messageID;
		this.photoID = photoID;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		messageID = buf.readByte();
		photoID = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeByte(messageID);
		buf.writeInt(photoID);
	}

	public static class Handler implements IMessageHandler<MessageReplyForNextPhotoID, IMessage> {
		@Override
		public IMessage onMessage(MessageReplyForNextPhotoID message, MessageContext ctx) {
			switch(message.messageID) {
			case 0:
				File file = new File(MCData.photosFolderPathClient + "/0.png");
				File file1 = new File(MCData.photosFolderPathClient + "/" + message.photoID + ".png");
				if(file1.exists())
					file1.delete();
				if(file.exists()) {
					file.renameTo(new File(MCData.photosFolderPathClient + "/" + message.photoID + ".png"));
					PhotosData.addPhoto(message.photoID);
					if(!Minecraft.getMinecraft().isSingleplayer())
						MCData.imageIDToLoadToServer = message.photoID;
					else MCData.imageIDToLoadToServer = 0;
				}
				else {
					System.out.println("Can't save this photo!!!");
				}
				if(Minecraft.getMinecraft().currentScreen instanceof ConfirmSavingPhotoGui)
					Minecraft.getMinecraft().displayGuiScreen((GuiScreen)null);
				break;
			case 1:
				MCData.imageIDToLoadToServer = 0;
				break;
			}
			return null;
		}
	}
}
