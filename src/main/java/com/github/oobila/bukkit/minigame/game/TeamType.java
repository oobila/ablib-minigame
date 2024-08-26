package com.github.oobila.bukkit.minigame.game;

import com.github.oobila.bukkit.common.utils.model.BlockColor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TeamType {

    private String name;
    private TeamColor teamColor;

    @AllArgsConstructor
    @Getter
    public enum TeamColor {
        WHITE(BlockColor.WHITE),
        GRAY(BlockColor.GRAY),
        RED(BlockColor.RED),
        YELLOW(BlockColor.YELLOW),
        GREEN(BlockColor.LIME),
        CYAN(BlockColor.LIGHT_BLUE),
        PINK(BlockColor.PINK);

        private BlockColor blockColor;
    }

}
