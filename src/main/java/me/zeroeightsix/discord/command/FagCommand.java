package me.zeroeightsix.discord.command;

import com.sasha.simplecmdsys.SimpleCommand;
import me.zeroeightsix.discord.DiscordListener;
import me.zeroeightsix.discord.MapArtBot;

/**
 * https://i.imgur.com/vCmrcPx.png
 */
public class FagCommand extends SimpleCommand {

    public FagCommand() {
        super("fag");
    }

    @Override
    public void onCommand() {
        MapArtBot.generate(DiscordListener.lastEvent.getChannel(), DiscordListener.lastEvent.getAuthor().getName() + " is a fag.", false).submit();
    }
}
