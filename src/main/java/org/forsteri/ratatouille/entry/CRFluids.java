package org.forsteri.ratatouille.entry;

import com.mojang.math.Vector3f;
import com.simibubi.create.AllFluids;
import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.content.fluids.VirtualFluid;
import com.simibubi.create.foundation.utility.Color;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.util.entry.FluidEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.forsteri.ratatouille.Ratatouille;

import java.util.function.Supplier;

public class CRFluids {
    public static final FluidEntry<ForgeFlowingFluid.Flowing> COCOA_LIQUOR =
            Ratatouille.REGISTRATE
            .standardFluid("cocoa_liquor", BaseFluidType.create(6430752, () -> {return 0.1f;}))
            .lang("Cocoa Liquor")
            .register();


    public static final FluidEntry<ForgeFlowingFluid.Flowing> CAKE_BATTER =
            Ratatouille.REGISTRATE
                    .standardFluid("cake_batter")
                    .noBucket()
                    .lang("Cake Batter")
                    .register();

    public static final FluidEntry<ForgeFlowingFluid.Flowing> MINCE_MEAT =
            Ratatouille.REGISTRATE
                    .standardFluid("mince_meat")
                    .noBucket()
                    .lang("Mince Meat")
                    .register();

    public static final FluidEntry<ForgeFlowingFluid.Flowing> EGG_YOLK =
            Ratatouille.REGISTRATE
                    .standardFluid("egg_yolk")
                    .noBucket()
                    .lang("Egg Yolk")
                    .register();




    public CRFluids() {}
    public static void register() {}

    private static class BaseFluidType extends AllFluids.TintedFluidType {
        private Vector3f fogColor;
        private Supplier<Float> fogDistance;

        public static FluidBuilder.FluidTypeFactory create(int fogColor, Supplier<Float> fogDistance) {
            return (p, s, f) -> {
                BaseFluidType fluidType = new BaseFluidType(p, s, f);
                fluidType.fogColor = (new Color(fogColor, false)).asVectorF();
                fluidType.fogDistance = fogDistance;
                return fluidType;
            };
        }

        private BaseFluidType(FluidType.Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
            super(properties, stillTexture, flowingTexture);
        }

        protected int getTintColor(FluidStack stack) {
            return -1;
        }

        public int getTintColor(FluidState state, BlockAndTintGetter world, BlockPos pos) {
            return 16777215;
        }

        protected Vector3f getCustomFogColor() {
            return this.fogColor;
        }

        protected float getFogDistanceModifier() {
            return (Float)this.fogDistance.get();
        }
    }

}
