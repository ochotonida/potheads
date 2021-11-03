package potheads.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraftforge.common.crafting.IShapedRecipe;
import potheads.PotHeads;
import potheads.init.ModItems;
import potheads.item.PottedPlantItem;

import java.util.ArrayList;
import java.util.Collection;
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
        registration.addRecipes(createRecipes(), VanillaRecipeCategoryUid.CRAFTING);
    }

    private static Collection<IShapedRecipe<?>> createRecipes() {
        List<IShapedRecipe<?>> result = new ArrayList<>();
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

            // noinspection ConstantConditions
            ResourceLocation pottingId = new ResourceLocation(PotHeads.MODID, "jei/potting/" + flowerPot.getRegistryName().getPath());
            ResourceLocation unpottingId = new ResourceLocation(PotHeads.MODID, "jei/unpotting/" + flowerPot.getRegistryName().getPath());

            ShapedRecipe pottingRecipe = new ShapedRecipe(pottingId, pottingGroup, 2, 1, pottingIngredients, pottedPlant);
            ShapedRecipe unpottingRecipe = new ShapedRecipe(unpottingId, unpottingGroup, 1, 1, unpottingIngredients, unpottingResult);

            result.add(pottingRecipe);
            result.add(unpottingRecipe);
        }
        return result;
    }
}
