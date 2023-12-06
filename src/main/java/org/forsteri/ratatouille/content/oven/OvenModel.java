package org.forsteri.ratatouille.content.oven;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.fluids.tank.FluidTankCTBehaviour;
import com.simibubi.create.content.fluids.tank.FluidTankGenerator;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlock;
import com.simibubi.create.foundation.block.connected.CTModel;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.utility.Iterate;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.client.model.generators.ModelFile;
import org.forsteri.ratatouille.entry.Registrate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class OvenModel extends CTModel {
    public OvenModel(BakedModel originalModel, CTSpriteShiftEntry side, CTSpriteShiftEntry top,
                           CTSpriteShiftEntry topInner, CTSpriteShiftEntry bottom, CTSpriteShiftEntry bottomInner, CTSpriteShiftEntry shift2x2) {
        super(originalModel, new OvenCTBehavior(side, top, topInner, bottom, bottomInner, shift2x2));
    }

    public static class OvenCTBehavior extends FluidTankCTBehaviour {
        private final CTSpriteShiftEntry bottomShift;
        private final CTSpriteShiftEntry bottomInnerShift;
        private final CTSpriteShiftEntry shift2x2;

        public OvenCTBehavior(CTSpriteShiftEntry layerShift, CTSpriteShiftEntry topShift, CTSpriteShiftEntry innerShift,
                              CTSpriteShiftEntry bottom, CTSpriteShiftEntry bottomInner, CTSpriteShiftEntry shift2x2) {
            super(layerShift, topShift, innerShift);
            this.bottomShift = bottom;
            this.bottomInnerShift = bottomInner;
            this.shift2x2 = shift2x2;
        }

        @Override
        public CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
            if (sprite != null && direction.getAxis() == Direction.Axis.Y && bottomShift.getOriginal() == sprite)
                return bottomShift;
            if (sprite != null && direction.getAxis() == Direction.Axis.Y && bottomInnerShift.getOriginal() == sprite)
                return bottomInnerShift;
            CTSpriteShiftEntry shift = super.getShift(state, direction, sprite);
            if (shift == Registrate.OVEN_SPRITE && state.getValue(OvenBlock.IS_2x2))
                return shift2x2;
            return shift;
        }
    }

    protected static final ModelProperty<CullData> CULL_PROPERTY = new ModelProperty<>();

    @Override
    protected ModelData.Builder gatherModelData(ModelData.Builder builder, BlockAndTintGetter world, BlockPos pos, BlockState state,
                                                ModelData blockEntityData) {
        super.gatherModelData(builder, world, pos, state, blockEntityData);
        CullData cullData = new CullData();
        for (Direction d : Iterate.directions) {
            cullData.setCulled(d, ConnectivityHandler.isConnected(world, pos, pos.relative(d)) ||
                    (world.getBlockState(pos.relative(d)).getBlock() instanceof EncasedFanBlock &&
                            world.getBlockState(pos.relative(d)).getValue(EncasedFanBlock.FACING) == d.getOpposite()));
        }

        cullData.setCulled(null, !(ConnectivityHandler.isConnected(world, pos, pos.above()) ||
                ConnectivityHandler.isConnected(world, pos, pos.below())));

        return builder.with(CULL_PROPERTY, cullData);
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData extraData, RenderType renderType) {
        if (side != null)
            return Collections.emptyList();

        List<BakedQuad> quads = new ArrayList<>();
        for (Direction d : Iterate.directions) {
            if (extraData.has(CULL_PROPERTY) && Objects.requireNonNull(extraData.get(CULL_PROPERTY))
                    .isCulled(d))
                continue;
            quads.addAll(super.getQuads(state, d, rand, extraData, renderType));
        }

        if (extraData.has(CULL_PROPERTY) && !Objects.requireNonNull(extraData.get(CULL_PROPERTY))
                .isCulled(null))
            quads.addAll(super.getQuads(state, null, rand, extraData, renderType));
        return quads;
    }

    private static class CullData {
        Map<Direction, Boolean> culledFaces = new HashMap<>();

        public CullData() {
            for (Direction d : Iterate.directions)
                culledFaces.put(d, false);
        }

        void setCulled(Direction face, boolean cull) {
            culledFaces.put(face, cull);
        }

        boolean isCulled(Direction face) {
            return culledFaces.get(face);
        }
    }

    public static class OvenGenerator extends FluidTankGenerator {
        protected ModelFile model = null;

        @Override
        public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
                                                    BlockState state) {
            if (model != null)
                return model;
            return model = prov.models()
                    .withExistingParent(ctx.getName(), prov.mcLoc("block/block"))
                    .element()
                        .from(16, 16, 16)
                        .to(0, 0, 0)
                        .face(Direction.UP)
                            .texture("#inner_bottom")
                            .cullface(Direction.DOWN)
                        .end()
                        .face(Direction.DOWN)
                            .texture("#inner_top")
                            .cullface(Direction.UP)
                        .end()
                        .face(Direction.NORTH)
                            .texture("#inner_side")
                            .cullface(Direction.SOUTH)
                        .end()
                        .face(Direction.SOUTH)
                            .texture("#inner_side")
                            .cullface(Direction.NORTH)
                        .end()
                        .face(Direction.EAST)
                            .texture("#inner_side")
                            .cullface(Direction.WEST)
                        .end()
                        .face(Direction.WEST)
                            .texture("#inner_side")
                            .cullface(Direction.EAST)
                        .end()
                    .end()
                    .element()
                        .from(0, 0, 0)
                        .to(16, 16, 16)
                        .face(Direction.UP)
                            .texture("#top")
                            .cullface(Direction.UP)
                        .end()
                        .face(Direction.DOWN)
                            .texture("#bottom")
                            .cullface(Direction.DOWN)
                        .end()
                        .face(Direction.NORTH)
                            .texture("#side")
                            .cullface(Direction.NORTH)
                        .end()
                        .face(Direction.SOUTH)
                            .texture("#side")
                            .cullface(Direction.SOUTH)
                        .end()
                        .face(Direction.EAST)
                            .texture("#side")
                            .cullface(Direction.EAST)
                        .end()
                        .face(Direction.WEST)
                            .texture("#side")
                            .cullface(Direction.WEST)
                        .end()
                    .end()
                    .element()
                        .from(0, 7, 0)
                        .to(16, 7, 16)
                        .face(Direction.UP)
                            .texture("#layer")
                        .end()
                        .face(Direction.DOWN)
                            .texture("#layer")
                        .end()
                    .end()
                    .texture("particle", prov.modLoc("block/oven/oven"))
                    .texture("top", prov.modLoc("block/oven/oven_top"))
                    .texture("bottom", prov.modLoc("block/oven/oven_bottom"))
                    .texture("side", prov.modLoc("block/oven/oven"))
                    .texture("inner_top", prov.modLoc("block/oven/oven_top_inner"))
                    .texture("inner_bottom", prov.modLoc("block/oven/oven_bottom_inner"))
                    .texture("inner_side", prov.modLoc("block/oven/oven"))
                    .texture("layer", prov.modLoc("block/oven/oven_layer"));
        }
    }
}
