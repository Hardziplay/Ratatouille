package org.forsteri.ratatouille.content.demoulder;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class MechanicalDemolderBlockEntity extends KineticBlockEntity  implements PressingBehaviour.PressingBehaviourSpecifics {

    public PressingBehaviour pressingBehaviour;

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
        pressingBehaviour = new PressingBehaviour(this);
        behaviours.add(pressingBehaviour);
    }

    public PressingBehaviour getPressingBehaviour() {
        return pressingBehaviour;
    }

    @Override
    public boolean tryProcessInBasin(boolean simulate) {
        return false;
    }

    @Override
    public boolean tryProcessOnBelt(TransportedItemStack input, List<ItemStack> outputList, boolean simulate) {
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

    @Override
    public void onPressingCompleted() {

    }

    @Override
    public int getParticleAmount() {
        return 0;
    }

    @Override
    public float getKineticSpeed() {
        return 0;
    }
}
