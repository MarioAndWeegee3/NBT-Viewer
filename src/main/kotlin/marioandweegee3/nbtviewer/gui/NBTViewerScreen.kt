package marioandweegee3.nbtviewer.gui

import io.github.cottonmc.cotton.gui.client.CottonClientScreen
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.text.LiteralText
import net.minecraft.text.TranslatableText

@Environment(EnvType.CLIENT)
class NBTViewerScreen(
    description: NBTViewerGui,
): CottonClientScreen(
    if (description.name === null)
        TranslatableText("text.nbtviewer.title")
    else
        LiteralText(description.name),
    description
)