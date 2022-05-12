package top.focess.mc.mi.nuclear;

import top.focess.scheduler.FocessScheduler;
import top.focess.scheduler.Scheduler;
import top.focess.scheduler.ThreadPoolScheduler;

import java.time.Duration;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scheduler scheduler = new ThreadPoolScheduler(1, false, "Tick");
        NuclearSimulation simulation = new NuclearSimulation(5);
        scheduler.runTimer(simulation::tick, Duration.ZERO,Duration.ofMillis(50));
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String next = scanner.next();
            if (next.equals("stop"))
                break;
        }
        scanner.close();
        scheduler.close();
    }
}
