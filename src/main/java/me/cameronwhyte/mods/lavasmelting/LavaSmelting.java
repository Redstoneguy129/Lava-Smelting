package me.cameronwhyte.mods.lavasmelting;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class LavaSmelting implements ModInitializer {

    @Override
    public void onInitialize() {
        ServerTickCallback.EVENT.register(minecraftServer -> {
            List<ServerPlayerEntity> playerList = minecraftServer.getPlayerManager().getPlayerList();
            playerList.forEach(player -> {
                for(int i=0; i < player.inventory.size();) {
                    ItemStack item = player.inventory.getStack(i);
                    if(item.getOrCreateTag().contains("type")) {
                        item.removeSubTag("type");
                    }
                    i+=1;
                }
            });
        });
        System.out.println("Lava Smelting has been loaded!");
    }
}
