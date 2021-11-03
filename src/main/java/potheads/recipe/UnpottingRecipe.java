package potheads.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FlowerPotBlock;
import potheads.init.ModItems;
import potheads.init.ModRecipeSerializers;

public class UnpottingRecipe extends CustomRecipe {

    public UnpottingRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingContainer inventory, Level world) {
        int itemCount = 0;
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            if (!inventory.getItem(slot).isEmpty() && ++itemCount > 1) {
                return false;
            }
        }
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            if (!inventory.getItem(slot).isEmpty()) {
                ItemStack stack = inventory.getItem(slot);
                if (stack.getItem() != ModItems.POTTED_PLANT.get()) {
                    return false;
                }
                FlowerPotBlock flowerPotBlock = ModItems.POTTED_PLANT.get().getAsBlock(stack);
                return flowerPotBlock != flowerPotBlock.getEmptyPot() && flowerPotBlock.getContent().asItem() instanceof BlockItem;
            }
        }
        return false;
    }

    @Override
    public ItemStack assemble(CraftingContainer inventory) {
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            if (!inventory.getItem(slot).isEmpty()) {
                ItemStack stack = inventory.getItem(slot);
                FlowerPotBlock flowerPotBlock = ModItems.POTTED_PLANT.get().getAsBlock(stack);
                return new ItemStack(flowerPotBlock.getContent().asItem());
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.UNPOTTING_SERIALIZER.get();
    }
}
