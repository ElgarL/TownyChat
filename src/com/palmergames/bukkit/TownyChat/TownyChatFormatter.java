package com.palmergames.bukkit.TownyChat;

import org.bukkit.entity.Player;

import com.palmergames.bukkit.TownyChat.config.ChatSettings;
import com.palmergames.bukkit.TownyChat.listener.LocalTownyChatEvent;
import com.palmergames.bukkit.TownyChat.util.StringReplaceManager;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.object.Government;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.util.Colors;
import com.palmergames.util.StringMgmt;
import com.palmergames.bukkit.towny.TownyUniverse;

public class TownyChatFormatter {
	private static StringReplaceManager<LocalTownyChatEvent> replacer = new StringReplaceManager<LocalTownyChatEvent>();

	static {
		replacer.registerReplacer("{worldname}", event -> getWorldTag(event));
		replacer.registerReplacer("{town}", event -> getTownName(event.getResident()));
		replacer.registerReplacer("{townformatted}", event -> formatTownTag(event.getResident(), false, true));
		replacer.registerReplacer("{towntag}", event -> formatTownTag(event.getResident(), false, false));
		replacer.registerReplacer("{towntagoverride}", event -> formatTownTag(event.getResident(), true, false));
		replacer.registerReplacer("{nation}", event -> getNationName(event.getResident()));
		replacer.registerReplacer("{nationformatted}", event -> formatNationTag(event.getResident(), false, true));
		replacer.registerReplacer("{nationtag}", event -> formatNationTag(event.getResident(), false, false));
		replacer.registerReplacer("{nationtagoverride}", event -> formatNationTag(event.getResident(), true, false));
		replacer.registerReplacer("{townytag}", event -> formatTownyTag(event.getResident(), false, false));
		replacer.registerReplacer("{townyformatted}", event -> formatTownyTag(event.getResident(), false, true));
		replacer.registerReplacer("{townytagoverride}", event -> formatTownyTag(event.getResident(), true, false));
		replacer.registerReplacer("{title}", event -> getPrefix(event.getResident(), true));
		replacer.registerReplacer("{surname}", event -> getSuffix(event.getResident(), false));
		replacer.registerReplacer("{townynameprefix}", event -> getNamePrefix(event.getResident()));
		replacer.registerReplacer("{townynamepostfix}", event -> getNamePostfix(event.getResident()));
		replacer.registerReplacer("{townyprefix}", event -> getPrefix(event.getResident(), false));
		replacer.registerReplacer("{townypostfix}", event -> getSuffix(event.getResident(), false));
		replacer.registerReplacer("{townycolor}", event -> getTownyColour(event.getResident()));
		replacer.registerReplacer("{group}", event -> getVaultGroup(event.getEvent().getPlayer()));
		replacer.registerReplacer("{permprefix}", event -> getVaultPrefixSuffix(event.getResident(), "prefix"));
		replacer.registerReplacer("{permsuffix}", event -> getVaultPrefixSuffix(event.getResident(), "suffix"));
		replacer.registerReplacer("{permuserprefix}", event -> getVaultPrefixSuffix(event.getResident(), "userprefix"));
		replacer.registerReplacer("{permusersuffix}", event -> getVaultPrefixSuffix(event.getResident(), "usersuffix"));
		replacer.registerReplacer("{permgroupprefix}", event -> getVaultPrefixSuffix(event.getResident(), "groupprefix"));
		replacer.registerReplacer("{permgroupsuffix}", event -> getVaultPrefixSuffix(event.getResident(), "groupsuffix"));
		replacer.registerReplacer("{playername}", event -> event.getEvent().getPlayer().getName());
	}

	public static String getChatFormat(LocalTownyChatEvent event) {
		String out = replacer.replaceAll(event.getFormat(), event) // Replace the townychat tags with their values. 
							 .replace("%", "")                     // Prevent user-inputted tags from breaking chat format. 
							 .replace("{modplayername}", "%1$s")   // Other plugins will replace this with the DisplayName.
							 .replace("{msg}", "%2$s");            // Replace the {msg} here so it's not regex parsed.
		return Colors.translateColorCodes(out);
	}

	public static String getWorldTag(LocalTownyChatEvent event) {
		return String.format(ChatSettings.getWorldTag(), event.getEvent().getPlayer().getWorld().getName());
	}

	public static String getTownName(Resident resident) {
		return resident.hasTown() ? resident.getTownOrNull().getName() : "";
	}

