package org.forsteri.ratatouille.content.compost_tower;

import com.simibubi.create.content.fluids.transfer.ItemDrainBlockEntity;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.createmod.catnip.lang.LangBuilder;
import org.forsteri.ratatouille.util.Lang;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class CompostTowerBlockEntity extends ItemDrainBlockEntity implements IHaveGoggleInformation {
    public CompostTowerBlockEntity(BlockEntityType<? extends ItemDrainBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        LazyOptional<IItemHandler> items = getCapability(ForgeCapabilities.ITEM_HANDLER);
        LazyOptional<IFluidHandler> fluids = getCapability(ForgeCapabilities.FLUID_HANDLER);
        boolean empty = true;

        IItemHandler handler = items.orElse(new ItemStackHandler());
        for (int i = 0; i < handler.getSlots(); i++) {
            if (handler.getStackInSlot(i).isEmpty())
                continue;
            Lang.text("")
                    .add(Component.translatable(handler.getStackInSlot(i).getDescriptionId())
                            .withStyle(ChatFormatting.GRAY))
                    .add(Lang.text(" x" + handler.getStackInSlot(i).getCount())
                            .style(ChatFormatting.GREEN))
                    .forGoggles(tooltip, 1);
            empty = false;
        }

        LangBuilder mb = Lang.translate("generic.unit.millibuckets");
        IFluidHandler fh = fluids.orElse(null);
        if (fh != null)
            for (int i = 0; i < fh.getTanks(); i++) {
                FluidStack fs = fh.getFluidInTank(i);
                if (fs.isEmpty())
                    continue;
                Lang.text("")
                        .add(Lang.fluidName(fs)
                                .add(Lang.text(" "))
                                .style(ChatFormatting.GRAY)
                                .add(Lang.number(fs.getAmount())
                                        .add(mb)
                                        .style(ChatFormatting.BLUE)))
                        .forGoggles(tooltip, 1);
                empty = false;
            }

        return !empty;
    }
}
