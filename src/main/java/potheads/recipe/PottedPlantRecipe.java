package potheads.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;
import potheads.Potheads;

import java.util.function.Supplier;

public class PottedPlantRecipe extends CustomRecipe {

    public PottedPlantRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingContainer inventory, Level world) {
        return !assemble(inventory).isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingContainer inventory) {
        FlowerPotBlock emptyPot = null;
        ResourceLocation flower = null;
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof BlockItem) {
                    Block block = ((BlockItem) stack.getItem()).getBlock();
                    if (block instanceof FlowerPotBlock flowerPot && flowerPot.getEmptyPot() == flowerPot) {
                        if (emptyPot != null) {
                            return ItemStack.EMPTY; // too many pots
                        }
                        emptyPot = flowerPot;
                        continue;
                    }
                    if (flower != null) {
                        return ItemStack.EMPTY; // too many flowers
                    }
                    flower = block.getRegistryName();
                }
            }
        }
        if (flower == null || emptyPot == null) {
            return ItemStack.EMPTY;
        }

        Supplier<? extends Block> pottedPlant = emptyPot.getFullPotsView().get(flower);
        if (pottedPlant == null) {
            return ItemStack.EMPTY;
        }
        ItemStack result = new ItemStack(Potheads.POTTED_PLANT.get());
        // noinspection ConstantConditions
        result.getOrCreateTag().putString("flower_pot", pottedPlant.get().getRegistryName().toString());
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Potheads.POTTED_PLANT_CRAFTING_SERIALIZER.get();
    }
}
