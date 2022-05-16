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

import top.focess.mc.mi.nuclear.mc.Fluid;
import top.focess.mc.mi.nuclear.mc.FluidVariant;
import top.focess.mc.mi.nuclear.mc.Fluids;
import top.focess.mc.mi.nuclear.mc.MatterHolder;
import top.focess.mc.mi.nuclear.mi.MINuclearInventory;

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
    public double tick(MatterHolder fluidInputs, MINuclearInventory fluidOutputs) {

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

    private double tryMakeSteam(MatterHolder input, MINuclearInventory output, Fluid water, Fluid steam, int euPerSteamMb) {
        FluidVariant waterKey = FluidVariant.of(water);
        FluidVariant steamKey = FluidVariant.of(steam);

        if (getTemperature() > 100d) {
            long steamProduction = (long) (81 * (getTemperature() - 100d) / (temperatureMax - 100d) * maxEuProduction / euPerSteamMb);
            long extracted = input.extract(waterKey, steamProduction / STEAM_TO_WATER);
            if (output.output(steamKey, extracted * STEAM_TO_WATER) != extracted * STEAM_TO_WATER)
                throw new IllegalStateException("Steam Component : Logic bug: failed to insert");
        }
        return 0;

    }
}
