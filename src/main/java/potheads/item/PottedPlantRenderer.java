package potheads.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.data.EmptyModelData;

public class PottedPlantRenderer extends BlockEntityWithoutLevelRenderer {

    public PottedPlantRenderer() {
        // noinspection ConstantConditions
        super(null, null);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        if (stack.getItem() instanceof PottedPlantItem) {
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(((PottedPlantItem) stack.getItem()).getAsBlock(stack).defaultBlockState(), poseStack, buffer, light, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
        }
    }
}
