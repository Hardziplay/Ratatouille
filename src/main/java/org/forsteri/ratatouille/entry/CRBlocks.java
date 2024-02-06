package org.forsteri.ratatouille.entry;

import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MaterialColor;
import org.forsteri.ratatouille.Ratatouille;
import org.forsteri.ratatouille.content.oven.*;
import org.forsteri.ratatouille.content.oven_fan.OvenFanBlock;
import org.forsteri.ratatouille.content.squeeze_basin.SqueezeBasinBlock;
import org.forsteri.ratatouille.content.squeeze_basin.SqueezeBasinGenerator;
import org.forsteri.ratatouille.content.thresher.ThresherBlock;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

public class CRBlocks {

    static {
        Ratatouille.REGISTRATE.creativeModeTab(() -> {
            return CRCreativeModeTabs.BASE_CREATIVE_TAB;
        });
    }
    @SuppressWarnings("removal")
    public static final BlockEntry<OvenBlock> OVEN = Ratatouille.REGISTRATE
            .block("oven", OvenBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.lightLevel(state -> 1).isRedstoneConductor((p1, p2, p3) -> true).noOcclusion())
            .transform(pickaxeOnly())
            .blockstate(new OvenModel.OvenGenerator()::generate)
            .onRegister(CreateRegistrate.blockModel(() -> originalModel -> new OvenModel(originalModel,
                    CRSpriteShifts.OVEN_SPRITE, CRSpriteShifts.OVEN_SPRITE_TOP, CRSpriteShifts.OVEN_SPRITE_TOP_INNER, CRSpriteShifts.OVEN_SPRITE_BOTTOM, CRSpriteShifts.OVEN_SPRITE_BOTTOM_INNER, CRSpriteShifts.OVEN_SPRITE_SHIFT_2x2)))
            .addLayer(() -> RenderType::cutoutMipped)
            .item(OvenBlockItem::new)
            .build()
            .register();

    @SuppressWarnings("removal")
    public static final BlockEntry<ThresherBlock> THRESHER = Ratatouille.REGISTRATE
            .block("thresher", ThresherBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.lightLevel(state -> 1).noOcclusion().isRedstoneConductor((p1, p2, p3) -> true))
            .transform(pickaxeOnly())
            .blockstate((c, p) -> p.horizontalBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p), 270))
            .addLayer(() -> RenderType::cutoutMipped)
            .item()
            .model((c, p) -> p.withExistingParent(c.getName(), new ResourceLocation(Ratatouille.MOD_ID, "block/thresher/item")))
            .build()
            .register();

    @SuppressWarnings("removal")
    public static final BlockEntry<OvenFanBlock> OVEN_FAN = Ratatouille.REGISTRATE
            .block("oven_fan", OvenFanBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().color(MaterialColor.STONE))
            .transform(pickaxeOnly())
            .blockstate(BlockStateGen.horizontalBlockProvider(true))
            .addLayer(() -> RenderType::cutoutMipped)
            .item()
            .model((c, p) -> p.withExistingParent(c.getName(), new ResourceLocation(Ratatouille.MOD_ID, "block/oven_fan/item")))
            .build()
            .register();

    @SuppressWarnings("removal")
    public static final BlockEntry<SqueezeBasinBlock> SQUEEZE_BASIN = Ratatouille.REGISTRATE
            .block("squeeze_basin", SqueezeBasinBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().color(MaterialColor.STONE))
            .transform(pickaxeOnly())
            .blockstate(new SqueezeBasinGenerator()::generate)
            .addLayer(() -> RenderType::cutoutMipped)
            .item()
            .model((c, p) -> p.withExistingParent(c.getName(), new ResourceLocation(Ratatouille.MOD_ID, "block/squeeze_basin/item")))
            .build()
            .register();

    public static final BlockEntry<Block> SUGAR_BLOCK = Ratatouille.REGISTRATE
            .block("sugar_block", Block::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().color(MaterialColor.STONE))
            .transform(pickaxeOnly())
            .item()
            .build()
            .register();

    public static final BlockEntry<Block> SOLID_SUGAR_BLOCK = Ratatouille.REGISTRATE
            .block("solid_sugar_block", Block::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().color(MaterialColor.STONE))
            .transform(pickaxeOnly())
            .item()
            .build()
            .register();

    public static void register() {}
}
