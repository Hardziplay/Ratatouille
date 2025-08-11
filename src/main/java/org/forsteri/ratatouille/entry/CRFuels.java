package org.forsteri.ratatouille.entry;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.forsteri.ratatouille.Ratatouille;
import org.forsteri.ratatouille.entry.CRItems;

@Mod.EventBusSubscriber(modid = Ratatouille.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CRFuels {

    @SubscribeEvent
    public static void onFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        ItemStack stack = event.getItemStack();

        if (stack.is(CRItems.COMPOST_RESIDUE.get())) {
            event.setBurnTime(80);
        }

        if (stack.is(CRFluids.BIO_GAS.getBucket().get())) {
            event.setBurnTime(4000);
        }
    }
}
