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

import static net.minecraft.core.Direction.UP;

import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.components.NeutronHistoryComponent;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.components.SteamHeaterComponent;
import aztech.modern_industrialization.machines.components.TemperatureComponent;
import aztech.modern_industrialization.machines.components.sync.TemperatureBar;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.HatchType;
import aztech.modern_industrialization.nuclear.*;
import com.google.common.base.Preconditions;
import java.util.*;
import java.util.stream.Collectors;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import top.focess.mc.mi.nuclear.mc.*;
import top.focess.mc.mi.nuclear.mi.MINuclearInventory;

public class NuclearHatch implements INuclearTile {

    private static final Random RANDOM = new Random();

    private final MINuclearInventory inventory;

    public final NeutronHistoryComponent neutronHistory;
    public final TemperatureComponent nuclearReactorComponent;
    public final boolean isFluid;
    public static final long capacity = 64000 * 81;

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
                    this.inventory.get(1).addMatterVariant(abs.getNeutronProduct(), abs.getNeutronProductAmount());
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
        return this.inventory.get(0).getMatterVariant();
    }

    @Override
    public long getVariantAmount() {
        return this.inventory.get(0).getAmount();
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
                    try (Transaction tx = Transaction.openOuter()) {
                        ConfigurableItemStack absStack = this.inventory.getItemStacks().get(0);
                        absStack.updateSnapshots(tx);
                        absStack.setAmount(0);
                        absStack.setKey(ItemVariant.blank());

                        if (abs.getNeutronProduct() != null) {
                            long inserted = this.inventory.itemStorage.insert(abs.getNeutronProduct(), abs.getNeutronProductAmount(), tx,
                                    AbstractConfigurableStack::canPipesExtract, true);

                            if (inserted == abs.getNeutronProductAmount()) {
                                tx.commit();
                            } else {
                                tx.abort();
                            }
                        } else {
                            tx.commit();
                        }
                    }
                } else {
                    this.getInventory().get(0).setMatterVariant(ItemVariant.of(stack));
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

                int actualRecipe = randIntFromDouble(neutron * component.getNeutronProductProbability(), this.getLevel().getRandom());

                if (simul) {
                    actualRecipe = neutron;
                }

                if (simul || actualRecipe > 0) {
                    try (Transaction tx = Transaction.openOuter()) {
                        long extracted = this.inventory.fluidStorage.extractAllSlot(component.getVariant(), actualRecipe, tx,
                                AbstractConfigurableStack::canPipesInsert);
                        this.inventory.fluidStorage.insert(component.getNeutronProduct(), extracted * component.getNeutronProductAmount(), tx,
                                AbstractConfigurableStack::canPipesExtract, true);

                        if (!simul) {
                            tx.commit();
                        }
                    }
                }
            }
        }
    }

    private void checkComponentMaxTemperature() {
        if (!isFluid) {
            this.getComponent().ifPresent((component) -> {
                if (component.getMaxTemperature() < this.getTemperature()) {
                    this.inventory.getItemStacks().get(0).empty();
                }
            });
        }
    }

    public void nuclearTick(INuclearGrid grid) {
        neutronHistory.tick();
        fluidNeutronProductTick(randIntFromDouble(neutronHistory.getAverageReceived(NeutronType.BOTH), RANDOM), false);

        if (isFluid) {
            double euProduced = ((SteamHeaterComponent) nuclearReactorComponent).tick(Collections.singletonList(inventory.getFluidStacks().get(0)),
                    inventory.getFluidStacks().stream().filter(AbstractConfigurableStack::canPipesExtract).collect(Collectors.toList()));
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
