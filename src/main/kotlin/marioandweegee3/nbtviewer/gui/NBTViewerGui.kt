package marioandweegee3.nbtviewer.gui

import io.ejekta.kambrik.ext.iterator
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription
import io.github.cottonmc.cotton.gui.widget.*
import io.github.cottonmc.cotton.gui.widget.data.Insets
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.nbt.*
import net.minecraft.nbt.visitor.NbtElementVisitor
import net.minecraft.text.TranslatableText

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
                            .iterator()
                            .asSequence()
                            .sortedWith { a, b -> String.CASE_INSENSITIVE_ORDER.compare(a.first, b.first) }

                        for ((k, v) in seq) {
                            add(getWidgetForObject(k, v))
                        }
                    }
                }
                is AbstractNbtList<*> -> {
                    buildList {
                        for ((index, value) in nbt.withIndex()) {
                            add(getWidgetForList(index, value))
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
                WButton(TranslatableText("text.nbtviewer.back"))
                    .setOnClick {
                        MinecraftClient
                            .getInstance()
                            .setScreen(NBTViewerScreen(enclosing))
                    }
            } else {
                WButton(TranslatableText("text.nbtviewer.close"))
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

    private fun getWidgetForList(index: Int, element: AbstractNbtList<*>): WWidget {
        val button = WButton(TranslatableText("text.nbtviewer.element.list", index))
        val name = getSubScreenName(index.toString())
        button.setOnClick {
            MinecraftClient
                .getInstance()
                .setScreen(NBTViewerScreen(NBTViewerGui(element, this, name)))
        }
        return button
    }

    private fun getWidgetForList(index: Int, element: NbtElement): WWidget {
        var widget: WWidget = WLabel("undefined for type")

        val enclosing = this

        element.accept(object : NbtElementVisitor {
            override fun visitString(element: NbtString) {
                widget = WLabel(
                    TranslatableText("text.nbtviewer.element.string", index, element.asString())
                )
            }

            override fun visitByte(element: NbtByte) {
                widget = WLabel(
                    TranslatableText("text.nbtviewer.element.int", index, element.intValue())
                )
            }

            override fun visitShort(element: NbtShort) {
                widget = WLabel(
                    TranslatableText("text.nbtviewer.element.int", index, element.intValue())
                )
            }

            override fun visitInt(element: NbtInt) {
                widget = WLabel(
                    TranslatableText("text.nbtviewer.element.int", index, element.intValue())
                )
            }

            override fun visitLong(element: NbtLong) {
                widget = WLabel(
                    TranslatableText("text.nbtviewer.element.int", index, element.longValue())
                )
            }

            override fun visitFloat(element: NbtFloat) {
                widget = WLabel(
                    TranslatableText("text.nbtviewer.element.float", index, element.doubleValue())
                )
            }

            override fun visitDouble(element: NbtDouble) {
                widget = WLabel(
                    TranslatableText("text.nbtviewer.element.float", index, element.doubleValue())
                )
            }

            override fun visitByteArray(element: NbtByteArray) {
                widget = getWidgetForList(index, element)
            }

            override fun visitIntArray(element: NbtIntArray) {
                widget = getWidgetForList(index, element)
            }

            override fun visitLongArray(element: NbtLongArray) {
                widget = getWidgetForList(index, element)
            }

            override fun visitList(element: NbtList) {
                widget = getWidgetForList(index, element)
            }

            override fun visitCompound(compound: NbtCompound) {
                val button = WButton(TranslatableText("text.nbtviewer.element.object", index))
                val name = getSubScreenName(index.toString())
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

    private fun getWidgetForObject(key: String, element: AbstractNbtList<*>): WWidget {
        val button = WButton(TranslatableText("text.nbtviewer.element.list", key))
        val name = getSubScreenName(key)
        button.setOnClick {
            MinecraftClient
                .getInstance()
                .setScreen(NBTViewerScreen(NBTViewerGui(element, this, name)))
        }
        return button
    }

    private fun getWidgetForObject(key: String, element: NbtElement): WWidget {
        var widget: WWidget = WLabel("undefined for type")

        val enclosing = this

        element.accept(object : NbtElementVisitor {
            override fun visitString(element: NbtString) {
                widget = WLabel(
                    TranslatableText("text.nbtviewer.element.string", key, element.asString())
                )
            }

            override fun visitByte(element: NbtByte) {
                widget = WLabel(
                    TranslatableText("text.nbtviewer.element.byte", key, element.intValue())
                )
            }

            override fun visitShort(element: NbtShort) {
                widget = WLabel(
                    TranslatableText("text.nbtviewer.element.short", key, element.intValue())
                )
            }

            override fun visitInt(element: NbtInt) {
                widget = WLabel(
                    TranslatableText("text.nbtviewer.element.int", key, element.intValue())
                )
            }

            override fun visitLong(element: NbtLong) {
                widget = WLabel(
                    TranslatableText("text.nbtviewer.element.long", key, element.longValue())
                )
            }

            override fun visitFloat(element: NbtFloat) {
                widget = WLabel(
                    TranslatableText("text.nbtviewer.element.float", key, element.doubleValue())
                )
            }

            override fun visitDouble(element: NbtDouble) {
                widget = WLabel(
                    TranslatableText("text.nbtviewer.element.double", key, element.doubleValue())
                )
            }

            override fun visitByteArray(element: NbtByteArray) {
                widget = getWidgetForObject(key, element)
            }

            override fun visitIntArray(element: NbtIntArray) {
                widget = getWidgetForObject(key, element)
            }

            override fun visitLongArray(element: NbtLongArray) {
                widget = getWidgetForObject(key, element)
            }

            override fun visitList(element: NbtList) {
                widget = getWidgetForObject(key, element)
            }

            override fun visitCompound(compound: NbtCompound) {
                val button = WButton(TranslatableText("text.nbtviewer.element.object", key))
                val name = getSubScreenName(key)
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