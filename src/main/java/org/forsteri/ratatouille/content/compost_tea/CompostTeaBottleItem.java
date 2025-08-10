package org.forsteri.ratatouille.content.compost_tea;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CompostTeaBottleItem extends Item {

    public CompostTeaBottleItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        ItemStack stack = ctx.getItemInHand();
        Player player = ctx.getPlayer();

        if (applyBonemealLike(level, pos)) {
            if (!level.isClientSide) {
                if (player != null) {
                    player.getCooldowns().addCooldown(this, 20);
                    if (!player.getAbilities().instabuild) {
                        stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(ctx.getHand()));
                    }
                }
                level.levelEvent(1505, pos, 0);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    private boolean applyBonemealLike(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof BonemealableBlock growable) {
            if (growable.isValidBonemealTarget(level, pos, state, level.isClientSide)) {
                if (!level.isClientSide) {
                    RandomSource rand = level.getRandom();
                    if (growable.isBonemealSuccess(level, rand, pos, state)) {
                        growable.performBonemeal((ServerLevel) level, rand, pos, state);
                    }
                }
                return true;
            }
        }
        return false;
    }
}
