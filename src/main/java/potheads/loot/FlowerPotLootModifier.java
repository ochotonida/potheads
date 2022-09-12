package potheads.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import potheads.init.ModItems;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class FlowerPotLootModifier extends LootModifier {

    public static final Supplier<Codec<FlowerPotLootModifier>> CODEC = Suppliers.memoize(
            () -> RecordCodecBuilder.create(instance -> codecStart(instance)
                    .apply(instance, FlowerPotLootModifier::new)
            )
    );

    protected FlowerPotLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Nonnull
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (!context.hasParam(LootContextParams.BLOCK_STATE)) {
            return generatedLoot;
        }
        if (!(context.getParam(LootContextParams.BLOCK_STATE).getBlock() instanceof FlowerPotBlock flowerPot) || flowerPot.getEmptyPot() == flowerPot) {
            return generatedLoot;
        }

        boolean containsPot = false;
        boolean containsPlant = false;

        Item emptyPot = flowerPot.getEmptyPot().asItem();
        Item plant = flowerPot.getContent().asItem();

        for (ItemStack stack : generatedLoot) {
            if (stack.getItem() == emptyPot) {
                containsPot = true;
            } else if (stack.getItem() == plant) {
                containsPlant = true;
            }
        }

        if (!containsPlant || !containsPot) {
            return generatedLoot;
        }

        generatedLoot.removeIf(stack -> stack.getItem() == emptyPot);
        generatedLoot.removeIf(stack -> stack.getItem() == plant);

        generatedLoot.add(ModItems.POTTED_PLANT.get().getAsItem(flowerPot));

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
