package me.zeroeightsix.discord.command;

import com.sasha.simplecmdsys.SimpleCommand;
import me.zeroeightsix.discord.DiscordListener;
import me.zeroeightsix.discord.MapArtBot;
import me.zeroeightsix.discord.groups.ReplaceGroup;

import static me.zeroeightsix.discord.MapArtBot.replaceMap;

public class HelpCommand extends SimpleCommand {

    public HelpCommand() {
        super("open");
    }

    @Override
    public void onCommand() {
        MapArtBot.generate(DiscordListener.lastEvent.getChannel(), "This bot converts schematics for easy mapart-ready schematics without the need of mcedit, worldedit..\n" +
                "What blocks it replaces with which, is completely up to you. Use the commands below to update the replace map.\n" +
                "To convert a file, just drop it in this chat.\n\n" +
                "!reset ~ Return the replace map to defaults\n" +
                "!map ~ View the entries (groups) in the current replace map\n" +
                "!open <groupid> ~ View the mappings of a group\n" +
                "!remove <groupid> ~ Removes a group from the map\n" +
                "!help ~ displays this message\n\n" +
                "!add <group> ~ Add a new replacegroup. Requires specific formatting, f.e: (**Must be one message!**)" +
                "\n\t!add" +
                "\n\t1:1 -> 1:3" +
                "\n\t5 -> 6" +
                "\n\t0 -> 3:1", false).submit();
    }
}
