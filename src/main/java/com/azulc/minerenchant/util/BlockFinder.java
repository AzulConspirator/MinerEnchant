package com.azulc.minerenchant.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public interface BlockFinder {
    BlockFinder DEFAULT = new BlockFinder() {};

    default Iterable<BlockPos> findBlocks(Direction facing, BlockPos pos, int depth) {
        // Step 1: Find the far-end anchor block based on depth
        BlockPos farPos = pos.relative(facing, depth);

        // Step 2: Use the minimum and maximum coordinates between the two anchor planes
        int minX = Math.min(pos.getX(), farPos.getX());
        int minY = Math.min(pos.getY(), farPos.getY());
        int minZ = Math.min(pos.getZ(), farPos.getZ());

        int maxX = Math.max(pos.getX(), farPos.getX());
        int maxY = Math.max(pos.getY(), farPos.getY());
        int maxZ = Math.max(pos.getZ(), farPos.getZ());

        // Step 3: Expand the box outward by 1 block perpendicular to the face you are looking at
        // If looking UP/DOWN, expand North/South/East/West (X and Z axes)
        if (facing.getAxis() == Direction.Axis.Y) {
            minX -= 1; maxX += 1;
            minZ -= 1; maxZ += 1;
        } 
        // If looking NORTH/SOUTH, expand Up/Down/East/West (X and Y axes)
        else if (facing.getAxis() == Direction.Axis.Z) {
            minX -= 1; maxX += 1;
            minY -= 1; maxY += 1;
        } 
        // If looking EAST/WEST, expand Up/Down/North/South (Y and Z axes)
        else if (facing.getAxis() == Direction.Axis.X) {
            minY -= 1; maxY += 1;
            minZ -= 1; maxZ += 1;
        }

        // Returns a perfectly structured iteration pool every single time
        return BlockPos.betweenClosed(
                new BlockPos(minX, minY, minZ),
                new BlockPos(maxX, maxY, maxZ)
        );
    }
}