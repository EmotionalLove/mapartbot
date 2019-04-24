package me.zeroeightsix.discord;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.List;

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
            lastEvent = e;
            MapArtBot.commandProcessor.processCommand(message);
            return;
        }

        List<Message.Attachment> attachments = e.getMessage().getAttachments();
        if (attachments.isEmpty()) return;
        MapArtBot.processFiles(e, attachments);
    }

}
