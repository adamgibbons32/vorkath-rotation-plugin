package com.gibbo32.vorkathrotation;

import com.gibbo32.vorkathrotation.config.PanelVisibility;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import static com.gibbo32.vorkathrotation.PluginConstants.CONFIG_KEY_PANEL_VISIBILITY;

@ConfigGroup("vorkathrotation")
public interface VorkathRotationConfig extends Config
{
    @ConfigItem(
            position = 1,
            keyName = CONFIG_KEY_PANEL_VISIBILITY,
            name = "Show panel",
            description = "Determines when the plugin panel is shown."
    )
    default PanelVisibility panelVisibility()
    {
        return PanelVisibility.AtVorkath;
    }
}
