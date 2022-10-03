package com.palmergames.bukkit.TownyChat.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.reader.UnicodeReader;


public class FileMgmt {

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getMap(String filepath, String resource) {

		File f = new File(filepath);
		if (!(f.exists() && f.isFile())) {
			// Populate a new file
			try {
				try (FileOutputStream fos = new FileOutputStream(f)) {
					fos.write(getResourceFileAsString("/" + resource).getBytes(StandardCharsets.UTF_8));
				}
			} catch (IOException e) {
				// No resource file found
				e.printStackTrace();
				return null;
			}
		}

		f = new File(filepath);

		Yaml yamlChannels = new Yaml(new SafeConstructor());
		Object channelsRootDataNode;

		try (FileInputStream fileInputStream = new FileInputStream(f)) {
			channelsRootDataNode = yamlChannels.load(new UnicodeReader(fileInputStream));
			if (channelsRootDataNode == null) {
				throw new NullPointerException();
			}
		} catch (Exception ex) {
			throw new IllegalArgumentException("The following file couldn't pass on Parser.\n" + f.getPath(), ex);
		}

		if (channelsRootDataNode instanceof Map)
			return (Map<String, Object>) channelsRootDataNode;

		return null;
	}

	/**
	 * Reads given resource file as a string.
	 *
	 * @param fileName path to the resource file
	 * @return the file's contents
	 * @throws IOException if read fails for any reason
	 */
	static String getResourceFileAsString(String fileName) throws IOException {
		if (fileName == null)
			return "";
		try (InputStream is = FileMgmt.class.getResourceAsStream(fileName);
			InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
			BufferedReader reader = new BufferedReader(isr)) {
				return reader.lines().collect(Collectors.joining(System.lineSeparator()));
		}
	}

}