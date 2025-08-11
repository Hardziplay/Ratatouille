package org.forsteri.ratatouille.entry;

import com.simibubi.create.AllFluids;
import com.simibubi.create.content.fluids.VirtualFluid;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.util.entry.FluidEntry;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.forsteri.ratatouille.Ratatouille;
import org.joml.Vector3f;

import java.util.function.Supplier;

public class CRFluids {
    public static final FluidEntry<ForgeFlowingFluid.Flowing> COCOA_LIQUOR =
            Ratatouille.REGISTRATE
            .standardFluid("cocoa_liquor", BaseFluidType.create(6430752, () -> {return 0.1f;}))
            .lang("Cocoa Liquor")
            .register();


    public static final FluidEntry<VirtualFluid> CAKE_BATTER =
            Ratatouille.REGISTRATE
                    .virtualFluid("cake_batter")
                    .lang("Cake Batter")
                    .register();

    public static final FluidEntry<VirtualFluid> MINCE_MEAT =
            Ratatouille.REGISTRATE
                    .virtualFluid("mince_meat")
                    .lang("Mince Meat")
                    .register();

    public static final FluidEntry<VirtualFluid> EGG_YOLK =
            Ratatouille.REGISTRATE
                    .virtualFluid("egg_yolk")
                    .lang("Egg Yolk")
                    .register();

    public static final FluidEntry<ForgeFlowingFluid.Flowing> COMPOST_TEA =
            Ratatouille.REGISTRATE
                    .standardFluid("compost_tea")
                    .properties(p -> p
                            .density(1050))
                    .lang("Compost Tea")
                    .source(ForgeFlowingFluid.Source::new).block().build()
                    .bucket().build().register();

    public static final FluidEntry<ForgeFlowingFluid.Flowing> BIO_GAS =
            Ratatouille.REGISTRATE
                    .standardFluid("bio_gas")
                    .properties(p -> p
                            .density(-150))
                    .lang("Biogas")
                    .source(ForgeFlowingFluid.Source::new).block().build()
                    .bucket().build().register();

    public static final FluidEntry<VirtualFluid> COMPOST_FLUID =
            Ratatouille.REGISTRATE
                    .virtualFluid("compost_fluid")
                    .properties(p -> p
                            .density(1400))
                    .lang("Compost Fluid")
                    .register();


    public static final FluidEntry<ForgeFlowingFluid.Flowing> COMPOST_RESIDUE_FLUID =
            Ratatouille.REGISTRATE
                    .standardFluid("compost_residue_fluid")
                    .properties(p -> p
                            .density(1450))
                    .lang("Compost Residue Fluid")
                    .source(ForgeFlowingFluid.Source::new).block().build()
                    .bucket().build().register();


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
            return NO_TINT;
        }

        public int getTintColor(FluidState state, BlockAndTintGetter world, BlockPos pos) {
            return 16777215;
        }

        protected Vector3f getCustomFogColor() {
            return this.fogColor;
        }

        protected float getFogDistanceModifier() {
            return this.fogDistance.get();
        }
    }

}
