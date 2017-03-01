package com.andreid278.shootit.Network;

import java.io.File;
import java.io.IOException;

import com.andreid278.shootit.Misc.Photos;
import com.andreid278.shootit.Misc.Statics;

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
				File file = new File(Statics.photosFolderPathClient + Statics.slash + "0.png");
				File file1 = new File(Statics.photosFolderPathClient + Statics.slash + message.photoID + ".png");
				if(file1.exists())
					file1.delete();
				if(file.exists()) {
					file.renameTo(new File(Statics.photosFolderPathClient + Statics.slash + message.photoID + ".png"));
					Photos.addPhoto(message.photoID);
					if(!Minecraft.getMinecraft().isSingleplayer())
						Statics.imageIDToLoadToServer = message.photoID;
					else Statics.imageIDToLoadToServer = 0;
				}
				else {
					System.out.println("Can't save this photo!!!");
				}
				Minecraft.getMinecraft().displayGuiScreen((GuiScreen)null);
				break;
			case 1:
				Statics.imageIDToLoadToServer = 0;
				break;
			}
			return null;
		}
	}
}
