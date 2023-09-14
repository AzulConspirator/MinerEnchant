package com.azul.minerenchant.mixin;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BlockBreakingInfo;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.azul.minerenchant.enchantment.digger_enchantment;
import com.azul.minerenchant.util.BlockFinder;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

@Mixin(WorldRenderer.class)
@Environment(EnvType.CLIENT)
public class WorldRendererMixin {

    @Shadow @Final private MinecraftClient client;
    @Shadow private ClientWorld world;
    @Shadow @Final private Long2ObjectMap<SortedSet<BlockBreakingInfo>> blockBreakingProgressions;

    @Inject(at = @At("HEAD"), method = "drawBlockOutline", cancellable = true)
    private void drawBlockOutline(MatrixStack stack, VertexConsumer vertexConsumer, Entity entity, double d, double e, double f, BlockPos blockPos, BlockState blockState, CallbackInfo ci) {

        // ensure player is not null
        if(this.client.player == null) {
            return;
        }

        // ensure world is not null
        if(this.client.world == null) {
            return;
        }

        // show extended outline if the player is holding a magna tool
        ItemStack heldStack = this.client.player.getInventory().getMainHandStack();
        if (heldStack == null || heldStack.isEmpty()) return;
        NbtList nbtList = heldStack.getEnchantments();
        for (int i = 0; i < nbtList.size(); i++) {
            NbtCompound compound = nbtList.getCompound(i);
            Registries.ENCHANTMENT.getOrEmpty(EnchantmentHelper.getIdFromNbt(compound)).ifPresent(enchantment -> {
                if (enchantment instanceof digger_enchantment) {
                    // only show extended outline for block raytraces
                    if (client.crosshairTarget instanceof BlockHitResult crosshairTarget)
                    {
                        BlockPos crosshairPos = crosshairTarget.getBlockPos();
                        BlockState crosshairState = client.world.getBlockState(crosshairPos);
                        // ensure we are not looking at air or an invalid block
                        if (!crosshairState.isAir() && client.world.getWorldBorder().contains(crosshairPos))
                        {
/*                             Iterable<BlockPos> iterator;

                            var pos2 = crosshairPos.offset(facing, 0); */
                            BlockHitResult blockHitResult = (BlockHitResult) this.client.player.raycast(4.5f, 1, false);
                            var facing = blockHitResult.getSide().getOpposite();
                            Iterable<BlockPos> positions = this.getBlockFinder().findBlocks(facing, blockHitResult.getBlockPos(),0);
/*                             if (facing.equals(Direction.DOWN) || facing.equals(Direction.UP)) 
                            {
                                iterator = BlockPos.iterate(crosshairPos.east().offset(Direction.NORTH),pos2.west().offset(Direction.SOUTH));
                            } 
                            else
                            {
                                iterator = BlockPos.iterate(crosshairPos.down().offset(facing.rotateCounterclockwise(Direction.Axis.Y)),pos2.up().offset(facing.rotateClockwise(Direction.Axis.Y)));
                            }
                            positions = iterator; */
                            List<VoxelShape> outlineShapes = new ArrayList<>();
                            outlineShapes.add(VoxelShapes.empty());

                            // assemble outline shape
                            for (BlockPos position : positions) {
                                BlockPos diffPos = position.subtract(crosshairPos);
                                BlockState offsetShape = world.getBlockState(position);

                                // if enableFull3x3 is 'true', all blocks will gain an outline, even if they are air
                                if (!offsetShape.isAir()) {
                                    // if fullBlockHitbox is 'true', all blocks will have a 16x16x16 hitbox regardless of their outline shape
                                    outlineShapes.set(0, VoxelShapes.union(outlineShapes.get(0), VoxelShapes.fullCube().offset(diffPos.getX(), diffPos.getY(), diffPos.getZ())));
                                }
                            }

                            outlineShapes.forEach(shape -> {
                                // draw extended hitbox
                                drawCuboidShapeOutline(
                                        stack,
                                        vertexConsumer,
                                        shape, // blockState.getOutlineShape(this.world, blockPos, ShapeContext.of(entity))
                                        (double) crosshairPos.getX() - d,
                                        (double) crosshairPos.getY() - e,
                                        (double) crosshairPos.getZ() - f,
                                        0.0F,
                                        0.0F,
                                        0.0F,
                                        0.4F);
                            });

                            // cancel 1x1 hitbox that would normally render
                            ci.cancel();
                        }
                    }
                }
            });
        }
    }
    public BlockFinder getBlockFinder()
    {
       return BlockFinder.DEFAULT;
    }
	@Invoker("drawCuboidShapeOutline")
    public static void drawCuboidShapeOutline(MatrixStack matrices, VertexConsumer vertexConsumer, VoxelShape shape, double offsetX, double offsetY, double offsetZ, float red, float green, float blue, float alpha) {
        throw new AssertionError();
    }
}