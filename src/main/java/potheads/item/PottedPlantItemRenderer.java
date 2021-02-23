package potheads.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.data.EmptyModelData;

public class PottedPlantItemRenderer extends ItemStackTileEntityRenderer {

    @Override
    public void func_239207_a_(ItemStack stack, ItemCameraTransforms.TransformType p_239207_2_, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        if (stack.getItem() instanceof PottedPlantItem) {
            Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(((PottedPlantItem) stack.getItem()).getBlock(stack).getDefaultState(), matrixStack, buffer, combinedLight, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
        }
    }
}
