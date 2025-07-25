package org.forsteri.ratatouille.content.compost_tower;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.forsteri.ratatouille.entry.CRRecipeTypes;
import org.forsteri.ratatouille.content.compost_tower.MultiSmartFluidTank;
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
        return new SmartFluidTank(1000, this::onFluidStackChanged);
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide || !isController())
            return;

        FluidTank tank = (FluidTank) getTankInventory();
        FluidStack inTank = tank.getFluid();
        if (inTank.isEmpty())
            return;

        Optional<CompostingRecipe> recipeOpt = level.getRecipeManager()
                .getAllRecipesFor((RecipeType<CompostingRecipe>) CRRecipeTypes.COMPOSTING.getType())
                .stream()
                .filter(r -> !r.getFluidIngredients().isEmpty()
                        && r.getFluidIngredients().get(0).test(inTank))
                .findFirst();

        if (recipeOpt.isEmpty())
            return;

        CompostingRecipe recipe = recipeOpt.get();
        int amount = recipe.getFluidIngredients().get(0).getRequiredAmount();
        if (inTank.getAmount() < amount)
            return;

        tank.drain(amount, IFluidHandler.FluidAction.EXECUTE);
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

