package com.partytobxpdrops;

import com.partytobxpdrops.misc.Hit;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class PartyHitsOverlay extends Overlay
{
    private final PartyTobXpDropsPlugin plugin;
    private final PartyTobXpDropsConfig config;
    @Inject
    private Client client;
    private final Map<Hit, Integer> hits = new HashMap<>();

    @Inject
    PartyHitsOverlay(PartyTobXpDropsPlugin plugin, PartyTobXpDropsConfig config)
    {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.UNDER_WIDGETS);
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
		graphics.setFont(new Font(config.partyHitFont().toString(), Font.BOLD, config.size()));

        Map<Hit, Integer> updatedHits = new HashMap<>();

        Iterator<Map.Entry<Hit, Integer>> iterator = hits.entrySet().iterator();

        while (iterator.hasNext())
        {
            Map.Entry<Hit, Integer> entry = iterator.next();
            Hit hit = entry.getKey();
            int duration = entry.getValue();
            if (duration <= 0)
            {
                iterator.remove();
                continue;
            }
            renderHit(graphics, hit);
            updatedHits.put(hit, duration - 1);
        }
        hits.putAll(updatedHits);

        return null;
    }

    private void renderHit(Graphics2D graphics, Hit hit)
    {
        String targetName = hit.getPlayer();
        Player target = null;
        for (Player p : client.getTopLevelWorldView().players())
        {
            if (Objects.equals(p.getName(), targetName))
            {
                target = p;
            }
        }

        if (target == null)
            return;

        String damageText = String.valueOf(hit.getDamage());
        Point pt = target.getCanvasTextLocation(graphics, damageText, config.offset() * 10);
        if (pt != null)
        {
			int x = pt.getX() + config.horOffset();
			int y = pt.getY();

			graphics.setColor(new Color(0,0,0, config.color().getAlpha()));
			graphics.drawString(damageText, x + 1, y + 1);

            graphics.setColor(config.color());
            graphics.drawString(damageText, x, y);
        }
    }

    public void addHit(Hit hit, int duration)
    {
        hits.keySet().removeIf(existingHit -> existingHit.getPlayer().equals(hit.getPlayer())); // if a player still has a hit, remove it

        hits.put(hit, duration);
    }
}
