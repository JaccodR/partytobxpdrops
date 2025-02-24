package com.partytobxpdrops;

import com.google.common.collect.ImmutableList;
import com.google.inject.Provides;
import com.partytobxpdrops.attacks.AttackStyle;
import com.partytobxpdrops.attacks.WeaponType;
import com.partytobxpdrops.constants.TobNpc;
import com.partytobxpdrops.constants.TobRegion;

import java.util.*;
import javax.inject.Inject;

import com.partytobxpdrops.maiden.MaidenHandler;
import com.partytobxpdrops.misc.Hit;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.kit.KitType;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.party.PartyPlugin;
import net.runelite.client.plugins.xptracker.XpTrackerPlugin;
import net.runelite.http.api.item.ItemEquipmentStats;
import net.runelite.http.api.item.ItemStats;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.PartyChanged;
import net.runelite.client.party.PartyService;
import net.runelite.client.party.WSClient;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.apache.commons.lang3.ArrayUtils;


@PluginDependency(XpTrackerPlugin.class)
@PluginDependency(PartyPlugin.class)
@PluginDescriptor(
	name = "Party Tob XP Drops",
	description = "Shows xp drops of other party members in tob."

)
@Slf4j
public class PartyTobXpDropsPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	public PartyTobXpDropsConfig config;

	@Inject
	private PartyService partyService;

	@Inject
	private WSClient wsClient;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private PartyTobXpDropsOverlay overlay;
	@Inject
	private PartyHitsOverlay partyHitsOverlay;
	@Inject
	private ItemManager itemManager;
	@Inject
	private XpToDamage xpToDamage;
	@Inject
	private MaidenHandler maidenHandler;
	@Inject
	private EventBus eventBus;

	@Getter
	private boolean inTob;

	@Getter
	private boolean inActiveRegion = false;

	private final Set<Integer> activeRegions = new HashSet<Integer>();

	@Data
	static class PartyXpItem
	{
		final Skill skill;
		final int xp;
		int cooldown;
	}

	@Data
	static class PlayerOrb
	{
		final private int healthVarb;
		final private int nameVarc;
	}

	@Getter
	private final HashMap<String, PartyXpItem> partyXp = new HashMap<>();

	private static final HashMap<Skill, Integer> previousXp = new HashMap<>();
	private static final HashMap<Skill, Integer> tickXp = new HashMap<>();

	@Getter
	private final ArrayList<PlayerOrb> playerOrbs = new ArrayList<PlayerOrb>();

	static final List<Skill> COMBAT_SKILLS = ImmutableList.of(
		Skill.ATTACK,
		Skill.STRENGTH,
		Skill.DEFENCE,
		Skill.RANGED,
		Skill.HITPOINTS,
		Skill.MAGIC);

	private static final Set<Integer> RANGED_BOWS = new HashSet<>(Arrays.asList(
			ItemID.TWISTED_BOW, ItemID.BOW_OF_FAERDHINEN, ItemID.BOW_OF_FAERDHINEN_C,
			ItemID.ARMADYL_CROSSBOW, ItemID.RUNE_CROSSBOW, ItemID.DRAGON_CROSSBOW
	));
	private static final Set<Integer> RANGED_THROWN = new HashSet<>(Arrays.asList(
			ItemID.CHINCHOMPA_10033, ItemID.RED_CHINCHOMPA_10034, ItemID.BLACK_CHINCHOMPA,
			ItemID.BLAZING_BLOWPIPE, ItemID.TOXIC_BLOWPIPE
	));
	private static final Set<Integer> POWERED_STAVES = new HashSet<>(Arrays.asList(
			ItemID.SANGUINESTI_STAFF,
			ItemID.TRIDENT_OF_THE_SEAS_FULL,
			ItemID.TRIDENT_OF_THE_SEAS,
			ItemID.TRIDENT_OF_THE_SWAMP,
			ItemID.TRIDENT_OF_THE_SWAMP_E,
			ItemID.HOLY_SANGUINESTI_STAFF,
			ItemID.ACCURSED_SCEPTRE,
			ItemID.WARPED_SCEPTRE
	));
	private static final Set<Integer> SHADOW = new HashSet<>(Arrays.asList(
			ItemID.TUMEKENS_SHADOW,
			ItemID.CORRUPTED_TUMEKENS_SHADOW
	));
	private final int MAIDEN_REGIONID = 12613;

	@Provides
	PartyTobXpDropsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PartyTobXpDropsConfig.class);
	}


	@Override
	protected void startUp() throws Exception
	{
		wsClient.registerMessage(XpDropMessage.class);
		wsClient.registerMessage(Hit.class);
		overlayManager.add(overlay);
		overlayManager.add(partyHitsOverlay);
		eventBus.register(maidenHandler);
		if (client.getGameState() == GameState.LOGGED_IN)
		{
			clientThread.invokeLater(this::loadPreviousXpValues);
		}
		_updateActiveRegions();
		_updateActivePlayers();
	}

	@Override
	protected void shutDown() throws Exception
	{
		wsClient.unregisterMessage(XpDropMessage.class);
		wsClient.unregisterMessage(Hit.class);
		overlayManager.remove(overlay);
		overlayManager.remove(partyHitsOverlay);
		eventBus.unregister(maidenHandler);
		tickXp.clear();
		partyXp.clear();
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
		if (!configChanged.getGroup().equals(PartyTobXpDropsConfig.GROUP))
		{
			return;
		}
		_updateActiveRegions();
		_updateActivePlayers();
		overlay._updateConfigurations();
	}

	@Subscribe
	public void onXpDropMessage(XpDropMessage xpDropMessage)
	{
		if (stopped())
		{
			return;
		}
		clientThread.invoke(() ->
		{
			log.debug("Received message from " + xpDropMessage.getPlayerName() + ", skill: " + xpDropMessage.getSkill() + ", xp : " + xpDropMessage.getXp());
			if (shouldDisplayMessage(xpDropMessage))
			{
				PartyXpItem partyXpItem = new PartyXpItem(xpDropMessage.getSkill(), xpDropMessage.getXp());
				if (config.ticksToKeep() > 0)
				{
					partyXpItem.setCooldown(config.ticksToKeep());
				}
				else
				{
					partyXpItem.setCooldown(xpDropMessage.getCooldown());
				}
				partyXp.put(xpDropMessage.getPlayerName(), partyXpItem);
			}
		});
	}

	@Subscribe
	protected void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (stopped())
		{
			return;
		}
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			clientThread.invokeLater(this::loadPreviousXpValues);
		}
	}

	@Subscribe
	public void onPartyChanged(final PartyChanged event)
	{
		if (inTob)
		{
			clientThread.invokeLater(this::loadPreviousXpValues);
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (client.getLocalPlayer() == null)
		{
			return;
		}
		inTob = client.getVarbitValue(Varbits.THEATRE_OF_BLOOD) > 1;
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		updateInActiveRegion();
		if (stopped())
		{
			return;
		}
		processTickXp();
		updateXpCooldowns();
	}

	@Subscribe
	protected void onStatChanged(StatChanged event)
	{
		if (!COMBAT_SKILLS.contains(event.getSkill()))
		{
			return;
		}
		int xpNow = event.getXp();
		int xpBefore = previousXp.getOrDefault(event.getSkill(), -1);
		if (xpBefore == -1)
		{
			return;
		}
		int xp = xpNow - xpBefore;
		if (xpBefore != 0 && xp > 0)
		{
			processXP(event.getSkill(), xp);

			if (!tickXp.containsKey(event.getSkill()))
			{
				tickXp.merge(event.getSkill(), xp, Integer::sum);
			}
		}
		previousXp.put(event.getSkill(), event.getXp());
	}

	@Subscribe
	protected void onFakeXpDrop(FakeXpDrop event)
	{
		if (!COMBAT_SKILLS.contains(event.getSkill()))
		{
			return;
		}
		int xp = event.getXp();
		if (xp >= 20000000)
		{
			return;
		}
		processXP(event.getSkill(), xp);

		tickXp.merge(event.getSkill(), xp, Integer::sum);
	}

	private void processXP(Skill skill, int xpDrop)
	{
		if (!inTob)
			return;

		if (config.showMaidenHp() && !inMaidenRegion())
			return;

		if (skill == Skill.HITPOINTS)
		{
			Player player = client.getLocalPlayer();
			if (player == null)
				return;

			Actor actor = player.getInteracting();
			if (actor instanceof NPC)
			{
				int npcId = ((NPC) actor).getId();

				int dmg = xpToDamage.calculateHit(npcId, xpDrop);
				if (dmg > 0)
				{
					int projectileDelay = 0;
					if (Objects.equals(actor.getName(), "The Maiden of Sugadinti"))
					{
						WorldPoint maidenLoc = actor.getWorldLocation();
						int minDistance = 10;

						for (int x = 0; x < 6; x++)
						{
							for (int y = 0; y < 6; y++)
							{
								WorldPoint tileLocation = new WorldPoint(maidenLoc.getX() + x, maidenLoc.getY() + y, maidenLoc.getPlane());
								int distance = player.getWorldLocation().distanceTo(tileLocation);

								if (distance < minDistance)
								{
									minDistance = distance;
								}
							}
						}
						projectileDelay = getTickDelay(minDistance);
					}

					Hit hit = new Hit(dmg, player.getName(), projectileDelay);
					sendHit(hit);
					if (config.showMaidenHp() && !config.syncHits())
						maidenHandler.queueDamage(hit, true);
				}
			}
		}
	}

	@Subscribe
	protected void onHit(Hit hit)
	{
		boolean isLocalPlayer = Objects.equals(hit.getPlayer(), client.getLocalPlayer().getName());

		if (config.showMaidenHp() && inMaidenRegion())
		{
			if (!isLocalPlayer || config.syncHits())
			{
				maidenHandler.queueDamage(hit, false);
			}
		}

		if (config.partyHits() && !isLocalPlayer)
		{
			partyHitsOverlay.addHit(hit, config.duration());
		}
	}

	@Subscribe
	protected void onNpcSpawned(NpcSpawned event)
	{
		if (!inTob || !inMaidenRegion())
			return;

		NPC npc = event.getNpc();
		int npcId = npc.getId();
		switch (npcId)
		{
			case NpcID.THE_MAIDEN_OF_SUGADINTI: // regular mode
			case NpcID.THE_MAIDEN_OF_SUGADINTI_10822: // hard mode
			case NpcID.THE_MAIDEN_OF_SUGADINTI_10814: // entry mode
				if (config.showMaidenHp())
					maidenHandler.init(npc);
				break;
		}
	}

	@Subscribe
	protected void onNpcDespawned(NpcDespawned event)
	{
		if (!inTob || !inMaidenRegion())
			return;

		String npcName = event.getNpc().getName();
		if (Objects.equals(npcName, "The Maiden of Sugadinti"))
		{
			if (maidenHandler.isMaidenActive())
				maidenHandler.deactivate();
		}
	}

	private int getTickDelay(int distance)
	{
		Player player = client.getLocalPlayer();
		if (player == null)
			return 0;

		PlayerComposition playerComposition = player.getPlayerComposition();
		if (playerComposition == null)
			return 0;

		int weaponUsed = playerComposition.getEquipmentId(KitType.WEAPON);
		if (RANGED_BOWS.contains(weaponUsed))
		{
			return (int) Math.floor((3 + distance) / 6.0) + 2;
		}
		else if (RANGED_THROWN.contains(weaponUsed))
		{
			return (int) Math.floor(distance / 6.0) + 2;
		}
		else if (POWERED_STAVES.contains(weaponUsed))
		{
			return (int) Math.floor((1 + distance) / 3.0) + 2;
		}
		else if (SHADOW.contains(weaponUsed))
		{
			return (int) Math.floor((1 + distance) / 3.0) + 3;
		}
		else if (weaponUsed == ItemID.ZARYTE_CROSSBOW)
		{
			return 3; // zcb spec has a set projectile delay of 3, later differentiate between auto & spec
		}
		else if (weaponUsed == ItemID.TONALZTICS_OF_RALOS)
		{
			return 2;
		}
		return 1; // Assuming all other weapons have a delay of 1, later fix for multi tick weapons like claws/dualies
	}

	private void sendHit(Hit hit)
	{
		if (partyService.isInParty())
		{
			if (config.ownHits() && Objects.equals(hit.getPlayer(), client.getLocalPlayer().getName()))
				partyHitsOverlay.addHit(hit, config.duration());

			clientThread.invokeLater(() -> partyService.send(hit));
		}
	}

	public void _updateActiveRegions()
	{
		activeRegions.clear();
		if ((config.showMaiden()) | (config.showMaidenCrabs()))
		{
			activeRegions.add(TobRegion.MAIDEN_REGION);
		}
		if (config.showBloat())
		{
			activeRegions.add(TobRegion.BLOAT_REGION);
		}
		if (config.showNylocas())
		{
			activeRegions.add(TobRegion.NYLO_REGION);
		}
		if (config.showSotetseg())
		{
			activeRegions.add(TobRegion.SOTETSEG_REGION);
			activeRegions.add(TobRegion.SOTETSEG_UNDER_REGION);
		}
		if (config.showXarpus())
		{
			activeRegions.add(TobRegion.XARPUS_REGION);
		}
		if (config.showVerzik())
		{
			activeRegions.add(TobRegion.VERZIK_REGION);
		}
	}

	private void _updateActivePlayers()
	{
		playerOrbs.clear();
		if (config.showPlayer1())
		{
			playerOrbs.add(new PlayerOrb(6442, 330));
		}
		if (config.showPlayer2())
		{
			playerOrbs.add(new PlayerOrb(6443, 331));
		}
		if (config.showPlayer3())
		{
			playerOrbs.add(new PlayerOrb(6444, 332));
		}
		if (config.showPlayer4())
		{
			playerOrbs.add(new PlayerOrb(6445, 333));
		}
		if (config.showPlayer5())
		{
			playerOrbs.add(new PlayerOrb(6446, 334));
		}
	}

	private void processTickXp()
	{
		if (tickXp.containsKey(Skill.MAGIC))
		{
			if (tickXp.size() == 1)
			{
				tickXp.clear();
				return;
			}
			if ((tickXp.containsKey(Skill.STRENGTH)) | (tickXp.containsKey(Skill.ATTACK)) | (tickXp.containsKey(Skill.RANGED)))
			{
				tickXp.remove(Skill.MAGIC);
			}
			if (tickXp.containsKey(Skill.DEFENCE))
			{
				tickXp.merge(Skill.MAGIC, tickXp.get(Skill.DEFENCE), Integer::sum);
				tickXp.remove(Skill.DEFENCE);
			}
		}
		if (tickXp.containsKey(Skill.RANGED))
		{
			if (tickXp.containsKey(Skill.DEFENCE))
			{
				tickXp.merge(Skill.RANGED, tickXp.get(Skill.DEFENCE), Integer::sum);
				tickXp.remove(Skill.DEFENCE);
			}
		}

		int hpXp;
		if (tickXp.containsKey(Skill.HITPOINTS))
		{
			hpXp = tickXp.remove(Skill.HITPOINTS);
		}
		else
		{
			hpXp = 0;
		}
		Actor interactingActor = client.getLocalPlayer().getInteracting();
		if (!(interactingActor instanceof NPC))
		{
			return;
		}
		String playerName = client.getLocalPlayer().getName();
		int interactingNpcId = ((NPC) interactingActor).getId();
		tickXp.forEach((skill, xp) -> {
//			onXpDropMessage(new XpDropMessage(playerName, skill, xp + hpXp, interactingNpcId, getWeaponSpeed()));
			partyService.send(new XpDropMessage(playerName, skill, xp + hpXp, interactingNpcId, getWeaponSpeed()));
		});
		tickXp.clear();
	}

	private void updateXpCooldowns()
	{
		Iterator<Map.Entry<String, PartyXpItem>> iterator = partyXp.entrySet().iterator();
		while (iterator.hasNext())
		{
			Map.Entry<String, PartyXpItem> entry = iterator.next();
			PartyXpItem partyXpItem = entry.getValue();
			partyXpItem.cooldown--;
			if (partyXpItem.cooldown == 0)
			{
				iterator.remove();
			}
		}
	}

	public boolean stopped()
	{
		return (!inActiveRegion) | (!partyService.isInParty());
	}

	private void updateInActiveRegion()
	{
		if (client.getTopLevelWorldView().getMapRegions() != null)
		{
			for (int regionId : client.getTopLevelWorldView().getMapRegions())
			{
				if (activeRegions.contains(regionId))
				{
					inActiveRegion = true;
					return;
				}
			}
		}
		inActiveRegion = false;
	}

	private void loadPreviousXpValues()
	{
		int[] xps = client.getSkillExperiences();
		for (Skill skill : COMBAT_SKILLS)
		{
			previousXp.put(skill, xps[skill.ordinal()]);
		}
	}

	private boolean shouldDisplayMessage(XpDropMessage xpDropMessage)
	{
		switch (xpDropMessage.getSkill())
		{
			case STRENGTH:
			case ATTACK:
			case DEFENCE:
				if (!config.showMelee())
				{
					return false;
				}
				break;
			case RANGED:
				if (!config.showRange())
				{
					return false;
				}
				break;
			case MAGIC:
				if (!config.showMagic())
				{
					return false;
				}
				break;
			default:
				return false;
		}
		switch (xpDropMessage.getNpcId())
		{
			case TobNpc.MAIDEN_P1:
			case TobNpc.MAIDEN_P1_HM:
			case TobNpc.MAIDEN_P1_SM:
			case TobNpc.MAIDEN_P2:
			case TobNpc.MAIDEN_P2_HM:
			case TobNpc.MAIDEN_P2_SM:
			case TobNpc.MAIDEN_P3:
			case TobNpc.MAIDEN_P3_HM:
			case TobNpc.MAIDEN_P3_SM:
				if (!config.showMaiden())
				{
					return false;
				}
				break;
			case TobNpc.MAIDEN_MATOMENOS:
			case TobNpc.MAIDEN_MATOMENOS_HM:
			case TobNpc.MAIDEN_MATOMENOS_SM:
				if (!config.showMaidenCrabs())
				{
					return false;
				}
				break;
			case TobNpc.BLOAT:
			case TobNpc.BLOAT_HM:
			case TobNpc.BLOAT_SM:
				if (!config.showBloat())
				{
					return false;
				}
				break;
			case TobNpc.NYLO_BOSS_MELEE:
			case TobNpc.NYLO_BOSS_MELEE_HM:
			case TobNpc.NYLO_BOSS_MELEE_SM:
			case TobNpc.NYLO_BOSS_RANGE:
			case TobNpc.NYLO_BOSS_RANGE_HM:
			case TobNpc.NYLO_BOSS_RANGE_SM:
			case TobNpc.NYLO_BOSS_MAGE:
			case TobNpc.NYLO_BOSS_MAGE_HM:
			case TobNpc.NYLO_BOSS_MAGE_SM:
			case TobNpc.NYLO_PRINKIPAS_MELEE:
			case TobNpc.NYLO_PRINKIPAS_RANGE:
			case TobNpc.NYLO_PRINKIPAS_MAGIC:
				if (!config.showNylocas())
				{
					return false;
				}
				break;
			case TobNpc.SOTETSEG:
			case TobNpc.SOTETSEG_HM:
			case TobNpc.SOTETSEG_SM:
				if (!config.showSotetseg())
				{
					return false;
				}
				break;
			case TobNpc.XARPUS_P23:
			case TobNpc.XARPUS_P23_HM:
			case TobNpc.XARPUS_P23_SM:
				if (!config.showXarpus())
				{
					return false;
				}
				break;
			case TobNpc.VERZIK_P1:
			case TobNpc.VERZIK_P1_HM:
			case TobNpc.VERZIK_P1_SM:
			case TobNpc.VERZIK_P2:
			case TobNpc.VERZIK_P2_HM:
			case TobNpc.VERZIK_P2_SM:
			case TobNpc.VERZIK_P3:
			case TobNpc.VERZIK_P3_HM:
			case TobNpc.VERZIK_P3_SM:
			case TobNpc.VERZIK_MATOMENOS:
			case TobNpc.VERZIK_MATOMENOS_HM:
			case TobNpc.VERZIK_MATOMENOS_SM:
				if (!config.showVerzik())
				{
					return false;
				}
				break;
		}
		if (ArrayUtils.contains(TobNpc.NYLOCAS_NPC_IDS, xpDropMessage.getNpcId()))
		{
			return config.showNylocas();
		}
		return true;
	}

	private int getWeaponSpeed()
	{
		ItemStats weaponStats = getWeaponStats();
		ItemEquipmentStats e = weaponStats.getEquipment();
		int speed = e.getAspeed();
		if (getAttackStyle() == AttackStyle.RANGING &&
			client.getVarpValue(VarPlayer.ATTACK_STYLE) == 1)
		{
			speed -= 1;
		}
		return speed;
	}

	private ItemStats getItemStatsFromContainer(ItemContainer container, int slotID)
	{
		if (container == null)
		{
			return null;
		}
		final Item item = container.getItem(slotID);
		return item != null ? itemManager.getItemStats(item.getId(), false) : null;
	}

	private ItemStats getWeaponStats()
	{
		return getItemStatsFromContainer(client.getItemContainer(InventoryID.EQUIPMENT),
			EquipmentInventorySlot.WEAPON.getSlotIdx());
	}

	private AttackStyle getAttackStyle()
	{
		final int currentAttackStyleVarbit = client.getVarpValue(VarPlayer.ATTACK_STYLE);
		final int currentEquippedWeaponTypeVarbit = client.getVarbitValue(Varbits.EQUIPPED_WEAPON_TYPE);
		AttackStyle[] attackStyles = WeaponType.getWeaponType(currentEquippedWeaponTypeVarbit).getAttackStyles();

		if (currentAttackStyleVarbit < attackStyles.length)
		{
			return attackStyles[currentAttackStyleVarbit];
		}
		return AttackStyle.ACCURATE;
	}

	private boolean inMaidenRegion()
	{
		return ArrayUtils.contains(client.getTopLevelWorldView().getMapRegions(), MAIDEN_REGIONID);
	}
}
