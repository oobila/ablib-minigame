package com.github.oobila.bukkit.minigame.build;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class Build {

    private UUID id = UUID.randomUUID();
    private List<BuildStep> steps;

    public void addStep(BuildStep buildStep) {
        steps.add(buildStep);
        buildStep.build = this;
    }

}
