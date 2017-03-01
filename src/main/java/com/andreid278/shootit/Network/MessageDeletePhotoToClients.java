package com.andreid278.shootit.Network;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import com.andreid278.shootit.Main;
import com.andreid278.shootit.Items.Camera;
import com.andreid278.shootit.Misc.Photos;
import com.andreid278.shootit.Misc.Statics;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageDeletePhotoToClients implements IMessage {
	public int photoID;

	public MessageDeletePhotoToClients() {

	}

	public MessageDeletePhotoToClients(int photoID) {
		this.photoID = photoID;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		photoID = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(photoID);
	}

	public static class Handler implements IMessageHandler<MessageDeletePhotoToClients, IMessage> {
		@Override
		public IMessage onMessage(MessageDeletePhotoToClients message, MessageContext ctx) {
			return Main.proxy.onMessage(message, ctx);
		}
	}
}
