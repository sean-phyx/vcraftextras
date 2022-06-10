package ga.phyx.vcraftextras.command.impl;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import ga.phyx.vcraftextras.Vcraftextras;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import ga.phyx.vcraftextras.command.Command;

import java.util.concurrent.CompletableFuture;

import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PActivityCommand implements Command {
    private final SimpleCommandExceptionType BAD_SENDER_EXCEPTION = new SimpleCommandExceptionType(Text.literal("Server cannot execute this command."));
    private final SimpleCommandExceptionType NO_TARGET_EXCEPTION = new SimpleCommandExceptionType(Text.literal("No such player."));

    private int runPlayer(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        MinecraftServer server = ctx.getSource().getServer();

        final ServerPlayerEntity sender = ctx.getSource().getPlayer();

        if (sender == null) {
            throw BAD_SENDER_EXCEPTION.create();
        }

        String targetName = ctx.getArgument("player", String.class);

        if (!Vcraftextras.playerActivities.containsKey(targetName)) {
            throw NO_TARGET_EXCEPTION.create();
        }
        String lastTime = String.valueOf(Vcraftextras.playerActivities.get(targetName));
        long seconds = (System.currentTimeMillis() - Long.valueOf(lastTime))/1000;
        long minutes = seconds/60;
        long hours = minutes/60;
        long days = hours/24;
        String time = days + " days " + hours % 24 + " hours " + minutes % 60 + " minutes " + seconds % 60 + " seconds";
        Text message = Text.literal(targetName + " has not played in " + time);
        sender.sendMessage(message);
        return 1;
    }

    private CompletableFuture<Suggestions> suggestActivePlayers(final CommandContext<ServerCommandSource> ctx, final SuggestionsBuilder builder) {
        PlayerManager playerManager = ctx.getSource().getServer().getPlayerManager();
        return CommandSource.suggestMatching(playerManager.getPlayerNames(), builder);
    }

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("pactivity")
                .then(argument("player", string())
                .suggests(this::suggestActivePlayers)
                .executes(this::runPlayer)));
    }
}
