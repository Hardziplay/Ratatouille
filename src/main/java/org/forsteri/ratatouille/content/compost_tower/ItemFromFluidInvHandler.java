package org.forsteri.ratatouille.content.compost_tower;

import com.simibubi.create.foundation.fluid.SmartFluidTank;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.forsteri.ratatouille.entry.CRFluids;
import org.forsteri.ratatouille.entry.CRItems;
import org.jetbrains.annotations.NotNull;

import static com.simibubi.create.content.fluids.tank.FluidTankBlockEntity.getCapacityMultiplier;

public class ItemFromFluidInvHandler implements IItemHandlerModifiable {
    private final SmartFluidTank[] tanks;
    private final static int ITEM_MB_RATION = getCapacityMultiplier() / 64;
    public ItemFromFluidInvHandler(SmartFluidTank[] tanks) {
        this.tanks = tanks;
    }
    public static FluidStack itemToFluid(ItemStack stack) {
        if (stack.isEmpty())
            return FluidStack.EMPTY;
        if (stack.getItem() == CRItems.COMPOST_RESIDUE.get())
            return new FluidStack(CRFluids.COMPOST_RESIDUE_FLUID.get(), stack.getCount() * ITEM_MB_RATION);
        if (stack.getItem() == CRItems.COMPOST_MASS.get())
            return new FluidStack(CRFluids.COMPOST_FLUID.get(), stack.getCount() * ITEM_MB_RATION);
        return FluidStack.EMPTY;
    }
    public static ItemStack fluidToItem(FluidStack stack) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;
        if (stack.getFluid() == CRFluids.COMPOST_RESIDUE_FLUID.get())
            return new ItemStack(CRItems.COMPOST_RESIDUE.get(), stack.getAmount() / ITEM_MB_RATION);
        if (stack.getFluid() == CRFluids.COMPOST_FLUID.get())
            return new ItemStack(CRItems.COMPOST_MASS.get(), stack.getAmount() / ITEM_MB_RATION);
        return ItemStack.EMPTY;
    }
    @Override
    public int getSlots() {
        return tanks.length;
    }

    protected void validateSlotIndex(int slot)
    {
        if (slot < 0 || slot >= tanks.length)
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + getSlots() + ")");
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        validateSlotIndex(slot);
        return fluidToItem(tanks[slot].getFluid());
    }

    protected int getStackLimit(int slot, @NotNull ItemStack stack)
    {
        return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
    }

    protected void onContentsChanged(int slot)
    {

    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        if (!isItemValid(slot, stack))
            return stack;

        validateSlotIndex(slot);
        var existing = getStackInSlot(slot);

        int limit = getStackLimit(slot, stack);

        if (!existing.isEmpty())
        {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                return stack;

            limit -= existing.getCount();
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate)
        {
            ItemStack itemStack;
            if (existing.isEmpty())
            {
                itemStack = reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack;
            }
            else
            {
                existing.grow(reachedLimit ? limit : stack.getCount());
                itemStack = existing;
            }
            tanks[slot].setFluid(itemToFluid(itemStack));
            onContentsChanged(slot);
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount()- limit) : ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0)
            return ItemStack.EMPTY;

        validateSlotIndex(slot);

        ItemStack existing = this.getStackInSlot(slot);

        if (existing.isEmpty())
            return ItemStack.EMPTY;

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract)
        {
            if (!simulate)
            {
                this.tanks[slot].setFluid(FluidStack.EMPTY);
                onContentsChanged(slot);
                return existing;
            }
            else
            {
                return existing.copy();
            }
        }
        else
        {
            if (!simulate)
            {
                this.tanks[slot].setFluid(itemToFluid(ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract)));
                onContentsChanged(slot);
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        validateSlotIndex(slot);
        return tanks[slot].getCapacity() / ITEM_MB_RATION;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return !itemToFluid(stack).isEmpty();
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        validateSlotIndex(slot);
        var fluidStack = itemToFluid(stack);
        if (!fluidStack.isEmpty()) this.tanks[slot].setFluid(fluidStack);
        onContentsChanged(slot);
    }

    public ItemStack consume(ItemStack stack, boolean simulate) {
        var fluidStack = itemToFluid(stack);
        if (fluidStack.isEmpty()) return ItemStack.EMPTY;
        int remaining = fluidStack.getAmount();
        for (var tank : tanks) {
            var tankFluid = tank.getFluid();
            if (tankFluid.isEmpty()) continue;
            if (tankFluid.isFluidEqual(fluidStack)) {
                if (tankFluid.getAmount() > remaining) {
                    if (!simulate) tankFluid.shrink(remaining);
                    remaining = 0;
                } else {
                    if (!simulate) tank.setFluid(FluidStack.EMPTY);
                    remaining -= tankFluid.getAmount();
                }
            }
        }
        return ItemHandlerHelper.copyStackWithSize(stack, remaining);
    }
}