package org.forsteri.ratatouille.entry;

import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.forsteri.ratatouille.Ratatouille;
import org.forsteri.ratatouille.content.oven.OvenBlock;
import org.forsteri.ratatouille.content.oven.OvenBlockEntity;
import org.forsteri.ratatouille.content.oven.OvenBlockItem;
import org.forsteri.ratatouille.content.oven.OvenModel;

import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

public class Registrate {
    public static final CTSpriteShiftEntry OVEN_SPRITE = getCT("oven/oven"),
                                            OVEN_SPRITE_TOP = getCT("oven/oven_top"),
                                            OVEN_SPRITE_TOP_INNER = getCT("oven/oven_top_inner"),
                                            OVEN_SPRITE_BOTTOM = getCT("oven/oven_bottom"),
                                            OVEN_SPRITE_BOTTOM_INNER = getCT("oven/oven_bottom_inner"),
                                            OVEN_SPRITE_SHIFT_2x2 = getCT("oven/oven", "oven/oven_2x2");

    @SuppressWarnings("removal")
    public static final BlockEntry<OvenBlock> OVEN = Ratatouille.REGISTRATE
            .block("oven", OvenBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.lightLevel(state -> 1).noOcclusion().isRedstoneConductor((p1, p2, p3) -> true))
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
            .validBlock(OVEN)
            .register();

    private static CTSpriteShiftEntry getCT(String blockTextureName, String connectedTextureName) {
        return CTSpriteShifter.getCT(AllCTTypes.RECTANGLE, new ResourceLocation(Ratatouille.MOD_ID, "block/" + blockTextureName),
                new ResourceLocation(Ratatouille.MOD_ID, "block/" + connectedTextureName + "_connected"));
    }

    private static CTSpriteShiftEntry getCT(String blockTextureName) {
        return getCT(blockTextureName, blockTextureName);
    }

    public static void register() {}
}
