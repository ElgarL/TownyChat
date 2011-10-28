package com.palmergames.bukkit.TownyChat;

import java.util.regex.Pattern;

import com.palmergames.bukkit.towny.NotRegisteredException;
import com.palmergames.bukkit.towny.TownyFormatter;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.TownyChat.event.TownyChatEvent;
import com.palmergames.bukkit.TownyChat.util.StringReplaceManager;

public class TownyChatFormatter {
	private static StringReplaceManager<TownyChatEvent> replacer = new StringReplaceManager<TownyChatEvent>();

	static {
		replacer.registerFormatReplacement(Pattern.quote("{town}"), new TownyChatReplacerCallable() {
			@Override
			public String call(String match, TownyChatEvent event) throws Exception {
				return event.getResident().hasTown() ? event.getResident().getTown().getName() : "";
			}
		});
		replacer.registerFormatReplacement(Pattern.quote("{townformatted}"), new TownyChatReplacerCallable() {
			@Override
			public String call(String match, TownyChatEvent event) throws Exception {
				return formatTownyTag(event.getResident(), false, true);
			}
		});
		replacer.registerFormatReplacement(Pattern.quote("{towntag}"), new TownyChatReplacerCallable() {
			@Override
			public String call(String match, TownyChatEvent event) throws Exception {
				return formatTownTag(event.getResident(), false, false);
			}
		});
		replacer.registerFormatReplacement(Pattern.quote("{towntagoverride}"), new TownyChatReplacerCallable() {
			@Override
			public String call(String match, TownyChatEvent event) throws Exception {
				return formatTownTag(event.getResident(), true, false);
			}
		});
		
		replacer.registerFormatReplacement(Pattern.quote("{nation}"), new TownyChatReplacerCallable() {
			@Override
			public String call(String match, TownyChatEvent event) throws Exception {
				return event.getResident().hasNation() ? event.getResident().getTown().getNation().getName() : "";
			}
		});
		replacer.registerFormatReplacement(Pattern.quote("{nationformatted}"), new TownyChatReplacerCallable() {
			@Override
			public String call(String match, TownyChatEvent event) throws Exception {
				return formatNationTag(event.getResident(), false, true);
			}
		});
		replacer.registerFormatReplacement(Pattern.quote("{nationtag}"), new TownyChatReplacerCallable() {
			@Override
			public String call(String match, TownyChatEvent event) throws Exception {
				return formatNationTag(event.getResident(), false, false);
			}
		});
		replacer.registerFormatReplacement(Pattern.quote("{nationtagoverride}"), new TownyChatReplacerCallable() {
			@Override
			public String call(String match, TownyChatEvent event) throws Exception {
				return formatNationTag(event.getResident(), true, false);
			}
		});
		
		replacer.registerFormatReplacement(Pattern.quote("{townytag}"), new TownyChatReplacerCallable() {
			@Override
			public String call(String match, TownyChatEvent event) throws Exception {
				return formatTownyTag(event.getResident(), false, false);
			}
		});
		replacer.registerFormatReplacement(Pattern.quote("{townyformatted}"), new TownyChatReplacerCallable() {
			@Override
			public String call(String match, TownyChatEvent event) throws Exception {
				return formatTownyTag(event.getResident(), false, true);
			}
		});
		replacer.registerFormatReplacement(Pattern.quote("{townytagoverride}"), new TownyChatReplacerCallable() {
			@Override
			public String call(String match, TownyChatEvent event) throws Exception {
				return formatTownyTag(event.getResident(), true, false);
			}
		});
		
		replacer.registerFormatReplacement(Pattern.quote("{title}"), new TownyChatReplacerCallable() {
			@Override
			public String call(String match, TownyChatEvent event) throws Exception {
				return event.getResident().hasTitle() ? event.getResident().getTitle() : "";
			}
		});
		replacer.registerFormatReplacement(Pattern.quote("{surname}"), new TownyChatReplacerCallable() {
			@Override
			public String call(String match, TownyChatEvent event) throws Exception {
				return event.getResident().hasSurname() ? event.getResident().getSurname() : "";
			}
		});
		replacer.registerFormatReplacement(Pattern.quote("{townynameprefix}"), new TownyChatReplacerCallable() {
			@Override
			public String call(String match, TownyChatEvent event) throws Exception {
				return TownyFormatter.getNamePrefix(event.getResident());
			}
		});
		replacer.registerFormatReplacement(Pattern.quote("{townynamepostfix}"), new TownyChatReplacerCallable() {
			@Override
			public String call(String match, TownyChatEvent event) throws Exception {
				return TownyFormatter.getNamePostfix(event.getResident());
			}
		});
		replacer.registerFormatReplacement(Pattern.quote("{townyprefix}"), new TownyChatReplacerCallable() {
			@Override
			public String call(String match, TownyChatEvent event) throws Exception {
				return event.getResident().hasTitle() ? event.getResident().getTitle() : TownyFormatter.getNamePrefix(event.getResident());
			}
		});
		replacer.registerFormatReplacement(Pattern.quote("{townypostfix}"), new TownyChatReplacerCallable() {
			@Override
			public String call(String match, TownyChatEvent event) throws Exception {
				return event.getResident().hasSurname() ? event.getResident().getSurname() : TownyFormatter.getNamePostfix(event.getResident());
			}
		});
		
		replacer.registerFormatReplacement(Pattern.quote("{townycolor}"), new TownyChatReplacerCallable() {
			@Override
			public String call(String match, TownyChatEvent event) throws Exception {
				return event.getResident().isMayor() ? (event.getResident().isKing() ? TownySettings.getKingColour() : TownySettings.getMayorColour()) : "";
			}
		});
		replacer.registerFormatReplacement(Pattern.quote("{group}"), new TownyChatReplacerCallable() {
			@Override
			public String call(String match, TownyChatEvent event) throws Exception {
				return TownyUniverse.getPermissionSource().getPlayerGroup(event.getEvent().getPlayer());
			}
		});
		replacer.registerFormatReplacement(Pattern.quote("{permprefix}"), new TownyChatReplacerCallable() {
			@Override
			public String call(String match, TownyChatEvent event) throws Exception {
				return TownyUniverse.getPermissionSource().getPrefixSuffix(event.getResident(), "prefix");
			}
		});
		replacer.registerFormatReplacement(Pattern.quote("{permsuffix}"), new TownyChatReplacerCallable() {
			@Override
			public String call(String match, TownyChatEvent event) throws Exception {
				return TownyUniverse.getPermissionSource().getPrefixSuffix(event.getResident(), "suffix");
			}
		});

		replacer.registerFormatReplacement(Pattern.quote("{playername}"), new TownyChatReplacerCallable() {
			@Override
			public String call(String match, TownyChatEvent event) throws Exception {
				return event.getEvent().getPlayer().getName();
			}
		});
		/*
		replacer.registerFormatReplacement(Pattern.quote("{modplayername}"), new TownyChatReplacerCallable() {
			@Override
			public String call(String match, TownyChatEvent event) throws Exception {
				return event.getEvent().getPlayer().getDisplayName();
			}
		});
		*/
		replacer.registerFormatReplacement(Pattern.quote("{channelTag}"), new TownyChatReplacerCallable() {
			@Override
			public String call(String match, TownyChatEvent event) throws Exception {
				return formatTownyTag(event.getResident(), false, false);
			}
		});

		// Replace colours last ({msg} is replaced last as it can't be regex parsed).
		replacer.registerFormatReplacement("&{1}[0-9A-Fa-f]{1}", new TownyChatReplacerCallable() {
			@Override
			public String call(String match, TownyChatEvent event) throws Exception {
				return "\u00A7" + match.charAt(1);
			}
		});

	}

