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
package aztech.modern_industrialization.nuclear;

import top.focess.mc.mi.nuclear.mc.Item;
import top.focess.mc.mi.nuclear.mc.ItemVariant;
import top.focess.mc.mi.nuclear.mc.MatterHolder;

import java.util.Random;

public class NuclearFuel extends NuclearAbsorbable {

    public final double directEnergyFactor;
    public final double neutronMultiplicationFactor;

    public final String depletedVersionId;

    public final int size;

    public final int directEUbyDesintegration;
    public final int totalEUbyDesintegration;

    public final int tempLimitLow;
    public final int tempLimitHigh;

    public NuclearFuel(String name, int maxCount, NuclearFuelParams params, INeutronBehaviour neutronBehaviour, String depletedVersionId) {

        this(name, maxCount, params.desintegrationMax, params.maxTemperature, params.tempLimitLow, params.tempLimitHigh, params.neutronMultiplicationFactor,
                params.directEnergyFactor, neutronBehaviour, params.size, depletedVersionId);

    }

    private NuclearFuel(String name, int maxCount, int desintegrationMax, int maxTemperature, int tempLimitLow, int tempLimitHigh,
                        double neutronMultiplicationFactor, double directEnergyFactor, INeutronBehaviour neutronBehaviour, int size, String depletedVersionId) {

        super(name, maxCount, clampTemp(maxTemperature), 0.8 * NuclearConstant.BASE_HEAT_CONDUCTION, neutronBehaviour, desintegrationMax);

        this.size = size;
        this.directEnergyFactor = directEnergyFactor;
        this.neutronMultiplicationFactor = neutronMultiplicationFactor;
        this.depletedVersionId = depletedVersionId;

        this.tempLimitLow = clampTemp(tempLimitLow);
        this.tempLimitHigh = clampTemp(tempLimitHigh);

        this.directEUbyDesintegration = (int) (NuclearConstant.EU_FOR_FAST_NEUTRON * directEnergyFactor * neutronMultiplicationFactor);
        this.totalEUbyDesintegration = (int) (NuclearConstant.EU_FOR_FAST_NEUTRON * (1.0 + directEnergyFactor) * neutronMultiplicationFactor);

    }

    private static int clampTemp(int temperature) {
        return 25 * (int) (temperature / 25d);
    }

    public static NuclearFuel of(String id, NuclearFuelParams params, INeutronBehaviour neutronBehaviour, String depletedVersionId) {

        return new NuclearFuel(id, 1, params, neutronBehaviour, depletedVersionId);
    }

    @Override
    public ItemVariant getNeutronProduct() {
        return ItemVariant.of(Item.getItem("modern-industrialization", depletedVersionId));
    }

    @Override
    public long getNeutronProductAmount() {
        return size;
    }

    public double efficiencyFactor(double temperature) {
        double factor = 1;
        if (temperature > tempLimitLow) {
            factor = Math.max(0, 1 - (temperature - tempLimitLow) / (tempLimitHigh - tempLimitLow));
        }
        return factor;
    }

    public int simulateDesintegration(double neutronsReceived, MatterHolder stack, double temperature, Random rand, INuclearGrid grid) {
        int absorption = simulateAbsorption(neutronsReceived, stack, rand);
        double fuelEuConsumed = absorption * totalEUbyDesintegration;
        grid.registerEuFuelConsumption(fuelEuConsumed);
        return randIntFromDouble(efficiencyFactor(temperature) * absorption * neutronMultiplicationFactor, rand);
    }

    public record NuclearFuelParams(int desintegrationMax, int maxTemperature, int tempLimitLow, int tempLimitHigh,
                                    double neutronMultiplicationFactor, double directEnergyFactor, int size) {
    }

}
