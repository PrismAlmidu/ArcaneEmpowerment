package net.prismalmidu.arcaneempowerment.capability;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.common.util.LazyOptional;

import java.util.UUID;

public record PerkModifier(
        UUID uuid,                    // Unique ID ensuring this cell doesn't clash with others
        LazyOptional<Attribute> targetAttribute, // Wrapped vanilla attribute (or empty for custom mods/placeholders)
        double value,                 // The numerical modifier amount (e.g., 0.15 for +15%, 12.0 for +12 HP)
        net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation operation
) {}