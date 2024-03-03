package org.forsteri.ratatouille.ponders;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.logistics.depot.DepotBehaviour;
import com.simibubi.create.content.logistics.depot.DepotBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import com.simibubi.create.foundation.utility.IntAttached;
import com.simibubi.create.foundation.utility.NBTHelper;
import com.simibubi.create.foundation.utility.Pointing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import org.forsteri.ratatouille.content.squeeze_basin.SqueezeBasinBlock;
import org.forsteri.ratatouille.entry.CRFluids;
import org.forsteri.ratatouille.entry.CRItems;
import vectorwing.farmersdelight.common.registry.ModItems;

import static com.simibubi.create.content.processing.basin.BasinBlockEntity.OUTPUT_ANIMATION_TIME;

public class SqueezeBasinScene {
    public static void squeezeBasin(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("squeeze_basin", "Squeezing");
        scene.configureBasePlate(0, 0, 5);
        scene.world.showSection(util.select.fromTo(0, 0, 0, 4, 0, 4), Direction.UP);

        scene.idle(5);
        BlockPos squeezeBasinPos = util.grid.at(3, 2, 2);
        scene.world.showSection(util.select.fromTo(3, 1, 2, 3, 1, 2), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.position(squeezeBasinPos), Direction.DOWN);

        scene.overlay.showText(50)
                .text("Squeeze basin are used for squeezing and filling tasks")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector.topOf(squeezeBasinPos));
        scene.idle(60);

