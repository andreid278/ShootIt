package com.andreid278.shootit.Entity;

import java.util.List;

import javax.annotation.Nullable;

import com.andreid278.shootit.CommonProxy;
import com.andreid278.shootit.Main;
import com.andreid278.shootit.Items.PhotoItem;
import com.google.common.collect.Lists;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityPainting extends Entity implements IEntityAdditionalSpawnData {
	public EnumFacing facing;
	public double offsetX;
	public double offsetY;
	public double offsetZ;
	public int index;
	public int width;
	public int height;
	public int rotation;
	public ResourceLocation framesRL;
	public ResourceLocation backRL;

	public EntityPainting(World worldIn) {
		super(worldIn);
	}

	public EntityPainting(World worldIn, BlockPos bp, EnumFacing facing, int width, int height, int index, int rotation, ResourceLocation framesRL, ResourceLocation backRL) {
		super(worldIn);
		this.posX = bp.getX() + facing.getFrontOffsetX() * 1.01;
		this.posY = bp.getY();
		this.posZ = bp.getZ() + facing.getFrontOffsetZ() * 1.01;
		this.facing = facing;
		this.width = width;
		this.height = height;
		this.index = index;
		switch(facing) {
		case SOUTH:
			offsetX = width;
			offsetY = height;
			offsetZ = 0;
			break;
		case EAST:
			posZ++;
			offsetX = 0;
			offsetY = height;
			offsetZ = -width;
			break;
		case NORTH:
			posX++;
			posZ++;
			offsetX = -width;
			offsetY = height;
			offsetZ = 0;
			break;
		case WEST:
			posX++;
			offsetX = 0;
			offsetY = height;
			offsetZ = width;
			break;
		case DOWN:
			posY += facing.getFrontOffsetY() * 0.01;
			offsetY = 0;
			switch(rotation) {
			case 0:
				posX += 1;
				offsetX = -height;
				offsetZ = width;
				break;
			case 1:
				offsetX = width;
				offsetZ = height;
				break;
			case 2:
				posZ += 1;
				offsetX = height;
				offsetZ = -width;
				break;
			case 3:
				posX += 1;
				posZ += 1;
				offsetX = -width;
				offsetZ = -height;
				break;
			}
			break;
		case UP:
			posY += facing.getFrontOffsetY() * 1.01;
			offsetY = 0;
			switch(rotation) {
			case 3:
				posX += 1;
				offsetX = -width;
				offsetZ = height;
				break;
			case 2:
				posX += 1;
				posZ += 1;
				offsetX = -height;
				offsetZ = -width;
				break;
			case 1:
				posZ += 1;
				offsetX = width;
				offsetZ = -height;
				break;
			case 0:
				offsetX = height;
				offsetZ = width;
				break;
			}
			break;
		}
		posX += offsetX / 2;
		offsetX /= 2;
		posY += offsetY / 2;
		offsetY /= 2;
		posZ += offsetZ / 2;
		offsetZ /= 2;
		this.setEntityBoundingBox(new AxisAlignedBB(posX - (offsetX == 0 ? 0.05 : offsetX), posY - (offsetY == 0 ? 0.05 : offsetY), posZ - (offsetZ == 0 ? 0.05 : offsetZ), posX + (offsetX == 0 ? 0.05 : offsetX), posY + (offsetY == 0 ? 0.05 : offsetY), posZ + (offsetZ == 0 ? 0.05 : offsetZ)));
		this.rotation = rotation;
		this.framesRL = framesRL;
		this.backRL = backRL;
	}	

	@Override
	protected void entityInit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		offsetX = compound.getDouble("offsetx");
		offsetY = compound.getDouble("offsety");
		offsetZ = compound.getDouble("offsetz");
		posX = compound.getDouble("posx");
		posY = compound.getDouble("posy");
		posZ = compound.getDouble("posz");
		rotation = compound.getInteger("rotation");
		facing = EnumFacing.getFront(compound.getByte("facing"));
		index = compound.getInteger("index");
		width = compound.getInteger("width");
		height = compound.getInteger("height");
		this.setEntityBoundingBox(new AxisAlignedBB(posX - (offsetX == 0 ? 0.05 : offsetX), posY - (offsetY == 0 ? 0.05 : offsetY), posZ - (offsetZ == 0 ? 0.05 : offsetZ), posX + (offsetX == 0 ? 0.05 : offsetX), posY + (offsetY == 0 ? 0.05 : offsetY), posZ + (offsetZ == 0 ? 0.05 : offsetZ)));
		String s = compound.getString("frames");
		framesRL = s.equals("") ? null : new ResourceLocation(s);
		s = compound.getString("back");
		backRL = s.equals("") ? null : new ResourceLocation(s);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setDouble("offsetx", offsetX);
		compound.setDouble("offsety", offsetY);
		compound.setDouble("offsetz", offsetZ);
		compound.setDouble("posx", posX);
		compound.setDouble("posy", posY);
		compound.setDouble("posz", posZ);
		compound.setInteger("rotation", rotation);
		compound.setByte("facing", (byte)facing.getIndex());
		compound.setInteger("index", index);
		compound.setInteger("width", width);
		compound.setInteger("height", height);
		compound.setString("frames", framesRL == null ? "" : framesRL.toString());
		compound.setString("back", backRL == null ? "" : backRL.toString());
		return compound;
	}

	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
	}

	public boolean hitByEntity(Entity entityIn) {
		return entityIn instanceof EntityPlayer ? this.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)entityIn), 0.0F) : false;    	
	}

	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.isEntityInvulnerable(source))
			return false;
		if (!this.isDead && !this.worldObj.isRemote) {
			ItemStack photo = new ItemStack(CommonProxy.photoItem);
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("index", index);
			nbt.setByte("width", (byte)width);
			nbt.setByte("height", (byte)height);
			nbt.setString("frames", framesRL == null ? "" : framesRL.toString());
			nbt.setString("back", backRL == null ? "" : backRL.toString());
			photo.setTagCompound(nbt);
			Vec3d offset = new Vec3d(facing.getFrontOffsetX(), facing.getFrontOffsetY(), facing.getFrontOffsetZ());
			offset.normalize().scale(0.5);
			EntityItem photoEntity = new EntityItem(worldObj, posX + offset.xCoord, posY + offset.yCoord, posZ + offset.zCoord, photo);
			photoEntity.motionX = 0;
			photoEntity.motionY = 0;
			photoEntity.motionZ = 0;
			photoEntity.hoverStart = 0;
			worldObj.spawnEntityInWorld(photoEntity);
			this.setDead();
		}
		return true;
	}

	@Override
	public float getEyeHeight() {
		return 0;
	}

	public boolean canBeCollidedWith() {
		return true;
	}

	protected void setSize(float width, float height) {

	}

	public void setPosition(double x, double y, double z) {

	}

	public void moveEntity(double x, double y, double z) {

	}

	public AxisAlignedBB getCollisionBoundingBox() {
		return getEntityBoundingBox();
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeDouble(offsetX);
		buffer.writeDouble(offsetY);
		buffer.writeDouble(offsetZ);
		buffer.writeByte(facing.getIndex());
		buffer.writeInt(index);
		buffer.writeDouble(posX);
		buffer.writeDouble(posY);
		buffer.writeDouble(posZ);
		buffer.writeInt(width);
		buffer.writeInt(height);
		buffer.writeInt(rotation);
		if(framesRL == null)
			buffer.writeInt(0);
		else {
			byte[] byteBuffer = framesRL.toString().getBytes();
			buffer.writeInt(byteBuffer.length);
			buffer.writeBytes(byteBuffer, 0, byteBuffer.length);
		}
		if(backRL == null)
			buffer.writeInt(0);
		else {
			byte[] byteBuffer = backRL.toString().getBytes();
			buffer.writeInt(byteBuffer.length);
			buffer.writeBytes(byteBuffer, 0, byteBuffer.length);
		}
	}

	@Override
	public void readSpawnData(ByteBuf buffer) {
		offsetX = buffer.readDouble();
		offsetY = buffer.readDouble();
		offsetZ = buffer.readDouble();
		facing = EnumFacing.getFront(buffer.readByte());
		index = buffer.readInt();
		posX = buffer.readDouble();
		posY = buffer.readDouble();
		posZ = buffer.readDouble();
		width = buffer.readInt();
		height = buffer.readInt();
		rotation = buffer.readInt();
		int l = buffer.readInt();
		if(l > 0) {
			String s;
			byte[] byteBuffer = new byte[l];
			buffer.readBytes(byteBuffer, 0, l);
			framesRL = new ResourceLocation(new String(byteBuffer));
		}
		else framesRL = null;
		l = buffer.readInt();
		if(l > 0) {
			String s;
			byte[] byteBuffer = new byte[l];
			buffer.readBytes(byteBuffer, 0, l);
			backRL = new ResourceLocation(new String(byteBuffer));
		}
		else backRL = null;
		this.setEntityBoundingBox(new AxisAlignedBB(posX - (offsetX == 0 ? 0.05 : offsetX), posY - (offsetY == 0 ? 0.05 : offsetY), posZ - (offsetZ == 0 ? 0.05 : offsetZ), posX + (offsetX == 0 ? 0.05 : offsetX), posY + (offsetY == 0 ? 0.05 : offsetY), posZ + (offsetZ == 0 ? 0.05 : offsetZ)));
	}
}
