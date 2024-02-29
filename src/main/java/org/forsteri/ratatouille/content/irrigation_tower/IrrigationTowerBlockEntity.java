package org.forsteri.ratatouille.content.irrigation_tower;

import com.simibubi.create.AllParticleTypes;
import com.simibubi.create.content.fluids.particle.FluidParticleData;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;


public class IrrigationTowerBlockEntity extends FluidTankBlockEntity {

    public IrrigationTowerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void createFluidParticles(BlockPos pPos) {
        RandomSource r = level.random;
        level.addAlwaysVisibleParticle(
                new FluidParticleData(AllParticleTypes.FLUID_PARTICLE.get(), tankInventory.getFluid()),
                pPos.getX() + 0.5d,  pPos.getY() + 0.25d, pPos.getZ() + 0.5d, 0, 0, 0);
    }

    public static void isNearWater(LevelReader pLevel, BlockPos pPos, CallbackInfoReturnable<Boolean> cir) {
        for (BlockPos blockpos : BlockPos.betweenClosed(pPos.offset(-8, 0, -8), pPos.offset(8, 1, 8))) {
            if (pLevel.getBlockEntity(blockpos.above()) instanceof IrrigationTowerBlockEntity be) {
                if (!be.getTankInventory().drain(1000, IFluidHandler.FluidAction.SIMULATE).isEmpty()) {
                    be.createFluidParticles(blockpos.above());
                    be.getTankInventory().drain(1000, IFluidHandler.FluidAction.EXECUTE);
                    cir.setReturnValue(true);
                    cir.cancel();
                }
            }
        }
    }

}
