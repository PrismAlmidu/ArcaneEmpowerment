package net.prismalmidu.arcaneempowerment.event;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.prismalmidu.arcaneempowerment.ArcaneEmpowerment;
import net.prismalmidu.arcaneempowerment.capability.ModPerkRegistry;
import net.prismalmidu.arcaneempowerment.capability.PerkModifier;
import net.prismalmidu.arcaneempowerment.capability.PlayerPerksProvider;

import java.util.Map;

@Mod.EventBusSubscriber(modid = ArcaneEmpowerment.MOD_ID)
public class ModAttributesEvents {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) return;

        Player player = event.player;

        player.getCapability(PlayerPerksProvider.PLAYER_PERKS).ifPresent(perks -> {

            // Loops through every entry from your data-driven spreadsheet map registry
            for (Map.Entry<String, PerkModifier> entry : ModPerkRegistry.REGISTRY.entrySet()) {
                String cellId = entry.getKey();
                PerkModifier modifier = entry.getValue();

                // Skip processing placeholders for custom traits (Mana, LifeSteal, etc.) for now
                if (!modifier.targetAttribute().isPresent()) {
                    continue;
                }

                modifier.targetAttribute().ifPresent(attribute -> {
                    AttributeInstance attributeInstance = player.getAttribute(attribute);
                    if (attributeInstance != null) {

                        boolean playerOwnsThisPerk = perks.hasPerk(cellId);
                        boolean playerHasActiveModifier = attributeInstance.getModifier(modifier.uuid()) != null;

                        if (playerOwnsThisPerk && !playerHasActiveModifier) {
                            attributeInstance.addPermanentModifier(new AttributeModifier(
                                    modifier.uuid(),
                                    "Perk Cell " + cellId,
                                    modifier.value(),
                                    modifier.operation()
                            ));
                        } else if (!playerOwnsThisPerk && playerHasActiveModifier) {
                            attributeInstance.removeModifier(modifier.uuid());
                        }
                    }
                });
            }
        });
    }

    @SubscribeEvent
    public static void onBreakSpeed(net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        if (player == null) return;

        player.getCapability(PlayerPerksProvider.PLAYER_PERKS).ifPresent(perks -> {
            // Check mining spreadsheet cells for passive modifiers (e.g., d2)
            if (perks.hasPerk("d2")) {
                event.setNewSpeed(event.getOriginalSpeed() * 1.3f);
            }
        });
    }
}