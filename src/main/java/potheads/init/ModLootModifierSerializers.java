package potheads.init;

import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import potheads.PotHeads;
import potheads.loot.FlowerPotLootModifier;

public class ModLootModifierSerializers {

    public static final DeferredRegister<GlobalLootModifierSerializer<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, PotHeads.MODID);

    @SuppressWarnings("unused")
    public static final RegistryObject<FlowerPotLootModifier.Serializer> REPLACE_FLOWER_POT_DROPS = REGISTRY.register("replace_flower_pot_drops", FlowerPotLootModifier.Serializer::new);
}
