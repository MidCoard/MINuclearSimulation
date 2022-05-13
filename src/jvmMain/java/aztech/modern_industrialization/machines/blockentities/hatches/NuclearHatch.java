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
package aztech.modern_industrialization.machines.blockentities.hatches;

import aztech.modern_industrialization.machines.components.NeutronHistoryComponent;
import aztech.modern_industrialization.machines.components.SteamHeaterComponent;
import aztech.modern_industrialization.machines.components.TemperatureComponent;
import aztech.modern_industrialization.nuclear.*;
import com.google.common.base.Preconditions;
import top.focess.mc.mi.nuclear.mc.FluidVariant;
import top.focess.mc.mi.nuclear.mc.ItemVariant;
import top.focess.mc.mi.nuclear.mc.MatterHolder;
import top.focess.mc.mi.nuclear.mc.MatterVariant;
import top.focess.mc.mi.nuclear.mi.MINuclearInventory;

import java.util.Optional;
import java.util.Random;

public class NuclearHatch implements INuclearTile {

    private static final Random RANDOM = new Random();

    private final MINuclearInventory inventory;

    public final NeutronHistoryComponent neutronHistory;
    public final TemperatureComponent nuclearReactorComponent;
    public final boolean isFluid;

    public NuclearHatch(boolean isFluid) {
        this.isFluid = isFluid;
        inventory = new MINuclearInventory(isFluid);
        if (!isFluid) {
            nuclearReactorComponent = new TemperatureComponent(NuclearConstant.MAX_TEMPERATURE);
        } else {
            nuclearReactorComponent = new SteamHeaterComponent(NuclearConstant.MAX_TEMPERATURE, NuclearConstant.MAX_HATCH_EU_PRODUCTION,
                    NuclearConstant.EU_PER_DEGREE, true, true);
        }
        neutronHistory = new NeutronHistoryComponent();
    }

    public MINuclearInventory getInventory() {
        return inventory;
    }

    public final void tick() {

        if (isFluid) {
            fluidNeutronProductTick(1, true);
        } else {
            ItemVariant itemVariant = (ItemVariant) this.getVariant();
            if (!itemVariant.isBlank() && itemVariant.getItem() instanceof NuclearAbsorbable abs) {
                if (abs.getNeutronProduct() != null) {
                    this.inventory.output(abs.getNeutronProduct(), abs.getNeutronProductAmount());
                }
            }
        }

    }

    @Override
    public double getTemperature() {
        return nuclearReactorComponent.getTemperature();
    }

    @Override
    public double getHeatTransferCoeff() {
        return NuclearConstant.BASE_HEAT_CONDUCTION + (getComponent().isPresent() ? getComponent().get().getHeatConduction() : 0);
    }

    @Override
    public double getMeanNeutronAbsorption(NeutronType type) {
        return neutronHistory.getAverageReceived(type);
    }

    @Override
    public double getMeanNeutronFlux(NeutronType type) {
        return neutronHistory.getAverageFlux(type);
    }

    @Override
    public double getMeanNeutronGeneration() {
        return neutronHistory.getAverageGeneration();
    }

    @Override
    public double getMeanEuGeneration() {
        return neutronHistory.getAverageEuGeneration();
    }

    @Override
    public MatterVariant getVariant() {
        return this.inventory.input().getMatterVariant();
    }

    @Override
    public long getVariantAmount() {
        return this.inventory.input().getAmount();
    }

    @Override
    public boolean isFluid() {
        return isFluid;
    }

    @Override
    public void setTemperature(double temp) {
        nuclearReactorComponent.setTemperature(temp);
    }

    @Override
    public void putHeat(double eu) {
        Preconditions.checkArgument(eu >= 0);
        setTemperature(getTemperature() + eu / NuclearConstant.EU_PER_DEGREE);
        neutronHistory.addValue("euGeneration", (int) eu);
    }

    @Override
    public int neutronGenerationTick(INuclearGrid grid) {
        double meanNeutron = getMeanNeutronAbsorption(NeutronType.BOTH);
        int neutronsProduced = 0;

        if (!isFluid) {
            ItemVariant itemVariant = (ItemVariant) this.getVariant();

            if (!itemVariant.isBlank() && itemVariant.getItem() instanceof NuclearAbsorbable abs) {

                if (itemVariant.getItem() instanceof NuclearFuel) {
                    meanNeutron += NuclearConstant.BASE_NEUTRON;
                }

                MatterHolder stack = itemVariant.toStack((int) getVariantAmount());

                Random rand = RANDOM;

                if (abs instanceof NuclearFuel fuel) {
                    neutronsProduced = fuel.simulateDesintegration(meanNeutron, stack, this.nuclearReactorComponent.getTemperature(), rand, grid);
                } else {
                    abs.simulateAbsorption(meanNeutron, stack, rand);
                }

                if (abs.getRemainingDesintegrations(stack) == 0) {
                    this.inventory.input().setMatterVariant(ItemVariant.blank(), 0);
                    this.inventory.output(abs.getNeutronProduct(), abs.getNeutronProductAmount());
                } else {
                    this.getInventory().input().setMatterVariant(ItemVariant.of(stack));
                }

            }

            neutronHistory.addValue("neutronGeneration", neutronsProduced);
            return neutronsProduced;
        } else {
            return 0;
        }
    }

    private static int randIntFromDouble(double value, Random rand) {
        return (int) Math.floor(value) + (rand.nextDouble() < (value % 1) ? 1 : 0);
    }

    public void fluidNeutronProductTick(int neutron, boolean simul) {
        if (isFluid) {
            Optional<INuclearComponent> maybeComponent = this.getComponent();
            if (maybeComponent.isPresent()) {

                INuclearComponent<FluidVariant> component = maybeComponent.get();

                int actualRecipe = randIntFromDouble(neutron * component.getNeutronProductProbability(), RANDOM);

                if (simul) {
                    actualRecipe = neutron;
                }

                if (simul || actualRecipe > 0) {
                    long extracted = this.inventory.input().extract(component.getVariant(), actualRecipe);
                    this.inventory.output(component.getNeutronProduct(), extracted * component.getNeutronProductAmount());
                }
            }
        }
    }

    private void checkComponentMaxTemperature() {
        if (!isFluid) {
            this.getComponent().ifPresent((component) -> {
                if (component.getMaxTemperature() < this.getTemperature()) {
                    this.inventory.input().empty();
                }
            });
        }
    }

    public void nuclearTick(INuclearGrid grid) {
        neutronHistory.tick();
        fluidNeutronProductTick(randIntFromDouble(neutronHistory.getAverageReceived(NeutronType.BOTH), RANDOM), false);

        if (isFluid) {
            double euProduced = ((SteamHeaterComponent) nuclearReactorComponent).tick(inventory.input(), inventory);
            grid.registerEuProduction(euProduced);
        }

        checkComponentMaxTemperature();
    }

    public void absorbNeutrons(int neutronNumber, NeutronType type) {
        Preconditions.checkArgument(type != NeutronType.BOTH);
        if (type == NeutronType.FAST) {
            neutronHistory.addValue("fastNeutronReceived", neutronNumber);
        } else {
            neutronHistory.addValue("thermalNeutronReceived", neutronNumber);
        }

    }

    public void addNeutronsToFlux(int neutronNumber, NeutronType type) {
        Preconditions.checkArgument(type != NeutronType.BOTH);
        if (type == NeutronType.FAST) {
            neutronHistory.addValue("fastNeutronFlux", neutronNumber);
        } else {
            neutronHistory.addValue("thermalNeutronFlux", neutronNumber);
        }
    }

}
