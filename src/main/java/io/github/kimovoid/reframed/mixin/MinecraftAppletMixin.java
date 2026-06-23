package io.github.kimovoid.reframed.mixin;

import io.github.kimovoid.reframed.ReframedMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MinecraftApplet;
import net.minecraft.client.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.applet.Applet;

@SuppressWarnings({"removal"})
@Mixin(MinecraftApplet.class)
public class MinecraftAppletMixin extends Applet {

	@Shadow private Minecraft minecraft;

	/**
	 * @author kimoVoid
	 * @reason Replace AWT canvas
	 */
	@Overwrite
	public void init() {
		boolean bl = false;
		if (this.getParameter("fullscreen") != null) {
			bl = this.getParameter("fullscreen").equalsIgnoreCase("true");
		}

		this.minecraft = new ReframedMinecraft(getWidth(), getHeight(), bl);

		this.minecraft.hostAddress = this.getDocumentBase().getHost();
		if (this.getDocumentBase().getPort() > 0) {
			this.minecraft.hostAddress = this.minecraft.hostAddress + ":" + this.getDocumentBase().getPort();
		}
		if (this.getParameter("username") != null && this.getParameter("sessionid") != null) {
			this.minecraft.session = new Session(this.getParameter("username"), this.getParameter("sessionid"));
			System.out.println("Setting user: " + this.minecraft.session.username + ", " + this.minecraft.session.id);
			if (this.getParameter("mppass") != null) {
				this.minecraft.session.password = this.getParameter("mppass");
			}
		} else {
			this.minecraft.session = new Session("Player", "");
		}
		if (this.getParameter("server") != null && this.getParameter("port") != null) {
			this.minecraft.setStartupServer(this.getParameter("server"), Integer.parseInt(this.getParameter("port")));
		}

		this.minecraft.run();
	}
}