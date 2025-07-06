package dev.iidanto.kitPreview.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class ColorUtils {
    public static Component parse(String input){
        return MiniMessage.miniMessage().deserialize(input).decoration(TextDecoration.ITALIC, false);
    }

    public static Component empty(){
        return Component.empty();
    }
}