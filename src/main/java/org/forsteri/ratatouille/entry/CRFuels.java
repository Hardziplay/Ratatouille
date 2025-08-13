package org.forsteri.ratatouille.entry;

import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import org.forsteri.ratatouille.Ratatouille;

@EventBusSubscriber(modid = Ratatouille.MOD_ID)
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
