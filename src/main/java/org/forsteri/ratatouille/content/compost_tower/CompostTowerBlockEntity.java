package org.forsteri.ratatouille.content.compost_tower;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import org.forsteri.ratatouille.content.compost_tower.MultiFluidTank;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fluids.FluidStack;
import net.minecraft.world.item.crafting.RecipeType;
import org.forsteri.ratatouille.entry.CRRecipeTypes;
import java.util.Optional;

import java.util.List;

public class CompostTowerBlockEntity extends FluidTankBlockEntity {
    public CompostTowerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /**
     * Make connectivity updates callable from {@link CompostTowerBlock}
     * where the original protected visibility prevents access.
     */
    @Override
    public void updateConnectivity() {
        super.updateConnectivity();
    }

    @Override
    protected SmartFluidTank createInventory() {
        return new MultiFluidTank(1000, this::onFluidStackChanged);
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide || !isController())
            return;

        MultiFluidTank tank = (MultiFluidTank) getTankInventory();
        List<FluidStack> fluids = tank.getFluids();
        if (fluids.isEmpty())
            return;

        Optional<CompostingRecipe> recipeOpt = Optional.empty();
        FluidStack selected = FluidStack.EMPTY;
        for (FluidStack stack : fluids) {
            recipeOpt = level.getRecipeManager()
                    .getAllRecipesFor((RecipeType<CompostingRecipe>) CRRecipeTypes.COMPOSTING.getType())
                    .stream()
                    .filter(r -> !r.getFluidIngredients().isEmpty()
                            && r.getFluidIngredients().get(0).test(stack))
                    .findFirst();
            if (recipeOpt.isPresent()) {
                selected = stack;
                break;
            }
        }

        if (recipeOpt.isEmpty())
            return;

        CompostingRecipe recipe = recipeOpt.get();
        int amount = recipe.getFluidIngredients().get(0).getRequiredAmount();
        if (selected.getAmount() < amount)
            return;

        tank.drain(new FluidStack(selected, amount), IFluidHandler.FluidAction.EXECUTE);
        for (FluidStack out : recipe.getFluidResults()) {
            tank.fill(out.copy(), IFluidHandler.FluidAction.EXECUTE);
        }
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return containedFluidTooltip(tooltip, isPlayerSneaking,
                getCapability(ForgeCapabilities.FLUID_HANDLER));
    }
}

