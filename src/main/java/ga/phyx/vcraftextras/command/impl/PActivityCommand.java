package ga.phyx.vcraftextras.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import ga.phyx.vcraftextras.Vcraftextras;
import ga.phyx.vcraftextras.util.MilConvert;
import net.minecraft.command.CommandSource;
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
        final ServerPlayerEntity sender = ctx.getSource().getPlayer();

        if (sender == null) {
            throw BAD_SENDER_EXCEPTION.create();
        }

        String targetName = ctx.getArgument("player", String.class);

        if (!Vcraftextras.playerActivities.containsKey(targetName)) {
            throw NO_TARGET_EXCEPTION.create();
        }
        String lastTime = String.valueOf(Vcraftextras.playerActivities.get(targetName));
        String time = MilConvert.convertDifference(lastTime);
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
                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
                        .executes(ctx -> {
                            ctx.getSource().sendFeedback(Text.literal("You are not an operator"), false);
                            return 1;
                        })
                .then(argument("player", string())
                    .suggests(this::suggestActivePlayers)
                    .executes(this::runPlayer)));
    }
}
