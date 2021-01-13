package dev.lazurite.dropz.mixin.client;

import dev.lazurite.dropz.client.ClientInitializer;
import dev.lazurite.dropz.util.VersionChecker;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin executes whenever the player joins the game. In this case,
 * it runs the version checker and sends a message to the player if there
 * is an update for the mod.
 * @see VersionChecker
 */
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onGameJoin", at = @At("TAIL"))
    public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo info) {
        ClientInitializer.getVersionChecker().sendPlayerMessage();
    }
}
