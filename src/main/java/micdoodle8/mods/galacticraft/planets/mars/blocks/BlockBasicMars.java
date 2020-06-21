package micdoodle8.mods.galacticraft.planets.mars.blocks;

import com.google.common.base.Predicate;

import micdoodle8.mods.galacticraft.api.block.IDetectableResource;
import micdoodle8.mods.galacticraft.api.block.IPlantableBlock;
import micdoodle8.mods.galacticraft.api.block.ITerraformableBlock;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock;
import micdoodle8.mods.galacticraft.core.client.sounds.GCSounds;
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock;
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets;
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.item.ItemGroup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockBasicMars extends Block implements IDetectableResource, IPlantableBlock, ITerraformableBlock, ISortableBlock
{
    public static final EnumProperty<EnumBlockBasic> BASIC_TYPE = EnumProperty.create("basictypemars", EnumBlockBasic.class);

    public enum EnumBlockBasic implements IStringSerializable
    {
        ORE_COPPER(0, "ore_copper_mars"),
        ORE_TIN(1, "ore_tin_mars"),
        ORE_DESH(2, "ore_desh_mars"),
        ORE_IRON(3, "ore_iron_mars"),
        COBBLESTONE(4, "cobblestone"),
        SURFACE(5, "mars_surface"),
        MIDDLE(6, "mars_middle"),
        DUNGEON_BRICK(7, "dungeon_brick"),
        DESH_BLOCK(8, "desh_block"),
        MARS_STONE(9, "mars_stone");

        private final int meta;
        private final String name;

        private EnumBlockBasic(int meta, String name)
        {
            this.meta = meta;
            this.name = name;
        }

        public int getMeta()
        {
            return this.meta;
        }

        private final static EnumBlockBasic[] values = values();
        public static EnumBlockBasic byMetadata(int meta)
        {
            return values[meta % values.length];
        }

        @Override
        public String getName()
        {
            return this.name;
        }
    }

    public BlockBasicMars(Properties builder)
    {
        super(builder);
    }

    @Override
    public MapColor getMapColor(BlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        if (state.get(BASIC_TYPE) == EnumBlockBasic.DUNGEON_BRICK)
        {
            return MapColor.GREEN;
        }
        else if (state.get(BASIC_TYPE) == EnumBlockBasic.SURFACE)
        {
            return MapColor.DIRT;
        }

        return MapColor.RED;
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion)
    {
        EnumBlockBasic type = (EnumBlockBasic) world.getBlockState(pos).getValue(BASIC_TYPE);

        if (type == EnumBlockBasic.DUNGEON_BRICK)
        {
            return 40.0F;
        }
        else if (type == EnumBlockBasic.DESH_BLOCK)
        {
            return 60.0F;
        }

        return super.getExplosionResistance(world, pos, exploder, explosion);
    }

//    @OnlyIn(Dist.CLIENT)
//    @Override
//    public ItemGroup getCreativeTabToDisplayOn()
//    {
//        return GalacticraftCore.galacticraftBlocksTab;
//    }

    @Override
    public float getBlockHardness(BlockState blockState, World worldIn, BlockPos pos)
    {
        BlockState state = worldIn.getBlockState(pos);

        if (state.get(BASIC_TYPE) == EnumBlockBasic.DUNGEON_BRICK)
        {
            return 4.0F;
        }

        return this.blockHardness;
    }

    @Override
    public Item getItemDropped(BlockState state, Random rand, int fortune)
    {
        if (state.get(BASIC_TYPE) == EnumBlockBasic.ORE_DESH)
        {
            return MarsItems.marsItemBasic;
        }

        return Item.getItemFromBlock(this);
    }

    @Override
    public int damageDropped(BlockState state)
    {
        int meta = state.getBlock().getMetaFromState(state);
        if (state.get(BASIC_TYPE) == EnumBlockBasic.MARS_STONE)
        {
            return 4;
        }
        else if (state.get(BASIC_TYPE) == EnumBlockBasic.ORE_DESH)
        {
            return 0;
        }
        else
        {
            return meta;
        }
    }

    @Override
    public int quantityDropped(BlockState state, int fortune, Random random)
    {
        if (state.get(BASIC_TYPE) == EnumBlockBasic.ORE_DESH && fortune >= 1)
        {
            return (random.nextFloat() < fortune * 0.29F - 0.25F) ? 2 : 1;
        }

        return 1;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void getSubBlocks(ItemGroup tab, NonNullList<ItemStack> list)
    {
        for (EnumBlockBasic blockBasic : EnumBlockBasic.values())
        {
            list.add(new ItemStack(this, 1, blockBasic.getMeta()));
        }
    }

    @Override
    public boolean isValueable(BlockState state)
    {
        switch (this.getMetaFromState(state))
        {
        case 0:
        case 1:
        case 2:
        case 3:
            return true;
        default:
            return false;
        }
    }

    @Override
    public boolean canSustainPlant(BlockState state, IBlockAccess world, BlockPos pos, Direction direction, IPlantable plantable)
    {
        return false;
    }

    @Override
    public int requiredLiquidBlocksNearby()
    {
        return 4;
    }

    @Override
    public boolean isPlantable(BlockState state)
    {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, World worldIn, BlockPos pos, Random rand)
    {
        if (rand.nextInt(10) == 0)
        {
            if (state.getBlock() == this && state.get(BASIC_TYPE) == EnumBlockBasic.DUNGEON_BRICK)
            {
                GalacticraftPlanets.addParticle("sludgeDrip", new Vector3(pos.getX() + rand.nextDouble(), pos.getY(), pos.getZ() + rand.nextDouble()), new Vector3(0, 0, 0));

                if (rand.nextInt(100) == 0)
                {
                    worldIn.playSound(null, pos.getX(), pos.getY(), pos.getZ(), GCSounds.singleDrip, SoundCategory.AMBIENT, 1, 0.8F + rand.nextFloat() / 5.0F);
                }
            }
        }
    }

    @Override
    public boolean isTerraformable(World world, BlockPos pos)
    {
        BlockState state = world.getBlockState(pos);
        BlockState stateAbove = world.getBlockState(pos.up());
        return state.get(BASIC_TYPE) == EnumBlockBasic.SURFACE && !stateAbove.getBlock().isFullCube(stateAbove);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
    {
        return new ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(state));
    }

    @Override
    public boolean isReplaceableOreGen(BlockState state, IBlockAccess world, BlockPos pos, Predicate<BlockState> target)
    {
        return (state.get(BASIC_TYPE) == EnumBlockBasic.MIDDLE || state.get(BASIC_TYPE) == EnumBlockBasic.MARS_STONE);
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return state.getBlock().getMetaFromState(state) == 10;
    }

    @Override
    public BlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().with(BASIC_TYPE, EnumBlockBasic.byMetadata(meta));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(BASIC_TYPE);
    }

    @Override
    public EnumSortCategoryBlock getCategory(int meta)
    {
        switch (meta)
        {
        case 0:
        case 1:
        case 2:
        case 3:
            return EnumSortCategoryBlock.ORE;
        case 7:
            return EnumSortCategoryBlock.BRICKS;
        case 8:
            return EnumSortCategoryBlock.INGOT_BLOCK;
        }
        return EnumSortCategoryBlock.GENERAL;
    }

    @Override
    public int getExpDrop(BlockState state, IBlockAccess world, BlockPos pos, int fortune)
    {
        if (state.getBlock() != this) return 0;
        
        int meta = this.getMetaFromState(state);
        if (meta == 2)
        {
            Random rand = world instanceof World ? ((World)world).rand : new Random();
            return MathHelper.getInt(rand, 2, 5);
        }
        return 0;
    }
}
