package top.focess.mc.mi.nuclear;

import top.focess.mc.mi.nuclear.mc.ItemVariant;
import top.focess.mc.mi.nuclear.mi.MIItems;
import top.focess.mc.mi.nuclear.mi.NuclearReactionType;
import top.focess.scheduler.ThreadPoolScheduler;
import top.focess.util.yaml.YamlConfiguration;

import java.io.File;
import java.time.Duration;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        Class.forName("top.focess.mc.mi.nuclear.mi.MIItems");
        ThreadPoolScheduler scheduler = new ThreadPoolScheduler(1, false, "Tick");
        NuclearSimulation simulation = new NuclearSimulation((i, j) -> {
            if (i == 2 && j == 2) {
                return ItemVariant.of(MIItems.HE_MOX_FUEL_ROD);
            } else return ItemVariant.blank();
        }, NuclearReactionType.SIMULATION_3X3);
        scheduler.runTimer(simulation::tick, Duration.ZERO, Duration.ofMillis(50), "simulation", Throwable::printStackTrace);
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String next = scanner.next();
            if (next.equals("stop"))
                break;
        }
        scanner.close();
        scheduler.close();
        YamlConfiguration yamlConfiguration = new YamlConfiguration(null);
        yamlConfiguration.set("simulation", simulation);
        yamlConfiguration.save(new File("test.yml"));
        YamlConfiguration yamlConfiguration1 = YamlConfiguration.loadFile(new File("test.yml"));
        NuclearSimulation simulation1 = yamlConfiguration1.get("simulation");
        System.out.println(simulation.equals(simulation1));
    }

}
