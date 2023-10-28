package com.gibbo32.vorkathrotation.ui.components;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Button extends JButton
{

    public Button(String text)
    {
        super(text);
        setFocusable(false);
    }

    public Button(Icon icon)
    {
        super(icon);
        setFocusable(false);
    }

    public void addMouseButton1PressedHandler(Runnable callback)
    {
        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                if (e.getButton() == MouseEvent.BUTTON1)
                {
                    callback.run();
                }
            }
        });
    }

    public void resetStyling()
    {
        setEnabled(false);
        setEnabled(true);
    }
}