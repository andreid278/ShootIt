package com.andreid278.shootit.Events;

import com.andreid278.shootit.Items.Camera;
import com.andreid278.shootit.Items.MemoryCard;
import com.andreid278.shootit.Misc.Statics;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MouseEvents {
	@SubscribeEvent
	public void mouseWheel(MouseEvent event) {
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		if(ep.isSneaking())
			if(event.getDwheel() != 0)
			if(ep.getHeldItemMainhand() != null)
				if(ep.getHeldItemMainhand().getItem() instanceof Camera) {
					Statics.cameraFov -= Math.signum(event.getDwheel()) * 5;
					if(Statics.cameraFov < 10)
						Statics.cameraFov = 10;
					if(Statics.cameraFov > 120)
						Statics.cameraFov = 120;
					event.setCanceled(true);
				}
	}
}
