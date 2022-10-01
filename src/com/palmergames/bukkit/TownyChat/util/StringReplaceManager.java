package com.palmergames.bukkit.TownyChat.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.palmergames.bukkit.TownyChat.TownyChatReplacer;
import com.palmergames.bukkit.TownyChat.listener.LocalTownyChatEvent;


/**
 * Register keyword regex's to be dynamically replaced.
 * 
 * Credits to:
 * - http://stackoverflow.com/questions/1326682/java-replacing-multiple-different-substring-in-a-string-at-once-or-in-the-most-e/1326962#1326962
 * 
 * Improved in v 2.0 to use Java 8's Function.
 * 
 * @author Chris H (Shade) 1.0, LlmDl 2.0
 * @version 2.0
 */
public class StringReplaceManager<E> {
	private List<Pattern> replacementPatterns = new ArrayList<Pattern>();
	private List<TownyChatReplacer> replacements = new ArrayList<TownyChatReplacer>();

	public boolean registerReplacer(String chatSlug, Function<LocalTownyChatEvent, String> replacement) {
		Pattern pattern = Pattern.compile(Pattern.quote(chatSlug));
		replacementPatterns.add(pattern);
		replacements.add(new TownyChatReplacer(replacement));
		return true;
	}

	public String replaceAll(String format, LocalTownyChatEvent e) {
		String out = format;
		for (int i = 0; i < replacementPatterns.size(); i++) {
			Matcher matcher = replacementPatterns.get(i).matcher(out);
			TownyChatReplacer replacer = replacements.get(i);
			StringBuffer sb = new StringBuffer();
			
			while (matcher.find()) {
				if (replacer == null) {
					matcher.appendReplacement(sb, e.toString());
				} else {
					try {
						String replacement = Matcher.quoteReplacement(replacer.getWith(e));
						matcher.appendReplacement(sb, replacement);
					} catch (Exception exception) {
						continue;
					}
				}
			}
			matcher.appendTail(sb);
			out = sb.toString();
		}
		return out;
	}
}