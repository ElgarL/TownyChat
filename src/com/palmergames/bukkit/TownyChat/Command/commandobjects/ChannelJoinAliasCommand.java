package com.palmergames.bukkit.TownyChat.Command.commandobjects;

import com.palmergames.bukkit.TownyChat.Chat;
import com.palmergames.bukkit.TownyChat.channels.Channel;
import com.palmergames.bukkit.TownyChat.channels.channelTypes;
import com.palmergames.bukkit.TownyChat.util.TownyUtil;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.util.StringMgmt;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class ChannelJoinAliasCommand extends BukkitCommand {
	public Channel channel;
	public Chat plugin;

	public ChannelJoinAliasCommand(String name, Channel channel, Chat plugin) {
		super(name);
		this.channel = channel;
		this.plugin = plugin;
		this.description = name + " - Channel command join alias for " + channel.getName();
		this.usageMessage = "/" + name;
	}

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (commandSender instanceof Player) { // So a player has ran some /g command or something
			if (channel.getCommands().contains(this.getName())) { // Should in theory always be true.
				final Player player = (Player) commandSender;
				final Channel defaultChannel = plugin.getChannelsHandler().getDefaultChannel();
				String message = "";
				if (args.length > 0) {
					message = StringMgmt.join(args, " ");
				}
				if (message.isEmpty()) {
					if (plugin.getTowny().hasPlayerMode(player, channel.getName())) {
						// Find what the next channel is if any
						Channel nextChannel = null;
						if (defaultChannel != null && defaultChannel.isPresent(player.getName())) {
							nextChannel = defaultChannel;
                            if (!nextChannel.getName().equalsIgnoreCase(channel.getName())) {
                                TownyUtil.removeAndSetPlayerMode(plugin.getTowny(), player, channel.getName(), nextChannel.getName(), true);
                            } else {
                                // They're talking on default channel and want to leave but can't because they'll be put default again
                                // Their only option out is to leave the channel if they have permission to do so.
                                TownyMessaging.sendErrorMsg(player, TownySettings.getLangString("tc_err_you_are_already_talking_in_default_channel_help"));
                                return true;
                            }
						} else {
							TownyUtil.removePlayerMode(plugin.getTowny(), player, channel.getName(), false);
						}

						if (nextChannel == null) {
							nextChannel = plugin.getChannelsHandler().getActiveChannel(player, channelTypes.GLOBAL);
						}

						// If the new channel is not us, announce it
						if (nextChannel != null && !channel.getName().equalsIgnoreCase(nextChannel.getName())) {
							TownyMessaging.sendMsg(player, String.format(TownySettings.getLangString("tc_you_are_now_talking_in_channel"), nextChannel.getName()));
							return true;
						}
						return true;
					} else {
						// You can join a channel if:
						// - Towny doesn't recognize your permissions plugin
						// - channel has no permission set [by default they don't] OR
						//   - channel has permission set AND:
						//     - player has channel permission
						String joinPerm = channel.getPermission();
						if ((joinPerm != null && !TownyUniverse.getPermissionSource().has(player, joinPerm))) {
							TownyMessaging.sendErrorMsg(player, String.format(TownySettings.getLangString("tc_err_you_cannot_join_channel"), channel.getName()));
							return true;
						}
						
						plugin.getTowny().setPlayerMode(player, new String[]{channel.getName()}, true);
						TownyMessaging.sendMsg(player, String.format(TownySettings.getLangString("tc_you_are_now_talking_in_channel"), channel.getName()));
						return true;
					}
				} else {
					if (channel.isMuted(player.getName())) {
						TownyMessaging.sendErrorMsg(player, String.format(TownySettings.getLangString("tc_err_you_are_currently_muted_in_channel"), channel.getName()));
						return true;
					}
					// You can speak in a channel if:
					// - Towny doesn't recognize your permissions plugin
					// - channel has no permission set [by default they don't] OR
					//   - channel has permission set AND:
					//     - player has channel permission
					String joinPerm = channel.getPermission();
					if ((joinPerm != null && !TownyUniverse.getPermissionSource().has(player, joinPerm))) {
						TownyMessaging.sendErrorMsg(player, String.format(TownySettings.getLangString("tc_err_you_cannot_join_channel"), channel.getName()));
						return true;
					}

					plugin.getTownyPlayerListener().directedChat.put(player, this.getName());

					final String msg = message;

					// https://www.spigotmc.org/threads/plugins-triggering-commands-async.31815/
					if (!Bukkit.isPrimaryThread()) {
						Bukkit.getScheduler().runTask(plugin, () -> player.chat(msg));
					} else {
						player.chat(msg);
					}
					return true;
				}
			}
			return true;
		} else {
			commandSender.sendMessage("You may not use this command as the console!");
			return false;
		}
	}
}
