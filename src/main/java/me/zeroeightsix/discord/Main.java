package me.zeroeightsix.discord;

import me.zeroeightsix.discord.command.*;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;

import javax.security.auth.login.LoginException;
import java.io.File;

/**
 * Created by 086 on 15/01/2018.
 */
public class Main {

    public static JDA jda;

    public static void main(String[] args) throws LoginException, InterruptedException, InstantiationException, IllegalAccessException {
        if (args.length == 0) {
            System.err.println("Discord token required as argument");
            return;
        }
        MapArtBot.commandProcessor.register(FagCommand.class);
        MapArtBot.commandProcessor.register(HelpCommand.class);
        MapArtBot.commandProcessor.register(MapCommand.class);
        MapArtBot.commandProcessor.register(OpenCommand.class);
        MapArtBot.commandProcessor.register(RemoveCommand.class);
        MapArtBot.commandProcessor.register(ResetCommand.class);
        JDABuilder builder = new JDABuilder(AccountType.BOT);
        jda = builder.setAutoReconnect(true).setToken(args[0]).buildBlocking();
        File tmp = new File("tmp");
        if (!tmp.isDirectory() || !tmp.exists()) tmp.mkdirs();
        jda.setEventManager(new AnnotatedEventManager());
        jda.addEventListener(new DiscordListener());
        MapArtBot.start();
    }

}
