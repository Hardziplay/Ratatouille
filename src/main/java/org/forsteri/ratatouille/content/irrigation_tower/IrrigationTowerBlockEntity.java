package org.forsteri.ratatouille.content.irrigation_tower;

import com.simibubi.create.AllParticleTypes;
import com.simibubi.create.content.fluids.particle.FluidParticleData;
import com.simibubi.create.content.fluids.tank.CreativeFluidTankBlockEntity;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.WaterFluid;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.forsteri.ratatouille.entry.CRFluids;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Consumer;


public class IrrigationTowerBlockEntity extends FluidTankBlockEntity {

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return containedFluidTooltip(tooltip, isPlayerSneaking,
                getCapability(ForgeCapabilities.FLUID_HANDLER));
    }

    public IrrigationTowerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected SmartFluidTank createInventory() {
        return new IrrigationSmartFluidTank(1000, this::onFluidStackChanged);
    }

    public static void isNearWater(LevelReader pLevel, BlockPos pPos, CallbackInfoReturnable<Boolean> cir) {
        for (BlockPos blockpos : BlockPos.betweenClosed(pPos.offset(-8, 0, -8), pPos.offset(8, 0, 8))) {
            if (pLevel.getBlockEntity(blockpos.above()) instanceof IrrigationTowerBlockEntity be) {
                FluidStack fluid = be.getTankInventory().getFluid();
                if (!fluid.isEmpty() && fluid.getFluid().isSame(Fluids.WATER)) {
                    cir.setReturnValue(true);
                    cir.cancel();
                    return;
                }
            }
        }
    }

    public static class IrrigationSmartFluidTank extends SmartFluidTank {

        public IrrigationSmartFluidTank(int capacity, Consumer<FluidStack> updateCallback) {
            super(capacity, updateCallback);
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid().isSame(Fluids.WATER)
                    || stack.getFluid().isSame(CRFluids.Compost_Tea.get().getSource());
        }
    }

}
