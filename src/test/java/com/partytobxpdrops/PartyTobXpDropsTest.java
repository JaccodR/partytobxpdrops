package com.partytobxpdrops;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class PartyTobXpDropsTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(PartyTobXpDropsPlugin.class);
		RuneLite.main(args);
	}
}