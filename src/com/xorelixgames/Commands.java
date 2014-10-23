package com.xorelixgames;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
	private OreGen _og;

	public Commands(OreGen og) {
		_og = og;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (commandLabel.equalsIgnoreCase("og")) {
			if (args.length == 0) {
                                sender.sendMessage(ChatColor.GREEN + "===== OreGenerator Commands ===== \n" + "/og reload   Reloads the config file.\n" + "/og chances  lists out possable blocks and their respecitve chances of occuring");
				return true;
			} else {
				if (args[0].equalsIgnoreCase("reload")) {
					Player player = null;
					if (sender instanceof Player) {
						player = (Player) sender;
						if (player.hasPermission("og.reload")) {
                                                	_og.reload();
							sender.sendMessage(ChatColor.GREEN + "OreGenerator config file reloaded.");
						} else {
							sender.sendMessage(ChatColor.RED + "You do not have permission to use this command");
						}
					} else {
						_og.reload();
						_og.log.info("OreGenerator config file reloaded.");
					}
				} else if (args[0].equalsIgnoreCase("chances")) {
					Player player = null;
					if (sender instanceof Player) {
						player = (Player) sender;
						if (player.hasPermission("og.chances")) {
                                                        _og.printchances(player);
						} else {
							sender.sendMessage(ChatColor.RED + "You do not have permission to use this command");
						}
					} else {
                                                _og.printchances(sender);
					}
                                }
			}
		}
		return true;
	}
}