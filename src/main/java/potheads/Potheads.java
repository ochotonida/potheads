package potheads;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import potheads.item.PottedPlantItem;
import potheads.item.PottedPlantItemRenderer;
import potheads.recipe.PlantFromPotRecipe;
import potheads.recipe.PottedPlantRecipe;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

@Mod(Potheads.MODID)
public class Potheads {

    public static final String MODID = "potheads";

    private static final ItemGroup CREATIVE_TAB = new ItemGroup(MODID + ".potted_plants") {
        @Override
        @OnlyIn(Dist.CLIENT)
        public ItemStack createIcon() {
            return new ItemStack(Items.FLOWER_POT);
        }
    };

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

    public static final RegistryObject<Item> POTTED_PLANT = ITEMS.register("potted_plant", () -> new PottedPlantItem(new Item.Properties().group(CREATIVE_TAB).setISTER(() -> PottedPlantItemRenderer::new)));

    public static final RegistryObject<IRecipeSerializer<?>> POTTED_PLANT_CRAFTING_SERIALIZER = RECIPE_SERIALIZERS.register("crafting_special_potted_plant", () -> new SpecialRecipeSerializer<>(PottedPlantRecipe::new));
    public static final RegistryObject<IRecipeSerializer<?>> PLANT_FROM_POT_CRAFTING_SERIALIZER = RECIPE_SERIALIZERS.register("crafting_special_plant_from_pot", () -> new SpecialRecipeSerializer<>(PlantFromPotRecipe::new));

    public Potheads() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::enqueueInterModComms);
        ITEMS.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
    }

    public void enqueueInterModComms(InterModEnqueueEvent event) {
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.HEAD.getMessageBuilder().build());
    }
}
