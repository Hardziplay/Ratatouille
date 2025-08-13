package org.forsteri.ratatouille.entry;

import net.neoforged.fml.common.EventBusSubscriber;
import org.forsteri.ratatouille.content.compost_tower.CompostTowerBlockEntity;
import org.forsteri.ratatouille.content.demolder.MechanicalDemolderBlockEntity;
import org.forsteri.ratatouille.content.oven.OvenBlockEntity;
import org.forsteri.ratatouille.content.spreader.SpreaderBlockEntity;
import org.forsteri.ratatouille.content.squeeze_basin.SqueezeBasinBlockEntity;
import org.forsteri.ratatouille.content.thresher.ThresherBlockEntity;

@EventBusSubscriber
public class CREvents {
    @net.neoforged.bus.api.SubscribeEvent
    public static void registerCapabilities(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event) {
        CompostTowerBlockEntity.registerCapabilities(event);
        MechanicalDemolderBlockEntity.registerCapabilities(event);
        CompostTowerBlockEntity.registerCapabilities(event);
        OvenBlockEntity.registerCapabilities(event);
        SpreaderBlockEntity.registerCapabilities(event);
        SqueezeBasinBlockEntity.registerCapabilities(event);
        ThresherBlockEntity.registerCapabilities(event);
    }
}
