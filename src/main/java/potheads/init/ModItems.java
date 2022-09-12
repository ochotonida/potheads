package potheads.init;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import potheads.PotHeads;
import potheads.item.PottedPlantItem;

public class ModItems {

    private static final CreativeModeTab CREATIVE_TAB = new CreativeModeTab(PotHeads.MODID + ".potted_plants") {
        @Override
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return new ItemStack(Items.FLOWER_POT);
        }
    };

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, PotHeads.MODID);

    public static final RegistryObject<PottedPlantItem> POTTED_PLANT = ITEMS.register("potted_plant", () -> new PottedPlantItem(new Item.Properties().tab(CREATIVE_TAB)));
}
