package me.zeroeightsix.discord.command;

import com.sasha.simplecmdsys.SimpleCommand;
import me.zeroeightsix.discord.DiscordListener;
import me.zeroeightsix.discord.MapArtBot;
import me.zeroeightsix.discord.groups.ReplaceGroup;

import java.util.HashMap;

import static me.zeroeightsix.discord.MapArtBot.replaceMap;
import static me.zeroeightsix.discord.MapArtBot.toArray;

public class AddCommand extends SimpleCommand {

    public AddCommand() {
        super("add");
    }

    @Override
    public void onCommand() {
        if (this.getArguments() == null) {
            MapArtBot.generate(DiscordListener.lastEvent.getChannel(), "Please specify a Replace Map to add to. (ex !add woolcarpet)", true).submit();
            return;
        }
        HashMap<String, String> pairMap = new HashMap<>();
        String message = DiscordListener.lastEvent.getMessage().getContentRaw();
        String[] partz = message.split("\n");
        for (String s : partz) {
            if (!s.contains("->")) return;
            String[] parts1 = s.split("->");
            if (parts1.length != 2) {
                MapArtBot.generate(DiscordListener.lastEvent.getChannel(), "Must have a single \"->\"", true).submit();
                return;
            }
            for (String a : parts1) {
                a = a.replace(" ", "");
                int c = a.length() - a.replace(":", "").length();
                if (c > 1) {
                    MapArtBot.generate(DiscordListener.lastEvent.getChannel(), "id can only be composed of one data value.", true).submit();
                    return;
                }
                if (!(a.replaceAll("\\d|:", "")).isEmpty()) {
                    MapArtBot.generate(DiscordListener.lastEvent.getChannel(), "id must be a number.", true).submit();
                    return;
                }
            }
            pairMap.put(parts1[0], parts1[1]);
        }
        if (pairMap.isEmpty()) {
            MapArtBot.generate(DiscordListener.lastEvent.getChannel(), "empty group. (did you forget to specify the mappings?)", true).submit();
            return;
        }

        ReplaceGroup group = replaceMap.stream().filter(replaceGroup -> replaceGroup.shortName.equalsIgnoreCase(this.getArguments()[0])).findFirst().orElse(null);
        if (group == null) {
            MapArtBot.generate(DiscordListener.lastEvent.getChannel(), "Unknown group id", true).submit();
            return;
        }
        final String groupName = DiscordListener.lastEvent.getMember().getUser().getName();
        final int[] a = new int[]{0};
        while (replaceMap.stream().anyMatch(replaceGroup -> replaceGroup.shortName.equalsIgnoreCase(groupName + a[0])))
            a[0]++;
        ReplaceGroup group1 = new ReplaceGroup("Custom group by " + DiscordListener.lastEvent.getMember().getUser().getName(),
                toArray(pairMap),
                groupName + a[0]);
        replaceMap.add(group1);
        //////////////////////////////////////////////////////////////////////////////// dont care
        MapArtBot.generate(DiscordListener.lastEvent.getChannel(), "new group [] created".replace("[]", groupName), false).submit();
    }
}
