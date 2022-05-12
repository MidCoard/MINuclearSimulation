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

import com.google.common.base.Preconditions;
import top.focess.mc.mi.nuclear.mc.MatterHolder;

import java.util.Map;
import java.util.Random;

public class NuclearAbsorbable extends NuclearComponentItem {

    public final int desintegrationMax;

    public NuclearAbsorbable(String name,int maxCount, int maxTemperature, double heatConduction, INeutronBehaviour neutronBehaviour,
            int desintegrationMax) {
        super(name, maxCount,maxTemperature, heatConduction, neutronBehaviour);
        this.desintegrationMax = desintegrationMax;
    }

    public void setRemainingDesintegrations(MatterHolder stack, int value) {
        Preconditions.checkArgument(value >= 0 & value <= desintegrationMax,
                String.format("Remaining desintegration %d must be between 0 and max desintegration = %d", value, desintegrationMax));
        stack.getTag().put("desRem", value);
    }

    public static NuclearComponentItem of(String id, int maxTemperature, double heatConduction, INeutronBehaviour neutronBehaviour,
                                          int desintegrationMax) {
        return new NuclearAbsorbable( id, 1, maxTemperature, heatConduction, neutronBehaviour, desintegrationMax);
    }

    public double getDurabilityBarProgress(MatterHolder stack) {
        return (double) getRemainingDesintegrations(stack) / desintegrationMax;

    }

    public int getRemainingDesintegrations(MatterHolder stack) {
        Map<String,Object> tag = stack.getTag();
        if (tag == null || !tag.containsKey("desRem")) {
            return desintegrationMax;
        }
        return (int) tag.get("desRem");
    }

    protected static int randIntFromDouble(double value, Random rand) {
        return (int) Math.floor(value) + (rand.nextDouble() < (value % 1) ? 1 : 0);
    }

    public int simulateAbsorption(double neutronsReceived, MatterHolder stack, Random rand) {
        int absorbNeutrons = Math.min(randIntFromDouble(neutronsReceived, rand), getRemainingDesintegrations(stack));

        setRemainingDesintegrations(stack, getRemainingDesintegrations(stack) - absorbNeutrons);
        return absorbNeutrons;

    }
}
