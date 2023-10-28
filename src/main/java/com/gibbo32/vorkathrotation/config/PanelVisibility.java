package com.gibbo32.vorkathrotation.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PanelVisibility {
    Always("Always"),
    AtVorkath("At Vorkath"),
    Never("Never");

    private final String name;

    @Override
    public String toString()
    {
        return name;
    }
}
