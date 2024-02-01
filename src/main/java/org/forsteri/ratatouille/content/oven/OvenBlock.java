package org.forsteri.ratatouille.content.oven;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.forsteri.ratatouille.entry.CRBlockEntityTypes;
import org.jetbrains.annotations.NotNull;

public class OvenBlock extends Block implements IWrenchable, IBE<OvenBlockEntity> {
    public static final BooleanProperty IS_2x2 = BooleanProperty.create("is_2x2");

    public OvenBlock(Properties p_i48440_1_) {
        super(p_i48440_1_);
        registerDefaultState(defaultBlockState().setValue(IS_2x2, false));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onPlace(BlockState state, @NotNull Level world, @NotNull BlockPos pos, BlockState oldState, boolean moved) {
        if (oldState.getBlock() == state.getBlock())
            return;
        if (moved)
            return;

        withBlockEntityDo(world, pos, OvenBlockEntity::updateConnectivity);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_206840_1_) {
        p_206840_1_.add(IS_2x2);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (!(be instanceof OvenBlockEntity ovenBE))
                return;
            world.removeBlockEntity(pos);
            ConnectivityHandler.splitMulti(ovenBE);
        }
    }

    @Override
    public Class<OvenBlockEntity> getBlockEntityClass() {
        return OvenBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends OvenBlockEntity> getBlockEntityType() {
        return CRBlockEntityTypes.OVEN_ENTITY.get();
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        withBlockEntityDo(level, pos, OvenBlockEntity::updateOvenState);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState p_60541_, @NotNull Direction p_60542_, @NotNull BlockState p_60543_, @NotNull LevelAccessor p_60544_, @NotNull BlockPos p_60545_, @NotNull BlockPos p_60546_) {
        if (p_60543_.getBlock() != this)
            withBlockEntityDo(p_60544_, p_60545_, OvenBlockEntity::updateBakeData);
        return super.updateShape(p_60541_, p_60542_, p_60543_, p_60544_, p_60545_, p_60546_);
    }

    @Override
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull RandomSource random) {
        super.animateTick(state, level, blockPos, random);
        OvenBlockEntity be = getBlockEntity(level, blockPos);
        if (be == null)
            return;

        be = be.getControllerBE();

        if (be == null)
            return;

        if (random.nextInt(5) != 0)
            return;

        if (be.bakeData.tempLevel <= 0)
            return;

        if (level.getBlockState(blockPos.above()).getBlock() instanceof OvenBlock)
            return;

        CampfireBlock.makeParticles(level, blockPos, be.bakeData.tempLevel > 4, false);
    }
}
