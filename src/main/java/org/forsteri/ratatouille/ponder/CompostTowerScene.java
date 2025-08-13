package org.forsteri.ratatouille.ponder;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import org.forsteri.ratatouille.entry.CRItems;

public class CompostTowerScene {
    public CompostTowerScene() {
    }

    public static void tower(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.scaleSceneView(.9f);

        scene.title("compost_tower", "Turn organic waste into compost");
        scene.configureBasePlate(0, 0, 5);
        scene.world().showSection(util.select().everywhere(), Direction.UP);
        //scene.world().setKineticSpeed(util.select().position(0, 0, 1), -32);
        scene.world().createItemOnBelt(util.grid().at(3, 1, 1), Direction.NORTH, new ItemStack(CRItems.COMPOST_MASS.get(), 64));
        scene.idle(40);
        scene.world().setKineticSpeed(util.select().everywhere(), 32);
        scene.idle(50);
    }
}
