package com.andreid278.shootit.client.event;

import com.andreid278.shootit.ShootIt;
import com.andreid278.shootit.client.ClientProxy;
import com.andreid278.shootit.client.gui.GuiHandler;
import com.andreid278.shootit.common.MCData;
import com.andreid278.shootit.common.item.Camera;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class InputEvents {
	@SubscribeEvent
	public void mouseWheel(MouseEvent event) {
		if(event.getDwheel() != 0) {
			EntityPlayer ep = Minecraft.getMinecraft().player;
			if(ep.isSneaking()) {
				if(ep.getHeldItemMainhand().getItem() instanceof Camera) {
					MCData.cameraFov -= Math.signum(event.getDwheel()) * 5;
					if(MCData.cameraFov < 10)
						MCData.cameraFov = 10;
					if(MCData.cameraFov > 120)
						MCData.cameraFov = 120;
					event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public void keyEvent(InputEvent.KeyInputEvent event) {
		if(ClientProxy.cameraFilters.isPressed()) {
			if(Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() instanceof Camera) {
				Minecraft.getMinecraft().player.openGui(ShootIt.instance, GuiHandler.CAMERA_FILTERS_GUI, Minecraft.getMinecraft().world, 0, 0, 0);
			}
		}
	}
}
