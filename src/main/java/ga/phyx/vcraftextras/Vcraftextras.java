package ga.phyx.vcraftextras;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import ga.phyx.vcraftextras.command.Command;
import ga.phyx.vcraftextras.command.impl.*;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class Vcraftextras implements ModInitializer {

    public static final String MOD_ID = "VCraftextras";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static HashMap<String, Long> playerActivities;

    private static final Command[] COMMANDS = {
            new PActivityCommand()
    };

    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        for (Command command : COMMANDS) {
            command.register(dispatcher);
        }
    }

    @Override
    public void onInitialize() {
        LOGGER.info("VCraftExtras Initialising, adding useful features to VCraft!");
        playerActivities = new HashMap<>();
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) ->  {
            if (environment.dedicated) {
                for (Command command : COMMANDS) {
                    command.register(dispatcher);
                }
            }
        }));
    }
}
