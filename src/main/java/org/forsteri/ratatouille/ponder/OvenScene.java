package org.forsteri.ratatouille.ponder;

import org.forsteri.ratatouille.content.oven.OvenBlockEntity;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.logistics.crate.CreativeCrateBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class OvenScene {
    public OvenScene() {
    }

    public static void oven(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("oven", "Efficient Food Baking with the Oven");
        scene.configureBasePlate(0, 0, 7);
        scene.setSceneOffsetY(-1);
        scene.scaleSceneView(.9f);
        scene.world().showSection(util.select().layer(0), Direction.UP);
        scene.world().showSection(util.select().fromTo(7, 0, 0, 7, 1, 2), Direction.UP);
        scene.world().showSection(util.select().fromTo(0, 1, 7, 0, 1, 7), Direction.UP);
        scene.idle(5);

        Selection blazer = util.select().fromTo(3, 1, 3, 4, 1, 4);
        Selection smallOven = util.select().fromTo(3, 2, 3, 4, 2, 4);
        Selection smallFan = util.select().fromTo(5, 2, 3, 5, 2, 4);
        Selection bigOven = util.select().fromTo(3, 2, 5, 4, 5, 6);
        Selection bigFan = util.select().fromTo(5, 2, 5, 5, 5, 6);
        Selection inputSource = util.select().fromTo(5, 1, 2, 6, 1, 2);
        Selection outSource = util.select().fromTo(0, 1, 5, 0, 1, 6);


        scene.world().showSection(blazer, Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(smallOven, Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(50)
                .attachKeyFrame()
                .text("The oven heated by blaze burner")
                .pointAt(util.vector().centerOf(util.grid().at(3, 1, 3)))
                .placeNearTarget();
        scene.idle(60);
        scene.world().showSection(inputSource, Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(smallFan, Direction.WEST);
        scene.idle(5);

        AABB ovenFanBounds = AllShapes.CASING_13PX.get(Direction.WEST)
                .bounds();
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.GREEN, new Object(), ovenFanBounds.move(5, 2, 3), 60);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.GREEN, new Object(), ovenFanBounds.move(5, 2, 4), 60);

        scene.overlay().showText(50)
                .attachKeyFrame()
                .text("Oven fans ensure sufficient air within the oven")
                .pointAt(util.vector().centerOf(util.grid().at(5, 2, 3)))
                .placeNearTarget();
        scene.idle(60);
        scene.overlay().showText(50)
                .attachKeyFrame()
                .text("With sufficient heat, air, and stocked inventory...")
                .pointAt(util.vector().centerOf(util.grid().at(3, 2, 3)))
                .placeNearTarget();
        scene.idle(60);
        scene.world().modifyBlockEntity(util.grid().at(4, 2, 0), CreativeCrateBlockEntity.class, (be) -> {
            be.getBehaviour(FilteringBehaviour.TYPE).setFilter(new ItemStack(Items.PORKCHOP));
        });
        scene.world().showSection(outSource, Direction.DOWN);
        scene.world().showSection(util.select().fromTo(4, 1, 0, 4, 2, 2), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().fromTo(0, 1, 4, 2, 2, 4), Direction.DOWN);
        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("...It can efficiently bake all types of food!")
                .pointAt(util.vector().centerOf(util.grid().at(3, 2, 3)))
                .placeNearTarget();

        BlockPos inputPos = util.grid().at(4, 1, 2);
        ItemStack itemStack = new ItemStack(Items.PORKCHOP, 64);
        scene.world().createItemOnBelt(util.grid().at(4, 1, 1), Direction.NORTH, itemStack);

        for (int i = 0; i < 1; i++) {
            scene.idle(12);
            scene.world().modifyBlockEntity(util.grid().at(4, 2, 3), OvenBlockEntity.class, (be)-> {
                be.getControllerBE().getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(
                        (inv) -> {
                            for (int slot = 0; slot < inv.getSlots(); slot++) {
                                inv.insertItem(slot, itemStack.copy(), false);
                            }
                        }
                );
            });
            scene.world().removeItemsFromBelt(inputPos);
            scene.world().flapFunnel(inputPos.above(), false);
            scene.world().createItemOnBelt(util.grid().at(4, 1, 1), Direction.NORTH, itemStack.copy());
        }
        scene.idle(58);

        scene.world().hideSection(smallFan, Direction.EAST);
        scene.idle(5);
        scene.world().hideSection(smallOven, Direction.EAST);
        scene.idle(10);

        ElementLink<WorldSectionElement> bigOvenLink = scene.world().showIndependentSectionImmediately(bigOven);
        scene.idle(5);
        ElementLink<WorldSectionElement> bigFanLink = scene.world().showIndependentSectionImmediately(bigFan);
        scene.world().moveSection(bigFanLink, util.vector().of(0, 0, -2), 10);
        scene.world().moveSection(bigOvenLink, util.vector().of(0, 0, -2), 10);

        scene.overlay().showText(40)
                .attachKeyFrame()
                .text("Higher efficiency demands more space, heat, and airflow")
                .pointAt(util.vector().centerOf(util.grid().at(3, 3, 3)))
                .placeNearTarget();
        for (int i = 0; i < 4; i++) {
            scene.idle(12);
            scene.world().modifyBlockEntity(util.grid().at(4, 2, 5), OvenBlockEntity.class, (be)-> {
                be.getControllerBE().getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(
                        (inv) -> {
                            ItemStack insertStack = itemStack.copy();
                            for (int slot = 0; slot < inv.getSlots(); slot++) {
                                ItemStack returnedStack = inv.insertItem(slot, insertStack, false);
                                if (insertStack.isEmpty())
                                    break;
                                insertStack = returnedStack;
                            }
                        }
                );
            });

            scene.world().removeItemsFromBelt(inputPos);
            scene.world().flapFunnel(inputPos.above(), false);
            scene.world().createItemOnBelt(util.grid().at(4, 1, 1), Direction.NORTH, itemStack.copy());
        }
        scene.overlay().showText(40)
                .attachKeyFrame()
                .text("Use goggles to monitor the current oven level")
                .pointAt(util.vector().centerOf(util.grid().at(3, 3, 3)))
                .placeNearTarget();
        scene.idle(50);
    }
}
