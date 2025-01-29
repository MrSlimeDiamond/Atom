package net.slimediamond.atom.telegram;

import com.google.inject.Inject;
import net.slimediamond.atom.command.CommandBuilder;
import net.slimediamond.atom.command.CommandManager;
import net.slimediamond.atom.command.telegram.TelegramMessageListener;
import net.slimediamond.atom.common.annotations.Service;
import net.slimediamond.atom.reference.TelegramReference;
import net.slimediamond.atom.telegram.commands.BansCommand;
import net.slimediamond.telegram.TelegramClient;

@Service("telegram")
public class Telegram {
    @Inject
    CommandManager commandManager;

    @Service.Start
    public void onStart() {
        TelegramClient client = new TelegramClient(TelegramReference.token);
        client.addListener(new TelegramMessageListener(commandManager));

        commandManager.register(new CommandBuilder()
                .addAliases("ping")
                .setDescription("replies with pong")
                .setUsage("ping")
                .telegram()
                .setExecutor(ctx -> ctx.reply("Pong!"))
                .then().build()
        );

        // MCO COMMANDS
        commandManager.register(new CommandBuilder()
                .addAliases("bans", "bancount")
                .setDescription("Get ban count for MinecraftOnline")
                .setUsage("bans")
                .telegram().setExecutor(new BansCommand())
                .then().build()
        );
    }
}
