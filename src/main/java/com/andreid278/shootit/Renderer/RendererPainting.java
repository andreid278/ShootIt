package com.andreid278.shootit.Renderer;

import java.io.File;
import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.andreid278.shootit.Main;
import com.andreid278.shootit.Entity.EntityPainting;
import com.andreid278.shootit.Misc.Photos;
import com.andreid278.shootit.Misc.Statics;
import com.andreid278.shootit.Network.MessageRequestForPhoto;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class RendererPainting extends Render<EntityPainting> {
	public RendererPainting(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityPainting entity) {
		return Statics.LOADING;
	}

	public void doRender(EntityPainting entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.enableRescaleNormal();

		if(Photos.photos.containsKey(entity.index))
			Minecraft.getMinecraft().getTextureManager().bindTexture(Photos.photos.get(entity.index));
		else {
			if(Statics.imageIDToLoadFromServer == 0 && !Minecraft.getMinecraft().isSingleplayer()) {
				Statics.imageIDToLoadFromServer = entity.index;
				Main.network.sendToServer(new MessageRequestForPhoto(entity.index));
			}
			else if(Minecraft.getMinecraft().isSingleplayer()) {
				File file = new File(Statics.photosFolderPathClient + Statics.slash + entity.index + ".png");
				if(file.exists())
					Photos.addPhoto(entity.index);
				else Photos.addEmptyPhoto(entity.index);
			}
			Minecraft.getMinecraft().getTextureManager().bindTexture(Statics.LOADING);
		}

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
		vertexbuffer.pos(entity.offsetX, entity.offsetY, entity.offsetZ).tex(1, 0).normal(0, 0, -1).endVertex();
		vertexbuffer.pos(-entity.offsetX, entity.offsetY, -entity.offsetZ).tex(0, 0).normal(0, 0, -1).endVertex();
		vertexbuffer.pos(-entity.offsetX, -entity.offsetY, -entity.offsetZ).tex(0, 1).normal(0, 0, -1).endVertex();
		vertexbuffer.pos(entity.offsetX, -entity.offsetY, entity.offsetZ).tex(1, 1).normal(0, 0, -1).endVertex();
		tessellator.draw();
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(Statics.PHOTO_BACK);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
		vertexbuffer.pos(entity.offsetX, -entity.offsetY, entity.offsetZ).tex(1, 1).normal(0, 0, -1).endVertex();
		vertexbuffer.pos(-entity.offsetX, -entity.offsetY, -entity.offsetZ).tex(0, 1).normal(0, 0, -1).endVertex();
		vertexbuffer.pos(-entity.offsetX, entity.offsetY, -entity.offsetZ).tex(0, 0).normal(0, 0, -1).endVertex();
		vertexbuffer.pos(entity.offsetX, entity.offsetY, entity.offsetZ).tex(1, 0).normal(0, 0, -1).endVertex();
		tessellator.draw();

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}
}
