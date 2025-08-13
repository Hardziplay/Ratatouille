package org.forsteri.ratatouille.content.frozen_block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.forsteri.ratatouille.entry.CRDataComponents;
import org.forsteri.ratatouille.entry.CRRecipeTypes;

import java.util.List;
import java.util.Optional;

public class FrozenBlockEntity extends BlockEntity {
    public static int MAX_CHILLNESS = 50;

    public FrozenBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }


    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, FrozenBlockEntity pBlockEntity) {
        if (pLevel == null || pLevel.isClientSide) return;

        double x = pPos.getX();
        double y = pPos.getY() + 1;
        double z = pPos.getZ();
        AABB searchBox = new AABB(x, y, z, x + 1, y + 0.25, z + 1);
        List<ItemEntity> itemEntities = pLevel.getEntitiesOfClass(ItemEntity.class, searchBox);

        for (ItemEntity itemEntity : itemEntities) {
            ItemStack itemStack = itemEntity.getItem();
            Optional<RecipeHolder<FreezingRecipe>> recipe = CRRecipeTypes.FREEZING.find(new SingleRecipeInput(itemStack), pLevel);
            if (recipe.isEmpty()) continue;

            Integer chillness = itemStack.getOrDefault(CRDataComponents.COLLECTING_CHILLNESS, 0);
            if (chillness >= MAX_CHILLNESS) {
                itemStack.remove(CRDataComponents.COLLECTING_CHILLNESS);

                ItemStack newStack = recipe.get().value().getResultItem(pLevel.registryAccess());
                newStack.setCount(itemStack.getCount());
                itemEntity.setItem(newStack);
            } else {
                itemStack.set(CRDataComponents.COLLECTING_CHILLNESS, chillness + 1);
            }
        }
    }

}
