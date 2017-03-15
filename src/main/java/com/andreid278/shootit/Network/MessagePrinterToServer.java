package com.andreid278.shootit.Network;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

import com.andreid278.shootit.CommonProxy;
import com.andreid278.shootit.Main;
import com.andreid278.shootit.TileEntities.TEPrinter;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessagePrinterToServer implements IMessage {
	public byte messageID;
	public int index;
	public byte width;
	public byte height;
	public int posX;
	public int posY;
	public int posZ;
	public boolean increase;
	public ResourceLocation framesRL;
	public ResourceLocation backRL;
	public double[] textureCoords;

	public MessagePrinterToServer() {

	}

	public MessagePrinterToServer(byte messageID, int index, byte width, byte height, BlockPos pos, ResourceLocation framesRL, ResourceLocation backRL, double[] textureCoords) {
		this.messageID = messageID;
		this.index = index;
		this.width = width;
		this.height = height;
		posX = pos.getX();
		posY = pos.getY();
		posZ = pos.getZ();
		this.framesRL = framesRL;
		this.backRL = backRL;
		this.textureCoords = textureCoords;
	}

	public MessagePrinterToServer(byte messageID, boolean increase, BlockPos pos) {
		this.messageID = messageID;
		this.increase = increase;
		posX = pos.getX();
		posY = pos.getY();
		posZ = pos.getZ();
	}

	public MessagePrinterToServer(byte messageID, BlockPos pos) {
		this.messageID = messageID;
		posX = pos.getX();
		posY = pos.getY();
		posZ = pos.getZ();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		messageID = buf.readByte();
		if(messageID == 0) {
			index = buf.readInt();
			width = buf.readByte();
			height = buf.readByte();
			int l = buf.readInt();
			if(l > 0) {
				String s;
				byte[] byteBuffer = new byte[l];
				buf.readBytes(byteBuffer, 0, l);
				framesRL = new ResourceLocation(new String(byteBuffer));
			}
			else framesRL = null;
			l = buf.readInt();
			if(l > 0) {
				String s;
				byte[] byteBuffer = new byte[l];
				buf.readBytes(byteBuffer, 0, l);
				backRL = new ResourceLocation(new String(byteBuffer));
			}
			else backRL = null;
			textureCoords = new double[4];
			for(int i = 0; i < 4; i++)
				textureCoords[i] = buf.readDouble();
		}
		else if(messageID == 1 || messageID == 2 || messageID == 3)
			increase = buf.readBoolean();
		posX = buf.readInt();
		posY = buf.readInt();
		posZ = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeByte(messageID);
		if(messageID == 0) {
			buf.writeInt(index);
			buf.writeByte(width);
			buf.writeByte(height);
			if(framesRL == null)
				buf.writeInt(0);
			else {
				byte[] byteBuffer = framesRL.toString().getBytes();
				buf.writeInt(byteBuffer.length);
				buf.writeBytes(byteBuffer, 0, byteBuffer.length);
			}
			if(backRL == null)
				buf.writeInt(0);
			else {
				byte[] byteBuffer = backRL.toString().getBytes();
				buf.writeInt(byteBuffer.length);
				buf.writeBytes(byteBuffer, 0, byteBuffer.length);
			}
			for(int i = 0; i < 4; i++)
				buf.writeDouble(textureCoords[i]);
		}
		else if(messageID == 1 || messageID == 2 || messageID == 3)
			buf.writeBoolean(increase);
		buf.writeInt(posX);
		buf.writeInt(posY);
		buf.writeInt(posZ);
	}

	public static class Handler implements IMessageHandler<MessagePrinterToServer, IMessage> {
		@Override
		public IMessage onMessage(MessagePrinterToServer message, MessageContext ctx) {
			TileEntity tileEntity = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(new BlockPos(message.posX, message.posY, message.posZ));
			if(tileEntity != null)
				if(tileEntity instanceof TEPrinter) {
					switch(message.messageID) {
					case 0:
						ItemStack stack = new ItemStack(CommonProxy.photoItem);
						NBTTagCompound compound = new NBTTagCompound();
						compound.setInteger("index", message.index);
						compound.setByte("width", message.width);
						compound.setByte("height", message.height);
						compound.setString("frames", message.framesRL == null ? "" : message.framesRL.toString());
						compound.setString("back", message.backRL == null ? "" : message.backRL.toString());
						ByteBuffer byteBuffer = ByteBuffer.allocate(32);
						DoubleBuffer doubleBuffer = byteBuffer.asDoubleBuffer();
						doubleBuffer.put(message.textureCoords);
						compound.setByteArray("textureCoords", byteBuffer.array());
						stack.setTagCompound(compound);
						if(((TEPrinter)tileEntity).getStackInSlot(6) == null) {
							for(int i = 1; i < 6; i++) {
								ItemStack item = ((TEPrinter)tileEntity).getStackInSlot(i);
								if(item == null || item.stackSize < (int)Math.sqrt(message.width * message.height))
									return null;
							}
							((TEPrinter)tileEntity).setInventorySlotContents(6, stack);
							for(int i = 1; i < 6; i++)
								((TEPrinter)tileEntity).decrStackSize(i, (int)Math.sqrt(message.width * message.height));
						}
						break;
					case 1:
						ItemStack memoryCard = ((TEPrinter)tileEntity).inventory[0];
						if(memoryCard != null)
							if(memoryCard.hasTagCompound()) {
								int length = memoryCard.getTagCompound().getIntArray("indexes").length;
								if(length > 0) {
									int curPhoto = ((TEPrinter)tileEntity).getField(2);
									if(message.increase) {
										curPhoto = (curPhoto + 1) % length;
									}
									else {
										curPhoto = curPhoto > 0 ? curPhoto - 1 : length - 1;
									}
									((TEPrinter) tileEntity).setField(2, curPhoto);
									Main.network.sendTo(new MessagePrinterToClient(new BlockPos(message.posX, message.posY, message.posZ), (byte)2, curPhoto), ctx.getServerHandler().playerEntity);
								}
							}
						break;
					case 2:
						int width = ((TEPrinter) tileEntity).getField(0);
						if(message.increase) {
							if(width < 10) {
								width++;
								((TEPrinter) tileEntity).setField(0, width);
								Main.network.sendTo(new MessagePrinterToClient(new BlockPos(message.posX, message.posY, message.posZ), (byte)0, width), ctx.getServerHandler().playerEntity);
							}
						}
						else if(width > 1) {
							width--;
							((TEPrinter) tileEntity).setField(0, width);
							Main.network.sendTo(new MessagePrinterToClient(new BlockPos(message.posX, message.posY, message.posZ), (byte)0, width), ctx.getServerHandler().playerEntity);							
						}
						break;
					case 3:
						int height = ((TEPrinter) tileEntity).getField(1);
						if(message.increase) {
							if(height < 10) {
								height++;
								((TEPrinter) tileEntity).setField(1, height);
								Main.network.sendTo(new MessagePrinterToClient(new BlockPos(message.posX, message.posY, message.posZ), (byte)1, height), ctx.getServerHandler().playerEntity);
							}
						}
						else if(height > 1) {
							height--;
							((TEPrinter) tileEntity).setField(1, height);
							Main.network.sendTo(new MessagePrinterToClient(new BlockPos(message.posX, message.posY, message.posZ), (byte)1, height), ctx.getServerHandler().playerEntity);							
						}
						break;
					case 4:
						((TEPrinter)tileEntity).checkboxCustom = !((TEPrinter)tileEntity).checkboxCustom;
						Main.network.sendTo(new MessagePrinterToClient(new BlockPos(message.posX, message.posY, message.posZ), (byte)3, ((TEPrinter)tileEntity).checkboxCustom), ctx.getServerHandler().playerEntity);							
						break;
					}
				}
			return null;
		}
	}
}
