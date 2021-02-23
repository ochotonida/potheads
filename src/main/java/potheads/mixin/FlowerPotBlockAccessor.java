package potheads.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.function.Supplier;

@Mixin(FlowerPotBlock.class)
public interface FlowerPotBlockAccessor {

    @Accessor
    Map<ResourceLocation, Supplier<? extends Block>> getFullPots();
}
