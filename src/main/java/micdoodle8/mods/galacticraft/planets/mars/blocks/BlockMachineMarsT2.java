package micdoodle8.mods.galacticraft.planets.mars.blocks;

import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.blocks.BlockMachineBase;
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets;
import micdoodle8.mods.galacticraft.planets.GuiIdsPlanets;
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityElectrolyzer;
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityGasLiquefier;
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityMethaneSynthesizer;
import net.minecraft.block.BlockState;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockMachineMarsT2 extends BlockMachineBase
{
    public static final EnumProperty<EnumMachineType> TYPE = EnumProperty.create("type", EnumMachineType.class);

    public enum EnumMachineType implements EnumMachineBase, IStringSerializable
    {
        GAS_LIQUEFIER(0, "gas_liquefier", TileEntityGasLiquefier::new, "tile.gas_liquefier.description", "tile.mars_machine.4"),
        METHANE_SYNTHESIZER(4, "methane_synthesizer", TileEntityMethaneSynthesizer::new, "tile.methane_synthesizer.description", "tile.mars_machine.5"),
        ELECTROLYZER(8, "electrolyzer", TileEntityElectrolyzer::new, "tile.electrolyzer.description", "tile.mars_machine.6");

        private final int meta;
        private final String name;
        private final TileConstructor tile;
        private final String shiftDescriptionKey;
        private final String blockName;

        EnumMachineType(int meta, String name, TileConstructor tile, String key, String blockName)
        {
            this.meta = meta;
            this.name = name;
            this.tile = tile;
            this.shiftDescriptionKey = key;
            this.blockName = blockName;
        }

        @Override
        public int getMetadata()
        {
            return this.meta;
        }

        private final static EnumMachineType[] values = values();
        @Override
        public EnumMachineType fromMetadata(int meta)
        {
            return values[(meta / 4) % values.length];
        }
        
        @Override
        public String getName()
        {
            return this.name;
        }
        
        @Override
        public TileEntity tileConstructor()
        {
            return this.tile.create();
        }

        @FunctionalInterface
        private static interface TileConstructor
        {
              TileEntity create();
        }

        @Override
        public String getShiftDescriptionKey()
        {
            return this.shiftDescriptionKey;
        }

        @Override
        public String getTranslationKey()
        {
            return this.blockName;
        }
    }

    public BlockMachineMarsT2(Properties builder)
    {
        super(builder);
    }

    @Override
    protected void initialiseTypes()
    {
        this.types = EnumMachineType.values;
        this.typeBase = EnumMachineType.values[0];
    }

    /**
     * Called when the block is right clicked by the player
     */
    @Override
    public boolean onMachineActivated(World worldIn, BlockPos pos, BlockState state, PlayerEntity playerIn, Hand hand, ItemStack heldItem, BlockRayTraceResult hit)
    {
        playerIn.openGui(GalacticraftPlanets.instance, GuiIdsPlanets.MACHINE_MARS, worldIn, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
    {
        int metadata = this.getMetaFromState(state) & BlockMachineBase.METADATA_MASK;
        return new ItemStack(this, 1, metadata);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        final TileEntity te = worldIn.getTileEntity(pos);

        if (te instanceof TileEntityGasLiquefier)
        {
            final TileEntityGasLiquefier tileEntity = (TileEntityGasLiquefier) te;

            if (tileEntity.processTicks > 0)
            {
                final float x = pos.getX() + 0.5F;
                final float y = pos.getY() + 0.8F + 0.05F * rand.nextInt(3);
                final float z = pos.getZ() + 0.5F;

                for (float i = -0.41F + 0.16F * rand.nextFloat(); i < 0.5F; i += 0.167F)
                {
                    if (rand.nextInt(3) == 0)
                    {
                        this.world.addParticle("whiteSmokeTiny", new Vector3(x + i, y, z - 0.41F), new Vector3(0.0D, -0.015D, -0.0015D), new Object[] {});
                    }
                    if (rand.nextInt(3) == 0)
                    {
                        this.world.addParticle("whiteSmokeTiny", new Vector3(x + i, y, z + 0.537F), new Vector3(0.0D, -0.015D, 0.0015D), new Object[] {});
                    }
                    if (rand.nextInt(3) == 0)
                    {
                        this.world.addParticle("whiteSmokeTiny", new Vector3(x - 0.41F, y, z + i), new Vector3(-0.0015D, -0.015D, 0.0D), new Object[] {});
                    }
                    if (rand.nextInt(3) == 0)
                    {
                        this.world.addParticle("whiteSmokeTiny", new Vector3(x + 0.537F, y, z + i), new Vector3(0.0015D, -0.015D, 0.0D), new Object[] {});
                    }
                }
            }
        }
    }

    @Override
    public BlockState getStateFromMeta(int meta)
    {
        Direction enumfacing = Direction.byHorizontalIndex(meta % 4);
        EnumMachineType type = (EnumMachineType) typeBase.fromMetadata(meta);
        return this.getDefaultState().with(FACING, enumfacing).with(TYPE, type);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, TYPE);
    }
}
