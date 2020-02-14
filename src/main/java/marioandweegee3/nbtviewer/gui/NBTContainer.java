package marioandweegee3.nbtviewer.gui;

import java.util.ArrayList;
import java.util.List;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WListPanel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class NBTContainer extends LightweightGuiDescription {
    public NBTContainer(CompoundTag tag) {
        WGridPanel root = new WGridPanel();
        setRootPanel(root);

        WLabel title = new WLabel(new TranslatableText("text.nbtviewer.title"), WLabel.DEFAULT_TEXT_COLOR);
        root.add(title, 0, 0, 3, 1);

        WListPanel<String, WLabel> list = new WListPanel<>(getLines(tag, ""), () -> new WLabel(""), (str, label) -> label.setText(new LiteralText(str)));
        root.add(list, 0, 1, 15, 9);
    }

    private List<String> getLines(CompoundTag compound, String indent){
        List<String> lines = new ArrayList<>();

        if(compound.isEmpty()){
            lines.add(indent);
            return lines;
        }

        for(String key : compound.getKeys()){
            Tag tag = compound.get(key);
            
            if(tag instanceof CompoundTag){
                lines.add(indent + key + ": {");
                lines.addAll(getLines((CompoundTag) tag, indent + "    "));
                lines.add(indent + "}");
            } else if(tag instanceof ListTag){
                lines.add(indent + key+": [");
                lines.addAll(getLinesFromList((ListTag) tag, indent + "    "));
                lines.add(indent + "]");
            } else {
                lines.add(indent + key + ": " + tag.asString());
            }
            
        }

        return lines;
    }
    private List<String> getLinesFromList(ListTag list, String indent){
        List<String> lines = new ArrayList<>();

        for(Tag tag : list){
            if(tag instanceof CompoundTag){
                lines.add(indent + "{");
                lines.addAll(getLines((CompoundTag) tag, indent + "    "));
                lines.add(indent + "}");
            } else if(tag instanceof ListTag){
                lines.add(indent + "[");
                lines.addAll(getLinesFromList((ListTag)tag, indent + "    "));
                lines.add(indent + "]");
            } else {
                lines.add(indent + tag.asString());
            }
        }

        return lines;
    }
}