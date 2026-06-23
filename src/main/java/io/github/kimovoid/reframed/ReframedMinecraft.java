package io.github.kimovoid.reframed;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.Window;
import net.minecraft.util.crash.CrashReport;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

/**
 * Most of this stuff was ported from <a href="https://modrinth.com/mod/gambac">Gambac</a>
 * Credits to DanyGames2014
 */
public class ReframedMinecraft extends Minecraft {

    private final int previousWidth;
    private final int previousHeight;

    public ReframedMinecraft(int w, int h, boolean fullscreen) {
        super(null, null, null, w, h, fullscreen);
        this.previousWidth = w;
        this.previousHeight = h;
    }

    @Override
    public void handleCrash(CrashReport crashSummary) {
        io.github.kimovoid.reframed.lwjgl3compat.CrashReport.report(crashSummary);
        this.stop();
        System.exit(-1);
    }

    @Override
    public void tick() {
        if (Display.getWidth() != this.width || Display.getHeight() != this.height) {
            this.onResolutionChanged(Display.getWidth(), Display.getHeight());
        }

        super.tick();
    }

    @Override
    public void toggleFullscreen() {
        try {
            this.fullscreen = !this.fullscreen;

            if (fullscreen) {
                this.width = Display.getWidth();
                this.height = Display.getHeight();

                Display.setDisplayMode(Display.getDesktopDisplayMode());
                this.width = Display.getDisplayMode().getWidth();
                this.height = Display.getDisplayMode().getHeight();
            } else {
                this.width = this.previousWidth;
                this.height = this.previousHeight;
                Display.setDisplayMode(new DisplayMode(this.width, this.height));
            }
            if (this.width <= 0) {
                this.width = 1;
            }
            if (this.height <= 0) {
                this.height = 1;
            }

            if (this.screen != null) {
                this.onResolutionChanged(this.width, this.height);
            }

            Display.setFullscreen(fullscreen);
            Display.update();
        } catch (Exception ignored) {}
    }

    private void onResolutionChanged(int w, int h) {
        if (w <= 0) {
            w = 1;
        }
        if (h <= 0) {
            h = 1;
        }
        this.width = w;
        this.height = h;
        if (this.screen != null) {
            Window scaled = new Window(this.options, w, h);
            int scaledWidth = scaled.getWidth();
            int scaledHeight = scaled.getHeight();
            this.screen.init(this, scaledWidth, scaledHeight);
        }
    }
}