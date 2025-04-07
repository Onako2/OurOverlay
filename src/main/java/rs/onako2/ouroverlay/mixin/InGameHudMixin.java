package rs.onako2.ouroverlay.mixin;


import com.cinemamod.mcef.MCEF;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.LayeredDrawer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rs.onako2.ouroverlay.OuroverlayClient;

import static rs.onako2.ouroverlay.OuroverlayClient.browser;
import static rs.onako2.ouroverlay.OuroverlayClient.getUrl;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Unique
    private static int oldWidth;

    @Unique
    private static int oldHeight;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/LayeredDrawer;addSubDrawer(Lnet/minecraft/client/gui/LayeredDrawer;Ljava/util/function/BooleanSupplier;)Lnet/minecraft/client/gui/LayeredDrawer;", shift = At.Shift.BY, by = 2))
    private void init(MinecraftClient client, CallbackInfo ci, @Local(ordinal = 0) LayeredDrawer layeredDrawer) {
        if (browser == null) {
            boolean transparent = true;
            browser = MCEF.createBrowser(getUrl(), transparent);
        }
        layeredDrawer = layeredDrawer.addLayer(this::renderWebHud);
    }

    @Unique
    private void renderWebHud(DrawContext context, RenderTickCounter tickCounter) {
        if (OuroverlayClient.isVisible) {
            RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
            RenderSystem.setShaderTexture(0, browser.getRenderer().getTextureID());

            int width = context.getScaledWindowWidth();
            int height = context.getScaledWindowHeight();

            if (oldWidth != width || oldHeight != height) {
                browser.resize(width, height);
                browser.setZoomLevel(OuroverlayClient.getZoom());
            }

            oldWidth = width;
            oldHeight = height;

            Tessellator t = Tessellator.getInstance();
            BufferBuilder buffer = t.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            buffer.vertex(0, height, 0).texture(0.0f, 1.0f).color(255, 255, 255, 255);
            buffer.vertex(width, height, 0).texture(1.0f, 1.0f).color(255, 255, 255, 255);
            buffer.vertex(width, 0, 0).texture(1.0f, 0.0f).color(255, 255, 255, 255);
            buffer.vertex(0, 0, 0).texture(0.0f, 0.0f).color(255, 255, 255, 255);
            BufferRenderer.drawWithGlobalProgram(buffer.end());
            t.clear();
        }
    }
}