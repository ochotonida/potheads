package potheads.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import potheads.Potheads;
import potheads.item.PottedPlantItem;

public class PlantFromPotRecipe extends SpecialRecipe {

    public PlantFromPotRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        int itemCount = 0;
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            if (!inventory.getStackInSlot(slot).isEmpty() && ++itemCount > 1) {
                return false;
            }
        }
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            if (!inventory.getStackInSlot(slot).isEmpty()) {
                ItemStack stack = inventory.getStackInSlot(slot);
                if (stack.getItem() != Potheads.POTTED_PLANT.get()) {
                    return false;
                }
                FlowerPotBlock flowerPotBlock = ((PottedPlantItem) Potheads.POTTED_PLANT.get()).getBlock(stack);
                return flowerPotBlock != Blocks.FLOWER_POT && flowerPotBlock.getFlower().asItem() instanceof BlockItem;
            }
        }
        return false;
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inventory) {
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            if (!inventory.getStackInSlot(slot).isEmpty()) {
                ItemStack stack = inventory.getStackInSlot(slot);
                FlowerPotBlock flowerPotBlock = ((PottedPlantItem) Potheads.POTTED_PLANT.get()).getBlock(stack);
                return new ItemStack(flowerPotBlock.getFlower().asItem());
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return Potheads.PLANT_FROM_POT_CRAFTING_SERIALIZER.get();
    }
}
