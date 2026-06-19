package me.kimovoid.reframed.mixin.lwjgl3;

import me.kimovoid.reframed.lwjgl3compat.annotations.Public;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Screen.class)
public class MixinScreenFixClipboard {

	/**
	 * @author moehreag
	 * @reason Fix clipboard access with GLFW
	 */
	@Overwrite
	public static String getClipboard() {
		String clip = GLFW.glfwGetClipboardString(Display.getHandle());
        return clip == null ? "" : clip;
	}

	/**
	 * @author moehreag
	 * @reason Fix clipboard access with GLFW
	 */
	//@Overwrite
	@Public
	private static void setClipboard(String string){
		GLFW.glfwSetClipboardString(Display.getHandle(), string);
	}
}
