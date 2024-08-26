package com.github.oobila.bukkit.minigame.build;

import com.github.oobila.bukkit.common.scheduling.Job;
import lombok.Getter;

import java.util.UUID;

@Getter
public class BuildStep extends Job {

    private final UUID id = UUID.randomUUID();
    private final Job job;
    Build build;
    private BuildStepStatus status = BuildStepStatus.WAITING;

    public BuildStep(Job job) {
        this.job = job;
    }

    @Override
    public void run() {
        status = BuildStepStatus.RUNNING;
        job.run();
        status = BuildStepStatus.COMPLETE;
    }

    public enum BuildStepStatus {
        WAITING,
        RUNNING,
        COMPLETE;
    }

}
