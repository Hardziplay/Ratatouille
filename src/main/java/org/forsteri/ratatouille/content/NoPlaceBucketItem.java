package org.forsteri.ratatouille.content;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class NoPlaceBucketItem extends BucketItem {


    public NoPlaceBucketItem(Fluid content, Properties properties) {
        super(content, properties);
    }

    @Override
    public boolean emptyContents(@javax.annotation.Nullable Player pPlayer, Level pLevel, BlockPos pPos, @javax.annotation.Nullable BlockHitResult pResult, @Nullable ItemStack container) {
        return false;
    }
}
