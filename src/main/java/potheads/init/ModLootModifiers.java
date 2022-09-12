package potheads.init;

import com.mojang.serialization.Codec;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import potheads.PotHeads;
import potheads.loot.FlowerPotLootModifier;

public class ModLootModifiers {

    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, PotHeads.MODID);

    @SuppressWarnings("unused")
    public static final RegistryObject<Codec<FlowerPotLootModifier>> REPLACE_FLOWER_POT_DROPS = LOOT_MODIFIERS.register("replace_flower_pot_drops", FlowerPotLootModifier.CODEC);
}
