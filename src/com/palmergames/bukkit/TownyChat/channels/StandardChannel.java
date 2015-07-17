package com.palmergames.bukkit.TownyChat.channels;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.dynmap.DynmapAPI;

import com.earth2me.essentials.User;
import com.palmergames.bukkit.TownyChat.Chat;
import com.palmergames.bukkit.TownyChat.CraftIRCHandler;
import com.palmergames.bukkit.TownyChat.TownyChatFormatter;
import com.palmergames.bukkit.TownyChat.config.ChatSettings;
import com.palmergames.bukkit.TownyChat.events.AsyncChatHookEvent;
import com.palmergames.bukkit.TownyChat.listener.LocalTownyChatEvent;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.util.Colors;

public class StandardChannel extends Channel {

	private Chat plugin;
	
	public StandardChannel(Chat instance, String name) {
		super(name);
		this.plugin = instance;
	}

	@Override
	public void chatProcess(AsyncPlayerChatEvent event) {
		
		channelTypes exec = channelTypes.valueOf(getType().name());
		
		Player player = event.getPlayer();
		Set<Player> recipients = null;
		Resident resident = null;
		Town town = null;
		Nation nation = null;
		String Format = "";
		
		try {
			resident = TownyUniverse.getDataSource().getResident(player.getName());
			town = resident.getTown();
			nation = resident.getTown().getNation();
		} catch (NotRegisteredException e1) {
			// Not in a town/nation (doesn't matter which)
		}
		
		boolean notifyjoin = false;
		// If player sends a message to a channel it had left
		// tell the channel to add the player back
		if (isAbsent(player.getName())) {
			join(player.getName());
			notifyjoin = true;
		}

		/*
		 *  Retrieve the channel specific format
		 *  and compile a set of recipients
		 */
		switch (exec) {
		
		case TOWN:
			if (town == null) {
				event.setCancelled(true);
				return;
			}
			Format = ChatSettings.getRelevantFormatGroup(player).getTOWN();
			recipients = new HashSet<Player>(findRecipients(player, TownyUniverse.getOnlinePlayers(town)));
			recipients = checkSpying(recipients);
			break;
		
		case NATION:
			if (nation == null) {
				event.setCancelled(true);
				return;
			}
			Format = ChatSettings.getRelevantFormatGroup(player).getNATION();
			recipients = new HashSet<Player>(findRecipients(player, TownyUniverse.getOnlinePlayers(nation)));
			recipients = checkSpying(recipients);
			break;
			
		case DEFAULT:
			Format = ChatSettings.getRelevantFormatGroup(player).getDEFAULT();
			recipients = new HashSet<Player>(findRecipients(player, new ArrayList<Player>(event.getRecipients())));
			break;
			
		case GLOBAL:
			Format = ChatSettings.getRelevantFormatGroup(player).getGLOBAL();
			recipients = new HashSet<Player>(findRecipients(player, new ArrayList<Player>(event.getRecipients())));
			break;
			
		case PRIVATE:
			Format = ChatSettings.getRelevantFormatGroup(player).getGLOBAL();
			recipients = new HashSet<Player>(findRecipients(player, new ArrayList<Player>(event.getRecipients())));
			break;
		}
		
		/*
		 * Perform all replace functions on this format
		 */
		//if (ChatSettings.isModify_chat())
			event.setFormat(Format.replace("{channelTag}", getChannelTag()).replace("{msgcolour}", getMessageColour()));
		//else
		//	event.setFormat(event.getFormat().replace("{channelTag}", getChannelTag()).replace("{msgcolour}", getMessageColour()));
		
		LocalTownyChatEvent chatEvent = new LocalTownyChatEvent(event, resident);
		event.setFormat(TownyChatFormatter.getChatFormat(chatEvent));
		
		/*
		 *  Set all the listeners for Bukkit to send this message to.
		 */
        event.getRecipients().clear();
        event.getRecipients().addAll(recipients);
        
        if (isHooked()) {
        	AsyncChatHookEvent hookEvent = new AsyncChatHookEvent(event, this);
            Bukkit.getServer().getPluginManager().callEvent(hookEvent);
            if (hookEvent.isCancelled()) {
            	event.setCancelled(true);
            	return;
            }
            if (hookEvent.isChanged()) {
            	event.setMessage(hookEvent.getMessage());
            	event.setFormat(hookEvent.getFormat());
                event.getRecipients().clear();
                event.getRecipients().addAll(hookEvent.getRecipients());
            }
        }

        if (notifyjoin) {
			TownyMessaging.sendMsg(player, "You join " + Colors.White + getName());
        }

        /*
         * Perform any last channel specific functions
         * like logging this chat and relaying to IRC/Dynmap.
         */
        String msg = event.getFormat().replace("%1$s", event.getPlayer().getDisplayName()).replace("%2$s", event.getMessage());
        
        switch (exec) {
		
		case TOWN:
			//plugin.getLogger().info(ChatTools.stripColour("[Town Msg] " + town.getName() + ": " + msg));
			break;
		
		case NATION:
			//plugin.getLogger().info(ChatTools.stripColour("[Nation Msg] " + nation.getName() + ": " + msg));
			break;
			
		case DEFAULT:
			break;
			
		case PRIVATE:
		case GLOBAL:
			DynmapAPI dynMap = plugin.getDynmap();
			
			if (dynMap != null)
				dynMap.postPlayerMessageToWeb(player, event.getMessage());
			break;
		}
        
        // Relay to IRC
        CraftIRCHandler ircHander = plugin.getIRC();
        if (ircHander != null)
        	ircHander.IRCSender(msg, getCraftIRCTag());
		
	}

