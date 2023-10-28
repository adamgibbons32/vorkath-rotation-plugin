package com.gibbo32.vorkathrotation.ui;

import com.gibbo32.vorkathrotation.VorkathRotationPlugin;
import com.gibbo32.vorkathrotation.ui.components.Button;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

@Slf4j
public class VorkathRotationPanel extends PluginPanel
{
    private final VorkathRotationPlugin plugin;

    private final JPanel contentPanel;

    private final JPanel activeView;
    private final JPanel inactiveView;

    private final ArrayList<Button> activeButtons = new ArrayList<>();
    private final ArrayList<Button> inactiveButtons = new ArrayList<>();

    private JLabel specialLabel;

    @Inject
    public VorkathRotationPanel(VorkathRotationPlugin plugin)
    {
        super(false);
        this.plugin = plugin;

        JLabel title = new JLabel("Vorkath Rotation");
        title.setBorder(new EmptyBorder(0, 0, BORDER_OFFSET, 0));
        title.setForeground(Color.WHITE);

        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(BORDER_OFFSET, BORDER_OFFSET, BORDER_OFFSET, BORDER_OFFSET));
        add(title, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        activeView = createActiveView();
        inactiveView = createInactiveView();
    }

    private JPanel createActiveView()
    {
        JPanel activePanel = new JPanel();
        activePanel.setLayout(new BorderLayout());

        // North
        JLabel activeSpecial = createActiveSpecialImage();
        activePanel.add(activeSpecial, BorderLayout.NORTH);

        // Center
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        Button resetButton = createResetButton();
        centerPanel.add(resetButton, BorderLayout.NORTH);

        activePanel.add(centerPanel, BorderLayout.CENTER);


        return activePanel;
    }

    private JPanel createInactiveView()
    {
        JPanel inactivePanel = new JPanel();
        inactivePanel.setLayout(new BorderLayout());

        // North
        JLabel instructionLabel = new JLabel("<html>Select the Acid Pool or Spawn icon to start the rotation. Select the Reset button to stop the rotation timer.</html>");
        instructionLabel.setBorder(new EmptyBorder(BORDER_OFFSET, 0, BORDER_OFFSET, 0));
        inactivePanel.add(instructionLabel, BorderLayout.NORTH);

        // Center
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(createButtonPanel(), BorderLayout.NORTH);

        inactivePanel.add(centerPanel, BorderLayout.CENTER);

        return inactivePanel;
    }

    private JPanel createButtonPanel()
    {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());

        Button spawnButton = createSpawnButton();
        inactiveButtons.add(spawnButton);
        buttonPanel.add(spawnButton, BorderLayout.EAST);

        Button acidPoolButton = createAcidPoolButton();
        inactiveButtons.add(acidPoolButton);
        buttonPanel.add(acidPoolButton, BorderLayout.WEST);

        return buttonPanel;
    }

    private Button createAcidPoolButton()
    {
        Button button = new Button(new ImageIcon(ImageUtil.loadImageResource(getClass(), "/vorkathAcid.png")));
        button.addMouseButton1PressedHandler(() -> plugin.start(false));
        button.setPreferredSize(new Dimension(PANEL_WIDTH / 2 + BORDER_OFFSET / 2, 100));
        return button;
    }

    private Button createSpawnButton()
    {
        Button button = new Button(new ImageIcon(ImageUtil.loadImageResource(getClass(), "/spawn.png")));
        button.addMouseButton1PressedHandler(() -> plugin.start(true));
        button.setPreferredSize(new Dimension(PANEL_WIDTH / 2 + BORDER_OFFSET / 2, 100));
        return button;
    }

    private JLabel createActiveSpecialImage()
    {
        specialLabel = new JLabel(new ImageIcon(ImageUtil.loadImageResource(getClass(), "/spawn.png")));
        specialLabel.setPreferredSize(new Dimension(PANEL_WIDTH / 2 + BORDER_OFFSET / 2, 200));
        return specialLabel;
    }

    private Button createResetButton()
    {
        Button button = new Button("Reset");
        button.setPreferredSize(new Dimension(PANEL_WIDTH, 100));
        button.addMouseButton1PressedHandler(plugin::reset);
        return button;
    }

    public void setSpecial(String nextSpecial)
    {
        SwingUtilities.invokeLater(() ->
        {
            Icon specialIcon = new ImageIcon();
            if (nextSpecial.equals("spawn"))
            {
                specialIcon = new ImageIcon(ImageUtil.loadImageResource(getClass(), "/spawn.png"));
            }
            else
            {
                specialIcon = new ImageIcon(ImageUtil.loadImageResource(getClass(), "/vorkathAcid.png"));
            }

            specialLabel.setIcon(specialIcon);
            specialLabel.repaint();
        });
    }

    public void setCounterActiveState(boolean active)
    {
        SwingUtilities.invokeLater(() ->
        {
            contentPanel.removeAll();
            contentPanel.add(active ? activeView : inactiveView, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();

            // The styling of buttons is messed up because they are removed from
            // the view when switching between active and not active. Reset the
            // styling to prevent this behaviour.
            if (active)
            {
                for (Button button : inactiveButtons)
                {
                    button.resetStyling();
                }
            }
            else
            {
                for (Button button : activeButtons)
                {
                    button.resetStyling();
                }
            }
        });
    }
}
