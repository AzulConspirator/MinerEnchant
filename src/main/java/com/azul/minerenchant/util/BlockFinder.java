package com.azul.minerenchant.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;


public interface BlockFinder {
    BlockFinder DEFAULT = new BlockFinder() {};

    default Iterable<BlockPos> findBlocks(Direction facing, BlockPos pos, int depth) 
    {
        Iterable<BlockPos> iterator;

        var pos2 = pos.offset(facing, depth);

        if (facing.equals(Direction.DOWN) || facing.equals(Direction.UP)) {
            iterator = BlockPos.iterate(
                    pos.east().offset(Direction.NORTH),
                    pos2.west().offset(Direction.SOUTH)
            );
        } else {
            iterator = BlockPos.iterate(
                    pos.down().offset(facing.rotateCounterclockwise(Direction.Axis.Y)),
                    pos2.up().offset(facing.rotateClockwise(Direction.Axis.Y))
            );
        }

        return iterator;
    }
}