package org.forsteri.ratatouille.entry.tab;

import net.minecraft.world.item.ItemStack;
import org.forsteri.ratatouille.entry.Registrate;
import org.jetbrains.annotations.NotNull;

public class CRBaseCreativeModeTab extends CRCreativeModeTab {
    public CRBaseCreativeModeTab() {
        super("base");
    }

    public @NotNull ItemStack makeIcon() {
        return Registrate.OVEN.asStack();
    }
}
