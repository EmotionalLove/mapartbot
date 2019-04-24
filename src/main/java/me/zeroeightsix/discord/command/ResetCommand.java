package me.zeroeightsix.discord.command;

import com.sasha.simplecmdsys.SimpleCommand;
import me.zeroeightsix.discord.DiscordListener;
import me.zeroeightsix.discord.MapArtBot;

public class ResetCommand extends SimpleCommand {

    public ResetCommand() {
        super("reset");
    }

    @Override
    public void onCommand() {
        MapArtBot.reset();
        DiscordListener.lastEvent.getChannel().sendMessage(MapArtBot.generate("Replace map reset.", false)).submit();
    }
}
