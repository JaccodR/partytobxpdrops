package com.partytobxpdrops;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import javax.inject.Inject;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;


public class PartyTobXpDropsOverlay extends OverlayPanel
{
	private final Client client;
	private final PartyTobXpDropsPlugin plugin;
	private final PartyTobXpDropsConfig config;

	@Setter
	private Font defaultFont;
	@Setter
	private Color defaultColor;

	@Inject
	private PartyTobXpDropsOverlay(Client client, PartyTobXpDropsPlugin plugin, PartyTobXpDropsConfig config)
	{
		super(plugin);
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.TOP_LEFT);
		setPriority(0.75f);

		_updateConfigurations();
	}

	public void _updateConfigurations()
	{
		int style = Font.PLAIN;
		if (config.fontsBold())
		{
			style = Font.BOLD;
		}
		this.defaultFont = new Font(config.fontsName().toString(), style, config.fontsSize());
		this.defaultColor = config.playerNameColor();
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (plugin.stopped())
		{
			return null;
		}

		for (PartyTobXpDropsPlugin.PlayerOrb playerOrb : plugin.getPlayerOrbs())
		{
			if (client.getVarbitValue(playerOrb.getHealthVarb()) == 0) // Orb doesn't exist
			{
				break;
			}
			String playerName = client.getVarcStrValue(playerOrb.getNameVarc());
			String xp = null;
			Skill skill = null;
			PartyTobXpDropsPlugin.PartyXpItem partyXpItem = plugin.getPartyXp().getOrDefault(playerName, null);
			if (partyXpItem != null)
			{
				skill = partyXpItem.getSkill();
				xp = String.valueOf(partyXpItem.getXp());
				if (plugin.config.displayTicksRemaining())
				{
					xp += " (" + partyXpItem.getCooldown() + ")";
				}
			}
			Color xpColor = defaultColor;
			if (skill != null)
			{
				switch (skill)
				{
					case MAGIC:
						xpColor = Color.CYAN;
						break;
					case RANGED:
						xpColor = Color.GREEN;
						break;
					case ATTACK:
					case STRENGTH:
					case DEFENCE:
						xpColor = Color.ORANGE;
						break;
				}
			}
			panelComponent.getChildren().add(
				LineComponent.builder()
					.left(playerName)
					.leftFont(defaultFont)
					.leftColor(defaultColor)
					.right(xp)
					.rightFont(defaultFont)
					.rightColor(xpColor)
					.build()
			);
		}
		return super.render(graphics);
	}
}