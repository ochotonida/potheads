package potheads.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;

public class PottedPlantItem extends Item implements ICurioItem {

    public PottedPlantItem(Properties properties) {
        super(properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean canRender(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        return true;
    }

    @Override
    public void render(String identifier, int index, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, ItemStack stack) {
        EntityRenderer<?> renderer = Minecraft.getInstance().getRenderManager().getRenderer(livingEntity);
        if (!(renderer instanceof IEntityRenderer<?, ?>)) {
            return;
        }
        EntityModel<?> model = ((IEntityRenderer<?, ?>) renderer).getEntityModel();
        if (!(model instanceof BipedModel<?>)) {
            return;
        }

        // noinspection unchecked
        BipedModel<LivingEntity> bipedModel = (BipedModel<LivingEntity>) model;

        ICurio.RenderHelper.followHeadRotations(livingEntity, bipedModel.bipedHead);
        bipedModel.bipedHead.translateRotate(matrixStack);

        Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.HEAD, light, OverlayTexture.NO_OVERLAY, matrixStack, renderTypeBuffer);
    }

    public FlowerPotBlock getBlock(ItemStack stack) {
        if (stack.getTag() != null && stack.getTag().contains("flower_pot")) {
            String flowerPotName = stack.getTag().getString("flower_pot");
            Block flowerPot = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(flowerPotName));
            if (flowerPot instanceof FlowerPotBlock && flowerPot != Blocks.AIR) {
                return (FlowerPotBlock) flowerPot;
            }
        }
        return (FlowerPotBlock) Blocks.FLOWER_POT;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            for (Block block : ForgeRegistries.BLOCKS.getValues()) {
                if (block instanceof FlowerPotBlock && ((FlowerPotBlock) block).getEmptyPot() != block) {
                    ItemStack stack = new ItemStack(this);
                    // noinspection ConstantConditions
                    stack.getOrCreateTag().putString("flower_pot", block.getRegistryName().toString());
                    items.add(stack);
                }
            }
        }
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        Item emptyPot = getBlock(stack).getEmptyPot().asItem();
        if (emptyPot == Items.AIR) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(emptyPot);
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return !getContainerItem(stack).isEmpty();
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        if (stack.getTag() != null) {
            String flowerPotName = stack.getTag().getString("flower_pot");
            if (!flowerPotName.equals("")) {
                Block flowerPot = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(flowerPotName));
                if (flowerPot != Blocks.AIR && flowerPot != null) {
                    return flowerPot.getTranslatedName();
                }
            }
        }
        return super.getDisplayName(stack);
    }

    @Override
    public String getTranslationKey() {
        return Items.FLOWER_POT.getTranslationKey();
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        return tryPlace(new BlockItemUseContext(context));
    }

    public ActionResultType tryPlace(BlockItemUseContext context) {
        Item flowerPot = getBlock(context.getItem()).asItem();
        if (flowerPot instanceof BlockItem) {
            return ((BlockItem) flowerPot).tryPlace(context);
        }

        if (!context.canPlace()) {
            return ActionResultType.FAIL;
        } else {
            BlockState stateForPlacement = getStateForPlacement(context);
            if (stateForPlacement == null) {
                return ActionResultType.FAIL;
            } else if (!placeBlock(context, stateForPlacement)) {
                return ActionResultType.FAIL;
            } else {
                BlockPos pos = context.getPos();
                World world = context.getWorld();
                PlayerEntity player = context.getPlayer();
                ItemStack stack = context.getItem();
                BlockState placedState = world.getBlockState(pos);
                Block block = placedState.getBlock();
                if (block == stateForPlacement.getBlock()) {
                    placedState = getUpdatedState(pos, world, stack, placedState);
                    setTileEntityNBT(world, player, pos, stack);
                    block.onBlockPlacedBy(world, pos, placedState, player, stack);
                    if (player instanceof ServerPlayerEntity) {
                        CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity) player, pos, stack);
                    }
                }

                SoundType soundtype = placedState.getSoundType(world, pos, context.getPlayer());
                world.playSound(player, pos, getPlaceSound(placedState, world, pos, context.getPlayer()), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                if (player == null || !player.abilities.isCreativeMode) {
                    stack.shrink(1);
                }

                return ActionResultType.func_233537_a_(world.isRemote);
            }
        }
    }

    protected SoundEvent getPlaceSound(BlockState state, World world, BlockPos pos, PlayerEntity entity) {
        return state.getSoundType(world, pos, entity).getPlaceSound();
    }

    @Nullable
    protected BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState blockstate = this.getBlock(context.getItem()).getStateForPlacement(context);
        return blockstate != null && this.canPlace(context, blockstate) ? blockstate : null;
    }

    private BlockState getUpdatedState(BlockPos pos, World world, ItemStack stack, BlockState placedState) {
        BlockState result = placedState;
        CompoundNBT compoundnbt = stack.getTag();
        if (compoundnbt != null) {
            CompoundNBT blockStateTag = compoundnbt.getCompound("BlockStateTag");
            StateContainer<Block, BlockState> stateContainer = placedState.getBlock().getStateContainer();
            for (String key : blockStateTag.keySet()) {
                Property<?> property = stateContainer.getProperty(key);
                if (property != null) {
                    // noinspection ConstantConditions
                    result = applyProperty(result, property, blockStateTag.get(key).getString());
                }
            }
        }

        if (result != placedState) {
            world.setBlockState(pos, result, 2);
        }

        return result;
    }

    private static <T extends Comparable<T>> BlockState applyProperty(BlockState state, Property<T> property, String value) {
        return property.parseValue(value).map(v -> state.with(property, v)).orElse(state);
    }

    protected boolean canPlace(BlockItemUseContext useContext, BlockState state) {
        PlayerEntity player = useContext.getPlayer();
        ISelectionContext selectionContext = player == null ? ISelectionContext.dummy() : ISelectionContext.forEntity(player);
        return (state.isValidPosition(useContext.getWorld(), useContext.getPos())) && useContext.getWorld().placedBlockCollides(state, useContext.getPos(), selectionContext);
    }

    protected boolean placeBlock(BlockItemUseContext context, BlockState state) {
        return context.getWorld().setBlockState(context.getPos(), state, 11);
    }

    public static void setTileEntityNBT(World world, @Nullable PlayerEntity player, BlockPos pos, ItemStack stack) {
        MinecraftServer server = world.getServer();
        if (server != null) {
            CompoundNBT blockEntityTag = stack.getChildTag("BlockEntityTag");
            if (blockEntityTag != null) {
                TileEntity tileentity = world.getTileEntity(pos);
                if (tileentity != null && (world.isRemote || !tileentity.onlyOpsCanSetNbt() || (player != null && player.canUseCommandBlock()))) {
                    CompoundNBT tag = tileentity.write(new CompoundNBT());
                    CompoundNBT tagCopy = tag.copy();
                    tag.merge(blockEntityTag);
                    tag.putInt("x", pos.getX());
                    tag.putInt("y", pos.getY());
                    tag.putInt("z", pos.getZ());
                    if (!tag.equals(tagCopy)) {
                        tileentity.read(world.getBlockState(pos), tag);
                        tileentity.markDirty();
                    }
                }
            }
        }
    }
}
