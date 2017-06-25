package com.andreid278.shootit;

import com.andreid278.shootit.Blocks.BlockPainter;
import com.andreid278.shootit.Blocks.BlockPrinter;
import com.andreid278.shootit.Entity.EntityPainting;
import com.andreid278.shootit.Events.PlayerEvents;
import com.andreid278.shootit.Gui.GuiHandler;
import com.andreid278.shootit.Items.Camera;
import com.andreid278.shootit.Items.MemoryCard;
import com.andreid278.shootit.Items.PhotoItem;
import com.andreid278.shootit.Misc.Statics;
import com.andreid278.shootit.Network.MessageCameraToClient;
import com.andreid278.shootit.Network.MessageDeletePhotoToClients;
import com.andreid278.shootit.Network.MessagePainterToClient;
import com.andreid278.shootit.Network.MessagePlayerLoggedIn;
import com.andreid278.shootit.Network.MessagePrinterToClient;
import com.andreid278.shootit.TileEntities.TEPainter;
import com.andreid278.shootit.TileEntities.TEPrinter;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class CommonProxy {
	public static final CreativeTabs modTab = (new CreativeTabs("shootit") {
		@Override
		public Item getTabIconItem() {
			return photoItem;
		}
	});

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		String os = System.getProperty("os.name").toLowerCase();
		if(os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("mac") >= 0)
			Statics.slash = "/";
		registerItems();
		int id = 0;
		EntityRegistry.registerModEntity(EntityPainting.class, "photo", id++, Main.instance, 160, Integer.MAX_VALUE, false);
		//		MinecraftForge.EVENT_BUS.register(new WorldEvents());
		MinecraftForge.EVENT_BUS.register(new PlayerEvents());
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		GameRegistry.registerTileEntity(TEPrinter.class, "teprinter");
		GameRegistry.registerTileEntity(TEPainter.class, "tepainter");
		NetworkRegistry.INSTANCE.registerGuiHandler(Main.instance, new GuiHandler());
		registerRecipes();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}

	public static Camera camera;
	public static MemoryCard memoryCard;
	public static PhotoItem photoItem;
	public static BlockPrinter printer;
	public static BlockPainter painter;
	private void registerItems() {
		camera = new Camera();
		GameRegistry.register(camera);
		memoryCard = new MemoryCard();
		GameRegistry.register(memoryCard);
		photoItem = new PhotoItem();
		GameRegistry.register(photoItem);
		printer = new BlockPrinter(Material.IRON);
		GameRegistry.register(printer);
		GameRegistry.register(new ItemBlock(printer).setRegistryName(printer.getRegistryName()));
		painter = new BlockPainter(Material.IRON);
		GameRegistry.register(painter);
		GameRegistry.register(new ItemBlock(painter).setRegistryName(painter.getRegistryName()));
	}

	private void registerRecipes() {
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(camera), "A B", "CDC", "CCC", 'A', Items.REDSTONE, 'B', Blocks.STONE_BUTTON, 'C', Items.IRON_INGOT, 'D', "blockGlass"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(memoryCard), "AB ", "BCB", "BBB", 'A', Items.REDSTONE, 'B', Items.IRON_INGOT, 'C', "gemDiamond"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(printer), "ABA", "ACD", "AAA", 'A', Items.IRON_INGOT, 'B', "blockGlass", 'C', Items.REDSTONE, 'D', "dye"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(painter), "BBB", "ACA", "A A", 'A', Items.IRON_INGOT, 'B', "blockGlass", 'C', photoItem));
	}

	public IMessage onMessage(MessagePrinterToClient message, MessageContext ctx) {
		return null;
	}
	
	public IMessage onMessage(MessagePainterToClient message, MessageContext ctx) {
		return null;
	}
	
	public IMessage onMessage(MessagePlayerLoggedIn message, MessageContext ctx) {
		return null;
	}

	public IMessage onMessage(MessageCameraToClient message, MessageContext ctx) {
		return null;
	}

	public IMessage onMessage(MessageDeletePhotoToClients message, MessageContext ctx) {
		return null;
	}
}