	public static String getNationName(Resident resident) {
		return resident.hasNation() ? resident.getNationOrNull().getName() : "";
	}

	/**
	 * @param resident
	 * @param override	use full names if no tag is present
	 * @param full		Only use full names (no tags).
	 * @return string containing the correctly formatted nation/town data
	 */
	public static String formatTownyTag(Resident resident, Boolean override, Boolean full) {
		if (resident.hasTown()) {
			Town town = TownyAPI.getInstance().getResidentTownOrNull(resident);
			String townTag = getTag(town);
			Nation nation = null;
			String nationTag = null;
			if (resident.hasNation()) {
				nation = TownyAPI.getInstance().getResidentNationOrNull(resident);
				nationTag = getTag(nation);
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
				tTag = getName(town);

			// Load the nation tags/names
			if ((nationTag != null) && !nationTag.isEmpty())
				nTag = nationTag;
			else if (resident.hasNation() && (override || full))
				nTag = getName(nation);

			// Output depending on what tags are present
			if ((!tTag.isEmpty()) && (!nTag.isEmpty())) {
				if (ChatSettings.getBothTags().contains("%t") || ChatSettings.getBothTags().contains("%n")) {
					// Then it contains %s & %s
					// Small suttle change so that an issue is solved, it is documented in the config.
					// But only after addition of this. (v0.50)
					return ChatSettings.getBothTags().replace("%t", tTag).replace("%n", nTag);
				} else {
					return String.format(ChatSettings.getBothTags(), nTag, tTag);
				}
			}

			if (!nTag.isEmpty()) {
				return String.format(ChatSettings.getNationTag(), nTag);
			}

			if (!tTag.isEmpty()) {
				return String.format(ChatSettings.getTownTag(), tTag);
			}

		}
		return "";
	}

	public static String formatTownTag(Resident resident, Boolean override, Boolean full) {
		if (resident.hasTown()) {
			Town town = TownyAPI.getInstance().getResidentTownOrNull(resident);
			if (full)
				return String.format(ChatSettings.getTownTag(), getName(town));
			else if (town.hasTag())
				return String.format(ChatSettings.getTownTag(), getTag(town));
			else if (override)
				return String.format(ChatSettings.getTownTag(), getName(town));

		}
		return "";
	}

	public static String formatNationTag(Resident resident, Boolean override, Boolean full) {
		if (resident.hasNation()) {
			Nation nation = TownyAPI.getInstance().getResidentNationOrNull(resident);
			if (full)
				return String.format(ChatSettings.getNationTag(), getName(nation));
			else if (nation.hasTag())
				return String.format(ChatSettings.getNationTag(), getTag(nation));
			else if (override)
				return String.format(ChatSettings.getNationTag(), getName(nation));
		}
		return "";
	}

	public static String getNamePrefix(Resident resident) {

		if (resident == null)
			return "";
		if (resident.isKing())
			return TownySettings.getKingPrefix(resident);
		else if (resident.isMayor())
			return TownySettings.getMayorPrefix(resident);
		return "";
	}

	public static String getNamePostfix(Resident resident) {

		if (resident == null)
			return "";
		if (resident.isKing())
			return TownySettings.getKingPostfix(resident);
		else if (resident.isMayor())
			return TownySettings.getMayorPostfix(resident);
		return "";
	}
	
	private static String getName(Government gov) {
		return StringMgmt.remUnderscore(gov.getName());
	}
	
	private static String getTag(Government gov) {
		return gov.getTag();
	}

	public static String getPrefix(Resident resident, boolean blank) {
		return resident.hasTitle() ? resident.getTitle() + " " : blank ? "" : getNamePrefix(resident);
	}

	public static String getSuffix(Resident resident, boolean blank) {
		return resident.hasSurname() ? " " + resident.getSurname() : blank ? "" : getNamePostfix(resident);
	}

	public static String getTownyColour(Resident resident) {
		return !resident.hasTown() 
				? ChatSettings.getNomadColour()
				: resident.isMayor() ? (resident.isKing() ? ChatSettings.getKingColour() : ChatSettings.getMayorColour()) : ChatSettings.getResidentColour();
	}
	
	public static String getVaultGroup(Player player) {
		return TownyUniverse.getInstance().getPermissionSource().getPlayerGroup(player);
	}

	public static String getVaultPrefixSuffix(Resident resident, String slug) {
		return TownyUniverse.getInstance().getPermissionSource().getPrefixSuffix(resident, slug);
	}
}
