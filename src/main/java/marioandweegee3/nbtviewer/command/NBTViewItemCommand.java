package marioandweegee3.nbtviewer.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.netty.buffer.Unpooled;
import marioandweegee3.nbtviewer.NBTViewer;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.PacketByteBuf;

public class NBTViewItemCommand implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        PlayerEntity player = source.getPlayer();

        if(player == null){
            source.sendError(new TranslatableText("nbtviewer.command.nbtview.playertarget"));
            return -1;
        }

        ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);

        if(stack.isEmpty()){
            ItemStack offHand = player.getStackInHand(Hand.OFF_HAND);
            if(offHand.isEmpty()){
                source.sendError(new TranslatableText("nbtviewer.command.nbtview.stackempty"));
                return -1;
            } else {
                stack = offHand;
            }
        }

        CompoundTag tag = stack.getOrCreateTag();

        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeCompoundTag(tag);

        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, NBTViewer.OPEN_NBT_VIEW_PACKET, buf);

        return 1;
    }
}