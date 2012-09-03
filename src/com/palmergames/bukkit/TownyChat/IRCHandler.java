package com.palmergames.bukkit.TownyChat;

import java.io.IOException;
import java.util.HashMap;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

import com.palmergames.bukkit.TownyChat.channels.Channel;
import com.palmergames.bukkit.TownyChat.config.ChatSettings;
import com.palmergames.bukkit.TownyChat.util.IRCUtil;


public class IRCHandler extends PircBot {
	
	
	static Chat plugin;
	static IRCHandler bot;
	private String currentNick;
	private int takenInt = 0;
	private HashMap<String, Integer> connectionTries = new HashMap<String, Integer>();
	
	public IRCHandler(Chat plugin) {
		
		this.currentNick = ChatSettings.getBotNick().substring(0, Math.min(11, ChatSettings.getBotNick().length()));
		IRCHandler.plugin = plugin;
		
		try {
			
			this.init();
			
		} catch (NickAlreadyInUseException e) {

			fixNick();
			onReload();
		
		} catch (Exception e) {
		}
		
	}
	
	/**
	 * This connects to an IRC server we want!
	 * @param IRCServer
	 * @throws NickAlreadyInUseException
	 * @throws IOException
	 * @throws IrcException
	 */
	public void connectToServer(String IRCServer) throws NickAlreadyInUseException, IOException, IrcException {
		
		this.connect(IRCServer);
		
	}
	
	/**
	 * Connect to an String array of channels we tell it to!
	 * @param channels
	 */
	public void connectToChannels(String[] channels) {
		
		for (String chan : channels) {
			this.joinChannel(chan);
		}
		
	}
	
	/**
	 * Connect to a single channel we tell it!
	 * @param channels
	 */
	public void connectToChannels(String channels) {
		
		this.joinChannel(channels);
		
	}
	
	/**
	 * Call this when some one has done a TownyChat reload!
	 */
	public void onReload() {
		
		onDisable();
		
		if (this.isConnected()) {
			this.disconnect();
		}
		
		try {
			init();
		} catch (NickAlreadyInUseException e) {
			fixNick();
			onReload();
		} catch (Exception e) {
		}
		
	}

	
	/**
	 * Use this to start the bot!
	 * @throws NickAlreadyInUseException
	 * @throws IOException
	 * @throws IrcException
	 */
	public void init() throws NickAlreadyInUseException, IOException, IrcException {
		
		this.setVerbose(false);
		this.setName(currentNick);
		this.connect(ChatSettings.getServer(), ChatSettings.getPort(), ChatSettings.getServerPassword());
		
	}
	
	@Override
	public void onMessage(String channel, String sender, String login, String hostname, String message) {

		for (Channel sendTo : plugin.getChannelsHandler().isRelayIRC(channel)) {
			sendTo.sendMessage(IRCUtil.ircToIRCChat(sender, channel, message, getRank(sender, channel), sendTo.getFormat()));
			
		}
		
	}
	
	@Override
	public void onKick(final String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {

		// auto re-join
		if (recipientNick.equalsIgnoreCase(this.getNick())) {
			connectionTries.put(channel, plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {
				@Override
				public void run() {
					connectToChannels(channel);
				}
			}, 0, 10 * 20));
		}
		
	}
	
	/**
	 * Stop attempting to join a/ny channels that we are rejoining!
	 */
	public void onDisable() {
		
		for (String channel : connectionTries.keySet()) {
			plugin.getServer().getScheduler().cancelTask(connectionTries.get(channel));
		}
		
	}
	
	@Override
	public void onJoin(String channel, String sender, String login, String hostname) {
		
		if (connectionTries.containsKey(channel)) {
			plugin.getServer().getScheduler().cancelTask(connectionTries.get(channel));
		}
		
	}

	/**
	 * 
	 * Resets the user's nickname if there is an error with connection.
	 * 
	 */
	private void fixNick() {
		
		currentNick = ChatSettings.getBotNick().substring(0, Math.min(11, ChatSettings.getBotNick().length()) - Integer.toString(this.takenInt).length()) + this.takenInt;

	}
	
	private String getRank(String name, String channel) {

		String rank = "";
		
		for (User user : this.getUsers(channel)) {
			
			if (user.getNick().equalsIgnoreCase(name) && user.isOp()) {
				return "@";
			} else if (user.getNick().equalsIgnoreCase(name) && user.hasVoice()) {
				return "+";
			}
			
		}
		
		return rank;

	}
	
	/**
	 * 
	 * Hooks into PIRC libs, once connected ident the bot.
	 * 
	 */
	@Override
	protected void onConnect() {
		
		if (ChatSettings.getBotPassword() != null && !ChatSettings.getBotPassword().isEmpty()) {
			identify(ChatSettings.getBotPassword());
		}
		
	}
	
}
