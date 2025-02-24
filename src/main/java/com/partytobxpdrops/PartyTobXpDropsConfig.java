package com.partytobxpdrops;

import com.partytobxpdrops.misc.Fonts;
import java.awt.Color;
import java.awt.Font;

import net.runelite.client.config.*;

@ConfigGroup(PartyTobXpDropsConfig.GROUP)
public interface PartyTobXpDropsConfig extends Config {
	String GROUP = "partytobxpdrops";

	@ConfigSection(
			name = "General",
			position = 0,
			description = "GeneralSettings",
			closedByDefault = false
	)
	String generalSettings = "generalSettings";

	@ConfigItem(
			position = 0,
			keyName = "Display XP",
			name = "Display XP Drops",
			description = "Enable XP Drop display.",
			section = generalSettings
	)
	default boolean displayXpDrops()
	{
		return true;
	}

	@ConfigItem(
		position = 1,
		keyName = "displayTicksRemaining",
		name = "Display ticks remaining",
		description = "Display xp drop cooldown",
		section = generalSettings
	)
	default boolean displayTicksRemaining()
	{
		return true;
	}

	@ConfigItem(
		position = 2,
		keyName = "ticksToKeep",
		name = "Ticks to keep",
		description = "Number of ticks till xp drop disappears. If it's set to 0 players cooldown will be used instead",
		section = generalSettings
	)
	default int ticksToKeep()
	{
		return 0;
	}

	@ConfigItem(
		position = 3,
		keyName = "fontsName",
		name = "Fonts",
		description = "Fonts to use for xp",
		section = generalSettings
	)
	default Fonts fontsName()
	{
		return Fonts.ARIAL;
	}

	@ConfigItem(
		position = 4,
		keyName = "fontsSize",
		name = "Font Size",
		description = "Size of fonts",
		section = generalSettings
	)
	default int fontsSize()
	{
		return 12;
	}

