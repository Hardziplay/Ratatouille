package org.forsteri.ratatouille.content.compost_tower;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.forsteri.ratatouille.entry.CRFluids;
import org.forsteri.ratatouille.entry.CRItems;
import org.jetbrains.annotations.NotNull;

public class CompostItemHandler implements net.neoforged.neoforge.items.IItemHandlerModifiable {
    private static final int ITEM_FLUID_RATIO = 8000 / 100;
    private static final BiMap<Fluid, Item> ITEM_FLUID_MAP = HashBiMap.create();

    static {
        ITEM_FLUID_MAP.put(CRFluids.COMPOST_FLUID.get(), CRItems.COMPOST_MASS.get());
//        ITEM_FLUID_MAP.put(CRFluids.COMPOST_RESIDUE_FLUID.get(), CRItems.COMPOST_RESIDUE.get());
    }

    protected CompostFluidTank tank;

    CompostItemHandler(CompostFluidTank tank) {
        this.tank = tank;
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        tank.fill(itemToFluid(stack), IFluidHandler.FluidAction.EXECUTE);
    }

    private FluidStack itemToFluid(ItemStack itemStack) {
        var amount = itemStack.getCount() * ITEM_FLUID_RATIO;
        var item = itemStack.getItem();
        if (amount == 0 || !ITEM_FLUID_MAP.inverse().containsKey(item)) return FluidStack.EMPTY;
        return new FluidStack(ITEM_FLUID_MAP.inverse().get(item), amount);
    }

    @Override
    public int getSlots() {
        return tank.getTanks();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return fluidToItem(tank.getFluidInTank(slot));
    }

    private ItemStack fluidToItem(FluidStack fluidStack) {
        var amount = fluidStack.getAmount() / ITEM_FLUID_RATIO;
        var fluid = fluidStack.getFluid();
        if (amount == 0 || !ITEM_FLUID_MAP.containsKey(fluid)) return ItemStack.EMPTY;
        return new ItemStack(ITEM_FLUID_MAP.get(fluid), amount);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        var action = simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE;
        int remaining = stack.getCount() - tank.fill(itemToFluid(stack), action);
        return new ItemStack(stack.getItem(), remaining);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        var action = simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE;
        var targetFluid = tank.getFluidInTank(slot);
        var extracted = tank.drain(new FluidStack(targetFluid.getFluid(), amount), action);
        return fluidToItem(extracted);

    }

    @Override
    public int getSlotLimit(int slot) {
        return tank.getTankCapacity(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        var fluid = itemToFluid(stack);
        if (fluid.isEmpty()) return false;
        return tank.getFluidInTank(slot).is(fluid.getFluid());
    }

    public void consume(ItemStack item, boolean simulate) {
        var fluid = itemToFluid(item);
        if (fluid.isEmpty()) return;

        if (tank.drain(fluid, IFluidHandler.FluidAction.SIMULATE).getAmount() >= fluid.getAmount()) {
            tank.drain(fluid, IFluidHandler.FluidAction.EXECUTE);
        }
    }
}
