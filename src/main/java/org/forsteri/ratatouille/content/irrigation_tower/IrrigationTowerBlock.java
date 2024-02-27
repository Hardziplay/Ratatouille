package org.forsteri.ratatouille.content.irrigation_tower;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.forsteri.ratatouille.entry.CRBlockEntityTypes;

public class IrrigationTowerBlock extends HorizontalDirectionalBlock implements IWrenchable, IBE<IrrigationTowerBlockEntity> {

    public IrrigationTowerBlock(Properties pProperties) {
        super(pProperties);
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return Shapes.create(0, 0, 0, 1, 1.5F, 1);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(new Property[]{FACING}));
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction preferredFacing = preferredFacing = context.getHorizontalDirection().getOpposite();
        return (BlockState)this.defaultBlockState().setValue(FACING, context.getPlayer() != null && context.getPlayer().isShiftKeyDown() ? preferredFacing : preferredFacing.getOpposite());
    }

    @Override
    public Class<IrrigationTowerBlockEntity> getBlockEntityClass() {
        return IrrigationTowerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends IrrigationTowerBlockEntity> getBlockEntityType() {
        return CRBlockEntityTypes.IRRIGATION_TOWER_BLOCK_ENTITY.get();
    }
}
