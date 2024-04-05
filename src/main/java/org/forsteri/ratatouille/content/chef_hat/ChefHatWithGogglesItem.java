package org.forsteri.ratatouille.content.chef_hat;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import net.minecraft.world.entity.EquipmentSlot;
import org.forsteri.ratatouille.entry.CRItems;

public class ChefHatWithGogglesItem extends GogglesItem {
    static {
        addIsWearingPredicate(player -> CRItems.CHEF_HAT_WITH_GOGGLES.isIn(player.getItemBySlot(EquipmentSlot.HEAD)));
    }
    public ChefHatWithGogglesItem(Properties properties) {
        super(properties);
    }
}
