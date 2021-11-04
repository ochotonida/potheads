package potheads.loot;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import potheads.init.ModItems;

import javax.annotation.Nonnull;
import java.util.List;

public class FlowerPotLootModifier extends LootModifier {

    protected FlowerPotLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        if (!context.hasParam(LootContextParams.BLOCK_STATE)) {
            return generatedLoot;
        }
        if (!(context.getParam(LootContextParams.BLOCK_STATE).getBlock() instanceof FlowerPotBlock flowerPot) || flowerPot.getEmptyPot() == flowerPot) {
            return generatedLoot;
        }

        boolean containsPot = false;
        boolean containsPlant = false;

        for (ItemStack stack : generatedLoot) {
            if (stack.getItem() == flowerPot.getEmptyPot().asItem()) {
                containsPot = true;
            } else if (stack.getItem() == flowerPot.getContent().asItem()) {
                containsPlant = true;
            }
        }

        if (!containsPlant || !containsPot) {
            return generatedLoot;
        }

        generatedLoot.removeIf(stack -> stack.getItem() == flowerPot.getEmptyPot().asItem());
        generatedLoot.removeIf(stack -> stack.getItem() == flowerPot.getContent().asItem());

        generatedLoot.add(ModItems.POTTED_PLANT.get().getAsItem(flowerPot));

        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<FlowerPotLootModifier> {

        @Override
        public FlowerPotLootModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] conditions) {
            return new FlowerPotLootModifier(conditions);
        }

        @Override
        public JsonObject write(FlowerPotLootModifier instance) {
            return makeConditions(instance.conditions);
        }
    }
}
