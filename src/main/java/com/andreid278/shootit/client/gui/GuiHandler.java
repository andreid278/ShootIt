package com.andreid278.shootit.client.gui;

import com.andreid278.shootit.common.container.CameraContainer;
import com.andreid278.shootit.common.container.CameraInventory;
import com.andreid278.shootit.common.container.PainterContainer;
import com.andreid278.shootit.common.container.PrinterContainer;
import com.andreid278.shootit.common.item.Camera;
import com.andreid278.shootit.common.tiileentity.TEPainter;
import com.andreid278.shootit.common.tiileentity.TEPrinter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	public static final int CONFIRM_SAVING_PHOTO_GUI_ID = 0;
	public static final int PRINTER_GUI = 1;
	public static final int CAMERA_GUI = 2;
	public static final int PAINTER_GUI = 3;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch(ID) {
		case CONFIRM_SAVING_PHOTO_GUI_ID:
			break;
		case PRINTER_GUI:
			return new PrinterContainer(player.inventory, (TEPrinter)world.getTileEntity(new BlockPos(x, y, z)));
//		case CAMERA_GUI:
//			break;
		case CAMERA_GUI:
			return new CameraContainer(player.inventory, new CameraInventory(player.getHeldItemMainhand()));
		case PAINTER_GUI:
			return new PainterContainer(player.inventory, (TEPainter)world.getTileEntity(new BlockPos(x, y, z)));
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch(ID) {
		case CONFIRM_SAVING_PHOTO_GUI_ID:
			if(player.getHeldItemMainhand() != null)
				if(player.getHeldItemMainhand().getItem() instanceof Camera)
					return new ConfirmSavingPhotoGui(player.getHeldItemMainhand());
			break;
		case PRINTER_GUI:
			return new PrinterGui(player.inventory, (TEPrinter)world.getTileEntity(new BlockPos(x, y, z)));
//		case CAMERA_GUI:
//			return new CameraGui();
		case CAMERA_GUI:
			return new CameraGui(new CameraContainer(player.inventory, new CameraInventory(player.getHeldItemMainhand())));
		case PAINTER_GUI:
			return new PainterGui(player.inventory, (TEPainter)world.getTileEntity(new BlockPos(x, y, z)));
		}
		return null;
	}
}
