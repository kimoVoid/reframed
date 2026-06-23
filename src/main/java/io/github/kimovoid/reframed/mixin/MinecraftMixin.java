package io.github.kimovoid.reframed.mixin;

import io.github.kimovoid.reframed.ReframedMinecraft;
import net.minecraft.client.Minecraft;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.ByteBuffer;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/Display;setTitle(Ljava/lang/String;)V",
                    remap = false,
                    shift = At.Shift.AFTER
            )
    )
    private void setupDisplay(CallbackInfo ci) {
        this.applyIcons();

        if (Display.getTitle().contains("Minecraft Minecraft")) {
            Display.setTitle(Display.getTitle().replaceFirst("Minecraft ", ""));
        }

        Display.setResizable(true);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void lateSetupIcons(CallbackInfo ci) {
        this.applyIcons(); // windows is a lovely OS
    }

    @Redirect(
            method = "run",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/Display;isActive()Z",
                    remap = false
            )
    )
    private boolean noUnFullscreenOnUnfocus() {
        return true;
    }

    @Inject(method = "initLicenseCheckThread", at = @At("HEAD"), cancellable = true)
    private void killHttpRequestToDeadUrl(CallbackInfo ci) {
        ci.cancel();
    }

    @Unique
    private void applyIcons() {
        ByteBuffer[] icons = new ByteBuffer[4];

        try {
            icons[0] = loadIcon("/assets/reframed/icons/16.png");
            icons[1] = loadIcon("/assets/reframed/icons/32.png");
            icons[2] = loadIcon("/assets/reframed/icons/64.png");
            icons[3] = loadIcon("/assets/reframed/icons/256.png");
        } catch (Exception ignored) {}

        if (icons[0] != null
                && icons[1] != null
                && icons[2] != null
                && icons[3] != null) {
            Display.setIcon(icons);
        }
    }

    @Unique
    private ByteBuffer loadIcon(String path) {
        try {
            InputStream stream = ReframedMinecraft.class.getResourceAsStream(path);
            if (stream == null) return null;
            BufferedImage image = ImageIO.read(stream);
            int w = image.getWidth(), h = image.getHeight();
            int[] pixels = new int[w * h];
            image.getRGB(0, 0, w, h, pixels, 0, w);
            ByteBuffer buffer = BufferUtils.createByteBuffer(w * h * 4);
            for (int pixel : pixels) {
                buffer.put((byte) ((pixel >> 24) & 0xFF)); // A
                buffer.put((byte) ((pixel >> 16) & 0xFF)); // R
                buffer.put((byte) ((pixel >> 8) & 0xFF));  // G
                buffer.put((byte) (pixel & 0xFF));         // B
            }
            buffer.flip();
            return buffer;
        } catch (Exception e) {
            return null;
        }
    }
}