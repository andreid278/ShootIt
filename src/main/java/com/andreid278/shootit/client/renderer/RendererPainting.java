package com.andreid278.shootit.client.renderer;

import java.io.File;

import org.lwjgl.opengl.GL11;

import com.andreid278.shootit.ShootIt;
import com.andreid278.shootit.client.Resources;
import com.andreid278.shootit.common.MCData;
import com.andreid278.shootit.common.PhotosData;
import com.andreid278.shootit.common.entity.EntityPainting;
import com.andreid278.shootit.common.network.MessageRequestForPhoto;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class RendererPainting extends Render<EntityPainting> {
	public RendererPainting(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityPainting entity) {
		return Resources.LOADING;
	}
	
	@Override
	public void doRender(EntityPainting entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		
		if(PhotosData.photos.containsKey(entity.index))
			Minecraft.getMinecraft().getTextureManager().bindTexture(PhotosData.photos.get(entity.index));
		else {
			if(MCData.imageIDToLoadFromServer == 0 && !Minecraft.getMinecraft().isSingleplayer()) {
				MCData.imageIDToLoadFromServer = entity.index;
				ShootIt.network.sendToServer(new MessageRequestForPhoto(entity.index));
			}
			else if(Minecraft.getMinecraft().isSingleplayer()) {
				File file = new File(MCData.photosFolderPathClient + "/" + entity.index + ".png");
				if(file.exists())
					PhotosData.addPhoto(entity.index);
				else PhotosData.addEmptyPhoto(entity.index);
			}
			Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.LOADING);
		}

		if(entity.facing == EnumFacing.DOWN || entity.facing == EnumFacing.UP) {
			GlStateManager.rotate(entity.rotation * 90 - 90, 0, 1, 0);
			if(entity.facing == EnumFacing.DOWN)
				GlStateManager.rotate(90, 1, 0, 0);
			else GlStateManager.rotate(-90, 1, 0, 0);
		}
		else
			GlStateManager.rotate((entity.facing.getHorizontalIndex() % 2 == 0 ? entity.facing.getHorizontalIndex() : entity.facing.getOpposite().getHorizontalIndex()) * 90, 0, 1, 0);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		double width = entity.width / 2.0;
		double height = entity.height / 2.0;
		double widthR;
		double heightR;
		double framesWidth = 1 / 10.0;
		double t = 0.05;
		if(entity.framesRL == null) {
			widthR = width;
			heightR = height;
		}
		else {
			widthR = width - 1 / 10.0;
			heightR = height - 1 / 10.0;
		}
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
		vertexbuffer.pos(widthR, heightR, 0.01).tex(entity.textureX2, entity.textureY1).normal(0, 0, -1).endVertex();
		vertexbuffer.pos(-widthR, heightR, 0.01).tex(entity.textureX1, entity.textureY1).normal(0, 0, -1).endVertex();
		vertexbuffer.pos(-widthR, -heightR, 0.01).tex(entity.textureX1, entity.textureY2).normal(0, 0, -1).endVertex();
		vertexbuffer.pos(widthR, -heightR, 0.01).tex(entity.textureX2, entity.textureY2).normal(0, 0, -1).endVertex();
		tessellator.draw();

		if(entity.backRL == null)
			Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.PHOTO_BACK);
		else Minecraft.getMinecraft().getTextureManager().bindTexture(entity.backRL);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
		vertexbuffer.pos(width, -height, 0).tex(1 * entity.width, 1 * entity.height).normal(0, 0, 1).endVertex();
		vertexbuffer.pos(-width, -height, 0).tex(0, 1 * entity.height).normal(0, 0, 1).endVertex();
		vertexbuffer.pos(-width, height, 0).tex(0, 0).normal(0, 0, 1).endVertex();
		vertexbuffer.pos(width, height, 0).tex(1 * entity.width, 0).normal(0, 0, 1).endVertex();
		tessellator.draw();

		if(entity.framesRL != null) {
			Minecraft.getMinecraft().getTextureManager().bindTexture(entity.framesRL);
			vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);

			//Left
			vertexbuffer.pos(-widthR, height, t).tex(framesWidth, 0).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(-width, height, t).tex(0, 0).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(-width, -heightR, t).tex(0, entity.height - framesWidth).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(-widthR, -heightR, t).tex(framesWidth, entity.height - framesWidth).normal(0, 0, -1).endVertex();

			vertexbuffer.pos(-width, height, t).tex(t, 0).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(-width, height, 0).tex(0, 0).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(-width, -height, 0).tex(0, entity.height - framesWidth).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(-width, -height, t).tex(t, entity.height - framesWidth).normal(0, 0, -1).endVertex();

			vertexbuffer.pos(-widthR, heightR, 0).tex(t, 0).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(-widthR, heightR, t).tex(0, 0).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(-widthR, -heightR, t).tex(0, entity.height - framesWidth * 2).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(-widthR, -heightR, 0).tex(t, entity.height - framesWidth * 2).normal(0, 0, -1).endVertex();

			//Right
			vertexbuffer.pos(width, heightR, t).tex(framesWidth, 0).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(widthR, heightR, t).tex(0, 0).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(widthR, -height, t).tex(0, entity.height - framesWidth).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(width, -height, t).tex(framesWidth, entity.height - framesWidth).normal(0, 0, -1).endVertex();

			vertexbuffer.pos(width, height, 0).tex(t, 0).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(width, height, t).tex(0, 0).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(width, -height, t).tex(0, entity.height - framesWidth).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(width, -height, 0).tex(t, entity.height - framesWidth).normal(0, 0, -1).endVertex();

			vertexbuffer.pos(widthR, heightR, t).tex(t, 0).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(widthR, heightR, 0).tex(0, 0).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(widthR, -heightR, 0).tex(0, entity.height - framesWidth * 2).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(widthR, -heightR, t).tex(t, entity.height - framesWidth * 2).normal(0, 0, -1).endVertex();

			//Up
			vertexbuffer.pos(width, height, t).tex(entity.width - framesWidth, 0).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(-widthR, height, t).tex(0, 0).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(-widthR, heightR, t).tex(0, framesWidth).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(width, heightR, t).tex(entity.width - framesWidth, framesWidth).normal(0, 0, -1).endVertex();

			vertexbuffer.pos(width, height, 0).tex(entity.width - framesWidth, 0).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(-width, height, 0).tex(0, 0).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(-width, height, t).tex(0, t).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(width, height, t).tex(entity.width - framesWidth, t).normal(0, 0, -1).endVertex();

			vertexbuffer.pos(widthR, heightR, t).tex(entity.width - framesWidth * 2, 0).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(-widthR, heightR, t).tex(0, 0).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(-widthR, heightR, 0).tex(0, t).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(widthR, heightR, 0).tex(entity.width - framesWidth * 2, t).normal(0, 0, -1).endVertex();

			//Down
			vertexbuffer.pos(widthR, -heightR, t).tex(entity.width - framesWidth, 0).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(-width, -heightR, t).tex(0, 0).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(-width, -height, t).tex(0, framesWidth).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(widthR, -height, t).tex(entity.width - framesWidth, framesWidth).normal(0, 0, -1).endVertex();

			vertexbuffer.pos(width, -height, t).tex(entity.width - framesWidth, 0).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(-width, -height, t).tex(0, 0).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(-width, -height, 0).tex(0, t).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(width, -height, 0).tex(entity.width - framesWidth, t).normal(0, 0, -1).endVertex();

			vertexbuffer.pos(widthR, -heightR, 0).tex(entity.width - framesWidth * 2, 0).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(-widthR, -heightR, 0).tex(0, 0).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(-widthR, -heightR, t).tex(0, t).normal(0, 0, -1).endVertex();
			vertexbuffer.pos(widthR, -heightR, t).tex(entity.width - framesWidth * 2, t).normal(0, 0, -1).endVertex();

			tessellator.draw();
		}
		
		GlStateManager.popMatrix();
		GL11.glDisable(GL11.GL_BLEND);
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}
	
	@Override
	public void doRenderShadowAndFire(Entity entityIn, double x, double y, double z, float yaw, float partialTicks) {
		
	}
}
