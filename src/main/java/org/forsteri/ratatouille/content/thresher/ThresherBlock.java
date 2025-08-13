package org.forsteri.ratatouille.content.thresher;

import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.forsteri.ratatouille.entry.CRBlockEntityTypes;
import org.forsteri.ratatouille.entry.CRBlocks;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ThresherBlock extends HorizontalKineticBlock implements IBE<ThresherBlockEntity> {
    public ThresherBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return getRotationAxis(state) == face.getAxis();
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(HORIZONTAL_FACING)
                .getClockWise()
                .getAxis();
    }

    @Override
    public Class<ThresherBlockEntity> getBlockEntityClass() {
        return ThresherBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ThresherBlockEntity> getBlockEntityType() {
        return CRBlockEntityTypes.THRESHER_ENTITY.get();
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter worldIn, Entity entityIn) {
        super.updateEntityAfterFallOn(worldIn, entityIn);
        if (!CRBlocks.THRESHER.has(worldIn.getBlockState(entityIn.blockPosition())))
            return;
        if (!(entityIn instanceof ItemEntity))
            return;
        if (!entityIn.isAlive())
            return;
        ItemEntity itemEntity = (ItemEntity) entityIn;
        withBlockEntityDo(worldIn, entityIn.blockPosition(), be -> {

            ItemStack insertItem = ItemHandlerHelper.insertItem(be.inputInv, itemEntity.getItem()
                    .copy(), false);

            if (insertItem.isEmpty()) {
                itemEntity.discard();
                return;
            }

            itemEntity.setItem(insertItem);
        });
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        boolean isZ = pState.getValue(HORIZONTAL_FACING).getAxis() == Direction.Axis.Z;
        return Shapes.or(
                Shapes.create(0, 0, 0, 1, 2 / 16f, 1),
                Shapes.create(isZ ? 0 : 1 / 16f, 2 / 16f, isZ ? 1 / 16f : 0, isZ ? 1 : 15 / 16f, 15 / 16f, isZ ? 15 / 16f : 1)
        );
    }
}
