package potheads.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraftforge.registries.ForgeRegistries;
import potheads.PotHeads;
import potheads.init.ModItems;
import potheads.item.PottedPlantItem;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
@SuppressWarnings("unused")
public class JEIPlugin implements IModPlugin {

    public static final ResourceLocation ID = new ResourceLocation(PotHeads.MODID, "main");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.useNbtForSubtypes(ModItems.POTTED_PLANT.get());
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(RecipeTypes.CRAFTING, createRecipes());
    }

    private static List<CraftingRecipe> createRecipes() {
        List<CraftingRecipe> result = new ArrayList<>();
        for (FlowerPotBlock flowerPot : PottedPlantItem.getAllFlowerPots()) {
            ItemStack pottedPlant = ModItems.POTTED_PLANT.get().getAsItem(flowerPot);
            ItemStack unpottingResult = new ItemStack(flowerPot.getContent());

            String pottingGroup = "jei/potting";
            String unpottingGroup = "jei/unpotting";


            NonNullList<Ingredient> pottingIngredients = NonNullList.of(
                    Ingredient.EMPTY,
                    Ingredient.of(flowerPot.getEmptyPot()),
                    Ingredient.of(flowerPot.getContent())
            );

            NonNullList<Ingredient> unpottingIngredients = NonNullList.of(
                    Ingredient.EMPTY,
                    Ingredient.of(pottedPlant)
            );

            ResourceLocation id = ForgeRegistries.BLOCKS.getKey(flowerPot);
            // noinspection ConstantConditions
            String namespace = id.getNamespace();
            String path = id.getPath();

            ResourceLocation pottingId = new ResourceLocation(PotHeads.MODID, "jei/potting/%s/%s".formatted(namespace, path));
            ResourceLocation unpottingId = new ResourceLocation(PotHeads.MODID, "jei/unpotting/%s/%s".formatted(namespace, path));

            ShapedRecipe pottingRecipe = new ShapedRecipe(pottingId, pottingGroup, 2, 1, pottingIngredients, pottedPlant);
            ShapedRecipe unpottingRecipe = new ShapedRecipe(unpottingId, unpottingGroup, 1, 1, unpottingIngredients, unpottingResult);

            result.add(pottingRecipe);
            result.add(unpottingRecipe);
        }
        return result;
    }
}
