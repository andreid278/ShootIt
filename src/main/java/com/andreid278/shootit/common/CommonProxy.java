package com.andreid278.shootit.common;

import com.andreid278.shootit.ShootIt;
import com.andreid278.shootit.client.gui.GuiHandler;
import com.andreid278.shootit.common.block.BlockPainter;
import com.andreid278.shootit.common.block.BlockPrinter;
import com.andreid278.shootit.common.entity.EntityPainting;
import com.andreid278.shootit.common.event.PlayerEvents;
import com.andreid278.shootit.common.item.Camera;
import com.andreid278.shootit.common.item.MemoryCard;
import com.andreid278.shootit.common.item.PhotoItem;
import com.andreid278.shootit.common.network.MessageCameraToClient;
import com.andreid278.shootit.common.network.MessageDeletePhotoToClients;
import com.andreid278.shootit.common.network.MessagePainterToClient;
import com.andreid278.shootit.common.network.MessagePlayerLoggedIn;
import com.andreid278.shootit.common.network.MessagePrinterToClient;
import com.andreid278.shootit.common.tiileentity.TEPainter;
import com.andreid278.shootit.common.tiileentity.TEPrinter;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.RegistryManager;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		photoItem = new PhotoItem();
		memoryCardItem = new MemoryCard();
		cameraItem = new Camera();
		printerBlock = new BlockPrinter(Material.IRON);
		itemPrinter = new ItemBlock(printerBlock).setRegistryName(printerBlock.getRegistryName());
		painterBlock = new BlockPainter(Material.IRON);
		itemPainter = new ItemBlock(painterBlock).setRegistryName(painterBlock.getRegistryName());
		EntityRegistry.registerModEntity(new ResourceLocation(ShootIt.modId, "photo"), EntityPainting.class, "photo", 0, ShootIt.instance, 160, Integer.MAX_VALUE, false);
		MinecraftForge.EVENT_BUS.register(new ItemBlockRegister());
		MinecraftForge.EVENT_BUS.register(new PlayerEvents());
	}

	public void init(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(ShootIt.instance, new GuiHandler());
		GameRegistry.registerTileEntity(TEPrinter.class, "teprinter");
		GameRegistry.registerTileEntity(TEPainter.class, "tepainter");
	}

	public void postInit(FMLPostInitializationEvent event) {

	}

	public static PhotoItem photoItem;
	public static MemoryCard memoryCardItem;
	public static Camera cameraItem;
	
	public static BlockPrinter printerBlock;
	public static Item itemPrinter;
	
	public static BlockPainter painterBlock;
	public static Item itemPainter;

	private class ItemBlockRegister {
		@SubscribeEvent
		public void registerBlocks(RegistryEvent.Register<Block> event) {
			event.getRegistry().registerAll(printerBlock, painterBlock);
		}

		@SubscribeEvent
		public void registerItems(RegistryEvent.Register<Item> event) {
			event.getRegistry().registerAll(photoItem, itemPrinter, memoryCardItem, cameraItem, itemPainter);
		}
		
		@SubscribeEvent
		public void registerModels(ModelRegistryEvent event) {
			ModelLoader.setCustomModelResourceLocation(photoItem, 0, new ModelResourceLocation(photoItem.getRegistryName(), "inventory"));
			ModelLoader.setCustomModelResourceLocation(memoryCardItem, 0, new ModelResourceLocation(memoryCardItem.getRegistryName(), "inventory"));
			ModelLoader.setCustomModelResourceLocation(itemPrinter, 0, new ModelResourceLocation(itemPrinter.getRegistryName(), "inventory"));
			ModelLoader.setCustomModelResourceLocation(cameraItem, 0, new ModelResourceLocation(cameraItem.getRegistryName(), "inventory"));
			ModelLoader.setCustomModelResourceLocation(itemPainter, 0, new ModelResourceLocation(itemPainter.getRegistryName(), "inventory"));
		}
	}
	
	public IMessage onMessage(MessagePlayerLoggedIn message, MessageContext ctx) {
		return null;
	}
	
	public IMessage onMessage(MessageCameraToClient message, MessageContext ctx) {
		return null;
	}
	
	public IMessage onMessage(MessagePrinterToClient message, MessageContext ctx) {
		return null;
	}
	
	public IMessage onMessage(MessagePainterToClient message, MessageContext ctx) {
		return null;
	}
	
	public IMessage onMessage(MessageDeletePhotoToClients message, MessageContext ctx) {
		return null;
	}
}
