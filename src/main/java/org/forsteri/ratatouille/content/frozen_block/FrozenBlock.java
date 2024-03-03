package org.forsteri.ratatouille.content.frozen_block;

import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

public class FrozenBlock extends Block {
    public FrozenBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRand) {
        if (pRand.nextInt(5) != 0)
            return;
        Vec3 vec3 = VecHelper.clampComponentWise(VecHelper.offsetRandomly(Vec3.ZERO, pRand, .75f), .55f)
                .add(VecHelper.getCenterOf(pPos));
        pLevel.addParticle(ParticleTypes.END_ROD, vec3.x, vec3.y, vec3.z, pRand.nextGaussian() * 0.005D,
                pRand.nextGaussian() * 0.005D, pRand.nextGaussian() * 0.005D);
    }
    

    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        for (BlockPos blockpos : BlockPos.betweenClosed(pPos.offset(-4, -4, -4), pPos.offset(4, 4, 4))) {
            if (pLevel.getBlockState(blockpos).is(Blocks.TORCH)) {
                return;
            }
        }


        for (BlockPos blockpos : BlockPos.betweenClosed(pPos.offset(-4, -4, -4), pPos.offset(4, 4, 4))) {
            FluidState fluidState = pLevel.getFluidState(blockpos);
            if (fluidState.is(Fluids.WATER) && fluidState.isSource()) {
                pLevel.setBlockAndUpdate(blockpos, Blocks.ICE.defaultBlockState());
                return;
            }
        }
    }
}
