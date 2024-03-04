package org.forsteri.ratatouille.ponders;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import com.simibubi.create.foundation.utility.Pointing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.forsteri.ratatouille.content.irrigation_tower.IrrigationTowerBlockEntity;

public class IrrigationTowerScene {
    public static void irrigationTower(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("irrigation_tower", "Usage of thresher");
        scene.configureBasePlate(0, 0, 5);
        scene.world.showSection(util.select.layer(0), Direction.UP);

        scene.idle(5);
        BlockPos towerPos = util.grid.at(2, 1, 2);
        scene.world.showSection(util.select.fromTo(towerPos, towerPos), Direction.DOWN);

        scene.overlay.showText(50)
                .text("Irrigation towers keep range farmland moist")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector.topOf(towerPos));
        scene.idle(60);

        scene.overlay.showText(50)
                .text("Tanks need to be filled with water")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector.topOf(towerPos));
        scene.world.setBlock(towerPos.below().west(), Blocks.FARMLAND.defaultBlockState(), true);
        scene.world.setBlock(towerPos.below().south(), Blocks.FARMLAND.defaultBlockState(), true);
        scene.world.setBlock(towerPos.below().east(), Blocks.FARMLAND.defaultBlockState(), true);
        scene.world.setBlock(towerPos.below().north(), Blocks.FARMLAND.defaultBlockState(), true);
        scene.idle(10);
        scene.overlay.showControls(
                new InputWindowElement(util.vector.blockSurface(towerPos, Direction.UP), Pointing.DOWN)
                        .withItem(new ItemStack(Items.WATER_BUCKET))
                        .rightClick(),
                60);
        scene.world.modifyBlockEntity(towerPos, IrrigationTowerBlockEntity.class, (be) -> {be.getTankInventory().fill(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE);});
        scene.idle(60);
        scene.world.setBlock(towerPos.below().north(), Blocks.FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE, 7), false);
        scene.idle(10);
        scene.world.setBlock(towerPos.below().west(), Blocks.FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE, 7), false);
        scene.idle(20);
        scene.world.setBlock(towerPos.below().south(), Blocks.FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE, 7), false);
        scene.idle(5);
        scene.world.setBlock(towerPos.below().east(), Blocks.FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE, 7), false);
    }
}
