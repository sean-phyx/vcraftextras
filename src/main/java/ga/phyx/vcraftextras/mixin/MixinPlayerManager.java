package ga.phyx.vcraftextras.mixin;

import ga.phyx.vcraftextras.Vcraftextras;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.include.com.google.gson.Gson;
import org.spongepowered.include.com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.Writer;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {

    @Inject(method = "onPlayerConnect", at = @At("HEAD"))
    private void logConnection(ClientConnection con, ServerPlayerEntity player, CallbackInfo ci) {
        try {
            String filePath = "playeractivity.json";
            Writer writer = new FileWriter(filePath);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Long currentTime = System.currentTimeMillis();
            String name = player.getName().getString();
            Vcraftextras.playerActivities.put(name, currentTime);
            gson.toJson(Vcraftextras.playerActivities, writer);
            writer.close();
            Vcraftextras.LOGGER.info("Logged " + name + "'s login");
        } catch (Exception e) {
            System.out.println("An error occurred\n" + e);
        }
    }
}
