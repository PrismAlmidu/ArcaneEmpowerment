package net.prismalmidu.arcaneempowerment.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerPerksProvider implements ICapabilitySerializable<CompoundTag> {
    public static Capability<PlayerPerks> PLAYER_PERKS = CapabilityManager.get(new CapabilityToken<>() {});

    private PlayerPerks perks = null;
    private final LazyOptional<PlayerPerks> optional = LazyOptional.of(this::createPlayerPerks);

    private PlayerPerks createPlayerPerks() {
        if (this.perks == null) {
            this.perks = new PlayerPerks();
        }
        return this.perks;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == PLAYER_PERKS) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createPlayerPerks().saveNBT(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createPlayerPerks().loadNBT(nbt);
    }
}