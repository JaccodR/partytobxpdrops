package com.partytobxpdrops.misc;


import lombok.EqualsAndHashCode;
import lombok.Value;
import net.runelite.client.party.messages.PartyMemberMessage;


@Value
@EqualsAndHashCode(callSuper = true)
public class Hit extends PartyMemberMessage
{
    int damage;
    String player;
    int tickDelay;
}
