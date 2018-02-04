package com.andreid278.shootit.client.event;

import com.andreid278.shootit.ShootIt;
import com.andreid278.shootit.client.renderer.ArmModelRenderer;
import com.andreid278.shootit.common.MCData;
import com.andreid278.shootit.common.item.Camera;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerRenderEvents {
	@SubscribeEvent
	public void renderPlayer(RenderPlayerEvent.Pre event) {
		if(event.getEntityPlayer().ticksExisted % 10 == 0) {
			Boolean cameraHoldPhase = MCData.cameraHoldPhase.get(event.getEntityPlayer().getUniqueID());
			if(cameraHoldPhase == null) {
				cameraHoldPhase = false;
				MCData.cameraHoldPhase.put(event.getEntityPlayer().getUniqueID(), false);
			}
			if(event.getEntityPlayer().getHeldItemMainhand().getItem() instanceof Camera) {
				if(!cameraHoldPhase) {
					MCData.cameraHoldPhase.put(event.getEntityPlayer().getUniqueID(), true);
					ArmModelRenderer newArmRight = new ArmModelRenderer(event.getRenderer().getMainModel(), 40, 16, true);
					NetworkPlayerInfo networkplayerinfo = Minecraft.getMinecraft().getConnection().getPlayerInfo(event.getEntityPlayer().getUniqueID());
					boolean isSlim = (networkplayerinfo == null ? DefaultPlayerSkin.getSkinType(event.getEntityPlayer().getUniqueID()) : networkplayerinfo.getSkinType()) == "slim";
					if(isSlim) {
						newArmRight.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, 0);
						newArmRight.setRotationPoint(-5.0F, 2.5F, 0.0F);
					}
					else {
						newArmRight.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, 0);
						newArmRight.setRotationPoint(-5.0F, 2.0F, 0.0F);
					}
					event.getRenderer().getMainModel().bipedRightArm = newArmRight;
				}
				return;
			}
			if(cameraHoldPhase) {
				MCData.cameraHoldPhase.put(event.getEntityPlayer().getUniqueID(), false);
				ModelRenderer newArm = new ModelRenderer(event.getRenderer().getMainModel(), 40, 16);
				NetworkPlayerInfo networkplayerinfo = Minecraft.getMinecraft().getConnection().getPlayerInfo(event.getEntityPlayer().getUniqueID());
				boolean isSlim = (networkplayerinfo == null ? DefaultPlayerSkin.getSkinType(event.getEntityPlayer().getUniqueID()) : networkplayerinfo.getSkinType()) == "slim";
				if(isSlim) {
					newArm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, 0);
					newArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
				}
				else {
					newArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, 0);
					newArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
				}
				event.getRenderer().getMainModel().bipedRightArm = newArm;
			}
			return;
		}
	}
}
