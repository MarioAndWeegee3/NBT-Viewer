package marioandweegee3.nbtviewer.gui

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription
import io.github.cottonmc.cotton.gui.widget.*
import io.github.cottonmc.cotton.gui.widget.data.Insets
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.nbt.*
import net.minecraft.nbt.visitor.NbtElementVisitor
import net.minecraft.text.Text

@Environment(EnvType.CLIENT)
class NBTViewerGui private constructor(
    nbt: NbtElement,
    private val enclosing: NBTViewerGui?,
    val name: String?,
) : LightweightGuiDescription() {
    init {
        val root = WGridPanel()
        rootPanel = root

        with(root) {
            insets = Insets.ROOT_PANEL

            val children = when (nbt) {
                is NbtCompound -> {
                    buildList {
                        val seq = nbt
                            .keys
                            .asSequence()
                            .map { it!! }
                            .sortedWith { a, b -> String.CASE_INSENSITIVE_ORDER.compare(a, b) }
                            .map { it to nbt[it]!! }

                        for ((k, v) in seq) {
                            add(getWidgetForElement(k, v))
                        }
                    }
                }

                is AbstractNbtList<*> -> {
                    buildList {
                        for ((index, value) in nbt.withIndex()) {
                            add(getWidgetForElement(index.toString(), value))
                        }
                    }
                }

                else -> emptyList()
            }

            add(
                WListPanel(children, ::WGridPanel) { w, p ->
                    p.add(w, 0, 0, 14, 1)
                },
                0, 1, 15, 9
            )

            val button = if (enclosing !== null) {
                WButton(Text.translatableWithFallback("text.nbtviewer.back", "Back"))
                    .setOnClick {
                        MinecraftClient
                            .getInstance()
                            .setScreen(NBTViewerScreen(enclosing))
                    }
            } else {
                WButton(Text.translatableWithFallback("text.nbtviewer.close", "Close"))
                    .setOnClick {
                        MinecraftClient
                            .getInstance()
                            .setScreen(null)
                    }
            }

            add(button, 12, 0, 2, 1)
        }

        root.validate(this)
    }

    constructor(nbt: NbtCompound) : this(nbt as NbtElement, null, null)

    private fun getWidgetForElement(index: String, element: AbstractNbtList<*>): WWidget {
        val button = WButton(Text.translatable("text.nbtviewer.element.list", index))
        val name = getSubScreenName(index)
        button.setOnClick {
            MinecraftClient
                .getInstance()
                .setScreen(NBTViewerScreen(NBTViewerGui(element, this, name)))
        }
        return button
    }

    private fun getWidgetForElement(index: String, element: NbtElement): WWidget {
        var widget: WWidget = WLabel(Text.literal("undefined for type"))

        val enclosing = this

        element.accept(object : NbtElementVisitor {
            override fun visitString(element: NbtString) {
                widget = WButton(
                    Text.translatable("text.nbtviewer.element.string", index, element.asString())
                )
            }

            override fun visitByte(element: NbtByte) {
                widget = WButton(
                    Text.translatable("text.nbtviewer.element.int", index, element.intValue())
                )
            }

            override fun visitShort(element: NbtShort) {
                widget = WButton(
                    Text.translatable("text.nbtviewer.element.int", index, element.intValue())
                )
            }

            override fun visitInt(element: NbtInt) {
                widget = WButton(
                    Text.translatable("text.nbtviewer.element.int", index, element.intValue())
                )
            }

            override fun visitLong(element: NbtLong) {
                widget = WButton(
                    Text.translatable("text.nbtviewer.element.int", index, element.longValue())
                )
            }

            override fun visitFloat(element: NbtFloat) {
                widget = WButton(
                    Text.translatable("text.nbtviewer.element.float", index, element.doubleValue())
                )
            }

            override fun visitDouble(element: NbtDouble) {
                widget = WButton(
                    Text.translatable("text.nbtviewer.element.float", index, element.doubleValue())
                )
            }

            override fun visitByteArray(element: NbtByteArray) {
                widget = getWidgetForElement(index, element)
            }

            override fun visitIntArray(element: NbtIntArray) {
                widget = getWidgetForElement(index, element)
            }

            override fun visitLongArray(element: NbtLongArray) {
                widget = getWidgetForElement(index, element)
            }

            override fun visitList(element: NbtList) {
                widget = getWidgetForElement(index, element)
            }

            override fun visitCompound(compound: NbtCompound) {
                val button = WButton(Text.translatable("text.nbtviewer.element.object", index))
                val name = getSubScreenName(index)
                button.setOnClick {
                    MinecraftClient
                        .getInstance()
                        .setScreen(NBTViewerScreen(NBTViewerGui(compound, enclosing, name)))
                }
                widget = button
            }

            override fun visitEnd(element: NbtEnd) {
            }

        })

        return widget
    }

    private fun getSubScreenName(sub: String): String {
        val name = if (name === null) sub else "$name/$sub"
        return if (name.length >= 40) {
            "...${name.substring(name.length - 37)}"
        } else {
            name
        }
    }
}