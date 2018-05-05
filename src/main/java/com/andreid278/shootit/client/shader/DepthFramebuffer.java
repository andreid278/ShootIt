package com.andreid278.shootit.client.shader;

import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;

public class DepthFramebuffer extends Framebuffer {

	public int depthTexture = -1;

	public DepthFramebuffer(int width, int height, boolean useDepthIn) {
		super(width, height, useDepthIn);
	}

	@Override
	public void deleteFramebuffer() {
		if (OpenGlHelper.isFramebufferEnabled())
		{
			this.unbindFramebufferTexture();
			this.unbindFramebuffer();

			if (this.depthTexture > -1)
			{
				TextureUtil.deleteTexture(this.depthTexture);
				this.depthTexture = -1;
			}

			if (this.framebufferTexture > -1)
			{
				TextureUtil.deleteTexture(this.framebufferTexture);
				this.framebufferTexture = -1;
			}

			if (this.framebufferObject > -1)
			{
				OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, 0);
				OpenGlHelper.glDeleteFramebuffers(this.framebufferObject);
				this.framebufferObject = -1;
			}
		}
	}

	@Override
	public void createFramebuffer(int width, int height) {
		this.framebufferWidth = width;
		this.framebufferHeight = height;
		this.framebufferTextureWidth = width;
		this.framebufferTextureHeight = height;

		if (!OpenGlHelper.isFramebufferEnabled())
		{
			this.framebufferClear();
			}
		else
		{
			this.framebufferObject = OpenGlHelper.glGenFramebuffers();
			this.framebufferTexture = TextureUtil.glGenTextures();
			this.depthTexture = TextureUtil.glGenTextures();

			this.setFramebufferFilter(9728);
			GlStateManager.bindTexture(this.framebufferTexture);
			GlStateManager.glTexImage2D(3553, 0, 32856, this.framebufferTextureWidth, this.framebufferTextureHeight, 0, 6408, 5121, (IntBuffer)null);
			OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, this.framebufferObject);
			OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0, 3553, this.framebufferTexture, 0);

			GlStateManager.bindTexture(this.depthTexture);
			GlStateManager.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, this.framebufferTextureWidth, this.framebufferTextureHeight, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (IntBuffer)null);
			OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_DEPTH_ATTACHMENT, 3553, this.depthTexture, 0);
			
			this.framebufferClear();
			this.unbindFramebufferTexture();
		}
	}

	public void setFramebufferFilter(int framebufferFilterIn)
	{
		if (OpenGlHelper.isFramebufferEnabled())
		{
			this.framebufferFilter = framebufferFilterIn;

			GlStateManager.bindTexture(this.framebufferTexture);
			GlStateManager.glTexParameteri(3553, 10241, framebufferFilterIn);
			GlStateManager.glTexParameteri(3553, 10240, framebufferFilterIn);
			GlStateManager.glTexParameteri(3553, 10242, 10496);
			GlStateManager.glTexParameteri(3553, 10243, 10496);
			
			GlStateManager.bindTexture(this.depthTexture);
			GlStateManager.glTexParameteri(3553, 10241, framebufferFilterIn);
			GlStateManager.glTexParameteri(3553, 10240, framebufferFilterIn);
			GlStateManager.glTexParameteri(3553, 10242, 10496);
			GlStateManager.glTexParameteri(3553, 10243, 10496);
			GlStateManager.bindTexture(0);
		}
	}

	public void framebufferClear()
	{
		this.bindFramebuffer(true);
		GlStateManager.clearColor(this.framebufferColor[0], this.framebufferColor[1], this.framebufferColor[2], this.framebufferColor[3]);
		GlStateManager.clearDepth(1.0D);
		int i = 16640;

		GlStateManager.clear(i);
		this.unbindFramebuffer();
	}
}
