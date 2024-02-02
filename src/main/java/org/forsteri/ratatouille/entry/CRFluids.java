package org.forsteri.ratatouille.entry;

import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.util.entry.FluidEntry;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.forsteri.ratatouille.Ratatouille;

public class CRFluids {
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MEAT_SOUP =
            ((FluidBuilder) Ratatouille.REGISTRATE
                .standardFluid("meat_soup")
                .source(ForgeFlowingFluid.Source::new)
                .lang("Meat soup")
                .bucket()
                .build()
            )
            .register();

    public CRFluids() {}
    public static void register() {}

}
