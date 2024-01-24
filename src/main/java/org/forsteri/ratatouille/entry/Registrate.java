package org.forsteri.ratatouille.entry;

import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.content.kinetics.crafter.ShaftlessCogwheelInstance;
import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.forsteri.ratatouille.Ratatouille;
import org.forsteri.ratatouille.content.oven.*;
import org.forsteri.ratatouille.content.oven_fan.OvenFanBlock;
import org.forsteri.ratatouille.content.oven_fan.OvenFanBlockEntity;
import org.forsteri.ratatouille.content.oven_fan.OvenFanRenderer;
import org.forsteri.ratatouille.content.thresher.ThresherBlock;
import org.forsteri.ratatouille.content.thresher.ThresherBlockEntity;
import org.forsteri.ratatouille.content.thresher.ThresherInstance;
import org.forsteri.ratatouille.content.thresher.ThresherRenderer;

import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

public class Registrate {
    public static final CTSpriteShiftEntry OVEN_SPRITE = getCT("oven/oven"),
                                            OVEN_SPRITE_TOP = getCT("oven/oven_top"),
                                            OVEN_SPRITE_TOP_INNER = getCT("oven/oven_top_inner"),
                                            OVEN_SPRITE_BOTTOM = getCT("oven/oven_bottom"),
                                            OVEN_SPRITE_BOTTOM_INNER = getCT("oven/oven_bottom_inner"),
                                            OVEN_SPRITE_SHIFT_2x2 = getCT("oven/oven", "oven/oven_2x2");

    public static final PartialModel THRESHER_BLADE = of("block/thresher/partial");

    @SuppressWarnings("removal")
    public static final BlockEntry<OvenBlock> OVEN = Ratatouille.REGISTRATE
            .block("oven", OvenBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.lightLevel(state -> 1).isRedstoneConductor((p1, p2, p3) -> true))
            .transform(pickaxeOnly())
            .blockstate(new OvenModel.OvenGenerator()::generate)
            .onRegister(CreateRegistrate.blockModel(() -> originalModel -> new OvenModel(originalModel,
                    OVEN_SPRITE, OVEN_SPRITE_TOP, OVEN_SPRITE_TOP_INNER, OVEN_SPRITE_BOTTOM, OVEN_SPRITE_BOTTOM_INNER, OVEN_SPRITE_SHIFT_2x2)))
            .addLayer(() -> RenderType::cutoutMipped)
            .item(OvenBlockItem::new)
            .build()
            .register();

    public static final BlockEntityEntry<OvenBlockEntity> OVEN_ENTITY = Ratatouille.REGISTRATE
            .blockEntity("oven", OvenBlockEntity::new)
            .renderer(() -> OvenRenderer::new)
            .validBlock(OVEN)
            .register();

    @SuppressWarnings("removal")
    public static final BlockEntry<ThresherBlock> THRESHER = Ratatouille.REGISTRATE
            .block("thresher", ThresherBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.lightLevel(state -> 1).noOcclusion().isRedstoneConductor((p1, p2, p3) -> true))
            .transform(pickaxeOnly())
            .blockstate((c, p) -> p.horizontalBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p), 270))
            .addLayer(() -> RenderType::cutoutMipped)
            .item()
            .model((c, p) -> p.withExistingParent(c.getName(), new ResourceLocation(Ratatouille.MOD_ID, "block/thresher/item")))
            .build()
            .register();

    public static final BlockEntityEntry<ThresherBlockEntity> THRESHER_ENTITY = Ratatouille.REGISTRATE
            .blockEntity("thresher", ThresherBlockEntity::new)
            .instance(() -> ThresherInstance::new)
            .validBlock(THRESHER)
            .renderer(() -> ThresherRenderer::new)
            .register();

    @SuppressWarnings("removal")
    public static final BlockEntry<OvenFanBlock> OVEN_FAN = Ratatouille.REGISTRATE
            .block("oven_fan", OvenFanBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.lightLevel(state -> 1).noOcclusion().isRedstoneConductor((p1, p2, p3) -> true))
            .transform(pickaxeOnly())
            .blockstate((c, p) -> p.horizontalBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p), 270))
            .addLayer(() -> RenderType::cutoutMipped)
            .item()
            .model((c, p) -> p.withExistingParent(c.getName(), new ResourceLocation(Ratatouille.MOD_ID, "block/oven_fan/item")))
            .build()
            .register();

    public static final BlockEntityEntry<OvenFanBlockEntity> OVEN_FAN_ENTITY = Ratatouille.REGISTRATE
            .blockEntity("oven_fan", OvenFanBlockEntity::new)
            .instance(() -> ShaftlessCogwheelInstance::new)
            .validBlock(OVEN_FAN)
            .renderer(() -> OvenFanRenderer::new)
            .register();

    private static CTSpriteShiftEntry getCT(String blockTextureName, String connectedTextureName) {
        return CTSpriteShifter.getCT(AllCTTypes.RECTANGLE, new ResourceLocation(Ratatouille.MOD_ID, "block/" + blockTextureName),
                new ResourceLocation(Ratatouille.MOD_ID, "block/" + connectedTextureName + "_connected"));
    }

    private static CTSpriteShiftEntry getCT(String blockTextureName) {
        return getCT(blockTextureName, blockTextureName);
    }

    private static PartialModel of(@SuppressWarnings("SameParameterValue") String path) {
        return new PartialModel(new ResourceLocation(Ratatouille.MOD_ID, path));
    }

    public static void register() {}
}
