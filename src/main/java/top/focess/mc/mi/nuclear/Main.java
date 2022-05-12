package top.focess.mc.mi.nuclear;

import top.focess.mc.mi.nuclear.mc.ItemVariant;
import top.focess.mc.mi.nuclear.mi.MIItems;
import top.focess.scheduler.ThreadPoolScheduler;

import java.time.Duration;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName("top.focess.mc.mi.nuclear.mi.MIItems");
        ThreadPoolScheduler scheduler = new ThreadPoolScheduler(1, false, "Tick");
        NuclearSimulation simulation = new NuclearSimulation(5, (i,j)->{
            if (i == 2 && j == 2) {
                return ItemVariant.of(MIItems.HE_MOX_FUEL_ROD);
            } else return ItemVariant.blank();
        });
        scheduler.runTimer(simulation::tick, Duration.ZERO,Duration.ofMillis(50), "simulation", Throwable::printStackTrace);
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
