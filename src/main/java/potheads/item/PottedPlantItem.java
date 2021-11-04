package potheads.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PottedPlantItem extends Item {

    public PottedPlantItem(Properties properties) {
        super(properties);
    }

    public FlowerPotBlock getAsBlock(ItemStack stack) {
        if (stack.getTag() != null && stack.getTag().contains("FlowerPot")) {
            String flowerPotName = stack.getTag().getString("FlowerPot");
            Block flowerPot = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(flowerPotName));
            if (flowerPot instanceof FlowerPotBlock && flowerPot != Blocks.AIR) {
                return (FlowerPotBlock) flowerPot;
            }
        }
        return (FlowerPotBlock) Blocks.FLOWER_POT;
    }

    public ItemStack getAsItem(FlowerPotBlock flowerPot) {
        ItemStack result = new ItemStack(this);
        // noinspection ConstantConditions
        result.getOrCreateTag().putString("FlowerPot", flowerPot.getRegistryName().toString());
        return result;
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
        if (this.allowdedIn(tab)) {
            for (FlowerPotBlock flowerPot : getAllFlowerPots()) {
                items.add(getAsItem(flowerPot));
            }
        }
    }

    public static List<FlowerPotBlock> getAllFlowerPots() {
        return ForgeRegistries.BLOCKS.getValues()
                .stream()
                .filter(block -> block instanceof FlowerPotBlock flowerPot && flowerPot.getEmptyPot() != flowerPot)
                .map(block -> (FlowerPotBlock) block)
                .collect(Collectors.toList());
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        Item emptyPot = getAsBlock(stack).getEmptyPot().asItem();
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
    public Component getName(ItemStack stack) {
        return getAsBlock(stack).getName();
    }

    @Override
    protected String getOrCreateDescriptionId() {
        return Items.FLOWER_POT.getDescriptionId();
    }

    @Override
    public InteractionResult useOn(UseOnContext useContext) {
        InteractionResult placeResult = place(new BlockPlaceContext(useContext));
        if (!placeResult.consumesAction() && isEdible() && useContext.getPlayer() != null) {
            InteractionResult useResult = use(useContext.getLevel(), useContext.getPlayer(), useContext.getHand()).getResult();
            return useResult == InteractionResult.CONSUME ? InteractionResult.CONSUME_PARTIAL : useResult;
        } else {
            return placeResult;
        }
    }

    public InteractionResult place(BlockPlaceContext placeContext) {
        if (!placeContext.canPlace()) {
            return InteractionResult.FAIL;
        }

        BlockState blockstate = this.getPlacementState(placeContext);
        if (blockstate == null) {
            return InteractionResult.FAIL;
        } else if (!this.placeBlock(placeContext, blockstate)) {
            return InteractionResult.FAIL;
        }

        BlockPos blockpos = placeContext.getClickedPos();
        Level level = placeContext.getLevel();
        Player player = placeContext.getPlayer();
        ItemStack itemstack = placeContext.getItemInHand();
        BlockState placedState = level.getBlockState(blockpos);
        if (placedState.is(blockstate.getBlock())) {
            placedState = this.updateBlockStateFromTag(blockpos, level, itemstack, placedState);
            updateCustomBlockEntityTag(blockpos, level, player, itemstack);
            placedState.getBlock().setPlacedBy(level, blockpos, placedState, player, itemstack);
            if (player instanceof ServerPlayer) {
                CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) player, blockpos, itemstack);
            }
        }

        level.gameEvent(player, GameEvent.BLOCK_PLACE, blockpos);
        SoundType soundtype = placedState.getSoundType(level, blockpos, placeContext.getPlayer());
        level.playSound(player, blockpos, this.getPlaceSound(placedState, level, blockpos, placeContext.getPlayer()), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
        if (player == null || !player.getAbilities().instabuild) {
            itemstack.shrink(1);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    protected SoundEvent getPlaceSound(BlockState state, Level world, BlockPos pos, Player entity) {
        return state.getSoundType(world, pos, entity).getPlaceSound();
    }

    @Nullable
    protected BlockState getPlacementState(BlockPlaceContext context) {
        BlockState blockstate = getAsBlock(context.getItemInHand()).getStateForPlacement(context);
        return blockstate != null && canPlace(context, blockstate) ? blockstate : null;
    }

    protected void updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack stack) {
        BlockItem.updateCustomBlockEntityTag(level, player, pos, stack);
    }

    private BlockState updateBlockStateFromTag(BlockPos pos, Level level, ItemStack stack, BlockState state) {
        BlockState blockstate = state;
        CompoundTag compoundtag = stack.getTag();
        if (compoundtag != null) {
            CompoundTag blockStateTag = compoundtag.getCompound("BlockStateTag");
            StateDefinition<Block, BlockState> stateDefinition = state.getBlock().getStateDefinition();

            for (String key : blockStateTag.getAllKeys()) {
                Property<?> property = stateDefinition.getProperty(key);
                if (property != null) {
                    // noinspection ConstantConditions
                    String keyName = blockStateTag.get(key).getAsString();
                    blockstate = updateState(blockstate, property, keyName);
                }
            }
        }

        if (blockstate != state) {
            level.setBlock(pos, blockstate, 2);
        }

        return blockstate;
    }

    private static <T extends Comparable<T>> BlockState updateState(BlockState state, Property<T> property, String valueIdentifier) {
        return property.getValue(valueIdentifier).map((value) -> state.setValue(property, value)).orElse(state);
    }

    protected boolean canPlace(BlockPlaceContext context, BlockState state) {
        Player player = context.getPlayer();
        CollisionContext collisioncontext = player == null ? CollisionContext.empty() : CollisionContext.of(player);
        return (!this.mustSurvive() || state.canSurvive(context.getLevel(), context.getClickedPos())) && context.getLevel().isUnobstructed(state, context.getClickedPos(), collisioncontext);
    }

    protected boolean mustSurvive() {
        return true;
    }

    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        return context.getLevel().setBlock(context.getClickedPos(), state, 11);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        this.getAsBlock(stack).appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {

            private final BlockEntityWithoutLevelRenderer renderer = new PottedPlantRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return renderer;
            }
        });
    }
}
