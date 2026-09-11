package net.prismalmidu.arcaneempowerment.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.HashSet;
import java.util.Set;

public class PlayerPerks {
    private final Set<String> perks = new HashSet<>();

    private int perkPoints = 0;
    private int totalPointsClaimed = 0;

    // Tracks consumption points for 4 individual tiers/food types (max 16 each)
    private int foodPointsTier1 = 0;
    private int foodPointsTier2 = 0;
    private int foodPointsTier3 = 0;
    private int foodPointsTier4 = 0;

    public void unlockPerk(String perkId) {
        perks.add(perkId);
    }

    public boolean hasPerk(String perkId) {
        return perks.contains(perkId);
    }

    public Set<String> getPerks() {
        return perks;
    }

    public int getPerkPoints() { return this.perkPoints; }
    public void addPerkPoints(int amount) { this.perkPoints += amount; }
    public void consumePerkPoints(int amount) { this.perkPoints -= amount; }

    public int getTotalPointsClaimed() { return this.totalPointsClaimed; }

    /**
     * Tries to add points from a specific food tier. Enforces the 16 point cap per tier.
     * @return The actual number of points successfully added (0 if capped).
     */
    public int tryAddFoodPoints(int tier, int amount) {
        if (this.totalPointsClaimed >= 64) return 0;

        int spaceLeft;
        switch (tier) {
            case 1 -> {
                spaceLeft = 16 - this.foodPointsTier1;
                int added = Math.min(amount, spaceLeft);
                this.foodPointsTier1 += added;
                this.totalPointsClaimed += added;
                this.perkPoints += added;
                return added;
            }
            case 2 -> {
                spaceLeft = 16 - this.foodPointsTier2;
                int added = Math.min(amount, spaceLeft);
                this.foodPointsTier2 += added;
                this.totalPointsClaimed += added;
                this.perkPoints += added;
                return added;
            }
            case 3 -> {
                spaceLeft = 16 - this.foodPointsTier3;
                int added = Math.min(amount, spaceLeft);
                this.foodPointsTier3 += added;
                this.totalPointsClaimed += added;
                this.perkPoints += added;
                return added;
            }
            case 4 -> {
                spaceLeft = 16 - this.foodPointsTier4;
                int added = Math.min(amount, spaceLeft);
                this.foodPointsTier4 += added;
                this.totalPointsClaimed += added;
                this.perkPoints += added;
                return added;
            }
            default -> { return 0; }
        }
    }

    public void copyFrom(PlayerPerks source) {
        this.perks.clear();
        this.perks.addAll(source.perks);
        this.totalPointsClaimed = source.totalPointsClaimed;
        this.foodPointsTier1 = source.foodPointsTier1;
        this.foodPointsTier2 = source.foodPointsTier2;
        this.foodPointsTier3 = source.foodPointsTier3;
        this.foodPointsTier4 = source.foodPointsTier4;
    }

    // Save capability data to player's NBT file
    public void saveNBT(CompoundTag nbt) {
        ListTag list = new ListTag();
        for (String perk : perks) {
            list.add(StringTag.valueOf(perk));
        }
        nbt.put("UnlockedPerks", list);
        nbt.putInt("PerkPoints", this.perkPoints);
        nbt.putInt("TotalPointsClaimed", this.totalPointsClaimed);
        nbt.putInt("FoodTier1", this.foodPointsTier1);
        nbt.putInt("FoodTier2", this.foodPointsTier2);
        nbt.putInt("FoodTier3", this.foodPointsTier3);
        nbt.putInt("FoodTier4", this.foodPointsTier4);
    }

    // Load capability data from player's NBT file
    public void loadNBT(CompoundTag nbt) {
        perks.clear();
        if (nbt.contains("UnlockedPerks", Tag.TAG_LIST)) {
            ListTag list = nbt.getList("UnlockedPerks", Tag.TAG_STRING);
            for (int i = 0; i < list.size(); i++) {
                perks.add(list.getString(i));
            }
        }
        this.perkPoints = nbt.getInt("PerkPoints");
        this.totalPointsClaimed = nbt.getInt("TotalPointsClaimed");
        this.foodPointsTier1 = nbt.getInt("FoodTier1");
        this.foodPointsTier2 = nbt.getInt("FoodTier2");
        this.foodPointsTier3 = nbt.getInt("FoodTier3");
        this.foodPointsTier4 = nbt.getInt("FoodTier4");
    }

    // Add this method inside your PlayerPerks class
    public void respec() {
        this.perks.clear();
        this.perkPoints = this.totalPointsClaimed; // Reclaim all spent points
    }
}