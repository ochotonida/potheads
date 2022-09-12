package potheads.init;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import potheads.PotHeads;
import potheads.recipe.PottingRecipe;
import potheads.recipe.UnpottingRecipe;

public class ModRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, PotHeads.MODID);

    public static final RegistryObject<RecipeSerializer<?>> POTTING_SERIALIZER = RECIPE_SERIALIZERS.register("potting", () -> new SimpleRecipeSerializer<>(PottingRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> UNPOTTING_SERIALIZER = RECIPE_SERIALIZERS.register("unpotting", () -> new SimpleRecipeSerializer<>(UnpottingRecipe::new));
}
