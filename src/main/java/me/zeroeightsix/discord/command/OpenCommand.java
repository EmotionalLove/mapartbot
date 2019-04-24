package me.zeroeightsix.discord.command;

import com.sasha.simplecmdsys.SimpleCommand;
import me.zeroeightsix.discord.DiscordListener;
import me.zeroeightsix.discord.MapArtBot;
import me.zeroeightsix.discord.groups.ReplaceGroup;

import static me.zeroeightsix.discord.MapArtBot.replaceMap;

public class OpenCommand extends SimpleCommand {

    public OpenCommand() {
        super("open");
    }

    @Override
    public void onCommand() {
        if (this.getArguments() == null || this.getArguments().length != 1) {
            MapArtBot.generate(DiscordListener.lastEvent.getChannel(), "Please specify a Replace Map to view. (ex !open woolcarpet)", true).submit();
            return;
        }
        ReplaceGroup group = replaceMap.stream().filter(replaceGroup -> replaceGroup.shortName.equalsIgnoreCase(this.getArguments()[0])).findFirst().orElse(null);
        if (group == null) {
            MapArtBot.generate(DiscordListener.lastEvent.getChannel(), "Unknown group id", true).submit();
            return;
        }
        StringBuilder s = new StringBuilder();
        for (String[] strings : group.map) s.append(strings[0]).append(" -> ").append(strings[1]).append("\n");
        MapArtBot.generate(DiscordListener.lastEvent.getChannel(), s.toString(), false).submit();
    }
}
