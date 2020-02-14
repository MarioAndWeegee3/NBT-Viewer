package marioandweegee3.nbtviewer;

import com.mojang.brigadier.tree.LiteralCommandNode;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import marioandweegee3.nbtviewer.command.NBTViewItemCommand;

public class NBTViewer implements ModInitializer {
    public static final Identifier OPEN_NBT_VIEW_PACKET = new Identifier("nbtviewer:open_view_gui");

    @Override
    public void onInitialize() {
        CommandRegistry.INSTANCE.register(false, dispatcher -> {
            LiteralCommandNode<ServerCommandSource> nbtViewNode = CommandManager
                .literal("nbtview")
                .build();
            
            LiteralCommandNode<ServerCommandSource> itemNode = CommandManager
                .literal("item")
                .executes(new NBTViewItemCommand())
                .build();

            dispatcher.getRoot().addChild(nbtViewNode);
            nbtViewNode.addChild(itemNode);
        });

        
    }
}