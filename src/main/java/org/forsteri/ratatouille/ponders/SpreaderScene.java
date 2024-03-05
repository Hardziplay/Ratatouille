package org.forsteri.ratatouille.ponders;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.depot.DepotBehaviour;
import com.simibubi.create.content.logistics.depot.DepotBlockEntity;
import com.simibubi.create.foundation.ponder.ElementLink;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.Selection;
import com.simibubi.create.foundation.ponder.element.ParrotElement;
import com.simibubi.create.foundation.ponder.instruction.EmitParticlesInstruction;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class SpreaderScene {
    private static void addGrowthParticles(SceneBuilder scene, BlockPos corpPos) {
        double d0 = 0.5D;
        double d1 = 1.0D;
        RandomSource randomsource = RandomSource.create();
        for(int i = 0; i < 15; ++i) {
            double d2 = randomsource.nextGaussian() * 0.02D;
            double d3 = randomsource.nextGaussian() * 0.02D;
            double d4 = randomsource.nextGaussian() * 0.02D;
            double d5 = 0.5D - d0;
            double d6 = (double)corpPos.getX() + d5 + randomsource.nextDouble() * d0 * 2.0D;
            double d7 = (double)corpPos.getY() + randomsource.nextDouble() * d1;
            double d8 = (double)corpPos.getZ() + d5 + randomsource.nextDouble() * d0 * 2.0D;
            scene.effects.emitParticles(new Vec3(d6, d7, d8), EmitParticlesInstruction.Emitter.simple(ParticleTypes.HAPPY_VILLAGER, new Vec3(d2, d3, d4)) ,.5f, 1);
        }
    }
    public static void spreader(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("spreader", "Accelerate Crop Growth with the Spreader");
        scene.configureBasePlate(0, 0, 5);
        scene.world.showSection(util.select.layer(0), Direction.UP);
        scene.world.showSection(util.select.fromTo(0, 1, 1, 1, 1, 3), Direction.UP);
        scene.idle(5);

        BlockPos spreaderPos = util.grid.at(3, 1, 2);
        BlockPos nozzlePos = util.grid.at(2, 1, 2);
        BlockPos depotPos = util.grid.at(3, 0, 1);
        BlockPos corpPos = util.grid.at(1, 1, 2);

        scene.world.setBlock(nozzlePos, Blocks.AIR.defaultBlockState(),false);
        scene.world.showSection(util.select.fromTo(5, 1, 1, 3, 1, 2), Direction.DOWN);
        scene.idle(10);
        scene.overlay.showText(60)
                .text("The spreader rapidly ripens crops within its designated range")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector.topOf(spreaderPos));
        scene.idle(70);

        scene.world.modifyBlockEntity(depotPos, DepotBlockEntity.class, (be) -> {be.getBehaviour(DepotBehaviour.TYPE).setHeldItem(new TransportedItemStack(new ItemStack(Items.BONE_MEAL)));});
        scene.overlay.showText(60)
                .text("Each grown action consumes one bone meal")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector.topOf(spreaderPos));
        scene.idle(70);
        scene.world.createItemOnBeltLike(depotPos, Direction.UP, ItemStack.EMPTY);
        scene.world.modifyBlockEntity(depotPos, DepotBlockEntity.class, (be) -> {be.getBehaviour(DepotBehaviour.TYPE).setHeldItem(new TransportedItemStack(ItemStack.EMPTY));});
        scene.world.setBlock(corpPos, Blocks.WHEAT.defaultBlockState().setValue(CropBlock.AGE, 5), false);
        addGrowthParticles(scene, corpPos);

        scene.idle(40);
        scene.world.setBlock(nozzlePos, AllBlocks.NOZZLE.getDefaultState().setValue(DirectionalBlock.FACING, Direction.WEST),false);
        scene.world.showSection(util.select.fromTo(nozzlePos, nozzlePos), Direction.DOWN);
        scene.world.modifyKineticSpeed(util.select.everywhere(), f -> 256f);
        scene.overlay.showText(60)
                .text("By adjusting speed and attach nozzle, you can significantly expand the ripening area")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector.topOf(spreaderPos));
        scene.idle(70);
        scene.world.setBlock(util.grid.at(1, 1, 1), Blocks.WHEAT.defaultBlockState().setValue(CropBlock.AGE, 3), false);
        addGrowthParticles(scene, util.grid.at(1, 1, 1));
        scene.world.setBlock(util.grid.at(0, 1, 2), Blocks.WHEAT.defaultBlockState().setValue(CropBlock.AGE, 2), false);
        addGrowthParticles(scene, util.grid.at(0, 1, 2));
        scene.world.setBlock(util.grid.at(1, 1, 3), Blocks.WHEAT.defaultBlockState().setValue(CropBlock.AGE, 3), false);
        addGrowthParticles(scene, util.grid.at(1, 1, 3));
        scene.idle(40);
        scene.world.setBlock(util.grid.at(0, 1, 2), Blocks.WHEAT.defaultBlockState().setValue(CropBlock.AGE, 6), false);
        addGrowthParticles(scene, util.grid.at(0, 1, 2));
        scene.world.setBlock(util.grid.at(0, 1, 2), Blocks.WHEAT.defaultBlockState().setValue(CropBlock.AGE, 4), false);
        addGrowthParticles(scene, util.grid.at(0, 1, 2));
        scene.world.setBlock(util.grid.at(0, 1, 1), Blocks.WHEAT.defaultBlockState().setValue(CropBlock.AGE, 3), false);
        addGrowthParticles(scene, util.grid.at(0, 1, 1));
    }
}
