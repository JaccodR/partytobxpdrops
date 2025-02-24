package com.partytobxpdrops.maiden;

import com.partytobxpdrops.PartyTobXpDropsConfig;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

public class MaidenOverlay extends Overlay
{
    private final PartyTobXpDropsConfig config;
    private final MaidenHandler maidenHandler;
    private double lastRenderedHp = 100.0;

    @Inject
    public MaidenOverlay(MaidenHandler maidenHandler, PartyTobXpDropsConfig config)
    {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.UNDER_WIDGETS);
        this.maidenHandler = maidenHandler;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (maidenHandler.isMaidenActive() && maidenHandler.getMaidenNpc() != null)
        {
            NPC maiden = maidenHandler.getMaidenNpc();
            double maidenHp = maidenHandler.getPredictedHpPercent();
            if (maidenHp < 0)
                maidenHp = 0;

            double threshold = config.updateThreshold();
            if (Math.abs(maidenHp - lastRenderedHp) >= threshold)
            {
                lastRenderedHp = maidenHp;
            }
            String hpText = String.format("%.1f", lastRenderedHp);

            Point pt = maiden.getCanvasTextLocation(graphics, hpText,config.maidenOffset() * 5);
            if (pt != null)
            {
				graphics.setFont(new Font(config.maidenFont().getName(), Font.BOLD, config.maidenSize()));
				int x = pt.getX() + config.maidenHorOffset();
				int y = pt.getY();

				graphics.setColor(new Color(0,0,0, config.maidenColor().getAlpha()));
				graphics.drawString(hpText, x + 1, y + 1);

				graphics.setColor(config.maidenColor());
				graphics.drawString(hpText, x, y);
            }
        }
        return null;
    }
}
