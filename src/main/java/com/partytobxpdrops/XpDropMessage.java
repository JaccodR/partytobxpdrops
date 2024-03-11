package com.partytobxpdrops;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.Value;
import net.runelite.api.Skill;
import net.runelite.client.party.messages.PartyMemberMessage;

@Value
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class XpDropMessage extends PartyMemberMessage
{
	String playerName;
	Skill skill;
	int xp;
	int npcId;
	int cooldown;
}
