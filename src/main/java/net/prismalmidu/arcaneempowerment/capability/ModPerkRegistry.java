package net.prismalmidu.arcaneempowerment.capability;

import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.LazyOptional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ModPerkRegistry {
    // The master database map that your tick loop scans
    public static final Map<String, PerkModifier> REGISTRY = new HashMap<>();

    /**
     * Generates a permanent, unique UUID for a specific cell ID string.
     * This keeps data safe across saves and stops bonuses from clashing.
     */
    private static UUID createCellUUID(String cellId) {
        return UUID.nameUUIDFromBytes(("arcaneempowerment:perk_" + cellId).getBytes());
    }

    static {
        // =========================================================================
        //REGISTRY.put("a1",  new PerkModifier(createCellUUID("a1"),  LazyOptional.of(() -> ModAttributes.SPELL_DAMAGE), 0.10D, Operation.MULTIPLY_BASE)); // Spell Damage +10%
        //REGISTRY.put("a2",  new PerkModifier(createCellUUID("a2"),  LazyOptional.of(() -> ModAttributes.SPELL_EFFICIENCY), 0.05D, Operation.MULTIPLY_BASE)); // Spell Efficiency +5%
        //REGISTRY.put("a3",  new PerkModifier(createCellUUID("a3"),  LazyOptional.of(() -> ModAttributes.CAST_SPEED), 0.05D, Operation.MULTIPLY_BASE)); //Cast Speed +5%
        //REGISTRY.put("a4",  new PerkModifier(createCellUUID("a4"),  LazyOptional.of(() -> ModAttributes.SPELL_EFFICIENCY), 0.05D, Operation.MULTIPLY_BASE)); // Spell Efficiency +5%
        //REGISTRY.put("a5",  new PerkModifier(createCellUUID("a5"),  LazyOptional.of(() -> ModAttributes.CAST_SPEED), 0.05D, Operation.MULTIPLY_BASE)); //Cast Speed +5%
        //REGISTRY.put("a6",  new PerkModifier(createCellUUID("a6"),  LazyOptional.of(() -> ModAttributes.SPELL_EFFICIENCY), 0.05D, Operation.MULTIPLY_BASE)); // Spell Efficiency +5%
        //REGISTRY.put("a7",  new PerkModifier(createCellUUID("a7"),  LazyOptional.of(() -> ModAttributes.CAST_SPEED), 0.05D, Operation.MULTIPLY_BASE)); //Cast Speed +5%
        //REGISTRY.put("a8",  new PerkModifier(createCellUUID("a8"),  LazyOptional.of(() -> ModAttributes.MANA_REGEN), 0.10D, Operation.MULTIPLY_BASE)); //Mana Regen +10%
        //REGISTRY.put("a9",  new PerkModifier(createCellUUID("a9"),  LazyOptional.of(() -> ModAttributes.MANA_REGEN), 0.10D, Operation.MULTIPLY_BASE)); //Mana Regen +10%
        //REGISTRY.put("a10",  new PerkModifier(createCellUUID("a10"),  LazyOptional.of(() -> ModAttributes.SPELL_EFFICIENCY), 0.05D, Operation.MULTIPLY_BASE)); // Spell Efficiency +5%
        //REGISTRY.put("a11",  new PerkModifier(createCellUUID("a11"),  LazyOptional.of(() -> ModAttributes.CAST_SPEED), 0.05D, Operation.MULTIPLY_BASE)); //Cast Speed +5%
        //REGISTRY.put("a12",  new PerkModifier(createCellUUID("a12"),  LazyOptional.of(() -> ModAttributes.SPELL_EFFICIENCY), 0.05D, Operation.MULTIPLY_BASE)); // Spell Efficiency +5%
        //REGISTRY.put("a13",  new PerkModifier(createCellUUID("a13"),  LazyOptional.of(() -> ModAttributes.CAST_SPEED), 0.05D, Operation.MULTIPLY_BASE)); //Cast Speed +5%
        //REGISTRY.put("a14",  new PerkModifier(createCellUUID("a14"),  LazyOptional.of(() -> ModAttributes.SPELL_DAMAGE), 0.05D, Operation.MULTIPLY_BASE)); // Spell Damage +5%
        //REGISTRY.put("a15",  new PerkModifier(createCellUUID("a15"),  LazyOptional.of(() -> ModAttributes.SPELL_DAMAGE), 0.05D, Operation.MULTIPLY_BASE)); // Spell Damage +5%
        REGISTRY.put("a16", new PerkModifier(createCellUUID("a16"), LazyOptional.of(() -> Attributes.MAX_HEALTH), 4.0D, Operation.ADDITION));  // Max Health +4
        REGISTRY.put("a17", new PerkModifier(createCellUUID("a17"), LazyOptional.of(() -> Attributes.MAX_HEALTH), 4.0D, Operation.ADDITION));  // Max Health +4
        REGISTRY.put("a18", new PerkModifier(createCellUUID("a18"), LazyOptional.of(() -> Attributes.ARMOR_TOUGHNESS), 2.0D, Operation.ADDITION)); // Armor Toughness +2
        REGISTRY.put("a19", new PerkModifier(createCellUUID("a19"), LazyOptional.of(() -> Attributes.ARMOR_TOUGHNESS), 2.0D, Operation.ADDITION)); // Armor Toughness +2
        //REGISTRY.put("a20",  new PerkModifier(createCellUUID("a20"),  LazyOptional.of(() -> ModAttributes.SPELL_EFFICIENCY), 0.05D, Operation.MULTIPLY_BASE)); // Spell Efficiency +5%
        //REGISTRY.put("a21",  new PerkModifier(createCellUUID("a21"),  LazyOptional.of(() -> ModAttributes.CAST_SPEED), 0.05D, Operation.MULTIPLY_BASE)); //Cast Speed +5%
        //REGISTRY.put("a22",  new PerkModifier(createCellUUID("a22"),  LazyOptional.of(() -> ModAttributes.MAX_MANA), 100.0D, Operation.ADDITION)); // Max Mana +100
        REGISTRY.put("a23", new PerkModifier(createCellUUID("a23"), LazyOptional.of(() -> Attributes.MAX_HEALTH), 12.0D, Operation.ADDITION));  // Max Health +12
        //REGISTRY.put("a24",  new PerkModifier(createCellUUID("a24"),  LazyOptional.of(() -> ModAttributes.MAX_MANA), 100.0D, Operation.ADDITION)); // Max Mana +100
        //REGISTRY.put("a25",  new PerkModifier(createCellUUID("a25"),  LazyOptional.of(() -> ModAttributes.SPELL_DAMAGE), 0.05D, Operation.MULTIPLY_BASE)); // Spell Damage +5%
        //REGISTRY.put("a26",  new PerkModifier(createCellUUID("a26"),  LazyOptional.of(() -> ModAttributes.SPELL_EFFICIENCY), 0.10D, Operation.MULTIPLY_BASE)); // Spell Efficiency +10%
        //REGISTRY.put("a27",  new PerkModifier(createCellUUID("a27"),  LazyOptional.of(() -> ModAttributes.CAST_SPEED), 0.10D, Operation.MULTIPLY_BASE)); //Cast Speed +10%
        //REGISTRY.put("a28",  new PerkModifier(createCellUUID("a28"),  LazyOptional.of(() -> ModAttributes.SPELL_DAMAGE), 0.10D, Operation.MULTIPLY_BASE)); // Spell Damage +10%
        //REGISTRY.put("a29",  new PerkModifier(createCellUUID("a29"),  LazyOptional.of(() -> ModAttributes.SPELL_DAMAGE), 0.10D, Operation.MULTIPLY_BASE)); // Spell Damage +10%
        //REGISTRY.put("a30",  new PerkModifier(createCellUUID("a30"),  LazyOptional.of(() -> ModAttributes.SPELL_DAMAGE), 0.10D, Operation.MULTIPLY_BASE)); // Spell Damage +10%

        //REGISTRY.put("b1",  new PerkModifier(createCellUUID("b1"),  LazyOptional.of(() -> ALObjects.Attributes.ARROW_DAMAGE), 0.10D, Operation.MULTIPLY_BASE)); // Arrow Damage +10%
        //REGISTRY.put("b2",  new PerkModifier(createCellUUID("b2"),  LazyOptional.of(() -> ALObjects.Attributes.DRAW_SPEED), 0.05D, Operation.MULTIPLY_BASE)); //Draw Speed +5%
        //REGISTRY.put("b3",  new PerkModifier(createCellUUID("b3"),  LazyOptional.of(() -> ALObjects.Attributes.ARROW_VELOCITY), 0.05D, Operation.MULTIPLY_BASE)); //Arrow Velocity +5%
        //REGISTRY.put("b4",  new PerkModifier(createCellUUID("b4"),  LazyOptional.of(() -> ALObjects.Attributes.DRAW_SPEED), 0.05D, Operation.MULTIPLY_BASE)); //Draw Speed +5%
        //REGISTRY.put("b5",  new PerkModifier(createCellUUID("b5"),  LazyOptional.of(() -> ALObjects.Attributes.ARROW_VELOCITY), 0.05D, Operation.MULTIPLY_BASE)); //Arrow Velocity +5%
        //REGISTRY.put("b6",  new PerkModifier(createCellUUID("b6"),  LazyOptional.of(() -> ALObjects.Attributes.DRAW_SPEED), 0.05D, Operation.MULTIPLY_BASE)); //Draw Speed +5%
        //REGISTRY.put("b7",  new PerkModifier(createCellUUID("b7"),  LazyOptional.of(() -> ALObjects.Attributes.ARROW_VELOCITY), 0.05D, Operation.MULTIPLY_BASE)); //Arrow Velocity +5%
        //REGISTRY.put("b8",  new PerkModifier(createCellUUID("b8"),  LazyOptional.of(() -> ModAttributes.MANA_REGEN), 0.10D, Operation.MULTIPLY_BASE)); //Mana Regen +10%
        //REGISTRY.put("b9",  new PerkModifier(createCellUUID("b9"),  LazyOptional.of(() -> ModAttributes.MANA_REGEN), 0.10D, Operation.MULTIPLY_BASE)); //Mana Regen +10%
        //REGISTRY.put("b10",  new PerkModifier(createCellUUID("b10"),  LazyOptional.of(() -> ALObjects.Attributes.DRAW_SPEED), 0.05D, Operation.MULTIPLY_BASE)); //Draw Speed +5%
        //REGISTRY.put("b11",  new PerkModifier(createCellUUID("b11"),  LazyOptional.of(() -> ALObjects.Attributes.ARROW_VELOCITY), 0.05D, Operation.MULTIPLY_BASE)); //Arrow Velocity +5%
        //REGISTRY.put("b12",  new PerkModifier(createCellUUID("b12"),  LazyOptional.of(() -> ALObjects.Attributes.DRAW_SPEED), 0.05D, Operation.MULTIPLY_BASE)); //Draw Speed +5%
        //REGISTRY.put("b13",  new PerkModifier(createCellUUID("b13"),  LazyOptional.of(() -> ALObjects.Attributes.ARROW_VELOCITY), 0.05D, Operation.MULTIPLY_BASE)); //Arrow Velocity +5%
        //REGISTRY.put("b14",  new PerkModifier(createCellUUID("b14"),  LazyOptional.of(() -> ALObjects.Attributes.ARROW_DAMAGE), 0.05D, Operation.MULTIPLY_BASE)); // Arrow Damage +5%
        //REGISTRY.put("b15",  new PerkModifier(createCellUUID("b15"),  LazyOptional.of(() -> ALObjects.Attributes.ARROW_DAMAGE), 0.05D, Operation.MULTIPLY_BASE)); // Arrow Damage +5%
        REGISTRY.put("b16", new PerkModifier(createCellUUID("b16"), LazyOptional.of(() -> Attributes.MAX_HEALTH), 4.0D, Operation.ADDITION));  // Max Health +4
        REGISTRY.put("b17", new PerkModifier(createCellUUID("b17"), LazyOptional.of(() -> Attributes.MAX_HEALTH), 4.0D, Operation.ADDITION));  // Max Health +4
        REGISTRY.put("b18", new PerkModifier(createCellUUID("b18"), LazyOptional.of(() -> Attributes.ARMOR_TOUGHNESS), 2.0D, Operation.ADDITION)); // Armor Toughness +2
        REGISTRY.put("b19", new PerkModifier(createCellUUID("b19"), LazyOptional.of(() -> Attributes.ARMOR_TOUGHNESS), 2.0D, Operation.ADDITION)); // Armor Toughness +2
        //REGISTRY.put("b20",  new PerkModifier(createCellUUID("b20"),  LazyOptional.of(() -> ALObjects.Attributes.DRAW_SPEED), 0.05D, Operation.MULTIPLY_BASE)); //Draw Speed +5%
        //REGISTRY.put("b21",  new PerkModifier(createCellUUID("b21"),  LazyOptional.of(() -> ALObjects.Attributes.ARROW_VELOCITY), 0.05D, Operation.MULTIPLY_BASE)); //Arrow Velocity +5%
        //REGISTRY.put("b22",  new PerkModifier(createCellUUID("b22"),  LazyOptional.of(() -> ModAttributes.MAX_MANA), 100.0D, Operation.ADDITION)); // Max Mana +100
        REGISTRY.put("b23",  new PerkModifier(createCellUUID("b23"), LazyOptional.of(() -> Attributes.MAX_HEALTH), 12.0D, Operation.ADDITION));  // Max Health +12
        //REGISTRY.put("b24",  new PerkModifier(createCellUUID("b24"),  LazyOptional.of(() -> ModAttributes.MAX_MANA), 100.0D, Operation.ADDITION)); // Max Mana +100
        //REGISTRY.put("b25",  new PerkModifier(createCellUUID("b25"),  LazyOptional.of(() -> ALObjects.Attributes.DRAW_SPEED), 0.10D, Operation.MULTIPLY_BASE)); //Draw Speed +10%
        //REGISTRY.put("b26",  new PerkModifier(createCellUUID("b26"),  LazyOptional.of(() -> ALObjects.Attributes.ARROW_DAMAGE), 0.05D, Operation.MULTIPLY_BASE)); // Arrow Damage +5%
        //REGISTRY.put("b27",  new PerkModifier(createCellUUID("b27"),  LazyOptional.of(() -> ALObjects.Attributes.ARROW_VELOCITY), 0.10D, Operation.MULTIPLY_BASE)); //Arrow Velocity +10%
        //REGISTRY.put("b28",  new PerkModifier(createCellUUID("b28"),  LazyOptional.of(() -> ALObjects.Attributes.ARROW_DAMAGE), 0.10D, Operation.MULTIPLY_BASE)); // Arrow Damage +10%
        //REGISTRY.put("b29",  new PerkModifier(createCellUUID("b29"),  LazyOptional.of(() -> ALObjects.Attributes.ARROW_DAMAGE), 0.10D, Operation.MULTIPLY_BASE)); // Arrow Damage +10%
        //REGISTRY.put("b30",  new PerkModifier(createCellUUID("b30"),  LazyOptional.of(() -> ALObjects.Attributes.ARROW_DAMAGE), 0.10D, Operation.MULTIPLY_BASE)); // Arrow Damage +10%

        REGISTRY.put("c1",  new PerkModifier(createCellUUID("c1"),  LazyOptional.of(() -> Attributes.ATTACK_DAMAGE), 0.10D, Operation.MULTIPLY_BASE));          // Attack Damage +10%
        REGISTRY.put("c2",  new PerkModifier(createCellUUID("c2"),  LazyOptional.of(() -> Attributes.ARMOR), 1.0D, Operation.ADDITION));          // Armor +1
        REGISTRY.put("c3",  new PerkModifier(createCellUUID("c3"),  LazyOptional.of(() -> Attributes.ATTACK_SPEED), 0.05D, Operation.MULTIPLY_BASE)); // Attack Speed +5%
        REGISTRY.put("c4",  new PerkModifier(createCellUUID("c4"),  LazyOptional.of(() -> Attributes.ARMOR), 1.0D, Operation.ADDITION));          // Armor +1
        REGISTRY.put("c5",  new PerkModifier(createCellUUID("c5"),  LazyOptional.of(() -> Attributes.ATTACK_SPEED), 0.05D, Operation.MULTIPLY_BASE)); // Attack Speed +5%
        REGISTRY.put("c6",  new PerkModifier(createCellUUID("c6"),  LazyOptional.of(() -> Attributes.ARMOR), 1.0D, Operation.ADDITION));          // Armor +1
        REGISTRY.put("c7",  new PerkModifier(createCellUUID("c7"),  LazyOptional.of(() -> Attributes.ATTACK_SPEED), 0.05D, Operation.MULTIPLY_BASE)); // Attack Speed +5%
        //REGISTRY.put("c8",  new PerkModifier(createCellUUID("c8"),  LazyOptional.of(() -> ModAttributes.MANA_REGEN), 0.10D, Operation.MULTIPLY_BASE)); //Mana Regen +10%
        //REGISTRY.put("c9",  new PerkModifier(createCellUUID("c9"),  LazyOptional.of(() -> ModAttributes.MANA_REGEN), 0.10D, Operation.MULTIPLY_BASE)); //Mana Regen +10%
        REGISTRY.put("c10",  new PerkModifier(createCellUUID("c10"),  LazyOptional.of(() -> Attributes.ARMOR), 1.0D, Operation.ADDITION));          // Armor +1
        REGISTRY.put("c11",  new PerkModifier(createCellUUID("c11"),  LazyOptional.of(() -> Attributes.ATTACK_SPEED), 0.05D, Operation.MULTIPLY_BASE)); // Attack Speed +5%
        REGISTRY.put("c12",  new PerkModifier(createCellUUID("c12"),  LazyOptional.of(() -> Attributes.ARMOR), 1.0D, Operation.ADDITION));          // Armor +1
        REGISTRY.put("c13",  new PerkModifier(createCellUUID("c13"),  LazyOptional.of(() -> Attributes.ATTACK_SPEED), 0.05D, Operation.MULTIPLY_BASE)); // Attack Speed +5%
        REGISTRY.put("c14",  new PerkModifier(createCellUUID("c14"),  LazyOptional.of(() -> Attributes.ATTACK_DAMAGE), 0.05D, Operation.MULTIPLY_BASE));          // Attack Damage +5%
        REGISTRY.put("c15",  new PerkModifier(createCellUUID("c15"),  LazyOptional.of(() -> Attributes.ATTACK_DAMAGE), 0.05D, Operation.MULTIPLY_BASE));          // Attack Damage +5%
        REGISTRY.put("c16", new PerkModifier(createCellUUID("c16"), LazyOptional.of(() -> Attributes.MAX_HEALTH), 4.0D, Operation.ADDITION));  // Max Health +4
        REGISTRY.put("c17", new PerkModifier(createCellUUID("c17"), LazyOptional.of(() -> Attributes.MAX_HEALTH), 4.0D, Operation.ADDITION));  // Max Health +4
        REGISTRY.put("c18", new PerkModifier(createCellUUID("c18"), LazyOptional.of(() -> Attributes.ARMOR_TOUGHNESS), 2.0D, Operation.ADDITION)); // Armor Toughness +2
        REGISTRY.put("c19", new PerkModifier(createCellUUID("c19"), LazyOptional.of(() -> Attributes.ARMOR_TOUGHNESS), 2.0D, Operation.ADDITION)); // Armor Toughness +2
        REGISTRY.put("c20",  new PerkModifier(createCellUUID("c20"),  LazyOptional.of(() -> Attributes.ARMOR), 1.0D, Operation.ADDITION));          // Armor +1
        REGISTRY.put("c21",  new PerkModifier(createCellUUID("c21"),  LazyOptional.of(() -> Attributes.ATTACK_SPEED), 0.05D, Operation.MULTIPLY_BASE)); // Attack Speed +5%
        //REGISTRY.put("c22",  new PerkModifier(createCellUUID("c22"),  LazyOptional.of(() -> ModAttributes.MAX_MANA), 100.0D, Operation.ADDITION)); // Max Mana +100
        REGISTRY.put("c23", new PerkModifier(createCellUUID("c23"), LazyOptional.of(() -> Attributes.MAX_HEALTH), 12.0D, Operation.ADDITION));  // Max Health +12
        //REGISTRY.put("c24",  new PerkModifier(createCellUUID("c24"),  LazyOptional.of(() -> ModAttributes.MAX_MANA), 100.0D, Operation.ADDITION)); // Max Mana +100
        REGISTRY.put("c25",  new PerkModifier(createCellUUID("c25"),  LazyOptional.of(() -> Attributes.ARMOR), 2.0D, Operation.ADDITION));          // Armor +2
        REGISTRY.put("c26",  new PerkModifier(createCellUUID("c26"),  LazyOptional.of(() -> Attributes.ATTACK_DAMAGE), 0.05D, Operation.MULTIPLY_BASE));          // Attack Damage +5%
        REGISTRY.put("c27",  new PerkModifier(createCellUUID("c27"),  LazyOptional.of(() -> Attributes.ATTACK_SPEED), 0.10D, Operation.MULTIPLY_BASE)); // Attack Speed +10%
        REGISTRY.put("c28",  new PerkModifier(createCellUUID("c28"),  LazyOptional.of(() -> Attributes.ATTACK_DAMAGE), 0.10D, Operation.MULTIPLY_BASE));          // Attack Damage +10%
        REGISTRY.put("c29",  new PerkModifier(createCellUUID("c29"),  LazyOptional.of(() -> Attributes.ATTACK_DAMAGE), 0.10D, Operation.MULTIPLY_BASE));          // Attack Damage +10%
        REGISTRY.put("c30",  new PerkModifier(createCellUUID("c30"),  LazyOptional.of(() -> Attributes.ATTACK_DAMAGE), 0.10D, Operation.MULTIPLY_BASE));          // Attack Damage +10%

        //REGISTRY.put("d1",  new PerkModifier(createCellUUID("d1"),  LazyOptional.of(() -> ALObjects.Attributes.EXPERIENCE_GAINED), 0.10D, Operation.MULTIPLY_BASE)); //Experience Gained +10%
        //REGISTRY.put("d2", new PerkModifier(createCellUUID("d2"), LazyOptional.of(() -> Attributes.SUBMERGED_MINING_SPEED), 0.1D, Operation.ADDITION));  // Submerged Mining Speed +0.1
        //REGISTRY.put("d3", new PerkModifier(createCellUUID("d3"), LazyOptional.of(() -> Attributes.SAFE_FALL_DISTANCE), 1.0D, Operation.ADDITION));  // Safe Fall Distance +1
        //REGISTRY.put("d4", new PerkModifier(createCellUUID("d4"), LazyOptional.of(() -> Attributes.SUBMERGED_MINING_SPEED), 0.1D, Operation.ADDITION));  // Submerged Mining Speed +0.1
        //REGISTRY.put("d5", new PerkModifier(createCellUUID("d5"), LazyOptional.of(() -> Attributes.SAFE_FALL_DISTANCE), 1.0D, Operation.ADDITION));  // Safe Fall Distance +1
        //REGISTRY.put("d6", new PerkModifier(createCellUUID("d6"), LazyOptional.of(() -> Attributes.SUBMERGED_MINING_SPEED), 0.1D, Operation.ADDITION));  // Submerged Mining Speed +0.1
        //REGISTRY.put("d7", new PerkModifier(createCellUUID("d7"), LazyOptional.of(() -> Attributes.SAFE_FALL_DISTANCE), 1.0D, Operation.ADDITION));  // Safe Fall Distance +1
        //REGISTRY.put("d8",  new PerkModifier(createCellUUID("d8"),  LazyOptional.of(() -> ModAttributes.MANA_REGEN), 0.10D, Operation.MULTIPLY_BASE)); //Mana Regen +10%
        //REGISTRY.put("d9",  new PerkModifier(createCellUUID("d9"),  LazyOptional.of(() -> ModAttributes.MANA_REGEN), 0.10D, Operation.MULTIPLY_BASE)); //Mana Regen +10%
        //REGISTRY.put("d10", new PerkModifier(createCellUUID("d10"), LazyOptional.of(() -> Attributes.SUBMERGED_MINING_SPEED), 0.1D, Operation.ADDITION));  // Submerged Mining Speed +0.1
        //REGISTRY.put("d11", new PerkModifier(createCellUUID("d11"), LazyOptional.of(() -> Attributes.SAFE_FALL_DISTANCE), 1.0D, Operation.ADDITION));  // Safe Fall Distance +1
        //REGISTRY.put("d12", new PerkModifier(createCellUUID("d12"), LazyOptional.of(() -> Attributes.SUBMERGED_MINING_SPEED), 0.1D, Operation.ADDITION));  // Submerged Mining Speed +0.1
        //REGISTRY.put("d13", new PerkModifier(createCellUUID("d13"), LazyOptional.of(() -> Attributes.SAFE_FALL_DISTANCE), 1.0D, Operation.ADDITION));  // Safe Fall Distance +1
        //REGISTRY.put("d14",  new PerkModifier(createCellUUID("d14"),  LazyOptional.of(() -> ALObjects.Attributes.EXPERIENCE_GAINED), 0.05D, Operation.MULTIPLY_BASE)); //Experience Gained +5%
        //REGISTRY.put("d15",  new PerkModifier(createCellUUID("d15"),  LazyOptional.of(() -> ALObjects.Attributes.EXPERIENCE_GAINED), 0.05D, Operation.MULTIPLY_BASE)); //Experience Gained +5%
        REGISTRY.put("d16", new PerkModifier(createCellUUID("d16"), LazyOptional.of(() -> Attributes.MAX_HEALTH), 4.0D, Operation.ADDITION));  // Max Health +4
        REGISTRY.put("d17", new PerkModifier(createCellUUID("d17"), LazyOptional.of(() -> Attributes.MAX_HEALTH), 4.0D, Operation.ADDITION));  // Max Health +4
        REGISTRY.put("d18", new PerkModifier(createCellUUID("d18"), LazyOptional.of(() -> Attributes.ARMOR_TOUGHNESS), 2.0D, Operation.ADDITION)); // Armor Toughness +2
        REGISTRY.put("d19", new PerkModifier(createCellUUID("d19"), LazyOptional.of(() -> Attributes.ARMOR_TOUGHNESS), 2.0D, Operation.ADDITION)); // Armor Toughness +2
        //REGISTRY.put("d20", new PerkModifier(createCellUUID("d20"), LazyOptional.of(() -> Attributes.SUBMERGED_MINING_SPEED), 0.1D, Operation.ADDITION));  // Submerged Mining Speed +0.1
        //REGISTRY.put("d21", new PerkModifier(createCellUUID("d21"), LazyOptional.of(() -> Attributes.SAFE_FALL_DISTANCE), 1.0D, Operation.ADDITION));  // Safe Fall Distance +1
        //REGISTRY.put("d22",  new PerkModifier(createCellUUID("d22"),  LazyOptional.of(() -> ModAttributes.MAX_MANA), 100.0D, Operation.ADDITION)); // Max Mana +100
        REGISTRY.put("d23", new PerkModifier(createCellUUID("d23"), LazyOptional.of(() -> Attributes.MAX_HEALTH), 12.0D, Operation.ADDITION));  // Max Health +12
        //REGISTRY.put("d24",  new PerkModifier(createCellUUID("d24"),  LazyOptional.of(() -> ModAttributes.MAX_MANA), 100.0D, Operation.ADDITION)); // Max Mana +100
        //REGISTRY.put("d25", new PerkModifier(createCellUUID("d25"), LazyOptional.of(() -> Attributes.SUBMERGED_MINING_SPEED), 0.2D, Operation.ADDITION));  // Submerged Mining Speed +0.2
        //REGISTRY.put("d26",  new PerkModifier(createCellUUID("d26"),  LazyOptional.of(() -> ALObjects.Attributes.EXPERIENCE_GAINED), 0.05D, Operation.MULTIPLY_BASE)); //Experience Gained +5%
        //REGISTRY.put("d27", new PerkModifier(createCellUUID("d27"), LazyOptional.of(() -> Attributes.SAFE_FALL_DISTANCE), 2.0D, Operation.ADDITION));  // Safe Fall Distance +2
        //REGISTRY.put("d28",  new PerkModifier(createCellUUID("d28"),  LazyOptional.of(() -> ALObjects.Attributes.EXPERIENCE_GAINED), 0.10D, Operation.MULTIPLY_BASE)); //Experience Gained +10%
        //REGISTRY.put("d29",  new PerkModifier(createCellUUID("d29"),  LazyOptional.of(() -> ALObjects.Attributes.EXPERIENCE_GAINED), 0.10D, Operation.MULTIPLY_BASE)); //Experience Gained +10%
        //REGISTRY.put("d30",  new PerkModifier(createCellUUID("d30"),  LazyOptional.of(() -> ALObjects.Attributes.EXPERIENCE_GAINED), 0.10D, Operation.MULTIPLY_BASE)); //Experience Gained +10%

        //REGISTRY.put("e1",  new PerkModifier(createCellUUID("e1"),  LazyOptional.of(() -> ModAttributes.MAX_MANA), 100.0D, Operation.ADDITION)); // Max Mana +100
        //REGISTRY.put("e2",  new PerkModifier(createCellUUID("e2"),  LazyOptional.of(() -> ModAttributes.MAX_MANA), 100.0D, Operation.ADDITION)); // Max Mana +100
        //REGISTRY.put("e3",  new PerkModifier(createCellUUID("e3"),  LazyOptional.of(() -> ALObjects.Attributes.CRIT_DAMAGE), 0.25D, Operation.MULTIPLY_BASE)); //Crit Damage +25%
        //REGISTRY.put("e4",  new PerkModifier(createCellUUID("e4"),  LazyOptional.of(() -> ALObjects.Attributes.LIFE_STEAL), 0.01D, Operation.MULTIPLY_BASE)); //Life Steal +1%
        //REGISTRY.put("e5",  new PerkModifier(createCellUUID("e5"),  LazyOptional.of(() -> ALObjects.Attributes.LIFE_STEAL), 0.01D, Operation.MULTIPLY_BASE)); //Life Steal +1%
        //REGISTRY.put("e6",  new PerkModifier(createCellUUID("e7"),  LazyOptional.of(() -> ALObjects.Attributes.CRIT_CHANCE), 0.02D, Operation.MULTIPLY_BASE)); //Crit Chance +2%
        //REGISTRY.put("e7",  new PerkModifier(createCellUUID("e6"),  LazyOptional.of(() -> ALObjects.Attributes.CRIT_CHANCE), 0.02D, Operation.MULTIPLY_BASE)); //Crit Chance +2%
        //REGISTRY.put("e8",  new PerkModifier(createCellUUID("e8"),  LazyOptional.of(() -> ALObjects.Attributes.CRIT_DAMAGE), 0.25D, Operation.MULTIPLY_BASE)); //Crit Damage +25%
        //REGISTRY.put("e9",  new PerkModifier(createCellUUID("e9"),  LazyOptional.of(() -> ALObjects.Attributes.CRIT_CHANCE), 0.02D, Operation.MULTIPLY_BASE)); //Crit Chance +2%
        //REGISTRY.put("e10",  new PerkModifier(createCellUUID("e10"),  LazyOptional.of(() -> ALObjects.Attributes.CRIT_CHANCE), 0.02D, Operation.MULTIPLY_BASE)); //Crit Chance +2%
        //REGISTRY.put("e11",  new PerkModifier(createCellUUID("e11"),  LazyOptional.of(() -> ALObjects.Attributes.CRIT_DAMAGE), 0.10D, Operation.MULTIPLY_BASE)); //Crit Damage +10%
        //REGISTRY.put("e12",  new PerkModifier(createCellUUID("e12"),  LazyOptional.of(() -> ALObjects.Attributes.COLD_DAMAGE), 5.0D, Operation.ADDITION)); // Cold Damage +5

        //REGISTRY.put("f1",  new PerkModifier(createCellUUID("f1"),  LazyOptional.of(() -> ALObjects.Attributes.CRIT_CHANCE), 0.02D, Operation.MULTIPLY_BASE)); //Crit Chance +2%
        //REGISTRY.put("f2",  new PerkModifier(createCellUUID("f2"),  LazyOptional.of(() -> ALObjects.Attributes.CRIT_CHANCE), 0.02D, Operation.MULTIPLY_BASE)); //Crit Chance +2%
        //REGISTRY.put("f3",  new PerkModifier(createCellUUID("f3"),  LazyOptional.of(() -> ALObjects.Attributes.LIFE_STEAL), 0.05D, Operation.MULTIPLY_BASE)); //Life Steal +5%
        //REGISTRY.put("f4",  new PerkModifier(createCellUUID("f4"),  LazyOptional.of(() -> ModAttributes.MAX_MANA), 100.0D, Operation.ADDITION)); // Max Mana +100
        //REGISTRY.put("f5",  new PerkModifier(createCellUUID("f5"),  LazyOptional.of(() -> ModAttributes.MAX_MANA), 100.0D, Operation.ADDITION)); // Max Mana +100
        REGISTRY.put("f6",  new PerkModifier(createCellUUID("f6"),  LazyOptional.of(() -> Attributes.KNOCKBACK_RESISTANCE), 0.20D, Operation.ADDITION)); // Knockback Resistance +0.2
        REGISTRY.put("f7",  new PerkModifier(createCellUUID("f7"),  LazyOptional.of(() -> Attributes.KNOCKBACK_RESISTANCE), 0.20D, Operation.ADDITION)); // Knockback Resistance +0.2
        //REGISTRY.put("f8",  new PerkModifier(createCellUUID("f8"),  LazyOptional.of(() -> ALObjects.Attributes.LIFE_STEAL), 0.05D, Operation.MULTIPLY_BASE)); //Life Steal +5%
        //REGISTRY.put("f9",  new PerkModifier(createCellUUID("f9"),  LazyOptional.of(() -> ALObjects.Attributes.LIFE_STEAL), 0.01D, Operation.MULTIPLY_BASE)); //Life Steal +1%
        //REGISTRY.put("f10",  new PerkModifier(createCellUUID("f10"),  LazyOptional.of(() -> ALObjects.Attributes.LIFE_STEAL), 0.01D, Operation.MULTIPLY_BASE)); //Life Steal +1%
        //REGISTRY.put("f11",  new PerkModifier(createCellUUID("f11"),  LazyOptional.of(() -> ALObjects.Attributes.LIFE_STEAL), 0.01D, Operation.MULTIPLY_BASE)); //Life Steal +1%
        //REGISTRY.put("f12",  new PerkModifier(createCellUUID("f12"),  LazyOptional.of(() -> ALObjects.Attributes.FIRE_DAMAGE), 5.0D, Operation.ADDITION)); // Fire Damage +5

        //REGISTRY.put("g1",  new PerkModifier(createCellUUID("g1"),  LazyOptional.of(() -> ALObjects.Attributes.LIFE_STEAL), 0.01D, Operation.MULTIPLY_BASE)); //Life Steal +1%
        //REGISTRY.put("g2",  new PerkModifier(createCellUUID("g2"),  LazyOptional.of(() -> ALObjects.Attributes.LIFE_STEAL), 0.01D, Operation.MULTIPLY_BASE)); //Life Steal +1%
        //REGISTRY.put("g3",  new PerkModifier(createCellUUID("g3"),  LazyOptional.of(() -> Attributes.SWEEPING_DAMAGE_RATIO), 0.20D, Operation.ADDITION)); // Sweeping Damage Ratio +0.2
        REGISTRY.put("g4",  new PerkModifier(createCellUUID("g4"),  LazyOptional.of(() -> Attributes.KNOCKBACK_RESISTANCE), 0.20D, Operation.ADDITION)); // Knockback Resistance +0.2
        REGISTRY.put("g5",  new PerkModifier(createCellUUID("g5"),  LazyOptional.of(() -> Attributes.KNOCKBACK_RESISTANCE), 0.20D, Operation.ADDITION)); // Knockback Resistance +0.2
        //REGISTRY.put("g6",  new PerkModifier(createCellUUID("g6"),  LazyOptional.of(() -> ALObjects.Attributes.CRIT_CHANCE), 0.02D, Operation.MULTIPLY_BASE)); //Crit Chance +2%
        //REGISTRY.put("g7",  new PerkModifier(createCellUUID("g7"),  LazyOptional.of(() -> ALObjects.Attributes.CRIT_CHANCE), 0.02D, Operation.MULTIPLY_BASE)); //Crit Chance +2%
        //REGISTRY.put("g8",  new PerkModifier(createCellUUID("g8"),  LazyOptional.of(() -> Attributes.SWEEPING_DAMAGE_RATIO), 0.20D, Operation.ADDITION)); // Sweeping Damage Ratio +0.2
        //REGISTRY.put("g9",  new PerkModifier(createCellUUID("g9"),  LazyOptional.of(() -> Attributes.EXPLOSION_KNOCKBACK_RESISTANCE), 0.20D, Operation.ADDITION)); // Explosion Knockback Resistance +0.2
        //REGISTRY.put("g10",  new PerkModifier(createCellUUID("g10"),  LazyOptional.of(() -> Attributes.EXPLOSION_KNOCKBACK_RESISTANCE), 0.20D, Operation.ADDITION)); // Explosion Knockback Resistance +0.2
        //REGISTRY.put("g11",  new PerkModifier(createCellUUID("g11"),  LazyOptional.of(() -> Attributes.SWEEPING_DAMAGE_RATIO), 0.10D, Operation.ADDITION)); // Sweeping Damage Ratio +0.1
        REGISTRY.put("g12",  new PerkModifier(createCellUUID("g12"),  LazyOptional.of(() -> ForgeMod.BLOCK_REACH.get()), 1.0D, Operation.ADDITION)); // Block Interaction Range +1

        //REGISTRY.put("h1",  new PerkModifier(createCellUUID("h1"),  LazyOptional.of(() -> Attributes.EXPLOSION_KNOCKBACK_RESISTANCE), 0.20D, Operation.ADDITION)); // Explosion Knockback Resistance +0.2
        //REGISTRY.put("h2",  new PerkModifier(createCellUUID("h2"),  LazyOptional.of(() -> Attributes.EXPLOSION_KNOCKBACK_RESISTANCE), 0.20D, Operation.ADDITION)); // Explosion Knockback Resistance +0.2
        //REGISTRY.put("h3",  new PerkModifier(createCellUUID("h3"),  LazyOptional.of(() -> ModAttributes.MAX_MANA), 200.0D, Operation.ADDITION)); // Max Mana +200
        //REGISTRY.put("h4",  new PerkModifier(createCellUUID("h4"),  LazyOptional.of(() -> ALObjects.Attributes.CRIT_CHANCE), 0.02D, Operation.MULTIPLY_BASE)); //Crit Chance +2%
        //REGISTRY.put("h5",  new PerkModifier(createCellUUID("h5"),  LazyOptional.of(() -> ALObjects.Attributes.CRIT_CHANCE), 0.02D, Operation.MULTIPLY_BASE)); //Crit Chance +2%
        //REGISTRY.put("h6",  new PerkModifier(createCellUUID("h6"),  LazyOptional.of(() -> ModAttributes.MAX_MANA), 100.0D, Operation.ADDITION)); // Max Mana +100
        //REGISTRY.put("h7",  new PerkModifier(createCellUUID("h7"),  LazyOptional.of(() -> ModAttributes.MAX_MANA), 100.0D, Operation.ADDITION)); // Max Mana +100
        //REGISTRY.put("h8",  new PerkModifier(createCellUUID("h8"),  LazyOptional.of(() -> ModAttributes.MAX_MANA), 200.0D, Operation.ADDITION)); // Max Mana +200
        //REGISTRY.put("h9",  new PerkModifier(createCellUUID("h9"),  LazyOptional.of(() -> ModAttributes.MAX_MANA), 100.0D, Operation.ADDITION)); // Max Mana +100
        //REGISTRY.put("h10",  new PerkModifier(createCellUUID("h10"),  LazyOptional.of(() -> ModAttributes.MAX_MANA), 100.0D, Operation.ADDITION)); // Max Mana +100
        //REGISTRY.put("h11",  new PerkModifier(createCellUUID("h11"),  LazyOptional.of(() -> ModAttributes.MAX_MANA), 100.0D, Operation.ADDITION)); // Max Mana +100
        //REGISTRY.put("h12",  new PerkModifier(createCellUUID("h12"),  LazyOptional.of(() -> ModAttributes.MAX_MANA_MULTIPLIER_FINAL), 0.20D, Operation.MULTIPLY_BASE)); //Max Mana Multiplier Final +20%

        REGISTRY.put("i1", new PerkModifier(createCellUUID("i1"), LazyOptional.of(() -> Attributes.MAX_HEALTH), 12.0D, Operation.ADDITION));  // Max Health +12
        REGISTRY.put("i2", new PerkModifier(createCellUUID("i2"), LazyOptional.of(() -> Attributes.MAX_HEALTH), 4.0D, Operation.ADDITION));  // Max Health +4
        REGISTRY.put("i3", new PerkModifier(createCellUUID("i3"), LazyOptional.of(() -> Attributes.MAX_HEALTH), 4.0D, Operation.ADDITION));  // Max Health +4
        REGISTRY.put("i4",  new PerkModifier(createCellUUID("i4"),  LazyOptional.of(() -> Attributes.ARMOR), 1.0D, Operation.ADDITION));          // Armor +1
        //REGISTRY.put("i5",  new PerkModifier(createCellUUID("i5"),  LazyOptional.of(() -> ModAttributes.MAX_MANA), 200.0D, Operation.ADDITION)); // Max Mana +200
        //REGISTRY.put("i6",  new PerkModifier(createCellUUID("i6"),  LazyOptional.of(() -> ModAttributes.MAX_MANA), 100.0D, Operation.ADDITION)); // Max Mana +100
        //REGISTRY.put("i7",  new PerkModifier(createCellUUID("i7"),  LazyOptional.of(() -> ModAttributes.MAX_MANA), 100.0D, Operation.ADDITION)); // Max Mana +100
        REGISTRY.put("i9",  new PerkModifier(createCellUUID("i9"),  LazyOptional.of(() -> Attributes.ARMOR), 1.0D, Operation.ADDITION));          // Armor +1
        REGISTRY.put("i10", new PerkModifier(createCellUUID("i10"), LazyOptional.of(() -> Attributes.MAX_HEALTH), 12.0D, Operation.ADDITION));  // Max Health +12
        REGISTRY.put("i11", new PerkModifier(createCellUUID("i11"), LazyOptional.of(() -> Attributes.MAX_HEALTH), 4.0D, Operation.ADDITION));  // Max Health +4
        REGISTRY.put("i12", new PerkModifier(createCellUUID("i12"), LazyOptional.of(() -> Attributes.MAX_HEALTH), 4.0D, Operation.ADDITION));  // Max Health +4
        REGISTRY.put("i13",  new PerkModifier(createCellUUID("i13"),  LazyOptional.of(() -> Attributes.ARMOR), 1.0D, Operation.ADDITION));          // Armor +1
        //REGISTRY.put("i14",  new PerkModifier(createCellUUID("i14"),  LazyOptional.of(() -> ModAttributes.MAX_MANA), 200.0D, Operation.ADDITION)); // Max Mana +200
        //REGISTRY.put("i14",  new PerkModifier(createCellUUID("i14"),  LazyOptional.of(() -> ModAttributes.MAX_MANA), 100.0D, Operation.ADDITION)); // Max Mana +100
        //REGISTRY.put("i15",  new PerkModifier(createCellUUID("i15"),  LazyOptional.of(() -> ModAttributes.MAX_MANA), 100.0D, Operation.ADDITION)); // Max Mana +100
        REGISTRY.put("i16",  new PerkModifier(createCellUUID("i16"),  LazyOptional.of(() -> Attributes.ARMOR), 1.0D, Operation.ADDITION));          // Armor +1
    }
}