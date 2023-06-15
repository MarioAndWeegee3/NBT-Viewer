package marioandweegee3.nbtviewer

import marioandweegee3.nbtviewer.gui.NBTViewerGui
import marioandweegee3.nbtviewer.gui.NBTViewerScreen
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.nbt.NbtCompound
import org.lwjgl.glfw.GLFW
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Environment(EnvType.CLIENT)
@Suppress("unused")
object NBTViewerMod : ClientModInitializer {
    const val modId = "nbtviewer"
    val logger: Logger = LoggerFactory.getLogger("NBT Viewer")

    private lateinit var key: KeyBinding

    override fun onInitializeClient() {
        logger.info("Initializing...")

        key = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.nbtviewer.view_nbt",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_ALT,
                "category.nbtviewer.keys"
            )
        )

        ClientTickEvents.END_CLIENT_TICK.register { client ->
            if (key.wasPressed()) {
                val inv = client.player?.inventory
                    ?: return@register

                val nbt = inv.mainHandStack?.nbt
                    ?: NbtCompound()

                client.setScreen(
                    NBTViewerScreen(NBTViewerGui(nbt))
                )
            }
        }
    }
}