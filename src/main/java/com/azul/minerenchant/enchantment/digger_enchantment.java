package com.azul.minerenchant.enchantment;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.block.Block;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

public class digger_enchantment  extends Enchantment
{
    protected digger_enchantment(Rarity weight, EnchantmentTarget target, EquipmentSlot... slotTypes) 
    {
        super(weight, target, slotTypes);
    }

    @Override
    public boolean isTreasure() {
        return true;
     }

    public void onBlockBreak(PlayerEntity player, BlockPos pos, ItemStack stack, int level) 
    {
        World world;
        if ((world = player.getWorld()).isClient) return;

        Iterable<BlockPos> iterable;
        if (level == 1) 
        {
            Iterable<BlockPos> iterator;
            BlockHitResult blockHitResult = (BlockHitResult) player.raycast(4.5f, 1, false);
            var facing = blockHitResult.getSide().getOpposite();
            var pos2 = pos.offset(facing, 0);
    
            if (facing.equals(Direction.DOWN) || facing.equals(Direction.UP)) 
            {
                iterator = BlockPos.iterate(pos.east().offset(Direction.NORTH),pos2.west().offset(Direction.SOUTH));
            } 
            else
            {
                iterator = BlockPos.iterate(pos.down().offset(facing.rotateCounterclockwise(Direction.Axis.Y)),pos2.up().offset(facing.rotateClockwise(Direction.Axis.Y)));
            }
            iterable = iterator;
        }
        else
        {
            int radius = 1;
            iterable = BlockPos.iterate(pos.add(-radius, -radius, -radius), pos.add(radius, radius, radius));
        }

        for (BlockPos blockPos : iterable) 
        {
            if (!world.canPlayerModifyAt(player, pos)) continue;
            BlockState blockState = world.getBlockState(blockPos);
            BlockEntity blockEntity = world.getBlockState(pos).hasBlockEntity() ? world.getBlockEntity(pos) : null;

            if (!blockState.isSolidBlock(world, blockPos) || !stack.isSuitableFor(blockState)) continue;
            world.syncWorldEvent(14004, blockPos, 0);
            if (pos.equals(blockPos)) continue;
            if (blockState.isIn(BlockTags.GUARDED_BY_PIGLINS)) {
                PiglinBrain.onGuardedBlockInteracted(player, false);
            }
            world.emitGameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Emitter.of(player, blockState));
            if (world.removeBlock(blockPos, false)) {
                blockState.getBlock().onBroken(world, blockPos, blockState);
            }
            
            // only drop items in creative
            if(!player.isCreative()) 
            {
                Vec3d offsetPos = new Vec3d(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5);
                // obtain dropped stacks for the given block
                List<ItemStack> droppedStacks = Block.getDroppedStacks(blockState, (ServerWorld) world, pos, blockEntity, player, player.getMainHandStack());
                // drop items
                dropItems(player, world, droppedStacks, offsetPos);
                blockState.onStacksDropped((ServerWorld) world, pos, player.getMainHandStack(), true);
            }
        }
    }

    private static void dropItems(PlayerEntity player, World world, List<ItemStack> stacks, Vec3d pos) {
        for(ItemStack stack : stacks) {
            // The stack passed in to insertStack is mutated, so we can operate on it here without worrying about duplicated items.
            if (!stack.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                world.spawnEntity(itemEntity);
            }
        }
    }
}
