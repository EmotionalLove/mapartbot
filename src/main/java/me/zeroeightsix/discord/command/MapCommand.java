package me.zeroeightsix.discord.command;

import com.sasha.simplecmdsys.SimpleCommand;
import me.zeroeightsix.discord.DiscordListener;
import me.zeroeightsix.discord.MapArtBot;
import me.zeroeightsix.discord.groups.ReplaceGroup;

import static me.zeroeightsix.discord.MapArtBot.replaceMap;

public class MapCommand extends SimpleCommand {

    public MapCommand() {
        super("map");
    }

    @Override
    public void onCommand() {
        if (replaceMap.isEmpty()) {
            MapArtBot.generate(DiscordListener.lastEvent.getChannel(), "empty replace group", true).submit();
            return;
        }
        StringBuilder s = new StringBuilder();
        for (ReplaceGroup group : replaceMap) {
            s.append("**(").append(group.shortName).append(")** ").append(group.description).append("\n");
        }
        MapArtBot.generate(DiscordListener.lastEvent.getChannel(), s.toString(), false).submit();
    }
}
