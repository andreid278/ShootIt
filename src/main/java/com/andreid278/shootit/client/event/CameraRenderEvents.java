package com.andreid278.shootit.client.event;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.andreid278.shootit.ShootIt;
import com.andreid278.shootit.client.Resources;
import com.andreid278.shootit.client.gui.GuiHandler;
import com.andreid278.shootit.common.MCData;
import com.andreid278.shootit.common.item.Camera;
import com.google.common.io.Files;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CameraRenderEvents {
	@SubscribeEvent
	public void renderCameraFirstView(RenderHandEvent event) {
		if(Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() instanceof Camera)
			event.setCanceled(true);
	}

	@SubscribeEvent
	public void renderCamera(RenderGameOverlayEvent.Post event) {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer ep = mc.player;
		if(ep.getHeldItemMainhand().getItem() instanceof Camera) {
			if(mc.gameSettings.thirdPersonView == 0) {
				Minecraft.getMinecraft().gameSettings.fovSetting = MCData.cameraFov;
				
				GlStateManager.pushMatrix();
				GlStateManager.enableBlend();
				mc.getTextureManager().bindTexture(Resources.CAMERA_FIRST_VIEW);
				ScaledResolution sr = new ScaledResolution(mc);
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder vertexbuffer = tessellator.getBuffer();
				vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
				vertexbuffer.pos(0.0D, sr.getScaledHeight_double(), 0).tex(0.0D, 1.0D).endVertex();
				vertexbuffer.pos(sr.getScaledWidth_double(), sr.getScaledHeight_double(), 0).tex(1.0D, 1.0D).endVertex();
				vertexbuffer.pos(sr.getScaledWidth_double(), 0.0D, 0).tex(1.0D, 0.0D).endVertex();
				vertexbuffer.pos(0.0D, 0.0D, 0).tex(0.0D, 0.0D).endVertex();
				tessellator.draw();
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();

				ItemStack camera = ep.getHeldItemMainhand();
				NBTTagCompound nbt = camera.getTagCompound();
				if(ep.ticksExisted % 10 == 0) {
					if(nbt != null) {
						int shader = nbt.getInteger("shader");
						if(MCData.lastShader != shader) {
							if(shader > 0) {
								mc.entityRenderer.loadShader(MCData.shaders.get(shader).rl);
							}
							else mc.entityRenderer.stopUseShader();
							MCData.lastShader = shader;
						}
					}
					else MCData.lastShader = 0;
				}
				
				if(MCData.isShooting) {
					MCData.isShooting = false;

					Framebuffer buffer = mc.getFramebuffer();
					int width = mc.displayWidth;
					int height = mc.displayHeight;
					if (OpenGlHelper.isFramebufferEnabled()) {
						width = buffer.framebufferTextureWidth;
						height = buffer.framebufferTextureHeight;							
					}
					IntBuffer pixelBuffer = BufferUtils.createIntBuffer(width * height);
					GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
					GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
					pixelBuffer.clear();
					if (OpenGlHelper.isFramebufferEnabled()) {
						GlStateManager.bindTexture(buffer.framebufferTexture);
						GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, (IntBuffer)pixelBuffer);
					}
					else {
						GL11.glReadPixels(0, 0, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, (IntBuffer)pixelBuffer);
					}

					int[] pixelValues = new int[width * height];
					pixelBuffer.get(pixelValues);
					TextureUtil.processPixelValues(pixelValues, width, height);

					BufferedImage bufferedimage = null;
					if (OpenGlHelper.isFramebufferEnabled()) {
						int bx1 = (int)(MCData.x1 * buffer.framebufferWidth / 256.0);
						int bx2 = (int)(MCData.x2 * buffer.framebufferWidth / 256.0);
						int by1 = (int)(MCData.y1 * buffer.framebufferHeight / 150.0);
						int by2 = (int)(MCData.y2 * buffer.framebufferHeight / 150.0);
						bufferedimage = new BufferedImage(bx2 - bx1, by2 - by1, 1);
						int j = buffer.framebufferTextureHeight - buffer.framebufferHeight;

						for(int k = j + by1; k < j + by2; ++k)
							for(int l = bx1; l < bx2; ++l) {
								bufferedimage.setRGB(l - bx1, k - j - by1, pixelValues[k * buffer.framebufferTextureWidth + l]);
							}
					}
					else {
						int bx1 = (int)(MCData.x1 * width / 256.0);
						int bx2 = (int)(MCData.x2 * width / 256.0);
						int by1 = (int)(MCData.y1 * height / 150.0);
						int by2 = (int)(MCData.y2 * height / 150.0);
						bufferedimage = new BufferedImage(bx2 - bx1, by2 - by1, 1);
						//bufferedimage.setRGB(bx1, by1, bx2 - bx1, by2 - by1, pixelValues, by1 * width + bx1, width);
						for(int k = by1; k < by2; ++k)
							for(int l = bx1; l < bx2; ++l) {
								bufferedimage.setRGB(l - bx1, k - by1, pixelValues[k * width + l]);
							}
					}
					try {
						File file = new File(MCData.photosFolderPathClient);
						if(!file.isDirectory())
							file.mkdirs();
						File imageFile;
						imageFile = new File(MCData.photosFolderPathClient + "/0.png");
						ImageIO.write(bufferedimage, "png", (File)imageFile);
						//							Photos.photos.put(0, new ResourceLocation("photos", "0.png"));
						if(MCData.imageIDToLoadToServer == 0) {
							if(!Minecraft.getMinecraft().isSingleplayer())
								MCData.imageToLoadToServer = Files.toByteArray(imageFile);
							ep.openGui(ShootIt.instance, GuiHandler.CONFIRM_SAVING_PHOTO_GUI_ID, mc.world, 0, 0, 0);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			else MCData.isShooting = false;
			
			return;
		}
		
		if(MCData.lastShader > 0) {
			mc.entityRenderer.stopUseShader();
			MCData.lastShader = 0;
		}
		
		if(MCData.fov != MCData.cameraFov) {
			MCData.cameraFov = MCData.fov;
			Minecraft.getMinecraft().gameSettings.fovSetting = MCData.fov;
		}
	}
}
