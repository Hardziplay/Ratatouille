package org.forsteri.ratatouille.content.demolder;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.List;
import java.util.Optional;

public class MechanicalDemolderBlockEntity extends KineticBlockEntity  implements PressingBehaviour.PressingBehaviourSpecifics {

    public PressingBehaviour demoldingBehaviour;

    public MechanicalDemolderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return new AABB(worldPosition).expandTowards(0, -1.5, 0)
                .expandTowards(0, 1, 0);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        demoldingBehaviour = new PressingBehaviour(this);
        behaviours.add(demoldingBehaviour);
    }

    public PressingBehaviour getPressingBehaviour() {
        return demoldingBehaviour;
    }

    private static final RecipeWrapper demoldingInv = new RecipeWrapper(new ItemStackHandler(1));

    public Optional<PressingRecipe> getRecipe(ItemStack item) {
        Optional<PressingRecipe> assemblyRecipe =
                SequencedAssemblyRecipe.getRecipe(level, item, AllRecipeTypes.PRESSING.getType(), PressingRecipe.class);
        if (assemblyRecipe.isPresent())
            return assemblyRecipe;

        demoldingInv.setItem(0, item);
        return AllRecipeTypes.PRESSING.find(demoldingInv, level);
    }

    @Override
    public boolean tryProcessOnBelt(TransportedItemStack input, List<ItemStack> outputList, boolean simulate) {
        Optional<PressingRecipe> recipe = getRecipe(input.stack);
        if (!recipe.isPresent())
            return false;
        if (simulate)
            return true;
        demoldingBehaviour.particleItems.add(input.stack);
        List<ItemStack> outputs = RecipeApplier.applyRecipeOn(
                canProcessInBulk() ? input.stack : ItemHandlerHelper.copyStackWithSize(input.stack, 1), recipe.get());

        outputList.addAll(outputs);
        return true;
    }

    @Override
    public int getParticleAmount() {
        return 15;
    }

    @Override
    public float getKineticSpeed() {
        return getSpeed();
    }

    @Override
    public void onPressingCompleted() {

    }

    @Override
    public boolean tryProcessInBasin(boolean simulate) {
        return false;
    }

    @Override
    public boolean tryProcessInWorld(ItemEntity itemEntity, boolean simulate) {
        return false;
    }

    @Override
    public boolean canProcessInBulk() {
        return false;
    }
}
