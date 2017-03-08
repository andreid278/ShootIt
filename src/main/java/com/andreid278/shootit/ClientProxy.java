package com.andreid278.shootit;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import com.andreid278.shootit.Entity.EntityPainting;
import com.andreid278.shootit.Events.MouseEvents;
import com.andreid278.shootit.Events.PlayerRenderEvents;
import com.andreid278.shootit.Gui.PrinterGui;
import com.andreid278.shootit.Items.Camera;
import com.andreid278.shootit.Misc.CustomFolderResourcePack;
import com.andreid278.shootit.Misc.Photos;
import com.andreid278.shootit.Misc.Statics;
import com.andreid278.shootit.Network.MessageCameraToClient;
import com.andreid278.shootit.Network.MessageDeletePhotoToClients;
import com.andreid278.shootit.Network.MessagePlayerLoggedIn;
import com.andreid278.shootit.Network.MessagePrinterToClient;
import com.andreid278.shootit.Renderer.CameraRenderEvents;
import com.andreid278.shootit.Renderer.RendererPainting;
import com.andreid278.shootit.TileEntities.TEPrinter;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class ClientProxy extends CommonProxy {
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		registerItemsModels();

		MinecraftForge.EVENT_BUS.register(new CameraRenderEvents());
		MinecraftForge.EVENT_BUS.register(new MouseEvents());
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
				Statics.photosFolderPathClient = Minecraft.getMinecraft().mcDataDir.getCanonicalPath();
				File file = new File(Statics.photosFolderPathClient + Statics.slash + "photos");
				if(!file.isDirectory())
					file.mkdirs();
				File file1 = new File(Statics.photosFolderPathClient + Statics.slash + "photos" + Statics.slash + "assets" + Statics.slash + "photos");
				if(!file1.isDirectory())
					file1.mkdirs();
				CustomFolderResourcePack frp = new CustomFolderResourcePack(file);
				defaultResourcePacks.add(frp);
				drp.set(Minecraft.getMinecraft(), defaultResourcePacks);
			} catch (IllegalArgumentException | IllegalAccessException | IOException e) {
				e.printStackTrace();
			}
		}
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		super.init(event);
		RenderManager rm = Minecraft.getMinecraft().getRenderManager();
		rm.entityRenderMap.put(EntityPainting.class, new RendererPainting(rm));

		try {
			File file = new File(Minecraft.getMinecraft().mcDataDir.getCanonicalPath() + Statics.slash + "photos" + Statics.slash + "assets" + Statics.slash + "photos" + Statics.slash + "Multiplayer" + Statics.slash);
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

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}

	public void registerItemsModels() {
		OBJLoader.INSTANCE.addDomain(Main.MODID);
		ModelLoader.setCustomModelResourceLocation(camera, 0, new ModelResourceLocation(Main.MODID + ":camera", "inventory"));
		ModelLoader.setCustomModelResourceLocation(memoryCard, 0, new ModelResourceLocation(Main.MODID + ":memorycard", "inventory"));
		ModelLoader.setCustomModelResourceLocation(photoItem, 0, new ModelResourceLocation(Main.MODID + ":photoitem", "inventory"));
		Item itemPrinter = Item.getItemFromBlock(printer);
		ModelLoader.setCustomModelResourceLocation(itemPrinter, 0, new ModelResourceLocation(Main.MODID + ":printer", "inventory"));
	}

	public IMessage onMessage(MessagePrinterToClient message, MessageContext ctx) {
		World world = Minecraft.getMinecraft().theWorld;
		TileEntity te = world.getTileEntity(message.pos);
		if(te != null)
			if(te instanceof TEPrinter) {
				if(message.id < 3)
					((TEPrinter) te).setField(message.id, message.value);
				else if(message.id == 3)
					((TEPrinter)te).checkboxFrames = message.checkbox;
				else if(message.id == 4)
					((TEPrinter)te).checkboxBack = message.checkbox;
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

	public IMessage onMessage(MessagePlayerLoggedIn message, MessageContext ctx) {
		Statics.isShooting = false;
		if(!Minecraft.getMinecraft().isSingleplayer()) {
			try {
				String s = Minecraft.getMinecraft().getCurrentServerData().serverIP;
				if(s.indexOf(':') > 0)
					s = s.replace(':', '-');
				Statics.photosFolderPathClient = Minecraft.getMinecraft().mcDataDir.getCanonicalPath() + Statics.slash + "photos" + Statics.slash + "assets" + Statics.slash + "photos" + Statics.slash + "Multiplayer" + Statics.slash + s;
				Statics.resourceLocationPath = "Multiplayer/" + s;
			} catch (IOException e) {
				e.printStackTrace();
			}
			File file = new File(Statics.photosFolderPathClient);
			if(!file.isDirectory())
				file.mkdirs();
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
					for(Map.Entry<Integer, ResourceLocation> photo : Photos.photos.entrySet()) {
						if(mapTextureObjects.containsKey(photo.getValue())) {
							mapTextureObjects.remove(photo.getValue());
						}
					}
					field.set(Minecraft.getMinecraft().getTextureManager(), mapTextureObjects);
				} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			Photos.photos.clear();
			for(String f : file.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.matches("^\\d+\\.(png)$");
				}
			}))
				Photos.addPhoto(f);
			Statics.imageIDToLoadFromServer = 0;
			Statics.imageIDToLoadToServer = 0;
			Statics.imageToLoadToServer = null;
			Statics.imageToLoadFromServer = null;
			Statics.partLengthSum = 0;
			Statics.startIndex = 0;
		}
		else {
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
				field.setAccessible(true);
				Map<ResourceLocation, ITextureObject> mapTextureObjects;
				try {
					mapTextureObjects = (Map<ResourceLocation, ITextureObject>) field.get(Minecraft.getMinecraft().getTextureManager());
					for(Map.Entry<Integer, ResourceLocation> photo : Photos.photos.entrySet()) {
						if(mapTextureObjects.containsKey(photo.getValue())) {
							mapTextureObjects.remove(photo.getValue());
						}
					}
					field.set(Minecraft.getMinecraft().getTextureManager(), mapTextureObjects);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			Photos.photos.clear();
			Statics.imageIDToLoadToServer = 0;
		}
		Statics.fov = Statics.cameraFov = Minecraft.getMinecraft().gameSettings.fovSetting;
		return null;
	}

	public IMessage onMessage(MessageCameraToClient message, MessageContext ctx) {
		ItemStack item = Minecraft.getMinecraft().thePlayer.getHeldItemMainhand();
		if(item != null)
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
					}
				}
		return null;
	}

	public IMessage onMessage(MessageDeletePhotoToClients message, MessageContext ctx) {
		if(Photos.photos.containsKey(message.photoID)) {
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
					if(mapTextureObjects.containsKey(Photos.photos.get(message.photoID))) {
						mapTextureObjects.remove(Photos.photos.get(message.photoID));
						field.set(Minecraft.getMinecraft().getTextureManager(), mapTextureObjects);
					}
				} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Photos.photos.remove(message.photoID);
		}
		return null;
	}
}
