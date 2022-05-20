package top.focess.mc.mi.nuclear.mi;

import org.checkerframework.checker.nullness.qual.NonNull;
import top.focess.mc.mi.nuclear.mc.*;
import top.focess.util.serialize.FocessSerializable;

import java.util.ArrayList;
import java.util.List;

public class MINuclearInventory implements FocessSerializable {

    private final InputMatterHolder input;
    private final List<OutputMatterHolder> outputMatterHolders = new ArrayList<>();

    private int outputCount;

    public MINuclearInventory(boolean isFluid) {
        this.input = new InputMatterHolder(isFluid);
        this.setOutputCount(2);
    }

    public InputMatterHolder getInput() {
        return input;
    }

    public List<OutputMatterHolder> getOutput() {
        return this.outputMatterHolders.subList(0, outputCount);
    }

    // return the amount that has been inserted
    public long tryOutput(@NonNull MatterVariant matterVariant, long amount) {
        long before = amount;
        List<OutputMatterHolder> copy = new ArrayList<>(outputMatterHolders.stream().map(OutputMatterHolder::copy).toList());
        for (OutputMatterHolder outputMatterHolder : copy) {
            amount = outputMatterHolder.insertMatterVariant(matterVariant, amount);
            if (amount == 0)
                break;
        }
        if (amount != 0)
            while (copy.size() < this.outputCount) {
                OutputMatterHolder outputMatterHolder = new OutputMatterHolder(this.input.isFluid());
                amount = outputMatterHolder.insertMatterVariant(matterVariant, amount);
                copy.add(outputMatterHolder);
                if (amount == 0)
                    break;
            }
        return before - amount;
    }

    public long output(@NonNull MatterVariant matterVariant, long amount) {
        long before = amount;
        for (OutputMatterHolder outputMatterHolder : this.outputMatterHolders) {
            amount = outputMatterHolder.insertMatterVariant(matterVariant, amount);
            if (amount == 0)
                break;
        }
        if (amount != 0)
            while (this.outputMatterHolders.size() < this.outputCount) {
                OutputMatterHolder outputMatterHolder = new OutputMatterHolder(this.input.isFluid());
                amount = outputMatterHolder.insertMatterVariant(matterVariant, amount);
                this.outputMatterHolders.add(outputMatterHolder);
                if (amount == 0)
                    break;
            }
        return before - amount;
    }

    public int getOutputCount() {
        return outputCount;
    }

    public void setOutputCount(int outputCount) {
        this.outputCount = outputCount;
        while (this.outputMatterHolders.size() < outputCount)
            this.outputMatterHolders.add(new OutputMatterHolder(this.input.isFluid()));
    }
}
