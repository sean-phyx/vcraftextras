package ga.phyx.vcraftextras.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import ga.phyx.vcraftextras.Vcraftextras;
import ga.phyx.vcraftextras.command.Command;
import ga.phyx.vcraftextras.util.MilConvert;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class PActivityListCommand implements Command {

    private final SimpleCommandExceptionType BAD_SENDER_EXCEPTION = new SimpleCommandExceptionType(Text.literal("Server cannot execute this command."));

    private int runPlayer(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        final ServerPlayerEntity sender = ctx.getSource().getPlayer();

        if (sender == null) {
            throw BAD_SENDER_EXCEPTION.create();
        }

        for (String player : Vcraftextras.playerActivities.keySet()) {
            String time = MilConvert.convertDifference(String.valueOf(Vcraftextras.playerActivities.get(player)));
            Text message = Text.literal(player + " has not played in " + time);
            sender.sendMessage(message);
        }
        return 1;
    }
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("pactivitylist")
                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
                    .executes(ctx -> {
                    ctx.getSource().sendFeedback(Text.literal("You are not an operator"), false);
                    return 1;
                    })
                .executes(this::runPlayer));

    }
}
