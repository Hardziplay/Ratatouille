package org.forsteri.ratatouille.mixin;


import com.simibubi.create.content.kinetics.fan.NozzleBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.client.particle.ExplodeParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.forsteri.ratatouille.content.spreader.SpreaderBlockEntity;
import org.forsteri.ratatouille.content.spreader.SpreaderParticle;
import org.forsteri.ratatouille.entry.CRParticleTypes;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = NozzleBlockEntity.class, remap = false)
public class NozzleBlockEntityMixin extends SmartBlockEntity {

    @Shadow
    private BlockPos fanPos;
    public NozzleBlockEntityMixin(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"), remap = true)
    private void tick(Level instance, ParticleOptions pParticleData, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
        if (fanPos != null && instance.getBlockEntity(fanPos) instanceof SpreaderBlockEntity) {
            instance.addParticle(new SpreaderParticle.Provider(), pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        } else {
            instance.addParticle(ParticleTypes.POOF, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        }
    }

    @Inject(method = "lazyTick", at=@At("HEAD"), cancellable = true)
    private void lazyTick(CallbackInfo ci) {
        if (getLevel() != null && fanPos != null && getLevel().getBlockEntity(fanPos) instanceof SpreaderBlockEntity)
            ci.cancel();
    }
}
