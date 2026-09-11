package net.prismalmidu.arcaneempowerment.item.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.prismalmidu.arcaneempowerment.capability.PlayerPerksProvider;
import net.prismalmidu.arcaneempowerment.networking.ClientboundSyncPerksPacket;
import net.prismalmidu.arcaneempowerment.networking.ModMessages;

public class PerkEssenceFoodItem extends Item {
    private final int tier;
    private final int pointsGranted;

    public PerkEssenceFoodItem(Properties pProperties, int tier, int pointsGranted) {
        // Automatically injects standard food properties so players can eat it even if full
        super(pProperties.food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1f).alwaysEat().build()));
        this.tier = tier;
        this.pointsGranted = pointsGranted;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        if (!pLevel.isClientSide() && pLivingEntity instanceof ServerPlayer player) {
            player.getCapability(PlayerPerksProvider.PLAYER_PERKS).ifPresent(perks -> {

                int added = perks.tryAddFoodPoints(this.tier, this.pointsGranted);

                if (added > 0) {
                    player.displayClientMessage(Component.literal("§aConsumed! Added " + added + " Perk Points. Total spendable: " + perks.getPerkPoints()), true);

                    // Force synchronize capability variables down to client interface
                    CompoundTag tag = new CompoundTag();
                    perks.saveNBT(tag);
                    ModMessages.sendToPlayer(new ClientboundSyncPerksPacket(tag), player);
                } else {
                    player.displayClientMessage(Component.literal("§cYou have already reached the 16-point maximum limit for this food tier!"), true);
                }
            });
        }
        return super.finishUsingItem(pStack, pLevel, pLivingEntity);
    }
}