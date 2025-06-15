package org.forsteri.ratatouille.content.compost_tower;

import com.simibubi.create.foundation.fluid.SmartFluidTank;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

/**
 * A simple fluid tank that stores multiple different fluids concurrently.
 * Each fluid keeps track of the order it was inserted to allow rendering
 * based on insertion time when priorities are equal.
 */
public class MultiFluidTank extends SmartFluidTank {
    private final List<Entry> fluids = new ArrayList<>();
    private long insertCounter = 0;
    private final Consumer<FluidStack> updateCallback;

    public MultiFluidTank(int capacity, Consumer<FluidStack> updateCallback) {
        super(capacity, updateCallback);
        this.updateCallback = updateCallback;
    }

    private record Entry(FluidStack stack, long order) {}

    public int getTotalAmount() {
        return fluids.stream().mapToInt(e -> e.stack.getAmount()).sum();
    }

    public List<FluidStack> getFluids() {
        List<Entry> copy = new ArrayList<>(fluids);
        copy.sort(Comparator.comparingInt((Entry e) ->
                e.stack.getOrCreateTag().getInt("render_priority"))
                .thenComparingLong(e -> e.order));
        List<FluidStack> stacks = new ArrayList<>();
        for (Entry e : copy) {
            stacks.add(e.stack);
        }
        return stacks;
    }

    @Override
    public FluidStack getFluid() {
        return fluids.isEmpty() ? FluidStack.EMPTY : fluids.get(0).stack;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty())
            return 0;
        int amount = Math.min(resource.getAmount(), capacity - getTotalAmount());
        if (amount <= 0)
            return 0;
        if (action.execute()) {
            for (Entry e : fluids) {
                if (e.stack.isFluidEqual(resource)) {
                    e.stack.grow(amount);
                    onUpdate();
                    return amount;
                }
            }
            FluidStack copy = resource.copy();
            copy.setAmount(amount);
            fluids.add(new Entry(copy, insertCounter++));
            onUpdate();
        }
        return amount;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (fluids.isEmpty() || maxDrain <= 0)
            return FluidStack.EMPTY;
        Entry first = fluids.get(0);
        int drained = Math.min(maxDrain, first.stack.getAmount());
        FluidStack out = new FluidStack(first.stack, drained);
        if (action.execute()) {
            first.stack.shrink(drained);
            if (first.stack.isEmpty())
                fluids.remove(0);
            onUpdate();
        }
        return out;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty())
            return FluidStack.EMPTY;
        for (int i = 0; i < fluids.size(); i++) {
            Entry e = fluids.get(i);
            if (e.stack.isFluidEqual(resource)) {
                int drained = Math.min(resource.getAmount(), e.stack.getAmount());
                FluidStack out = new FluidStack(e.stack, drained);
                if (action.execute()) {
                    e.stack.shrink(drained);
                    if (e.stack.isEmpty())
                        fluids.remove(i);
                    onUpdate();
                }
                return out;
            }
        }
        return FluidStack.EMPTY;
    }

    private void onUpdate() {
        updateCallback.accept(getFluid());
    }
}
