package potheads;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import potheads.init.ModItems;
import potheads.init.ModLootModifiers;
import potheads.init.ModRecipeSerializers;
import potheads.integration.curios.CuriosIntegration;

@Mod(PotHeads.MODID)
public class PotHeads {

    public static final String MODID = "potheads";

    public PotHeads() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(modEventBus);
        ModRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        ModLootModifiers.LOOT_MODIFIERS.register(modEventBus);

        if (ModList.get().isLoaded("curios")) {
            new CuriosIntegration();
        }
    }
}