	@ConfigItem(
		position = 5,
		keyName = "fontsBold",
		name = "Bold",
		description = "Bold fonts",
		section = generalSettings
	)
	default boolean fontsBold()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		position = 6,
		keyName = "playerNameColor",
		name = "Player Name Color",
		description = "Color of player name",
		section = generalSettings
	)
	default Color playerNameColor()
	{
		return Color.WHITE;
	}

	@ConfigSection(
		name = "Players",
		position = 1,
		description = "Show players (based on orb order)",
		closedByDefault = true
	)
	String playerSettings = "playerSettings";

	@ConfigItem(
		position = 0,
		keyName = "showPlayer1",
		name = "Player 1",
		description = "Show orb 1 player",
		section = playerSettings
	)
	default boolean showPlayer1()
	{
		return true;
	}

	@ConfigItem(
		position = 1,
		keyName = "showPlayer2",
		name = "Player 2",
		description = "Show orb 2 player",
		section = playerSettings
	)
	default boolean showPlayer2()
	{
		return true;
	}

	@ConfigItem(
		position = 2,
		keyName = "showPlayer3",
		name = "Player 3",
		description = "Show orb 3 player",
		section = playerSettings
	)
	default boolean showPlayer3()
	{
		return true;
	}

	@ConfigItem(
		position = 3,
		keyName = "showPlayer4",
		name = "Player 4",
		description = "Show orb 4 player",
		section = playerSettings
	)
	default boolean showPlayer4()
	{
		return true;
	}

	@ConfigItem(
		position = 4,
		keyName = "showPlayer5",
		name = "Player 5",
		description = "Show orb 5 player",
		section = playerSettings
	)
	default boolean showPlayer5()
	{
		return true;
	}

	@ConfigSection(
		name = "Targets",
		position = 2,
		description = "Show xp drops in rooms",
		closedByDefault = true
	)
	String roomSettings = "roomSettings";

	@ConfigItem(
		position = 0,
		keyName = "showMaiden",
		name = "Maiden",
		description = "Show xp drops at maiden",
		section = roomSettings
	)
	default boolean showMaiden()
	{
		return true;
	}

	@ConfigItem(
		position = 1,
		keyName = "showMaidenCrabs",
		name = "Maiden Crabs",
		description = "Show xp drops on maiden crabs",
		section = roomSettings
	)
	default boolean showMaidenCrabs()
	{
		return true;
	}

	@ConfigItem(
		position = 2,
		keyName = "showBloat",
		name = "Bloat",
		description = "Show xp drops at bloat",
		section = roomSettings
	)
	default boolean showBloat()
	{
		return true;
	}

	@ConfigItem(
		position = 3,
		keyName = "showNylocas",
		name = "Nylocas",
		description = "Show xp drops at nylocas",
		section = roomSettings
	)
	default boolean showNylocas()
	{
		return true;
	}

	@ConfigItem(
		position = 4,
		keyName = "showSotetseg",
		name = "Sotetseg",
		description = "Show xp drops at sotetseg",
		section = roomSettings
	)
	default boolean showSotetseg()
	{
		return true;
	}

	@ConfigItem(
		position = 5,
		keyName = "showXarpus",
		name = "Xarpus",
		description = "Show xp drops at xarpus",
		section = roomSettings
	)
	default boolean showXarpus()
	{
		return true;
	}

	@ConfigItem(
		position = 6,
		keyName = "showVerzik",
		name = "Verzik",
		description = "Show xp drops at verzik",
		section = roomSettings
	)
	default boolean showVerzik()
	{
		return true;
	}

	@ConfigSection(
		name = "Styles",
		position = 3,
		description = "Show styles",
		closedByDefault = true
	)
	String styleSettings = "styleSettings";

	@ConfigItem(
		position = 0,
		keyName = "showMagic",
		name = "Magic",
		description = "Display magic xp drops",
		section = styleSettings
	)
	default boolean showMagic()
	{
		return true;
	}

	@ConfigItem(
		position = 1,
		keyName = "showRange",
		name = "Range",
		description = "Display range xp drops",
		section = styleSettings
	)
	default boolean showRange()
	{
		return true;
	}

	@ConfigItem(
		position = 2,
		keyName = "showMelee",
		name = "Melee",
		description = "Display melee xp drops",
		section = styleSettings
	)
	default boolean showMelee()
	{
		return true;
	}
	@ConfigSection(
			position = 5,
			name = "Party Hits Overlay",
			description = "Party Hits Settings",
			closedByDefault = true
	)
	String partyHitsSettings = "partyHitsSettings";

	@ConfigItem(
			position = 0,
			keyName = "Show party hits",
			name = "Show party hits",
			description = "Show the hits of your party members.",
			section = partyHitsSettings
	)
	default boolean partyHits()
	{
		return false;
	}
	@ConfigItem(
			position = 1,
			keyName = "Duration",
			name = "Hitsplat duration",
			description = "How long should the hitsplat stay for (in frames).",
			section = partyHitsSettings
	)
	default int duration()
	{
		return 150;
	}
	@ConfigItem(
			position = 2,
			keyName = "Maiden Only",
			name = "Maiden Only",
			description = "Only show hits in the maiden room.",
			section = partyHitsSettings
	)
	default boolean maidenOnly()
	{
		return false;
	}
	@ConfigItem(
			position = 3,
			keyName = "Show Self",
			name = "Show Self",
			description = "Show your own hits.",
			section = partyHitsSettings
	)
	default boolean ownHits()
	{
		return false;
	}
	@Range(min = -100, max = 100)
	@ConfigItem(
			position = 5,
			keyName = "Height Offset",
			name = "Height Offset",
			description = "Make the hitsplat higher above the player.",
			section = partyHitsSettings
	)
	default int offset()
	{
		return 20;
	}
	@Range(min = -100, max = 100)
	@ConfigItem(
			position = 6,
			keyName = "Horizontal Offset",
			name = "Horizontal Offset",
			description = "Adjust the horizontal offset of the text.",
			section = partyHitsSettings
	)
	default int horOffset()
	{
		return 0;
	}
	@ConfigItem(
			position = 7,
			keyName = "Font",
			name = "Font",
			description = "Change the font of the text.",
			section = partyHitsSettings
	)
	default Fonts partyHitFont()
	{
		return Fonts.ARIAL;
	}
	@ConfigItem(
			position = 8,
			keyName = "Font Size",
			name = "Font Size",
			description = "Change the size of the text.",
			section = partyHitsSettings
	)
	default int size()
	{
		return 15;
	}
	@ConfigItem(
			position = 9,
			keyName = "Text Color",
			name = "Text Color",
			description = "Change the color of the text.",
			section = partyHitsSettings
	)
	default Color color()
	{
		return Color.WHITE;
	}

	@ConfigSection(
			position = 4,
			name = "Maiden Live HP",
			description = "Maiden HP settings",
			closedByDefault = true
	)
	String maidenHpSettings = "maidenHpSettings";

	@ConfigItem(
			position = 0,
			name = "Show Maiden HP",
			keyName = "Show Maiden HP",
			description = "Show Maiden Live HP Overlay",
			section = maidenHpSettings
	)
	default boolean showMaidenHp()
	{
		return false;
	}

	@Range(min = -100, max = 100)
	@ConfigItem(
			position = 1,
			keyName = "Maiden Height Offset",
			name = "Maiden Height Offset",
			description = "Make the Maiden HP higher.",
			section = maidenHpSettings
	)
	default int maidenOffset()
	{
		return 30;
	}
	@Range(min = -100, max = 100)
	@ConfigItem(
			position = 2,
			keyName = "Maiden Horizontal Offset",
			name = "Maiden Horizontal Offset",
			description = "Adjust the horizontal offset of the text on maiden.",
			section = maidenHpSettings
	)
	default int maidenHorOffset()
	{
		return -5;
	}
	@ConfigItem(
			position = 3,
			keyName = "Maiden Font",
			name = "Maiden Font",
			description = "Change the font of the HP on maiden.",
			section = maidenHpSettings
	)
	default Fonts maidenFont()
	{
		return Fonts.ARIAL;
	}
	@ConfigItem(
			position = 4,
			keyName = "Maiden Font Size",
			name = "Maiden Font Size",
			description = "Change the size of the HP on maiden.",
			section = maidenHpSettings
	)
	default int maidenSize()
	{
		return 15;
	}
	@ConfigItem(
			position = 5,
			keyName = "Maiden Text Color",
			name = "Maiden Text Color",
			description = "Change the color of the text on maiden.",
			section = maidenHpSettings
	)
	default Color maidenColor()
	{
		return Color.GREEN;
	}
	@ConfigItem(
			position = 6,
			keyName = "Sync hits",
			name = "Sync hits",
			description = "Sync your hits with your teammates (delays your hits updating slightly)",
			section = maidenHpSettings
	)
	default boolean syncHits()
	{
		return false;
	}
	@ConfigItem(
			position = 7,
			keyName = "Update Threshold",
			name = "Update Threshold",
			description = "Only update Maidens HP if change is more than x% from old hp",
			section = maidenHpSettings
	)
	default double updateThreshold()
	{
		return 0.2;
	}
}
