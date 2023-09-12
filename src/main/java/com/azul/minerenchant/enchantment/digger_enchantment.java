package com.azul.minerenchant.enchantment;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.ArrayList;
import java.util.List;

public class digger_enchantment  extends Enchantment
{
    protected digger_enchantment(Rarity weight, EnchantmentTarget target, EquipmentSlot... slotTypes) 
    {
        super(weight, target, slotTypes);
    }

    
    public void onBlockBreak(PlayerEntity player, BlockPos pos, ItemStack stack, int level) 
    {
        World world;
        if ((world = player.getWorld()).isClient) return;

        Iterable<BlockPos> iterable;
        if (level == 1) {
            List<BlockPos> temp = new ArrayList<>(2);
            temp.add(pos);
            boolean aligned = pos.getX() == player.getBlockX() && pos.getZ() == player.getBlockZ();
            if (pos.getY() >= player.getBlockY() + 1) {
                if (aligned) temp.add(pos.up());
                else temp.add(pos.down());
            } else {
                if (aligned) temp.add(pos.down());
                else temp.add(pos.up());
            }
            iterable = temp;
        } else {
            int radius = 1;
            iterable = BlockPos.iterate(pos.add(-radius, -radius, -radius), pos.add(radius, radius, radius));
        }

        for (BlockPos blockPos : iterable) {
            if (!world.canPlayerModifyAt(player, pos)) continue;
            BlockState blockState = world.getBlockState(blockPos);
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
        }
    }
}
