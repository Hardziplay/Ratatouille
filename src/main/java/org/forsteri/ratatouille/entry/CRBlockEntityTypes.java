package org.forsteri.ratatouille.entry;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import org.forsteri.ratatouille.Ratatouille;
import org.forsteri.ratatouille.content.demolder.DemolderInstance;
import org.forsteri.ratatouille.content.demolder.MechanicalDemolderBlockEntity;
import org.forsteri.ratatouille.content.demolder.MechanicalDemolderRenderer;
import org.forsteri.ratatouille.content.oven.OvenBlockEntity;
import org.forsteri.ratatouille.content.oven.OvenRenderer;
import org.forsteri.ratatouille.content.oven_fan.OvenFanBlockEntity;
import org.forsteri.ratatouille.content.oven_fan.OvenFanInstance;
import org.forsteri.ratatouille.content.oven_fan.OvenFanRenderer;
import org.forsteri.ratatouille.content.squeeze_basin.SqueezeBasinBlockEntity;
import org.forsteri.ratatouille.content.squeeze_basin.SqueezeBasinInstance;
import org.forsteri.ratatouille.content.thresher.ThresherBlockEntity;
import org.forsteri.ratatouille.content.thresher.ThresherInstance;
import org.forsteri.ratatouille.content.thresher.ThresherRenderer;

public class CRBlockEntityTypes {
    public CRBlockEntityTypes() {}
    public static final BlockEntityEntry<OvenBlockEntity> OVEN_ENTITY = Ratatouille.REGISTRATE
            .blockEntity("oven", OvenBlockEntity::new)
            .renderer(() -> OvenRenderer::new)
            .validBlock(CRBlocks.OVEN)
            .register();
    public static final BlockEntityEntry<ThresherBlockEntity> THRESHER_ENTITY = Ratatouille.REGISTRATE
            .blockEntity("thresher", ThresherBlockEntity::new)
            .instance(() -> ThresherInstance::new)
            .validBlock(CRBlocks.THRESHER)
            .renderer(() -> ThresherRenderer::new)
            .register();
    public static final BlockEntityEntry<OvenFanBlockEntity> OVEN_FAN_ENTITY = Ratatouille.REGISTRATE
            .blockEntity("oven_fan", OvenFanBlockEntity::new)
            .instance(() -> OvenFanInstance::new)
            .validBlock(CRBlocks.OVEN_FAN)
            .renderer(() -> OvenFanRenderer::new)
            .register();

    public static final BlockEntityEntry<SqueezeBasinBlockEntity> SQUEEZE_BASIN_ENTITY = Ratatouille.REGISTRATE
            .blockEntity("squeeze_basin", SqueezeBasinBlockEntity::new)
            .instance(() -> SqueezeBasinInstance::new)
            .validBlock(CRBlocks.SQUEEZE_BASIN)
            .register();

    public static final BlockEntityEntry<MechanicalDemolderBlockEntity> MECHANICAL_DEMOLDER_ENTITY = Ratatouille.REGISTRATE
            .blockEntity("mechanical_demolder", MechanicalDemolderBlockEntity::new)
            .instance(() -> DemolderInstance::new)
            .validBlock(CRBlocks.MECHANICAL_DEMOLDER)
            .renderer(() -> MechanicalDemolderRenderer::new)
            .register();

    public static void register() {}
}
