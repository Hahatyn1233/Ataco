package org.hahatyn.ataco.dialog;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.dialog.Dialog;
import net.md_5.bungee.api.dialog.DialogBase;
import net.md_5.bungee.api.dialog.NoticeDialog;
import net.md_5.bungee.api.dialog.action.ActionButton;
import net.md_5.bungee.api.dialog.action.CustomClickAction;
import net.md_5.bungee.api.dialog.input.DialogInput;
import net.md_5.bungee.api.dialog.input.TextInput;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DialogBuilder {

    private final List<BaseComponent> lines = new ArrayList<>();
    private final List<DialogInput> inputs = new ArrayList<>();
    private final List<ActionButton> buttons = new ArrayList<>();

    // Добавить строку текста (один или несколько BaseComponent)
    public DialogBuilder addLine(BaseComponent... components) {
        for (BaseComponent c : components) {
            lines.add(c);
        }
        return this;
    }

    // Добавить строку текста из строки с цветом и стилями
    public DialogBuilder addLine(String text, net.md_5.bungee.api.ChatColor color) {
        TextComponent tc = new TextComponent(text);
        tc.setColor(color);
        lines.add(tc);
        return this;
    }

    // Добавить поле ввода
    public DialogBuilder addInput(String id, String label) {
        BaseComponent[] labelComp = new ComponentBuilder(label).create();
        inputs.add(new TextInput(id, labelComp.length > 0 ? labelComp[0] : new TextComponent(label)));
        return this;
    }

    // Добавить кнопку с текстом и идентификатором действия
    public DialogBuilder addButton(String text, String actionId) {
        BaseComponent[] btnComp = new ComponentBuilder(text).create();
        buttons.add(new ActionButton(btnComp.length > 0 ? btnComp[0] : new TextComponent(text), new CustomClickAction(actionId)));
        return this;
    }

    // Создать Dialog
    public Dialog build() {
        TextComponent combined = new TextComponent();
        for (BaseComponent line : lines) {
            combined.addExtra(line);
            combined.addExtra("\n");
        }

        DialogBase base = new DialogBase(combined);

        if (!inputs.isEmpty()) {
            base.inputs(new ArrayList<>(inputs));
        }

        NoticeDialog dialog = new NoticeDialog(base);

        if (!buttons.isEmpty()) {
            for (ActionButton btn : buttons) {
                dialog.action(btn);
            }
        }

        return dialog;
    }

    // Показать диалог игроку
    public void show(Player player) {
        player.showDialog(build());
    }

}