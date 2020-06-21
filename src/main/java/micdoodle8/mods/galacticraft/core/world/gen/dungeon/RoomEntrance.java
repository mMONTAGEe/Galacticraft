package micdoodle8.mods.galacticraft.core.world.gen.dungeon;

import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;

import java.util.Random;

public class RoomEntrance extends SizedPiece
{
    public RoomEntrance(IStructurePieceType type)
    {
        super(type);
    }

    public RoomEntrance(IStructurePieceType type, DungeonConfiguration configuration, Random rand, int blockPosX, int blockPosZ)
    {
        super(type, configuration, rand.nextInt(4) + 6, 12, rand.nextInt(4) + 6, Direction.Plane.HORIZONTAL.random(rand));
        this.setCoordBaseMode(Direction.SOUTH);
        int sX = this.sizeX / 2;
        int sZ = this.sizeZ / 2;

        this.boundingBox = new MutableBoundingBox(blockPosX - sX, configuration.getYPosition(), blockPosZ - sZ, blockPosX - sX + this.sizeX, configuration.getYPosition() + this.sizeY, blockPosZ - sZ + this.sizeZ);
    }

    @Override
    public boolean addComponentParts(IWorld worldIn, Random random, MutableBoundingBox boundingBox, ChunkPos chunkPos)
    {
        for (int i = 0; i <= this.sizeX; i++)
        {
            for (int j = 0; j <= this.sizeY; j++)
            {
                for (int k = 0; k <= this.sizeZ; k++)
                {
                    if (i == 0 || i == this.sizeX || j == 0 /*|| j == this.sizeY*/ || k == 0 || k == this.sizeZ)
                    {
                        this.setBlockState(worldIn, this.configuration.getBrickBlock(), i, j, k, boundingBox);
                    }
                    else
                    {
                        this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), i, j, k, boundingBox);
                    }
                }
            }
        }

        return true;
    }

    @Override
    public Piece getNextPiece(DungeonStart startPiece, Random rand)
    {
        return getCorridor(rand, startPiece, 10, false);
    }
}