package com.andreid278.shootit;

import com.andreid278.shootit.Misc.ModCommands;
import com.andreid278.shootit.Network.MessageCameraToClient;
import com.andreid278.shootit.Network.MessageCameraToServer;
import com.andreid278.shootit.Network.MessageDeletePhotoRequest;
import com.andreid278.shootit.Network.MessageDeletePhotoToClients;
import com.andreid278.shootit.Network.MessagePlayerLoggedIn;
import com.andreid278.shootit.Network.MessagePrinterToServer;
import com.andreid278.shootit.Network.MessageReplyForNextPhotoID;
import com.andreid278.shootit.Network.MessageRequestForNextPhotoID;
import com.andreid278.shootit.Network.MessageRequestForPhoto;
import com.andreid278.shootit.Network.MessageRequestNoPhoto;
import com.andreid278.shootit.Network.MessageSpawnEntityOnClient;
import com.andreid278.shootit.Network.MessagePrinterToClient;
import com.andreid278.shootit.Network.PhotoLoaderToClient;
import com.andreid278.shootit.Network.PhotoLoaderToServer;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = Main.MODID, version = Main.VERSION)
public class Main {
	public final static String MODID = "shootit";
	public final static String VERSION = "v0.3";

	@Instance(value = MODID)
	public static Main instance;

	@SidedProxy(clientSide="com.andreid278.shootit.ClientProxy", serverSide="com.andreid278.shootit.CommonProxy")
	public static CommonProxy proxy;

	public static SimpleNetworkWrapper network;

	public static int photoID = 0;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
		network = NetworkRegistry.INSTANCE.newSimpleChannel("shootit");
		network.registerMessage(MessageSpawnEntityOnClient.Handler.class, MessageSpawnEntityOnClient.class, 0, Side.CLIENT);
		network.registerMessage(MessageRequestForPhoto.Handler.class, MessageRequestForPhoto.class, 1, Side.SERVER);
		network.registerMessage(MessageRequestForNextPhotoID.Handler.class, MessageRequestForNextPhotoID.class, 2, Side.SERVER);
		network.registerMessage(MessageReplyForNextPhotoID.Handler.class, MessageReplyForNextPhotoID.class, 3, Side.CLIENT);
		network.registerMessage(MessagePrinterToServer.Handler.class, MessagePrinterToServer.class, 4, Side.SERVER);
		network.registerMessage(MessagePrinterToClient.Handler.class, MessagePrinterToClient.class, 5, Side.CLIENT);
		network.registerMessage(PhotoLoaderToServer.Handler.class, PhotoLoaderToServer.class, 6, Side.SERVER);
		network.registerMessage(MessagePlayerLoggedIn.Handler.class, MessagePlayerLoggedIn.class, 7, Side.CLIENT);
		network.registerMessage(PhotoLoaderToClient.Handler.class, PhotoLoaderToClient.class, 8, Side.CLIENT);
		network.registerMessage(MessageRequestNoPhoto.Handler.class, MessageRequestNoPhoto.class, 9, Side.CLIENT);
		network.registerMessage(MessageCameraToServer.Handler.class, MessageCameraToServer.class, 10, Side.SERVER);
		network.registerMessage(MessageCameraToClient.Handler.class, MessageCameraToClient.class, 11, Side.CLIENT);
		network.registerMessage(MessageDeletePhotoRequest.Handler.class, MessageDeletePhotoRequest.class, 12, Side.SERVER);
		network.registerMessage(MessageDeletePhotoToClients.Handler.class, MessageDeletePhotoToClients.class, 13, Side.CLIENT);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}
	
	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		event.registerServerCommand(new ModCommands());
	}
}
