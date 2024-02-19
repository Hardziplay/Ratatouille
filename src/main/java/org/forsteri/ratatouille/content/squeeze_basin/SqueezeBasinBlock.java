package org.forsteri.ratatouille.content.squeeze_basin;

import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.FluidHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.forsteri.ratatouille.entry.CRBlockEntityTypes;
import org.forsteri.ratatouille.entry.CRItems;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class SqueezeBasinBlock extends HorizontalDirectionalBlock implements IBE<SqueezeBasinBlockEntity>, IWrenchable {
    public static final BooleanProperty CASING = BooleanProperty.create("casing");

    public SqueezeBasinBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue(CASING, false));
    }

    @Override
    public @NotNull InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack heldItem = pPlayer.getItemInHand(pHand);
        return this.onBlockEntityUse(pLevel, pPos, (be) -> {
            if (!heldItem.isEmpty()) {
                if (heldItem.is(CRItems.SAUSAGE_CASING.get()) && !pState.getValue(CASING)) {
                    heldItem.shrink(1);
                    pLevel.setBlockAndUpdate(pPos, pState.setValue(CASING, true));
                    return InteractionResult.SUCCESS;
                } else if (FluidHelper.tryEmptyItemIntoBE(pLevel, pPlayer, pHand, heldItem, be)) {
                    return InteractionResult.SUCCESS;
                } else if (FluidHelper.tryFillItemFromBE(pLevel, pPlayer, pHand, heldItem, be)) {
                    return InteractionResult.SUCCESS;
                } else if (!GenericItemEmptying.canItemBeEmptied(pLevel, heldItem) && !GenericItemFilling.canItemBeFilled(pLevel, heldItem)) {
                    return heldItem.getItem().equals(Items.SPONGE) && !((FluidStack)be.getCapability(ForgeCapabilities.FLUID_HANDLER).map((iFluidHandler) -> {
                        return iFluidHandler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
                    }).orElse(FluidStack.EMPTY)).isEmpty() ? InteractionResult.SUCCESS : InteractionResult.PASS;
                } else {
                    return InteractionResult.SUCCESS;
                }
            } else {
                IItemHandlerModifiable inv = (IItemHandlerModifiable)be.itemCapability.orElse(new ItemStackHandler(1));
                boolean success = false;

                for(int slot = 0; slot < inv.getSlots(); ++slot) {
                    ItemStack stackInSlot = inv.getStackInSlot(slot);
                    if (!stackInSlot.isEmpty()) {
                        pPlayer.getInventory().placeItemBackInInventory(stackInSlot);
                        inv.setStackInSlot(slot, ItemStack.EMPTY);
                        success = true;
                    }
                }

                if (success) {
                    pLevel.playSound((Player)null, pPos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, 1.0F + Create.RANDOM.nextFloat());
                }

                be.onEmptied();
                return InteractionResult.SUCCESS;
            }
        });
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(new Property[]{CASING, FACING}));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos.above());
        return !(blockEntity instanceof MechanicalPressBlockEntity);
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        IBE.onRemove(state, worldIn, pos, newState);
    }

    @Override
    public Class<SqueezeBasinBlockEntity> getBlockEntityClass() {
        return SqueezeBasinBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SqueezeBasinBlockEntity> getBlockEntityType() {
        return CRBlockEntityTypes.SQUEEZE_BASIN_ENTITY.get();
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction preferredFacing = preferredFacing = context.getHorizontalDirection().getOpposite();
        return (BlockState)this.defaultBlockState().setValue(FACING, context.getPlayer() != null && context.getPlayer().isShiftKeyDown() ? preferredFacing : preferredFacing.getOpposite()).setValue(CASING, false);
    }
}