        BlockPos pressPos = squeezeBasinPos.above(2);
        scene.world.showSection(util.select.position(pressPos), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.fromTo(3, 4, 3, 3, 4, 4), Direction.NORTH);
        scene.world.showSection(util.select.fromTo(3, 0, 5, 3, 4, 5), Direction.NORTH);
        scene.idle(5);
        BlockPos inputDepotPos = util.grid.at(3, 1, 1);
        BlockPos inputFunnelPos = util.grid.at(3, 2, 1);
        BlockPos outputDepotPos = util.grid.at(2, 1, 2);
        scene.world.modifyBlockEntity(inputDepotPos, DepotBlockEntity.class, (be) -> {
            be.getBehaviour(DepotBehaviour.TYPE).setHeldItem(new TransportedItemStack(CRItems.SALTY_DOUGH.asStack()));
        });
        scene.world.showSection(util.select.fromTo(inputDepotPos, inputFunnelPos), Direction.SOUTH);
        scene.idle(5);
        scene.world.showSection(util.select.position(outputDepotPos), Direction.EAST);
        scene.idle(5);
        scene.overlay.showText(50)
                .text("The product will try to output below the side with port")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector.topOf(outputDepotPos));
        scene.idle(10);
        scene.world.modifyBlockEntity(inputDepotPos, DepotBlockEntity.class, (be) -> {
            be.getBehaviour(DepotBehaviour.TYPE).setHeldItem(new TransportedItemStack(ItemStack.EMPTY));
        });
        scene.world.flapFunnel(inputFunnelPos, false);
        scene.world.modifyBlockEntity(pressPos, MechanicalPressBlockEntity.class, MechanicalPressBlockEntity::startProcessingBasin);
        scene.idle(30);
        scene.world.modifyBlockEntity(outputDepotPos, DepotBlockEntity.class, (be) -> {
            be.getBehaviour(DepotBehaviour.TYPE).setHeldItem(new TransportedItemStack(new ItemStack(ModItems.RAW_PASTA.get())));
        });
        scene.idle(45);

        scene.world.hideSection(util.select.fromTo(inputDepotPos, inputFunnelPos), Direction.NORTH);
        scene.world.modifyBlockEntity(outputDepotPos, DepotBlockEntity.class, (be) -> {
            be.getBehaviour(DepotBehaviour.TYPE).setHeldItem(new TransportedItemStack(ItemStack.EMPTY));
        });
        scene.idle(5);
        BlockPos mixerPos = util.grid.at(5, 2, 1);
        BlockPos basinPos = util.grid.at(5, 0, 1);
        scene.world.showSection(util.select.fromTo(basinPos, mixerPos), Direction.WEST);
        scene.idle(5);
        BlockPos pumpPos = util.grid.at(4, 2, 2);
        scene.world.createItemOnBeltLike(basinPos, Direction.UP, new ItemStack(Items.CHICKEN, 64));
        scene.world.createItemOnBeltLike(basinPos, Direction.UP, new ItemStack(CRItems.SALT.get(), 64));
        scene.world.showSection(util.select.fromTo(5, 0, 2, 5, 2, 2), Direction.WEST);
        scene.world.showSection(util.select.position(pumpPos), Direction.WEST);
        scene.idle(5);
        scene.overlay.showText(50)
                .text("Squeeze basin also accept fluid")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector.centerOf(pumpPos));
        scene.world.modifyBlockEntityNBT(util.select.position(basinPos), BasinBlockEntity.class, nbt -> {
            nbt.put("VisualizedFluids",
                    NBTHelper.writeCompoundList(ImmutableList.of(IntAttached.with(1, new FluidStack(CRFluids.MINCE_MEAT.get(), 1000))), ia -> ia.getValue().writeToNBT(new CompoundTag())));
        });
        scene.world.modifyBlock(squeezeBasinPos, s -> s.setValue(SqueezeBasinBlock.CASING, true), false);
        scene.world.modifyBlockEntity(mixerPos, MechanicalMixerBlockEntity.class, MechanicalMixerBlockEntity::startProcessingBasin);
        scene.idle(80);
        scene.world.modifyBlockEntity(pressPos, MechanicalPressBlockEntity.class, MechanicalPressBlockEntity::startProcessingBasin);
        scene.idle(30);
        scene.world.modifyBlock(squeezeBasinPos, s -> s.setValue(SqueezeBasinBlock.CASING, false), false);
        scene.world.modifyBlockEntity(outputDepotPos, DepotBlockEntity.class, (be) -> {
            be.getBehaviour(DepotBehaviour.TYPE).setHeldItem(new TransportedItemStack(CRItems.RAW_SAUSAGE.asStack()));
        });
        scene.idle(40);
        scene.overlay.showText(40)
                .text("However, when processing some recipes, it may be necessary to fill the output port with items")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector.blockSurface(squeezeBasinPos, Direction.WEST));
        scene.idle(50);
        scene.overlay.showControls(
                new InputWindowElement(util.vector.blockSurface(squeezeBasinPos, Direction.WEST), Pointing.DOWN)
                        .withItem(CRItems.SAUSAGE_CASING.asStack())
                        .rightClick(),
                30);
        scene.world.modifyBlockEntity(outputDepotPos, DepotBlockEntity.class, (be) -> {
            be.getBehaviour(DepotBehaviour.TYPE).setHeldItem(new TransportedItemStack(CRItems.RAW_SAUSAGE.asStack()));
        });
        scene.idle(40);
        scene.world.modifyBlock(squeezeBasinPos, s -> s.setValue(SqueezeBasinBlock.CASING, true), false);
        scene.idle(40);
        scene.world.modifyBlockEntity(outputDepotPos, DepotBlockEntity.class, (be) -> {
            be.getBehaviour(DepotBehaviour.TYPE).setHeldItem(new TransportedItemStack(ItemStack.EMPTY));
        });
        scene.world.modifyBlock(squeezeBasinPos, s -> s.setValue(SqueezeBasinBlock.CASING, false), false);

        BlockPos deployerPos = util.grid.at(1, 2, 2);
        scene.world.modifyBlockEntityNBT(util.select.position(deployerPos), DeployerBlockEntity.class,
                nbt -> nbt.put("HeldItem", CRItems.SAUSAGE_CASING.asStack().serializeNBT()));
        scene.world.showSection(util.select.position(deployerPos), Direction.EAST);
        scene.idle(5);
        scene.world.showSection(util.select.fromTo(1, 2, 3, 1, 2, 4), Direction.NORTH);
        scene.world.showSection(util.select.fromTo(1, 0, 5, 1, 2, 5), Direction.NORTH);
        scene.idle(5);
        scene.overlay.showText(40)
                .text("Deployer can be more efficient at this time")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector.centerOf(deployerPos));
        scene.idle(50);
        scene.world.moveDeployer(deployerPos, 1, 25);
        scene.idle(26);
        scene.world.modifyBlock(squeezeBasinPos, s -> s.setValue(SqueezeBasinBlock.CASING, true), false);
        scene.world.moveDeployer(deployerPos, -1, 25);
        scene.world.modifyBlockEntity(pressPos, MechanicalPressBlockEntity.class, MechanicalPressBlockEntity::startProcessingBasin);
        scene.idle(30);
        scene.world.modifyBlockEntity(outputDepotPos, DepotBlockEntity.class, (be) -> {
            be.getBehaviour(DepotBehaviour.TYPE).setHeldItem(new TransportedItemStack(CRItems.SAUSAGE.asStack()));
        });
        scene.world.modifyBlock(squeezeBasinPos, s -> s.setValue(SqueezeBasinBlock.CASING, false), false);

    }
}
