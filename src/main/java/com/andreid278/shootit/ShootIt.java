package com.andreid278.shootit;

import com.andreid278.shootit.common.CommonProxy;
import com.andreid278.shootit.common.network.MessageCameraToClient;
import com.andreid278.shootit.common.network.MessageCameraToServer;
import com.andreid278.shootit.common.network.MessageDeletePhotoRequest;
import com.andreid278.shootit.common.network.MessageDeletePhotoToClients;
import com.andreid278.shootit.common.network.MessagePainterToClient;
import com.andreid278.shootit.common.network.MessagePainterToServer;
import com.andreid278.shootit.common.network.MessagePlayerLoggedIn;
import com.andreid278.shootit.common.network.MessagePrinterToClient;
import com.andreid278.shootit.common.network.MessagePrinterToServer;
import com.andreid278.shootit.common.network.MessageReplyForNextPhotoID;
import com.andreid278.shootit.common.network.MessageRequestForNextPhotoID;
import com.andreid278.shootit.common.network.MessageRequestForPhoto;
import com.andreid278.shootit.common.network.MessageRequestNoPhoto;
import com.andreid278.shootit.common.network.PhotoLoaderToClient;
import com.andreid278.shootit.common.network.PhotoLoaderToServer;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.SidedProxy;

@Mod(modid = ShootIt.modId, name = ShootIt.modName, version = ShootIt.modVersion)
public class ShootIt {
	
	public static final String modId = "shootit";
	public static final String modName = "ShootIt";
	public static final String modVersion = "0.1";
	
	@Instance("shootit")
	public static ShootIt instance;
	
	public static SimpleNetworkWrapper network;
	
	@SidedProxy(clientSide = "com.andreid278.shootit.client.ClientProxy", serverSide = "com.andreid278.shootit.common.CommonProxy")
	public static CommonProxy proxy;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		network = NetworkRegistry.INSTANCE.newSimpleChannel("shootit");
		network.registerMessage(MessagePlayerLoggedIn.Handler.class, MessagePlayerLoggedIn.class, 0, Side.CLIENT);
		network.registerMessage(MessageRequestForNextPhotoID.Handler.class, MessageRequestForNextPhotoID.class, 1, Side.SERVER);
		network.registerMessage(MessageReplyForNextPhotoID.Handler.class, MessageReplyForNextPhotoID.class, 2, Side.CLIENT);
		network.registerMessage(PhotoLoaderToServer.Handler.class, PhotoLoaderToServer.class, 3, Side.SERVER);
		network.registerMessage(MessageCameraToServer.Handler.class, MessageCameraToServer.class, 4, Side.SERVER);
		network.registerMessage(MessageCameraToClient.Handler.class, MessageCameraToClient.class, 5, Side.CLIENT);
		network.registerMessage(MessageRequestForPhoto.Handler.class, MessageRequestForPhoto.class, 6, Side.SERVER);
		network.registerMessage(PhotoLoaderToClient.Handler.class, PhotoLoaderToClient.class, 7, Side.CLIENT);
		network.registerMessage(MessageRequestNoPhoto.Handler.class, MessageRequestNoPhoto.class, 8, Side.CLIENT);
		network.registerMessage(MessagePrinterToClient.Handler.class, MessagePrinterToClient.class, 9, Side.CLIENT);
		network.registerMessage(MessagePrinterToServer.Handler.class, MessagePrinterToServer.class, 10, Side.SERVER);
		network.registerMessage(MessagePainterToClient.Handler.class, MessagePainterToClient.class, 11, Side.CLIENT);
		network.registerMessage(MessagePainterToServer.Handler.class, MessagePainterToServer.class, 12, Side.SERVER);
		network.registerMessage(MessageDeletePhotoRequest.Handler.class, MessageDeletePhotoRequest.class, 13, Side.SERVER);
		network.registerMessage(MessageDeletePhotoToClients.Handler.class, MessageDeletePhotoToClients.class, 14, Side.CLIENT);
		proxy.preInit(event);
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}
}