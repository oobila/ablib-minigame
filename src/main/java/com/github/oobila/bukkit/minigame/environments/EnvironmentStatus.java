package com.github.oobila.bukkit.minigame.environments;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EnvironmentStatus {
    OPEN("6bff1b0cc0673ec3c6e8172bce0b651eb1004ceef3e890427d1ca6f36adc4efb"),
    CLOSING("e04289a75f53bf66ecea2efc92e2b6847872e718939642bcaf9cdd8854707cfb"),
    CLOSED("ca5b2ef596c2c8c48cd440a4a2a152036798429ccced741f8829a014d2d5ddef");

    private final String texture;
}
