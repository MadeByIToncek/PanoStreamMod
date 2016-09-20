package com.replaymod.panostream.mixin;

import com.replaymod.panostream.capture.PanoramicScreenshotCapturer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.File;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    private static PanoramicScreenshotCapturer screenshotCapturer;

    @Redirect(method = "dispatchKeypresses", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ScreenShotHelper;saveScreenshot(Ljava/io/File;IILnet/minecraft/client/shader/Framebuffer;)Lnet/minecraft/util/text/ITextComponent;"))
    private ITextComponent redirectScreenshot(File gameDirectory, int width, int height, Framebuffer buffer) {
        if(screenshotCapturer == null) {
            screenshotCapturer = new PanoramicScreenshotCapturer();
            screenshotCapturer.register();
        }

        if(!GuiScreen.isShiftKeyDown() && !GuiScreen.isCtrlKeyDown()) {
            return ScreenShotHelper.saveScreenshot(gameDirectory, width, height, buffer);
        }

        screenshotCapturer.captureScreenshotAsync(gameDirectory);

        return new TextComponentTranslation("panostream.chat.screenshot.saving");
    }

}
