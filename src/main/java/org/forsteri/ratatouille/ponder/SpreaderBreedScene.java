package org.forsteri.ratatouille.ponder;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.depot.DepotBehaviour;
import com.simibubi.create.content.logistics.depot.DepotBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.phys.Vec3;
import org.forsteri.ratatouille.entry.CRItems;

public class SpreaderBreedScene {

    private static void heartBurst(CreateSceneBuilder scene, Vec3 pos, int ticks) {
        scene.effects().emitParticles(
                pos,
                scene.effects().simpleParticleEmitter(ParticleTypes.HEART, new Vec3(0, 0.08, 0)),
                1.0f, ticks
        );
    }

    private static ElementLink<EntityElement> spawnPig(CreateSceneBuilder scene, Vec3 pos, boolean baby, float yaw) {
        return scene.world().createEntity(w -> {
            Pig p = EntityType.PIG.create(w);
            if (p != null) {
                p.setPos(pos.x, pos.y, pos.z);
                p.setNoAi(true);
                p.setYRot(yaw); // 设置水平朝向
                p.setYHeadRot(yaw);
                p.setDeltaMovement(Vec3.ZERO);
                if (baby) p.setAge(-24000);
            }
            return p;
        });
    }


    public static void spreader_breed(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("spreader_breed", "Spreader makes animals fall in love");
        scene.configureBasePlate(0, 0, 5);

        scene.world().showSection(util.select().layer(0), Direction.UP);
        scene.world().showSection(util.select().fromTo(0, 1, 1, 1, 1, 3), Direction.UP);
        scene.idle(5);

        BlockPos spreaderPos = util.grid().at(3, 1, 2);
        BlockPos nozzlePos   = util.grid().at(2, 1, 2);
        BlockPos depotPos    = util.grid().at(3, 0, 1);

        scene.world().setBlock(nozzlePos, AllBlocks.NOZZLE.getDefaultState()
                .setValue(DirectionalBlock.FACING, Direction.WEST), false);
        scene.world().showSection(util.select().fromTo(5, 1, 1, 3, 1, 2), Direction.DOWN);
        scene.world().modifyKineticSpeed(util.select().everywhere(), f -> 128f);
        scene.idle(10);

        scene.overlay().showText(70)
                .text("When supplied with the right item,\nnearby animals enter love mode.")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector().topOf(spreaderPos));
        scene.idle(80);

        Vec3 p1 = util.vector().centerOf(util.grid().at(1, 1, 2)).add(0.0, 0.0, -1.0);
        Vec3 p2 = util.vector().centerOf(util.grid().at(1, 1, 2)).add(0.0, 0.0, +1.0);
        ElementLink<EntityElement> pigA = spawnPig(scene, p1, false, 0);
        ElementLink<EntityElement> pigB = spawnPig(scene, p2, false, 180);
        scene.idle(10);

        scene.world().modifyBlockEntity(depotPos, DepotBlockEntity.class,
                be -> be.getBehaviour(DepotBehaviour.TYPE)
                        .setHeldItem(new TransportedItemStack(new ItemStack(CRItems.MATURE_MATTER.get()))));
        scene.overlay().showText(50)
                .text("Consumes one mature matter (placeholder)")
                .placeNearTarget()
                .pointAt(util.vector().topOf(depotPos));
        scene.idle(30);
        scene.world().modifyBlockEntity(depotPos, DepotBlockEntity.class,
                be -> be.getBehaviour(DepotBehaviour.TYPE).setHeldItem(null));
        scene.idle(10);

        heartBurst(scene, p1.add(0, 0.8, 0), 10);
        heartBurst(scene, p2.add(0, 0.8, 0), 10);
        scene.idle(35);

        Vec3 mid = p1.add(p2).scale(0.5).add(0.0, -0.5, 0.0);
        ElementLink<EntityElement> baby = spawnPig(scene, mid, true, 90);
        scene.overlay().showText(60)
                .text("After a short while, a baby pig appears.")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(mid);
        scene.idle(70);
    }
}
