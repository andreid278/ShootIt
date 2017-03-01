package com.andreid278.shootit.Misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.andreid278.shootit.Main;
import com.andreid278.shootit.WorldData.WorldData;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

public class ModCommands extends CommandBase {
	@Override
	public String getCommandName() {
		return Main.MODID;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return ":)";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(args.length == 1)
			if(args[0].equalsIgnoreCase("togglechecking")) {
				WorldData.getForWorld(server.getEntityWorld()).toggleChecking();
				if(sender instanceof EntityPlayer)
					sender.addChatMessage(new TextComponentString("Parameter was changed to " + WorldData.getForWorld(server.getEntityWorld()).data.getBoolean("needChecking")));
			}
	}

	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
		if(args.length == 1) {
			List<String> list = new ArrayList<String>();
			list.add("togglechecking");
			return getListOfStringsMatchingLastWord(args, list);
		}
		return Collections.<String>emptyList();
	}
}
