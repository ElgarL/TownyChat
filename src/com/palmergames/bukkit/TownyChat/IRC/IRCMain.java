package com.palmergames.bukkit.TownyChat.IRC;

import java.io.IOException;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;

import com.palmergames.bukkit.TownyChat.Chat;



public class IRCMain extends PircBot implements Runnable {
	
	
	static Chat plugin;
	static IRCMain bot;
	private String nickName;
	private HashMap<String, Integer> connectionTries = new HashMap<String, Integer>();
	
	public IRCMain(Chat plugin) {
		IRCMain.plugin = plugin;
		try {
			this.init();
		} catch (NickAlreadyInUseException e) {

			// TODO: Does this work?
			nickName += "_"; // Change nickName if it is taken
			onReload();
		
		} catch (IOException e) {
		} catch (IrcException e) {
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
	 * Sets the bot's NickName
	 * @param newNick
	 */
	public void setBotNick(String newNick) {
		this.changeNick(newNick);
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
			nickName += "_";
			onReload();
		} catch (IOException e) {
		} catch (IrcException e) {
		}
	}

	
	/**
	 * Use this to start the bot!
	 * @throws NickAlreadyInUseException
	 * @throws IOException
	 * @throws IrcException
	 */
	public void init() throws NickAlreadyInUseException, IOException, IrcException {
		// TODO: Add stuff in the config for this!
		this.setVerbose(false);
		this.setName(nickName);
		this.connect("irc.esper.net");
		this.joinChannel("#towny-dev");
		
	}
	
	/**
	 * This will send a message to IRC via the bot!
	 * @param channel
	 * @param message
	 */
	public static void sendMesage(String channel, String message) {
		bot.sendMessage(channel, message);
	}
	
	@Override
	public void onMessage(String channel, String sender, String login, String hostname, String message) {
		Player[] onlinePlayers = plugin.getServer().getOnlinePlayers();
		for (Player player : onlinePlayers)
			player.sendMessage(sender + " " + message); // TODO: Get the format you want irc to be in
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

	@Override
	public void run() {
		try {
			init();
		} catch (NickAlreadyInUseException e) {
			this.nickName += "_";
			onReload();
		} catch (IOException e) {
		} catch (IrcException e) {
		}
	}
}
