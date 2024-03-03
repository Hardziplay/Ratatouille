package org.forsteri.ratatouille.ponders;

import com.simibubi.create.foundation.ponder.ElementLink;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.element.EntityElement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.forsteri.ratatouille.content.thresher.ThresherBlockEntity;
import org.forsteri.ratatouille.entry.CRItems;

public class ThresherScene {
    public static void thresher(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("thresher", "Usage of thresher");
        scene.configureBasePlate(0, 0, 5);
        scene.world.showSection(util.select.layer(0), Direction.UP);
        scene.world.showSection(util.select.fromTo(2, 1, 3, 2, 2, 5), Direction.UP);
        scene.idle(5);
        BlockPos thresherPos = util.grid.at(2, 2, 2);
        scene.world.showSection(util.select.fromTo(thresherPos, thresherPos), Direction.DOWN);
        scene.overlay.showText(50)
                .text("Threshers can be used to process crops")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector.topOf(thresherPos));
        scene.idle(60);

        scene.overlay.showText(50)
                .text("The product will slide out of the side with the output slot")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector.topOf(thresherPos));
        scene.world.createItemEntity(util.vector.centerOf(thresherPos.above(2)), Vec3.ZERO, new ItemStack(Items.WHEAT));
        scene.idle(30);
        scene.world.modifyBlockEntity(thresherPos, ThresherBlockEntity.class, (be) -> {
            be.inputInv.setStackInSlot(0, ItemStack.EMPTY);
        });
        ElementLink<EntityElement> resultItem = scene.world.createItemEntity(util.vector.centerOf(1, 1, 2), Vec3.ZERO, new ItemStack(CRItems.WHEAT_KERNELS.get()));
        scene.idle(30);

        scene.world.modifyEntity(resultItem, (e) -> {e.remove(Entity.RemovalReason.KILLED);});
        BlockPos funnelPos = util.grid.at(3, 2, 2);
        scene.world.showSection(util.select.fromTo(0, 1, 2, 4, 1,2), Direction.WEST);
        scene.idle(5);
        scene.world.showSection(util.select.fromTo(funnelPos, funnelPos), Direction.WEST);
        scene.idle(5);
        scene.overlay.showText(50)
                .text("Can work with belt")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector.topOf(thresherPos));
        scene.idle(5);
        ElementLink<EntityElement> itemLink = scene.world.createItemEntity(util.vector.centerOf(4, 3, 2), Vec3.ZERO, new ItemStack(Items.WHEAT));
        scene.idle(10);
        scene.world.modifyEntity(itemLink, (e) -> {e.remove(Entity.RemovalReason.KILLED);});
        scene.world.createItemOnBeltLike(util.grid.at(4, 1, 2), Direction.EAST, new ItemStack(Items.WHEAT));
        scene.idle(6);
        scene.world.flapFunnel(util.grid.at(3, 2, 2), false);
        scene.world.removeItemsFromBelt(util.grid.at(3, 1, 2));
        scene.world.modifyBlockEntity(thresherPos, ThresherBlockEntity.class, (be) -> {
            be.lastRecipe = null;
            be.timer = 200;
            be.inputInv.insertItem(0, new ItemStack(Items.WHEAT), false);
        });
        scene.idle(26);
        scene.world.modifyBlockEntity(thresherPos, ThresherBlockEntity.class, (be) -> {
            be.inputInv.setStackInSlot(0, ItemStack.EMPTY);
        });
        scene.world.createItemOnBeltLike(util.grid.at(1, 1, 2), Direction.EAST, new ItemStack(CRItems.WHEAT_KERNELS.get()));
        scene.idle(10);
    }
}
