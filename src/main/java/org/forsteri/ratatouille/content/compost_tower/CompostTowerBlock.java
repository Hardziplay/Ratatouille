package org.forsteri.ratatouille.content.compost_tower;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.fluid.FluidHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.forsteri.ratatouille.entry.CRBlockEntityTypes;
import org.jetbrains.annotations.NotNull;

public class CompostTowerBlock extends Block implements IWrenchable, IBE<CompostTowerBlockEntity> {

    public CompostTowerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onPlace(BlockState state, @NotNull Level world, @NotNull BlockPos pos, BlockState oldState, boolean moved) {
        if (oldState.getBlock() == state.getBlock())
            return;
        if (moved)
            return;

        withBlockEntityDo(world, pos, CompostTowerBlockEntity::updateConnectivity);
    }

    @Override
    public void onRemove(BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (!(be instanceof CompostTowerBlockEntity towerBE))
                return;
            world.removeBlockEntity(pos);
            ConnectivityHandler.splitMulti(towerBE);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        return this.onBlockEntityUse(level, pos, be -> {
            if (!heldItem.isEmpty()) {
                if (FluidHelper.tryEmptyItemIntoBE(level, player, hand, heldItem, be)) {
                    return InteractionResult.SUCCESS;
                } else if (FluidHelper.tryFillItemFromBE(level, player, hand, heldItem, be)) {
                    return InteractionResult.SUCCESS;
                } else if (!GenericItemEmptying.canItemBeEmptied(level, heldItem) && !GenericItemFilling.canItemBeFilled(level, heldItem)) {
                    return heldItem.getItem().equals(Items.SPONGE) && !be.getCapability(ForgeCapabilities.FLUID_HANDLER)
                            .map(handler -> handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE))
                            .orElse(FluidStack.EMPTY).isEmpty() ? InteractionResult.SUCCESS : InteractionResult.PASS;
                } else {
                    return InteractionResult.SUCCESS;
                }
            } else {
                return InteractionResult.FAIL;
            }
        });
    }


    @Override
    public Class<CompostTowerBlockEntity> getBlockEntityClass() {
        return CompostTowerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CompostTowerBlockEntity> getBlockEntityType() {
        return CRBlockEntityTypes.COMPOST_TOWER_BLOCK_ENTITY.get();
    }
}