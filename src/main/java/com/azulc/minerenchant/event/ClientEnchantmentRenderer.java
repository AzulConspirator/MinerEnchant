package com.azulc.minerenchant.event;

import com.azulc.minerenchant.ModEnchantments;
import com.azulc.minerenchant.minerenchant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;

@EventBusSubscriber(modid = minerenchant.MODID, value = Dist.CLIENT)
public class ClientEnchantmentRenderer {

    @SubscribeEvent
    public static void onRenderHighlight(RenderHighlightEvent.Block event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.level == null) return;

        ItemStack heldItem = player.getMainHandItem();
        var enchantmentRegistry = mc.level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        var enchantmentHolder = enchantmentRegistry.getHolderOrThrow(ModEnchantments.DIGGER);
        int enchLevel = heldItem.getEnchantmentLevel(enchantmentHolder);

        if (enchLevel <= 0) return;

        event.setCanceled(true);
        BlockHitResult blockHit = event.getTarget();
        BlockPos origin = blockHit.getBlockPos();
        Direction side = blockHit.getDirection();
        int range = player.isCrouching() ? 0 : 1;
        AABB customBox = getSelectionBox(range, side, origin);
        PoseStack poseStack = event.getPoseStack();
        Vec3 cameraPos = event.getCamera().getPosition();
        poseStack.pushPose();
        poseStack.translate(customBox.minX - cameraPos.x, customBox.minY - cameraPos.y, customBox.minZ - cameraPos.z);
        AABB renderBox = new AABB(0, 0, 0, customBox.getXsize(), customBox.getYsize(), customBox.getZsize());

        VertexConsumer bufferBuilder = event.getMultiBufferSource().getBuffer(RenderType.lines());
        
        LevelRenderer.renderLineBox(
                poseStack, 
                bufferBuilder, 
                renderBox, 
                0.0F, 0.0F, 0.0F, 0.4F
        );

        poseStack.popPose();
    }

    private static AABB getSelectionBox(int range, Direction side, BlockPos origin) {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;

        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                BlockPos pos = switch (side.getAxis()) {
                    case Y -> origin.offset(x, 0, y);
                    case X -> origin.offset(0, y, x);
                    case Z -> origin.offset(x, y, 0);
                };

                minX = Math.min(minX, pos.getX());
                minY = Math.min(minY, pos.getY());
                minZ = Math.min(minZ, pos.getZ());
                maxX = Math.max(maxX, pos.getX() + 1);
                maxY = Math.max(maxY, pos.getY() + 1);
                maxZ = Math.max(maxZ, pos.getZ() + 1);
            }
        }

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }
}