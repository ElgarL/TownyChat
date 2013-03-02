package com.palmergames.bukkit.TownyChat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import org.apache.commons.lang.exception.ExceptionUtils;

import sun.security.action.GetLongAction;

import com.palmergames.bukkit.TownyChat.util.FileMgmt;
import com.palmergames.bukkit.towny.TownyMessaging;

public class Profiles {
	private String username;
	private Vector<String> ignored;
	public static String profilesPath = "";

	public Profiles(String name) {
		username = name;
		ignored = new Vector<String>();
		Load();
	}

	public boolean isIgnored(String name){
		return ignored.contains(name.toLowerCase());
	}
	
	public boolean addToIgnored(String name){
		if (!isIgnored(name)) {
			ignored.add(name.toLowerCase());
			Save();
			return true;
		}
		return false;
	}
	
	public boolean removeFromIgnored(String name){
		if(isIgnored(name)){
			ignored.remove(name.toLowerCase());
			Save();
			return true;
		}
		return false;
	}
	
	public void Save(){
		File profile = new File(profilesPath + FileMgmt.fileSeparator()
				+ username + ".pl");
		if (ignored.size()==0){
			if (profile.exists()) profile.delete();
			return;
		}
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(profile, false));
			for (String ignor : ignored) {
				bw.write(ignor);
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			TownyMessaging
					.sendErrorMsg(ExceptionUtils.getFullStackTrace(e));
			return;
		}
	}
	
	private void Load() {
		File profile = new File(profilesPath + FileMgmt.fileSeparator()
				+ username + ".pl");
		if (!profile.exists()) {
			return;
		}
		try {
			FileInputStream fstream = new FileInputStream(profile);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				if (!ignored.contains(strLine.trim()))
					ignored.add(strLine.trim());
			}
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			TownyMessaging.sendErrorMsg(ExceptionUtils.getFullStackTrace(e));
			return;
		}
	}
}
