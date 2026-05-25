package net.uku3lig.tiertagger.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.uku3lig.tiertagger.TierCache;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public record PlayerInfo(String uuid, String name, Map<String, Ranking> rankings, String region, int points,
                         int overall, List<Badge> badges, @SerializedName("combat_master") boolean combatMaster) {

    public record Ranking(int tier, int pos, @Nullable @SerializedName("peak_tier") Integer peakTier,
                          @Nullable @SerializedName("peak_pos") Integer peakPos, long attained,
                          boolean retired) {

        public int comparableTier() {
            return tier * 2 + pos;
        }

        public int comparablePeak() {
            if (peakTier == null || peakPos == null) {
                return Integer.MAX_VALUE;
            } else {
                return peakTier * 2 + peakPos;
            }
        }

        public NamedRanking asNamed(GameMode mode) {
            return new NamedRanking(mode, this);
        }
    }

    public record NamedRanking(@Nullable GameMode mode, Ranking ranking) {
    }

    public record Badge(String title, String desc) {
    }

    // Simplified for your Supabase table
    public static CompletableFuture<PlayerInfo> search(HttpClient client, String query) {
        // This is basic adaptation - may need more changes later
        return CompletableFuture.completedFuture(new PlayerInfo(
            "", query, Map.of(), "", 0, 0, List.of(), false
        ));
    }

    public static CompletableFuture<Map<String, Ranking>> getRankings(HttpClient client, UUID uuid) {
        return CompletableFuture.completedFuture(Map.of());
    }

    public int getRegionColor() {
        return 0xffffff; // default white
    }

    public static Optional<NamedRanking> getHighestRanking(Map<String, Ranking> rankings) {
        return rankings.entrySet().stream()
                .min(Comparator.comparingInt(e -> e.getValue().comparableTier()))
                .map(e -> e.getValue().asNamed(TierCache.findModeOrUgly(e.getKey())));
    }
}
