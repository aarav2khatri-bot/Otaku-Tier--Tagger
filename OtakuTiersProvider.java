package com.otaku.tiertagger.provider;

import com.otaku.tiertagger.data.PlayerTierData;
import com.otaku.tiertagger.provider.TierProvider;
import com.otaku.tiertagger.util.HttpUtil;
import com.google.gson.JsonObject;
import java.util.concurrent.CompletableFuture;

public class OtakuTiersProvider extends TierProvider {

    // ←←← CHANGE THIS LINE TO YOUR BOT'S LINK
    private static final String API_URL = "https://your-bot-link.com/tiers.json";

    @Override
    public CompletableFuture<PlayerTierData> getPlayerData(String username) {
        return HttpUtil.getJson(API_URL).thenApply(json -> {
            PlayerTierData data = new PlayerTierData(username);

            if (json.has("players")) {
                JsonObject players = json.getAsJsonObject("players");
                if (players.has(username)) {
                    JsonObject p = players.getAsJsonObject(username);
                    
                    String tier = p.get("tier").getAsString();
                    int points = p.has("points") ? p.get("points").getAsInt() : 0;
                    String mode = p.has("mode") ? p.get("mode").getAsString() : "Otaku";
                    String region = p.has("region") ? p.get("region").getAsString() : "";

                    data.addTier(mode, tier, points);
                    data.setRegion(region);
                }
            }
            return data;
        });
    }

    @Override
    public String getName() {
        return "Otaku Tiers";
    }
}
