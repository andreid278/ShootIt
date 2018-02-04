package com.andreid278.shootit.client.event;

import com.andreid278.shootit.common.MCData;
import com.andreid278.shootit.common.item.Camera;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
}
