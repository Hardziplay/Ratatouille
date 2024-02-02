package org.forsteri.ratatouille.entry;

import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.util.entry.FluidEntry;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.forsteri.ratatouille.Ratatouille;

public class CRFluids {
    public static final FluidEntry<ForgeFlowingFluid.Flowing> COCOA_LIQUOR =
            ((FluidBuilder) Ratatouille.REGISTRATE
                .standardFluid("cocoa_liquor")
                .source(ForgeFlowingFluid.Source::new)
                .lang("Cocoa liquor")
                .bucket()
                .build()
            )
            .register();

    public CRFluids() {}
    public static void register() {}

}
