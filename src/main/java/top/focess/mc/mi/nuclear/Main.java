package top.focess.mc.mi.nuclear;

import top.focess.scheduler.FocessScheduler;
import top.focess.scheduler.Scheduler;
import top.focess.scheduler.ThreadPoolScheduler;

import java.time.Duration;

public class Main {

    public static void tick() {

    }

    public static void main(String[] args) {
        Scheduler scheduler = new ThreadPoolScheduler(1, false, "Tick");
        scheduler.runTimer(Main::tick, Duration.ZERO,Duration.ofMillis(50));
    }
}
