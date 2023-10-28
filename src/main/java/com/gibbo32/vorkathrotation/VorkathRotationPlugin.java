package com.gibbo32.vorkathrotation;

import com.gibbo32.vorkathrotation.ui.VorkathRotationPanel;
import com.google.inject.Provides;
import javax.inject.Inject;
import javax.swing.*;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.gibbo32.vorkathrotation.PluginConstants.*;


@Slf4j
@PluginDescriptor(
        name = "Vorkath Rotation"
)
public class VorkathRotationPlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    private VorkathRotationConfig config;

    @Inject
    private ConfigManager configManager;

    @Inject
    private ClientToolbar clientToolbar;

    private NavigationButton navButton;

    private VorkathRotationPanel panel;

    private boolean started;

    private boolean isSpawn;

    private int counter;

    private boolean isPanelVisible;

    private ScheduledExecutorService executorService;

    @Override
    protected void startUp()
    {
        log.info("Vorkath plugin started!");

        panel = injector.getInstance(VorkathRotationPanel.class);
        panel.setCounterActiveState(false);

        navButton = NavigationButton.builder()
                .tooltip("Vorkath Rotation")
                .priority(100)
                .icon(ImageUtil.loadImageResource(getClass(), "/icon.png"))
                .panel(panel)
                .build();

        updatePanelVisibility(false);
    }

    public void start(boolean withSpawn)
    {
        started = true;
        isSpawn = withSpawn;

        if (withSpawn)
        {
            panel.setSpecial("spawn");
        }
        else
        {
            panel.setSpecial("acid");
        }
        panel.setCounterActiveState(started);
        counter = 0;

        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(this::tickCounter, 0, COUNTER_INTERVAL, TimeUnit.SECONDS);
    }

    private void tickCounter()
    {
        counter -= COUNTER_INTERVAL;
        if (counter <= 0)
        {
            if (isSpawn)
            {
                panel.setSpecial("acid");
                counter += ACID_ROTATION_DURATION;
            }
            else
            {
                panel.setSpecial("spawn");
                counter += SPAWN_ROTATION_DURATION;
            }

            isSpawn = !isSpawn;
        }
    }

    public void reset()
    {
        started = false;
        shutdownExecutorService();
        panel.setCounterActiveState(started);
    }

    @Override
    protected void shutDown() throws Exception
    {
        log.info("Vorkath plugin stopped!");
        updateNavigationBar(false, false);
        clientToolbar.removeNavigation(navButton);
        navButton = null;
        panel = null;
    }

    @Subscribe
    public void onGameTick(GameTick tick)
    {
        updatePanelVisibility(true);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        switch (event.getKey())
        {
            case CONFIG_KEY_PANEL_VISIBILITY:
                updatePanelVisibility(false);
                break;
        }
    }

    private void shutdownExecutorService()
    {
        if (executorService != null)
        {
            executorService.shutdownNow();
            try
            {
                if (!executorService.awaitTermination(100, TimeUnit.MILLISECONDS))
                {
                    log.warn("Executor service dit not shut down within the allocated timeout.");
                }
            }
            catch (InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
            executorService = null;
        }
    }

    private void updatePanelVisibility(boolean selectPanel)
    {
        boolean panelShouldBeVisible = shouldShowPanel();

        if (panelShouldBeVisible != isPanelVisible)
        {
            updateNavigationBar(panelShouldBeVisible, selectPanel);
            isPanelVisible = panelShouldBeVisible;
        }
    }

    private boolean shouldShowPanel()
    {
        switch (config.panelVisibility())
        {
            case Always: return true;
            case AtVorkath: return isAtVorkath();
            case Never:
            default: return false;
        }
    }

    private boolean isAtVorkath()
    {
        Player player = client.getLocalPlayer();

        if (player == null)
        {
            return false;
        }

        WorldPoint playerLocation = WorldPoint.fromLocalInstance(client, player.getLocalLocation());
        int regionId = playerLocation.getRegionID();

        if (regionId != VORKATH_REGION_ID)
        {
            return false;
        }

        System.out.println("Region ID: " + regionId);


        int playerX = playerLocation.getRegionX();
        int playerY = playerLocation.getRegionY();
        System.out.println("Player X: " + playerX);
        System.out.println("Player Y: " + playerY);

        return playerX >= VORKATH_ROOM_X_MIN && playerX <= VORKATH_ROOM_X_MAX
                && playerY >= VORKATH_ROOM_Y_MIN && playerY <= VORKATH_ROOM_Y_MAX;
    }

    private void updateNavigationBar(boolean enable, boolean selectPanel)
    {
        if (enable)
        {
            clientToolbar.addNavigation(navButton);
            if (selectPanel)
            {
                SwingUtilities.invokeLater(() ->
                {
                    if (!navButton.isSelected())
                    {
                        navButton.getOnSelect().run();
                    }
                });
            }
        }
        else
        {
            reset();
            navButton.setSelected(false);
            clientToolbar.removeNavigation(navButton);
        }
    }

    @Provides
    VorkathRotationConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(VorkathRotationConfig.class);
    }
}
