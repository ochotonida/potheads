package potheads.recipe;

import net.minecraft.block.Block;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import potheads.Potheads;
import potheads.mixin.FlowerPotBlockAccessor;

import java.util.function.Supplier;

public class PottedPlantRecipe extends SpecialRecipe {

    public PottedPlantRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        return !getCraftingResult(inventory).isEmpty();
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inventory) {
        FlowerPotBlock flowerPotBlock = null;
        ResourceLocation flower = null;
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof BlockItem) {
                    Block block = ((BlockItem) stack.getItem()).getBlock();
                    if (block instanceof FlowerPotBlock && ((FlowerPotBlock) block).getEmptyPot() == block) {
                        if (flowerPotBlock != null) {
                            return ItemStack.EMPTY; // too many pots
                        }
                        flowerPotBlock = (FlowerPotBlock) block;
                        continue;
                    }
                    if (flower != null) {
                        return ItemStack.EMPTY; // too many flowers
                    }
                    flower = block.getRegistryName();
                }
            }
        }
        if (flower == null || flowerPotBlock == null) {
            return ItemStack.EMPTY;
        }
        Supplier<? extends Block> pottedPlant = ((FlowerPotBlockAccessor) flowerPotBlock).getFullPots().get(flower);
        if (pottedPlant == null) {
            return ItemStack.EMPTY;
        }
        ItemStack result = new ItemStack(Potheads.POTTED_PLANT.get());
        // noinspection ConstantConditions
        result.getOrCreateTag().putString("flower_pot", pottedPlant.get().getRegistryName().toString());
        return result;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return Potheads.POTTED_PLANT_CRAFTING_SERIALIZER.get();
    }
}
