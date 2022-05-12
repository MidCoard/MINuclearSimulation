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

import top.focess.mc.mi.nuclear.mc.FluidVariant;
import top.focess.mc.mi.nuclear.mc.ItemVariant;
import top.focess.mc.mi.nuclear.mc.MatterVariant;

import java.util.Optional;

public interface INuclearTileData {

    double getTemperature();

    double getHeatTransferCoeff();

    double getMeanNeutronAbsorption(NeutronType type);

    double getMeanNeutronFlux(NeutronType type);

    double getMeanNeutronGeneration();

    double getMeanEuGeneration();

    MatterVariant getVariant();

    long getVariantAmount();

    boolean isFluid();

    default Optional<INuclearComponent> getComponent() {
        MatterVariant variant = getVariant();

        if (variant instanceof ItemVariant resource) {
            if (!variant.isBlank() && getVariantAmount() > 0 && resource instanceof INuclearComponent) {
                return Optional.of((INuclearComponent) resource);
            }

        } else if (variant instanceof FluidVariant resource) {
            if (!resource.isBlank() && getVariantAmount() > 0) {
                return Optional.ofNullable(INuclearComponent.of(resource));
            }
        }

        return Optional.empty();
    }

    static boolean areEquals(Optional<INuclearTileData> a, Optional<INuclearTileData> b) {
        if (a.isPresent() != b.isPresent()) {
            return false;
        } else if (a.isPresent()) {
            INuclearTileData A = a.get();
            INuclearTileData B = b.get();
            for (NeutronType type : NeutronType.TYPES) {
                if (A.getMeanNeutronAbsorption(type) != B.getMeanNeutronAbsorption(type)) {
                    return false;
                }
                if (A.getMeanNeutronFlux(type) != B.getMeanNeutronFlux(type)) {
                    return false;
                }
            }
            return A.getTemperature() == B.getTemperature() && A.getHeatTransferCoeff() == B.getTemperature()
                    && A.getVariantAmount() == B.getVariantAmount() && A.getMeanNeutronGeneration() == B.getMeanNeutronGeneration()
                    && A.getVariant().equals(B.getVariant()) && A.getMeanEuGeneration() == B.getMeanEuGeneration();
        } else {
            return true;
        }

    }

}
