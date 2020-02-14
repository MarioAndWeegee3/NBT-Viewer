package marioandweegee3.nbtviewer;

import marioandweegee3.nbtviewer.gui.NBTContainer;
import marioandweegee3.nbtviewer.gui.client.ClientNBTScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.CompoundTag;

public class NBTViewerClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientSidePacketRegistry.INSTANCE.register(NBTViewer.OPEN_NBT_VIEW_PACKET, 
        (context, data) -> {
            CompoundTag tag = data.readCompoundTag();
            context.getTaskQueue().execute(() -> {
                MinecraftClient.getInstance().openScreen(new ClientNBTScreen(new NBTContainer(tag)));
            });
        });
    }

}