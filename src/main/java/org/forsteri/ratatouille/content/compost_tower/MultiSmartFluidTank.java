package org.forsteri.ratatouille.content.compost_tower;

import com.simibubi.create.foundation.fluid.SmartFluidTank;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Simple multi tank wrapper storing several SmartFluidTanks.
 * This is a lightweight implementation aiming to mimic Create's basin
 * fluid storage behaviour.
 */
public class MultiSmartFluidTank extends SmartFluidTank {
    private final List<SmartFluidTank> tanks = new ArrayList<>();

    public MultiSmartFluidTank(int tankCount, int capacityPerTank, Consumer<FluidStack> updateCallback) {
        super(tankCount * capacityPerTank, updateCallback);
        for (int i = 0; i < tankCount; i++) {
            tanks.add(new SmartFluidTank(capacityPerTank, updateCallback));
        }
    }

    public List<SmartFluidTank> getInternalTanks() {
        return tanks;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        for (SmartFluidTank tank : tanks) {
            if (tank.isEmpty() || tank.getFluid().isFluidEqual(resource)) {
                return tank.fill(resource, action);
            }
        }
        return 0;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        for (SmartFluidTank tank : tanks) {
            if (!tank.isEmpty())
                return tank.drain(maxDrain, action);
        }
        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        for (SmartFluidTank tank : tanks) {
            if (resource.isFluidEqual(tank.getFluid()))
                return tank.drain(resource, action);
        }
        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack getFluid() {
        for (SmartFluidTank tank : tanks) {
            if (!tank.getFluid().isEmpty())
                return tank.getFluid();
        }
        return FluidStack.EMPTY;
    }

    @Override
    public int getTanks() {
        return tanks.size();
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return tanks.get(tank).getFluid();
    }

    @Override
    public int getTankCapacity(int tank) {
        return tanks.get(tank).getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        SmartFluidTank t = tanks.get(tank);
        return t.isEmpty() || t.getFluid().isFluidEqual(stack);
    }
}