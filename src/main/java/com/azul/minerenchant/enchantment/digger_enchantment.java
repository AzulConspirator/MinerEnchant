package com.azul.minerenchant.enchantment;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.block.Block;
import net.minecraft.util.math.Direction;

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
        int damage = 1;
        if ((world = player.getWorld()).isClient) return;

        Iterable<BlockPos> iterable = null;
        if (level == 1) 
        {
            BlockHitResult blockHitResult = (BlockHitResult) player.raycast(4.5f, 1, false);
            var facing = blockHitResult.getSide().getOpposite();
            var pos2 = pos.offset(facing, 0);
            
            if (facing.equals(Direction.DOWN) || facing.equals(Direction.UP)) 
            {
                iterable = BlockPos.iterate(pos.east().offset(Direction.NORTH),pos2.west().offset(Direction.SOUTH));
            } 
            else
            {
                iterable = BlockPos.iterate(pos.down().offset(facing.rotateCounterclockwise(Direction.Axis.Y)),pos2.up().offset(facing.rotateClockwise(Direction.Axis.Y)));
            }
        }

        for (BlockPos blockPos : iterable) 
        {
            damage++;
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
                world.getBlockState(blockPos).getBlock().onBreak(world, blockPos, blockState, player);
                Block.dropStacks(blockState, world, blockPos, blockEntity, player, stack);
                world.breakBlock(blockPos, false, player);
                blockState.onStacksDropped((ServerWorld) world, blockPos, player.getMainHandStack(), true);
            }
        }
        stack.damage(damage+1, player, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
    }
}
