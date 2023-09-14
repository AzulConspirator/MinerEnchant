package com.azul.minerenchant.enchantment;

import com.azul.minerenchant.util.BlockFinder;

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

import static net.minecraft.enchantment.Enchantments.FORTUNE;


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

     @Override
     public int getMaxLevel() {
        return 2;
     }
     @Override
     public int getMinPower(int level) {
         return 15 + (level - 1) * 9;
     }
 
     @Override
     public int getMaxPower(int level) {
         return super.getMinPower(level) + 50;
     }
 
     @Override
     protected boolean canAccept(Enchantment other) {
         return super.canAccept(other) && other != FORTUNE;
     }
     
    public void onBlockBreak(PlayerEntity player, BlockPos pos, ItemStack stack, int level) 
    {
        World world;
        int damage = 1;
        int depth = 0 ;
        if ((world = player.getWorld()).isClient) return;

        Iterable<BlockPos> iterable = null;
        if (level > 0) 
        {   
            BlockHitResult blockHitResult = (BlockHitResult) player.raycast(4.5f, 1, false);
            var facing = blockHitResult.getSide().getOpposite();
            if (level == 1) 
            {
                depth = 0;
            } 
            if (level == 2) 
            {
                depth = 1;
            } 
            iterable = this.getBlockFinder().findBlocks(facing,pos,depth);
        }

        for (BlockPos blockPos : iterable) 
        {
            if (!world.canPlayerModifyAt(player, pos)) continue;
            BlockState blockState = world.getBlockState(blockPos);
            if (!blockState.isSolidBlock(world, blockPos) || !stack.isSuitableFor(blockState) || blockState.isToolRequired()) continue;
            world.syncWorldEvent(14004, blockPos, 0);
            if (pos.equals(blockPos)) continue;
            BlockEntity blockEntity = world.getBlockState(blockPos).hasBlockEntity() ? world.getBlockEntity(blockPos) : null;

            if (blockState.isIn(BlockTags.GUARDED_BY_PIGLINS)) 
            {
                PiglinBrain.onGuardedBlockInteracted(player, false);
            }
            world.emitGameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Emitter.of(player, blockState));
            if (world.removeBlock(blockPos, false)) 
            {
                blockState.getBlock().onBroken(world, blockPos, blockState);
            }
            damage++;
            // only drop items in creative
            if(!player.isCreative()) 
            {
                world.getBlockState(blockPos).getBlock().onBreak(world, blockPos, blockState, player);
                Block.dropStacks(blockState, world, blockPos, blockEntity, player, stack);
                world.breakBlock(blockPos, false, player);
                blockState.onStacksDropped((ServerWorld) world, blockPos, player.getMainHandStack(), true);
            }
            else
            {
                damage++;
            }
        }
        stack.damage(damage+1, player, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
    }

    public BlockFinder getBlockFinder()
    {
       return BlockFinder.DEFAULT;
    }
}