	/**
	 * Check the distance between players and return a result based upon the range setting
	 * -1 = no limit
	 * 0 = same world
	 * any positive value = distance in blocks
	 * 
	 * @param player1
	 * @param player2
	 * @param range
	 * @return true if in range
	 */
	private boolean testDistance(Player player1, Player player2, double range) {
		
		// unlimited range (all worlds)
		if (range == -1)
			return true;
		
		// Same world only
		if (range == 0)
			return player1.getWorld().equals(player2.getWorld());
		
		// Range check (same world)
		if (player1.getWorld().equals(player2.getWorld()))
			return (player1.getLocation().distance(player2.getLocation()) < range);
		else
			return false;
	}
	
	/**
	 * Compile a list of valid recipients for this message.
	 * 
	 * @param sender
	 * @param list
	 * @return Set containing a list of players for this message.
	 */
	private Set<Player> findRecipients(Player sender, List<Player> list) {
		
		Set<Player> recipients = new HashSet<Player>();
		Boolean bEssentials = plugin.getTowny().isEssentials();
		String sendersName = sender.getName();
		
		// Compile the list of recipients
        for (Player test : list) {
        	
        	/*
        	 * If Not using permissions, or the player has the correct permission node.
        	 */
        	if (!plugin.getTowny().isPermissions() || (plugin.getTowny().isPermissions() && TownyUniverse.getPermissionSource().has(test, getPermission()))) {
        		
        		/*
        		 * If the player is within range for this channel
        		 * or the recipient has the spy mode.
        		 */
	        	if ((testDistance(sender, test, getRange())) || (plugin.getTowny().hasPlayerMode(test, "spy"))) {
	        		
	        		if (bEssentials) {
						try {
							User targetUser = plugin.getTowny().getEssentials().getUser(test);
							/*
							 *  Don't send this message if the user is ignored
							 */
							if (targetUser.isIgnoredPlayer(sendersName))
								continue;
						} catch (TownyException e) {
							// Failed to fetch user so ignore.
						}
					}

	        		// Spy's can leave channels and we'll respect that
	        		if (absentPlayers != null) {
	        			// Ignore players who have left this channel
	        			if (absentPlayers.containsKey(test.getName())) {
	        				continue;
	        			}
	        		}
	        		recipients.add(test);
	        	}
        	}
        }
        
        if ((recipients.size() <= 1) && (ChatSettings.isUsingAloneMessage()))
        	sender.sendMessage(ChatSettings.getUsingAloneMessageString());        	

        return recipients;
	}
	
	/**
	 * Add all spying players to the recipients list.
	 * 
	 * @param recipients
	 * @return new recipients including spying.
	 */
	private Set<Player> checkSpying(Set<Player> recipients) {

				
		// Compile the list of recipients with spy perms
		for (Player test : plugin.getServer().getOnlinePlayers()) {
        	
        	if ((plugin.getTowny().hasPlayerMode(test, "spy")) && !(recipients.contains(test))) {
        		recipients.add(test);
        	}

        }
		
		return recipients;
	}

}
