package com.andreid278.shootit.client;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import com.andreid278.shootit.ShootIt;
import com.andreid278.shootit.client.event.CameraRenderEvents;
import com.andreid278.shootit.client.event.InputEvents;
import com.andreid278.shootit.client.event.PlayerRenderEvents;
import com.andreid278.shootit.client.gui.PrinterGui;
import com.andreid278.shootit.client.renderer.RendererPainting;
import com.andreid278.shootit.common.CommonProxy;
import com.andreid278.shootit.common.MCData;
import com.andreid278.shootit.common.PhotosData;
import com.andreid278.shootit.common.entity.EntityPainting;
import com.andreid278.shootit.common.item.Camera;
import com.andreid278.shootit.common.network.MessageCameraToClient;
import com.andreid278.shootit.common.network.MessageDeletePhotoToClients;
import com.andreid278.shootit.common.network.MessagePainterToClient;
import com.andreid278.shootit.common.network.MessagePlayerLoggedIn;
import com.andreid278.shootit.common.network.MessagePrinterToClient;
import com.andreid278.shootit.common.tiileentity.TEPainter;
import com.andreid278.shootit.common.tiileentity.TEPrinter;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		OBJLoader.INSTANCE.addDomain(ShootIt.modId);
		super.preInit(event);

		MinecraftForge.EVENT_BUS.register(new CameraRenderEvents());
		MinecraftForge.EVENT_BUS.register(new InputEvents());
		MinecraftForge.EVENT_BUS.register(new PlayerRenderEvents());

		Class mc = Minecraft.getMinecraft().getClass();
		Field drp = null;
		try {
			drp = mc.getDeclaredField("defaultResourcePacks");
		}
		catch (NoSuchFieldException e) {
			try {
				drp = mc.getDeclaredField("field_110449_ao");
			} catch (NoSuchFieldException | SecurityException e1) {
				e1.printStackTrace();
			}
		}
		if(drp != null) {
			drp.setAccessible(true);
			List<IResourcePack> defaultResourcePacks;
			try {
				defaultResourcePacks = (List<IResourcePack>) drp.get(Minecraft.getMinecraft());
				MCData.photosFolderPathClient = Minecraft.getMinecraft().mcDataDir.getCanonicalPath();
				File file = new File(MCData.photosFolderPathClient + "/photos");
				if(!file.isDirectory())
					file.mkdirs();
				File file1 = new File(MCData.photosFolderPathClient + "/photos/assets/photos");
				if(!file1.isDirectory())
					file1.mkdirs();
				CustomFolderResourcePack frp = new CustomFolderResourcePack(file);
				defaultResourcePacks.add(frp);
				drp.set(Minecraft.getMinecraft(), defaultResourcePacks);
				Minecraft.getMinecraft().refreshResources();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);

		RenderManager rm = Minecraft.getMinecraft().getRenderManager();
		rm.entityRenderMap.put(EntityPainting.class, new RendererPainting(rm));

		try {
			File file = new File(Minecraft.getMinecraft().mcDataDir.getCanonicalPath() + "/photos/assets/photos/Multiplayer/");
			if(file.isDirectory()) {
				for(File file1 : file.listFiles())
					for(File f : file1.listFiles(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String name) {
							return name.matches("^\\d+\\.(png)$");
						}
					}))
						f.delete();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
		
		MCData.shaders.add(MCData.instance.new ShaderInfo("No filter"));
		MCData.shaders.add(MCData.instance.new ShaderInfo("Gray", new ResourceLocation("minecraft", "shaders/post/shootit_gray.json")));
		MCData.shaders.add(MCData.instance.new ShaderInfo("Red", new ResourceLocation("minecraft", "shaders/post/shootit_red.json")));
		MCData.shaders.add(MCData.instance.new ShaderInfo("Green", new ResourceLocation("minecraft", "shaders/post/shootit_green.json")));
		MCData.shaders.add(MCData.instance.new ShaderInfo("Blue", new ResourceLocation("minecraft", "shaders/post/shootit_blue.json")));
		MCData.shaders.add(MCData.instance.new ShaderInfo("Low resolution", new ResourceLocation("minecraft", "shaders/post/shootit_resolution.json")));
		MCData.shaders.add(MCData.instance.new ShaderInfo("Blur", new ResourceLocation("minecraft", "shaders/post/shootit_blur.json")));
		MCData.shaders.add(MCData.instance.new ShaderInfo("Bokeh effect", new ResourceLocation("minecraft", "shaders/post/shootit_bokeh.json")));
		//MCData.shaders.add(MCData.instance.new ShaderInfo("DOF", new ResourceLocation("minecraft", "shaders/post/shootit_dof.json")));
	}

	@Override
	public IMessage onMessage(MessagePlayerLoggedIn message, MessageContext ctx) {
		MCData.cameraHoldPhase.clear();

		Class tm = Minecraft.getMinecraft().getTextureManager().getClass();
		Field field = null;
		try {
			field = tm.getDeclaredField("mapTextureObjects");
		}
		catch (NoSuchFieldException e) {
			try {
				field = tm.getDeclaredField("field_110585_a");
			} catch (NoSuchFieldException | SecurityException e1) {
				e1.printStackTrace();
			}
		}
		if(field != null) {
			try {
				field.setAccessible(true);
				Map<ResourceLocation, ITextureObject> mapTextureObjects = (Map<ResourceLocation, ITextureObject>) field.get(Minecraft.getMinecraft().getTextureManager());
				for(Map.Entry<Integer, ResourceLocation> photo : PhotosData.photos.entrySet()) {
					if(mapTextureObjects.containsKey(photo.getValue())) {
						mapTextureObjects.remove(photo.getValue());
					}
				}
				field.set(Minecraft.getMinecraft().getTextureManager(), mapTextureObjects);
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		PhotosData.photos.clear();

		if(!Minecraft.getMinecraft().isSingleplayer()) {
			try {
				String s = Minecraft.getMinecraft().getCurrentServerData().serverIP;
				if(s.indexOf(':') > 0)
					s = s.replace(':', '-');
				MCData.photosFolderPathClient = Minecraft.getMinecraft().mcDataDir.getCanonicalPath() + "/photos/assets/photos/Multiplayer/" + s;
				MCData.resourceLocationPath = "Multiplayer/" + s;
			} catch (Exception e) {
				e.printStackTrace();
			}
			File file = new File(MCData.photosFolderPathClient);
			if(!file.isDirectory())
				file.mkdirs();

			for(String f : file.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.matches("^\\d+\\.(png)$");
				}
			}))
				PhotosData.addPhoto(f);
			MCData.imageIDToLoadFromServer = 0;
			MCData.imageIDToLoadToServer = 0;
			MCData.imageToLoadToServer = null;
			MCData.imageToLoadFromServer = null;
			MCData.partLengthSum = 0;
			MCData.startIndex = 0;
		}
		else {
			MCData.imageIDToLoadToServer = 0;
		}

		MCData.fov = MCData.cameraFov = Minecraft.getMinecraft().gameSettings.fovSetting;

		return null;
	}

	public IMessage onMessage(MessageCameraToClient message, MessageContext ctx) {
		ItemStack item = Minecraft.getMinecraft().player.getHeldItemMainhand();
		if(item.getItem() instanceof Camera)
			if(item.hasTagCompound()) {
				NBTTagCompound nbt = item.getTagCompound();
				switch(message.messageID) {
				case 0:
					nbt.setInteger("curPhoto", message.curPhoto);
					item.setTagCompound(nbt);
					break;
				case 1:
					nbt.setIntArray("indexes", ArrayUtils.removeElement(nbt.getIntArray("indexes"), message.curPhoto));
					nbt.setInteger("curPhoto", 0);
					item.setTagCompound(nbt);
					break;
				case 2:
					int shader = message.curPhoto;
					nbt.setInteger("shader", shader);
					item.setTagCompound(nbt);
//					if(shader > 0)
//						Minecraft.getMinecraft().entityRenderer.loadShader(MCData.shaders.get(shader).rl);
//					else Minecraft.getMinecraft().entityRenderer.stopUseShader();
//					MCData.lastShader = shader;
					break;
				}
			}
		return null;
	}

	public IMessage onMessage(MessagePrinterToClient message, MessageContext ctx) {
		World world = Minecraft.getMinecraft().world;
		TileEntity te = world.getTileEntity(message.pos);
		if(te != null)
			if(te instanceof TEPrinter) {
				if(message.id < 3)
					((TEPrinter) te).setField(message.id, message.value);
				else if(message.id == 3)
					((TEPrinter)te).checkboxCustom = message.checkbox;
				else if(message.id == 5 || message.id == 6) {
					if(Minecraft.getMinecraft().currentScreen instanceof PrinterGui) {
						PrinterGui gui = (PrinterGui) Minecraft.getMinecraft().currentScreen;
						if(message.id == 5) {
							gui.framesRL.clear();
							gui.curFrames = 0;
						}
						else {
							gui.backRL.clear();
							gui.curBack = 0;
						}
						Block block = Block.getBlockFromItem(message.itemStack.getItem());
						BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
						for(int i = 0; i < 6; i++) {
							IBlockState state = block.getStateFromMeta(message.itemStack.getMetadata());
							List<BakedQuad> quads = blockrendererdispatcher.getModelForState(state).getQuads(state, EnumFacing.getFront(i), 1);
							for(BakedQuad quad : quads) {
								ResourceLocation trl = new ResourceLocation(quad.getSprite().getIconName());
								trl = new ResourceLocation(trl.getResourceDomain(), String.format("%s/%s%s", new Object[] {"textures", trl.getResourcePath(), ".png"}));
								if(message.id == 5) {
									if(!gui.framesRL.contains(trl))
										gui.framesRL.add(trl);
								}
								else {
									if(!gui.backRL.contains(trl))
										gui.backRL.add(trl);
								}
							}
						}
					}
				}
				else if(message.id == 7) {
					if(Minecraft.getMinecraft().currentScreen instanceof PrinterGui) {
						PrinterGui gui = (PrinterGui) Minecraft.getMinecraft().currentScreen;
						gui.framesRL.clear();
						gui.curFrames = 0;
					}
				}
				else if(message.id == 8) {
					if(Minecraft.getMinecraft().currentScreen instanceof PrinterGui) {
						PrinterGui gui = (PrinterGui) Minecraft.getMinecraft().currentScreen;
						gui.backRL.clear();
						gui.curBack = 0;
					}
				}
			}
		return null;
	}

	public IMessage onMessage(MessagePainterToClient message, MessageContext ctx) {
		World world = Minecraft.getMinecraft().world;
		TileEntity te = world.getTileEntity(message.pos);
		if(te != null)
			if(te instanceof TEPainter) {
				if(message.id == 0)
					((TEPainter) te).curPhoto = message.value;
			}
		return null;
	}
	
	public IMessage onMessage(MessageDeletePhotoToClients message, MessageContext ctx) {
		if(PhotosData.photos.containsKey(message.photoID)) {
			Class tm = Minecraft.getMinecraft().getTextureManager().getClass();
			Field field = null;
			try {
				field = tm.getDeclaredField("mapTextureObjects");
			}
			catch (NoSuchFieldException e) {
				try {
					field = tm.getDeclaredField("field_110585_a");
				} catch (NoSuchFieldException | SecurityException e1) {
					e1.printStackTrace();
				}
			}
			if(field != null) {
				try {
					field.setAccessible(true);
					Map<ResourceLocation, ITextureObject> mapTextureObjects = (Map<ResourceLocation, ITextureObject>) field.get(Minecraft.getMinecraft().getTextureManager());
					if(mapTextureObjects.containsKey(PhotosData.photos.get(message.photoID))) {
						mapTextureObjects.remove(PhotosData.photos.get(message.photoID));
						field.set(Minecraft.getMinecraft().getTextureManager(), mapTextureObjects);
					}
				} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			PhotosData.photos.remove(message.photoID);
		}
		return null;
	}
}