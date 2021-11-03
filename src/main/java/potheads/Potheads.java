package potheads;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import potheads.integration.curios.CuriosIntegration;
import potheads.item.PottedPlantItem;
import potheads.recipe.UnpottingRecipe;
import potheads.recipe.PottingRecipe;

@Mod(Potheads.MODID)
public class Potheads {

    public static final String MODID = "potheads";

    private static final CreativeModeTab CREATIVE_TAB = new CreativeModeTab(MODID + ".potted_plants") {
        @Override
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return new ItemStack(Items.FLOWER_POT);
        }
    };

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

    public static final RegistryObject<Item> POTTED_PLANT = ITEMS.register("potted_plant", () -> new PottedPlantItem(new Item.Properties().tab(CREATIVE_TAB)));

    public static final RegistryObject<RecipeSerializer<?>> POTTED_PLANT_CRAFTING_SERIALIZER = RECIPE_SERIALIZERS.register("potting", () -> new SimpleRecipeSerializer<>(PottingRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> PLANT_FROM_POT_CRAFTING_SERIALIZER = RECIPE_SERIALIZERS.register("unpotting", () -> new SimpleRecipeSerializer<>(UnpottingRecipe::new));

    public Potheads() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);

        if (ModList.get().isLoaded("curios")) {
            new CuriosIntegration();
        }
    }
}