	public static String getChatFormat(TownyChatEvent event) {
		// Replace the {msg} here so it's not regex parsed.
		return replacer.replaceAll(event.getFormat(), event).replace("{modplayername}", "%1$s").replace("{msg}", "%2$s");
	}

	/**
	 * @param resident
	 * @param override	use full names if no tag is present
	 * @param full		Only use full names (no tags).
	 * @return
	 */
	public static String formatTownyTag(Resident resident, Boolean override, Boolean full) {
		try {
			if (resident.hasTown()) {
				Town town = resident.getTown();
				String townTag = town.getTag();
				Nation nation = null;
				String nationTag = null;
				if (resident.hasNation()) {
					nation = town.getNation();
					nationTag = nation.getTag();
				}

				String nTag = "", tTag = "";

				//Force use of full names only
				if (full) {
					nationTag = "";
					townTag = "";
				}
				// Load town tags/names
				if (townTag != null && !townTag.isEmpty())
					tTag = townTag;
				else if (override || full)
					tTag = town.getName();

				// Load the nation tags/names
				if ((nationTag != null) && !nationTag.isEmpty())
					nTag = nationTag;
				else if (resident.hasNation() && (override || full))
					nTag = nation.getName();

				// Output depending on what tags are present
				if ((!tTag.isEmpty()) && (!nTag.isEmpty()))
					return String.format(TownySettings.getChatTownNationTagFormat(), nTag, tTag);

				if (!nTag.isEmpty())
					return String.format(TownySettings.getChatNationTagFormat(), nTag);

				if (!tTag.isEmpty())
					return String.format(TownySettings.getChatTownTagFormat(), tTag);

			}
		} catch (NotRegisteredException e) {
			// no town or nation
		}
		return "";
	}

	public static String formatTownTag(Resident resident, Boolean override, Boolean full) {
		try {
			if (resident.hasTown())
				if (resident.getTown().hasTag())
					return String.format(TownySettings.getChatTownTagFormat(), resident.getTown().getTag());
				else if (override)
					return String.format(TownySettings.getChatTownTagFormat(), resident.getTown().getName());

		} catch (NotRegisteredException e) {
		}
		return "";
	}

	public static String formatNationTag(Resident resident, Boolean override, Boolean full) {
		try {
			if (resident.hasNation())
				if (resident.getTown().getNation().hasTag())
					return String.format(TownySettings.getChatNationTagFormat(), resident.getTown().getNation().getTag());
				else if (override)
					return String.format(TownySettings.getChatNationTagFormat(), resident.getTown().getNation().getName());

		} catch (NotRegisteredException e) {
		}
		return "";
	}
}
