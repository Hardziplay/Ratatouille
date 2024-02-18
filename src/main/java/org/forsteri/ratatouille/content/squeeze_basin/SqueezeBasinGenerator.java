package org.forsteri.ratatouille.content.squeeze_basin;

import com.simibubi.create.content.processing.basin.BasinBlock;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.ModelFile;

import static org.forsteri.ratatouille.content.squeeze_basin.SqueezeBasinBlock.CASING;

public class SqueezeBasinGenerator extends SpecialBlockStateGen {
    public SqueezeBasinGenerator() {}
    @Override
    protected int getXRotation(BlockState blockState) {
        return 0;
    }

    @Override
    protected int getYRotation(BlockState blockState) {
        return this.horizontalAngle((Direction)blockState.getValue(SqueezeBasinBlock.FACING));
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> dataGenContext, RegistrateBlockstateProvider registrateBlockstateProvider, BlockState blockState) {
        return blockState.getValue(CASING) ?
                AssetLookup.partialBaseModel(dataGenContext, registrateBlockstateProvider, new String[]{"casing"}) :
                AssetLookup.partialBaseModel(dataGenContext, registrateBlockstateProvider, new String[0]);
    }
}
