/*
 * MIT License
 *
 * Copyright (c) 2020 Azercoco & Technici4n
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package aztech.modern_industrialization.machines.components;

import com.google.common.collect.Maps;
import org.jetbrains.annotations.Nullable;
import top.focess.mc.mi.nuclear.mc.*;
import top.focess.mc.mi.nuclear.mi.MINuclearInventory;

import java.util.Map;

public class SteamHeaterComponent extends TemperatureComponent {

    private static final int STEAM_TO_WATER = 16;

    /**
     * mb/t of steam produced at max heat, assuming enough water
     */
    public final long maxEuProduction;
    /**
     * How many eu in one degree of heat.
     */
    public final long euPerDegree;

    public final boolean acceptHighPressure;
    public final boolean acceptLowPressure;

    public SteamHeaterComponent(double temperatureMax, long maxEuProduction, long euPerDegree) {
        super(temperatureMax);
        this.maxEuProduction = maxEuProduction;
        this.euPerDegree = euPerDegree;
        this.acceptLowPressure = true;
        this.acceptHighPressure = false;
    }

    public SteamHeaterComponent(double temperatureMax, long maxEuProduction, long euPerDegree, boolean acceptLowPressure,
                                boolean acceptHighPressure) {
        super(temperatureMax);
        this.maxEuProduction = maxEuProduction;
        this.euPerDegree = euPerDegree;
        this.acceptLowPressure = acceptLowPressure;
        this.acceptHighPressure = acceptHighPressure;
    }

    // return eu produced
    public double tick(InputMatterHolder fluidInputs, MINuclearInventory fluidOutputs) {

        double euProducedLowPressure = 0;
        if (acceptLowPressure) {
            euProducedLowPressure = tryMakeSteam(fluidInputs, fluidOutputs, Fluids.WATER, Fluids.STEAM, 1);
            if (euProducedLowPressure == 0) {
                euProducedLowPressure = tryMakeSteam(fluidInputs, fluidOutputs, Fluids.HEAVY_WATER, Fluids.HEAVY_WATER_STEAM, 1);
            }
        }

        double euProducedHighPressure = 0;
        if (acceptHighPressure) {
            euProducedHighPressure = tryMakeSteam(fluidInputs, fluidOutputs, Fluids.HIGH_PRESSURE_WATER, Fluids.HIGH_PRESSURE_STEAM, 8);
            if (euProducedHighPressure == 0) {
                euProducedHighPressure = tryMakeSteam(fluidInputs, fluidOutputs, Fluids.HIGH_PRESSURE_HEAVY_WATER,
                        Fluids.HIGH_PRESSURE_HEAVY_WATER_STEAM, 8);
            }
        }
        return euProducedLowPressure + euProducedHighPressure;
    }

    private double tryMakeSteam(InputMatterHolder input, MINuclearInventory outputInventory, Fluid water, Fluid steam, int euPerSteamMb) {
        FluidVariant waterKey = FluidVariant.of(water);
        FluidVariant steamKey = FluidVariant.of(steam);

        if (getTemperature() > 100d) {
            long steamProduction = (long) (81 * (getTemperature() - 100d) / (temperatureMax - 100d) * maxEuProduction / euPerSteamMb);
            long extracted = input.tryExtract(waterKey, steamProduction / STEAM_TO_WATER);
            long output = outputInventory.tryOutput(steamKey, extracted * STEAM_TO_WATER);
            if (output > 0) {
                long actual = output / STEAM_TO_WATER;
                if (actual != input.extract(waterKey, actual))
                    throw new IllegalStateException("Extract failed");
                else if (output != outputInventory.output(steamKey, output))
                    throw new IllegalStateException("Output failed");
                else {
                    double euProduced = extracted * STEAM_TO_WATER * euPerSteamMb / 81d;
                    decreaseTemperature(euProduced / euPerDegree);
                    return euProduced;
                }
            }
        }
        return 0;
    }

    @Nullable
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("maxEuProduction", maxEuProduction);
        map.put("euPerDegree", euPerDegree);
        map.put("acceptLowPressure", acceptLowPressure);
        map.put("acceptHighPressure", acceptHighPressure);
        map.put("temperatureMax", temperatureMax);
        map.put("temperature", getTemperature());
        return map;
    }

    public static SteamHeaterComponent deserialize(Map<String, Object> map) {
        SteamHeaterComponent ret = new SteamHeaterComponent((double) map.get("temperatureMax"), (long) map.get("maxEuProduction"),
                (long) map.get("euPerDegree"), (boolean) map.get("acceptLowPressure"), (boolean) map.get("acceptHighPressure"));
        ret.setTemperature((double) map.get("temperature"));
        return ret;
    }
}
