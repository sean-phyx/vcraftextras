package ga.phyx.vcraftextras;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import ga.phyx.vcraftextras.command.Command;
import ga.phyx.vcraftextras.command.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class Vcraftextras implements ModInitializer {

    public static final String MOD_ID = "VCraftextras";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static HashMap<String, Long> playerActivities;

    private static final Command[] COMMANDS = {
            new PActivityCommand(),
            new PActivityListCommand()
    };

    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        for (Command command : COMMANDS) {
            command.register(dispatcher);
        }
    }

    @Override
    public void onInitialize() {
        LOGGER.info("VCraftExtras Initialising, adding useful features to VCraft!");
        Gson gson = new GsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.STRING).setPrettyPrinting().create();
        playerActivities = new HashMap<>();
        try {
            Path path = Files.createTempFile("playeractivity", ".json");
            if (Files.exists(path)) {
                Reader reader = Files.newBufferedReader(Paths.get("playeractivity.json"));
                playerActivities = gson.fromJson(reader, HashMap.class);
                reader.close();
            }
        } catch (IOException e) {
            LOGGER.error("There was an error \n" + e);
        }
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) ->  {
            if (environment.dedicated) {
                for (Command command : COMMANDS) {
                    command.register(dispatcher);
                }
            }
        }));
    }
}
