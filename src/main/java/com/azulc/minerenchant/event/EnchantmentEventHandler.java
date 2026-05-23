package com.azulc.minerenchant.event;

import com.azulc.minerenchant.util.BlockFinder;
import com.azulc.minerenchant.ModEnchantments;
import com.azulc.minerenchant.minerenchant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.HashSet;
import java.util.Set;

@EventBusSubscriber(modid = minerenchant.MODID)
public class EnchantmentEventHandler {
    // Track blocks currently being processed to eliminate recursion crashes completely
    private static final Set<BlockPos> HARVESTED_BLOCKS = new HashSet<>();

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) 
    {
        if (event.isCanceled()) return;

        Player player = event.getPlayer();
        ItemStack mainHandItem = player.getMainHandItem();

        if (!(player instanceof ServerPlayer serverPlayer)) return;
        if (HARVESTED_BLOCKS.contains(event.getPos())) return;

        Level level = (Level) event.getLevel();
        if (level.isClientSide()) return;

        // Read data-driven enchantment structure
        var enchantmentRegistry = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        var enchantmentHolder = enchantmentRegistry.getHolderOrThrow(ModEnchantments.DIGGER);
        int enchLevel = mainHandItem.getEnchantmentLevel(enchantmentHolder);

        if (enchLevel <= 0) return;
        HARVESTED_BLOCKS.add(event.getPos());

        try {
            int damage = 1;
            int depth = (enchLevel == 2) ? 1 : 0;
            if (player.isCrouching()) return;

            // Dynamically query reach to safely support creative mode (5.0) and modded reach modifiers
            double reach = serverPlayer.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
            BlockHitResult traceResult = level.clip(new ClipContext(
                serverPlayer.getEyePosition(1f),
                serverPlayer.getEyePosition(1f).add(serverPlayer.getViewVector(1f).scale((float) reach)),
                ClipContext.Block.COLLIDER, 
                ClipContext.Fluid.NONE, 
                serverPlayer 
            ));

            if (traceResult.getType() == HitResult.Type.MISS) return;
            
            // Get facing direction relative to the broken block face
            Direction facing = traceResult.getDirection().getOpposite();

            // Run your customized directional expansion algorithm
            Iterable<BlockPos> targetBlocks = BlockFinder.DEFAULT.findBlocks(facing, event.getPos(), depth);
            
            for (BlockPos targetPos : targetBlocks) {
                if (targetPos.equals(event.getPos())) continue;
                if (HARVESTED_BLOCKS.contains(targetPos)) continue;

                // Stop mining if the tool broke mid-operation
                if (mainHandItem.isEmpty()) break;

                BlockState targetState = level.getBlockState(targetPos);

                // FIXED: Simplified tool checking condition to prevent skipping mineable blocks
                if (!mainHandItem.isCorrectToolForDrops(targetState)) {
                    continue;
                }

                HARVESTED_BLOCKS.add(targetPos);
                serverPlayer.gameMode.destroyBlock(targetPos);
                HARVESTED_BLOCKS.remove(targetPos);
                damage++;
            }
            // Apply accumulated durability damage to the tool
            int finalDamage = damage + 1;
            mainHandItem.hurtAndBreak(finalDamage, serverPlayer, EquipmentSlot.MAINHAND);
        } 
        catch (Exception e) 
        {
            minerenchant.LOGGER.error("Failed to execute Digger enchantment logic", e);
        } 
        finally {
            
            HARVESTED_BLOCKS.remove(event.getPos());
        }
    }
}