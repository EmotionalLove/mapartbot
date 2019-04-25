package me.zeroeightsix.discord;

import me.zeroeightsix.discord.groups.ReplaceGroup;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.HashMap;
import java.util.List;

import static me.zeroeightsix.discord.MapArtBot.replaceMap;
import static me.zeroeightsix.discord.MapArtBot.toArray;

public class DiscordListener {

    public static GuildMessageReceivedEvent lastEvent;

    @SubscribeEvent
    public void onMsgRx(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (MapArtBot.responseMap.containsKey(e.getMember())) {
            MapArtBot.responseMap.remove(e.getMember()).accept(e);
        }
        String message = e.getMessage().getContentDisplay();
        if (message.startsWith("!")) {
            if (message.startsWith("!add")) {
                processAdd(message, e.getMember());
                return;
            }
            lastEvent = e;
            MapArtBot.commandProcessor.processCommand(message);
            return;
        }

        List<Message.Attachment> attachments = e.getMessage().getAttachments();
        if (attachments.isEmpty()) return;
        MapArtBot.processFiles(e, attachments);
    }

    private void processAdd(String content, Member member) {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        String[] parts = content.split("\n");
        for (int i = 1; i < parts.length; i++) {
            String s = parts[i];
            s = s.trim();
            String[] parts1 = s.split("->");
            if (parts1.length != 2) {
                MapArtBot.generate(DiscordListener.lastEvent.getChannel(), "Must have a single \"->\"", true).submit();
                break;
            }
            for (String a : parts1) {
                a = a.replace(" ", "");
                int c = a.length() - a.replace(":", "").length();
                if (c > 1) {
                    MapArtBot.generate(DiscordListener.lastEvent.getChannel(), "id can only be composed of one data value.", true).submit();
                    break;
                }
                if (!(a.replaceAll("\\d|:", "")).isEmpty()) {
                    MapArtBot.generate(DiscordListener.lastEvent.getChannel(), "empty group. (did you forget to specify the mappings?)", true).submit();
                    break;
                }
            }
            stringStringHashMap.put(parts1[0], parts1[1]);
        }
        if (stringStringHashMap.isEmpty()) {
            MapArtBot.generate(DiscordListener.lastEvent.getChannel(), "empty group", true).submit();
            return;
        }
        final String groupName = member.getUser().getName();
        final int[] a = new int[]{0};
        while (replaceMap.stream().anyMatch(replaceGroup -> replaceGroup.shortName.equalsIgnoreCase(groupName + a[0])))
            a[0]++;
        ReplaceGroup group1 = new ReplaceGroup("Custom group by " + groupName,
                toArray(stringStringHashMap),
                groupName + a[0]);
        replaceMap.add(group1);
        MapArtBot.generate(DiscordListener.lastEvent.getChannel(), "done", false).submit();
    }

}
