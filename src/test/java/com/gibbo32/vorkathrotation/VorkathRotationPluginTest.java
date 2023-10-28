package com.gibbo32.vorkathrotation;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class VorkathRotationPluginTest
{
    public static void main(String[] args) throws Exception
    {
        ExternalPluginManager.loadBuiltin(VorkathRotationPlugin.class);
        RuneLite.main(args);
    }
}