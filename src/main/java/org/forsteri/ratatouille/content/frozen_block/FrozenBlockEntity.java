package org.forsteri.ratatouille.content.frozen_block;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.forsteri.ratatouille.entry.CRRecipeTypes;
import java.util.List;
import java.util.Optional;

public class FrozenBlockEntity extends BlockEntity {
    private static final RecipeWrapper freezingInv = new RecipeWrapper(new ItemStackHandler(1));
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
            freezingInv.setItem(0, itemStack);

            Optional<FreezingRecipe> recipe = CRRecipeTypes.FREEZING.find(freezingInv, pLevel);
            if (recipe.isEmpty()) continue;

            CompoundTag itemData = itemStack.getOrCreateTag();
            if (itemData.getInt("CollectingChillness") >= MAX_CHILLNESS) {
                itemData.remove("CollectingChillness");

                ItemStack newStack = recipe.get().getResultItem();
                newStack.setCount(itemStack.getCount());
                itemEntity.setItem(newStack);
            } else {
                itemData.putInt("CollectingChillness", itemData.getInt("CollectingChillness") + 1);
            }
        }
    }

}
