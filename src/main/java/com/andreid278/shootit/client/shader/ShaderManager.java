package com.andreid278.shootit.client.shader;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.andreid278.shootit.ShootIt;
import com.andreid278.shootit.client.shader.Shader.UniformInfo;
import com.andreid278.shootit.common.network.MessageCameraToServer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class ShaderManager {
	public static ShaderManager instance = new ShaderManager();
	public List<com.andreid278.shootit.client.shader.Shader> shaderList = new ArrayList<>();

	public Framebuffer mcFramebuffer;
	public Framebuffer modFramebuffer;

	public ShaderManager() {
		mcFramebuffer = null;
		modFramebuffer = null;

		com.andreid278.shootit.client.shader.Shader shader;

		shader = new com.andreid278.shootit.client.shader.Shader("No filter");
		shaderList.add(shader);
		
		shader = new com.andreid278.shootit.client.shader.Shader("Gray", new ResourceLocation("minecraft", "shaders/post/shootit_gray.json"));
		shaderList.add(shader);
		
		shader = new com.andreid278.shootit.client.shader.Shader("Invert", new ResourceLocation("minecraft", "shaders/post/shootit_invert.json"));
		shaderList.add(shader);

		shader = new com.andreid278.shootit.client.shader.Shader("Color", new ResourceLocation("minecraft", "shaders/post/shootit_color.json"));
		shader.addUniform("Red", 0.0f, 1.0f, 1.0f, 0.01f);
		shader.addUniform("Green", 0.0f, 1.0f, 1.0f, 0.01f);
		shader.addUniform("Blue", 0.0f, 1.0f, 1.0f, 0.01f);
		shaderList.add(shader);
		
		shader = new com.andreid278.shootit.client.shader.Shader("Saturation", new ResourceLocation("minecraft", "shaders/post/shootit_saturation.json"));
		shader.addUniform("Coeff", 0.0f, 10.0f, 1.0f, 0.01f);
		shaderList.add(shader);

		shader = new com.andreid278.shootit.client.shader.Shader("Contrast", new ResourceLocation("minecraft", "shaders/post/shootit_contrast.json"));
		shader.addUniform("Contrast", 0.0f, 10.0f, 1.0f, 0.01f);
		shader.addUniform("Brightness", 0.0f, 1.0f, 0.0f, 0.01f);
		shaderList.add(shader);
		
		shader = new com.andreid278.shootit.client.shader.Shader("Gamma", new ResourceLocation("minecraft", "shaders/post/shootit_gamma.json"));
		shader.addUniform("Coeff", 0.0f, 10.0f, 1.0f, 0.01f);
		shaderList.add(shader);
		
		shader = new com.andreid278.shootit.client.shader.Shader("Blur", new ResourceLocation("minecraft", "shaders/post/shootit_blur.json"));
		shader.addUniform("Radius", 0.0f, 25.0f, 0.0f, 1.0f);
		shaderList.add(shader);

		shader = new com.andreid278.shootit.client.shader.Shader("Resolution", new ResourceLocation("minecraft", "shaders/post/shootit_resolution.json"));
		shader.addUniform("Radius", 1.0f, 15.0f, 1.0f, 1.0f);
		shaderList.add(shader);

		shader = new com.andreid278.shootit.client.shader.Shader("Bokeh", new ResourceLocation("minecraft", "shaders/post/shootit_bokeh.json"));
		shader.addUniform("Radius", 1.0f, 15.0f, 1.0f, 1.0f);
		shader.addUniform("BokehCoeff", 0.0f, 1.0f, 0.5f, 0.01f);
		shaderList.add(shader);

		shader = new com.andreid278.shootit.client.shader.Shader("Emboss", new ResourceLocation("minecraft", "shaders/post/shootit_emboss.json"));
		shader.addUniform("Contrast", 0.0f, 10.0f, 1.0f, 0.01f);
		shader.addUniform("Colored", 0.0f, 1.0f, 0.0f, 1.0f);
		shaderList.add(shader);
		
		shader = new com.andreid278.shootit.client.shader.Shader("Posterization", new ResourceLocation("minecraft", "shaders/post/shootit_posterization.json"));
		shader.addUniform("Gamma", 0.01f, 5.0f, 1.0f, 0.01f);
		shader.addUniform("NumColors", 1.0f, 25.0f, 25.0f, 1.0f);
		shaderList.add(shader);
		
//		shader = new com.andreid278.shootit.client.shader.Shader("Neon", new ResourceLocation("minecraft", "shaders/post/shootit_neon.json"));
//		shader.addUniform("Radius", 0.0f, 10.0f, 0.0f, 1.0f);
//		shader.addUniform("Brightness", 0.0f, 1.0f, 0.0f, 0.01f);
//		shaderList.add(shader);
		
//		shader = new com.andreid278.shootit.client.shader.Shader("Kuwahara", new ResourceLocation("minecraft", "shaders/post/shootit_kuwahara.json"));
//		shader.addUniform("Radius", 1.0f, 25.0f, 1.0f, 1.0f);
//		shaderList.add(shader);
		
		shader = new com.andreid278.shootit.client.shader.Shader("DOF", new ResourceLocation("minecraft", "shaders/post/shootit_dof.json"));
		shader.addUniform("BlurRadius", 0.0f, 25.0f, 0.0f, 1.0f);
		shader.addUniform("BlurDepth", 0.0f, 1.0f, 0.5f, 0.01f);
		shaderList.add(shader);
	}

	public void setCurShaderUnifroms(int index, NBTTagCompound nbt) {
		if(Minecraft.getMinecraft().entityRenderer.getShaderGroup() == null) {
			return;
		}

		Class sg = Minecraft.getMinecraft().entityRenderer.getShaderGroup().getClass();
		Field ls = null;
		try {
			ls = sg.getDeclaredField("listShaders");
		}
		catch (NoSuchFieldException e) {
			try {
				ls = sg.getDeclaredField("field_148031_d");
			} catch (NoSuchFieldException | SecurityException e1) {
				e1.printStackTrace();
			}
		}
		if(ls != null) {
			ls.setAccessible(true);
			List<Shader> listShaders;
			try {
				listShaders = (List<Shader>) ls.get(Minecraft.getMinecraft().entityRenderer.getShaderGroup());
				for(Shader shader : listShaders) {
					for(UniformInfo uniform : shaderList.get(index).uniformList) {
						shader.getShaderManager().getShaderUniformOrDefault(uniform.name).set(nbt.getFloat(uniform.name));
					}
				}

				ls.set(Minecraft.getMinecraft().entityRenderer.getShaderGroup(), listShaders);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void updateCameraShaderInfo(int index, NBTTagCompound nbt) {
		ShootIt.network.sendToServer(new MessageCameraToServer((byte)3, index, nbt));
	}

	public void loadShader(int shaderID) {
		//setFramebuffer(shaderID);
		if(shaderID >= shaderList.size()) {
			return;
		}
		
		if(shaderID == 0) {
			Minecraft.getMinecraft().entityRenderer.stopUseShader();
			enableRenderHands(true);
		}
		else {

			enableRenderHands(false);
			Minecraft.getMinecraft().entityRenderer.loadShader(shaderList.get(shaderID).rl);

			Class sg = Minecraft.getMinecraft().entityRenderer.getShaderGroup().getClass();
			Field ls = null;
			try {
				ls = sg.getDeclaredField("listShaders");
			}
			catch (NoSuchFieldException e) {
				try {
					ls = sg.getDeclaredField("field_148031_d");
				} catch (NoSuchFieldException | SecurityException e1) {
					e1.printStackTrace();
				}
			}
			if(ls != null) {
				ls.setAccessible(true);
				List<Shader> listShaders;
				try {
					listShaders = (List<Shader>) ls.get(Minecraft.getMinecraft().entityRenderer.getShaderGroup());
					for(Shader shader : listShaders) {
						if(shader.framebufferIn.useDepth) {
							shader.addAuxFramebuffer("DepthSampler", ((DepthFramebuffer)Minecraft.getMinecraft().getFramebuffer()).depthTexture, shader.framebufferIn.framebufferTextureWidth, shader.framebufferIn.framebufferTextureHeight);
						}
					}

					ls.set(Minecraft.getMinecraft().entityRenderer.getShaderGroup(), listShaders);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void setFramebuffer(int shaderID) {
		Class mc = Minecraft.getMinecraft().getClass();
		Field fb = null;
		try {
			fb = mc.getDeclaredField("framebufferMc");
		}
		catch (NoSuchFieldException e) {
			try {
				fb = mc.getDeclaredField("field_147124_at");
			} catch (NoSuchFieldException | SecurityException e1) {
				e1.printStackTrace();
			}
		}
		if(fb != null) {
			fb.setAccessible(true);
			Framebuffer framebufferMc;
			try {
				framebufferMc = (Framebuffer) fb.get(Minecraft.getMinecraft());
				
				if(shaderID == 0) {
					framebufferMc = mcFramebuffer;
				}
				else {
					framebufferMc = modFramebuffer;
				}

				fb.set(Minecraft.getMinecraft(), framebufferMc);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setUpFramebuffers() {
		mcFramebuffer = Minecraft.getMinecraft().getFramebuffer();
		
		modFramebuffer = new DepthFramebuffer(mcFramebuffer.framebufferWidth, mcFramebuffer.framebufferHeight, true);
		modFramebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
		
		setFramebuffer(1);
	}
	
	private void enableRenderHands(boolean toRender) {
		Class sg = Minecraft.getMinecraft().entityRenderer.getClass();
		Field ls = null;
		try {
			ls = sg.getDeclaredField("renderHand");
		}
		catch (NoSuchFieldException e) {
			try {
				ls = sg.getDeclaredField("field_175074_C");
			} catch (NoSuchFieldException | SecurityException e1) {
				e1.printStackTrace();
			}
		}
		if(ls != null) {
			ls.setAccessible(true);
			try {
				ls.set(Minecraft.getMinecraft().entityRenderer,  toRender);
			} catch (IllegalArgumentException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
