package marioandweegee3.nbtviewer.gui

import io.github.cottonmc.cotton.gui.client.CottonClientScreen
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.text.Text

@Environment(EnvType.CLIENT)
class NBTViewerScreen(
    description: NBTViewerGui,
) : CottonClientScreen(
    when (description.name) {
        null -> Text.translatable("text.nbtviewer.title")
        else -> Text.literal(description.name)
    },
    description
)