package org.forsteri.ratatouille.content.irrigation_tower;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.fluid.FluidHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.forsteri.ratatouille.entry.CRBlockEntityTypes;
import org.forsteri.ratatouille.entry.CRFluids;
import vectorwing.farmersdelight.common.registry.ModBlocks;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class IrrigationTowerBlock extends HorizontalDirectionalBlock implements IWrenchable, IBE<IrrigationTowerBlockEntity> {
    public static final MapCodec<IrrigationTowerBlock> CODEC = simpleCodec(IrrigationTowerBlock::new);

    public IrrigationTowerBlock(Properties pProperties) {
        super(pProperties.randomTicks());

    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack heldItem, BlockState state, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult hitResult) {
        return this.onBlockEntityUseItemOn(pLevel, pPos, (be) -> {
            if (!heldItem.isEmpty()) {
                if (FluidHelper.tryEmptyItemIntoBE(pLevel, pPlayer, pHand, heldItem, be)) {
                    return ItemInteractionResult.SUCCESS;
                } else if (FluidHelper.tryFillItemFromBE(pLevel, pPlayer, pHand, heldItem, be)) {
                    return ItemInteractionResult.SUCCESS;
                } else if (GenericItemEmptying.canItemBeEmptied(pLevel, heldItem)
                        || GenericItemFilling.canItemBeFilled(pLevel, heldItem)) {
                    return ItemInteractionResult.SUCCESS;
                } else if (heldItem.getItem().equals(Items.SPONGE)) {
                    IFluidHandler fluidHandler = pLevel.getCapability(Capabilities.FluidHandler.BLOCK, pPos, null);
                    if (fluidHandler != null) {
                        FluidStack drained = fluidHandler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
                        if (!drained.isEmpty()) {
                            return ItemInteractionResult.SUCCESS;
                        }
                    } else {
                        return ItemInteractionResult.SUCCESS;
                    }
                }
            } else {
                return ItemInteractionResult.FAIL;
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        });
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return Shapes.create(0, 0, 0, 1, 1.5F, 1);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof IrrigationTowerBlockEntity tower)) return;

        FluidStack simulate = tower.getTankInventory().drain(
                new FluidStack(CRFluids.COMPOST_TEA.get().getSource(), 100),
                IFluidHandler.FluidAction.SIMULATE
        );

        if (simulate.isEmpty() || simulate.getAmount() < 100) return;

        BlockPos center = pos.below();
        for (int dx = -8; dx <= 8; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -8; dz <= 8; dz++) {
                    BlockPos target = center.offset(dx, dy, dz);
                    BlockState targetState = level.getBlockState(target);

                    if (targetState.is(Blocks.FARMLAND)) {
                        BlockState newState = ModBlocks.RICH_SOIL_FARMLAND.get().defaultBlockState();
                        level.setBlockAndUpdate(target, newState);
                        level.gameEvent(GameEvent.BLOCK_CHANGE, target, GameEvent.Context.of(null, newState));
                        tower.getTankInventory().drain(
                                new FluidStack(CRFluids.COMPOST_TEA.get().getSource(), 100),
                                IFluidHandler.FluidAction.EXECUTE
                        );
                        return;
                    }
                }
            }
        }
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction preferredFacing = preferredFacing = context.getHorizontalDirection().getOpposite();
        return (BlockState) this.defaultBlockState().setValue(FACING, context.getPlayer() != null && context.getPlayer().isShiftKeyDown() ? preferredFacing : preferredFacing.getOpposite());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(new Property[]{FACING}));
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
