package com.partytobxpdrops;

import com.partytobxpdrops.misc.Fonts;
import java.awt.Color;
import java.awt.Font;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(PartyTobXpDropsConfig.GROUP)
public interface PartyTobXpDropsConfig extends Config
{
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
		position = 1,
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
		position = 2,
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
		position = 3,
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
		position = 4,
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
		position = 5,
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
		closedByDefault = false
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
		closedByDefault = false
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
		closedByDefault = false
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
}
