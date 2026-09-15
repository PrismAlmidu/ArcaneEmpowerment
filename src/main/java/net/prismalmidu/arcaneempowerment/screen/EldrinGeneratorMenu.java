package net.prismalmidu.arcaneempowerment.screen;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;
import net.prismalmidu.arcaneempowerment.block.entity.EldrinGeneratorBlockEntity;
import net.prismalmidu.arcaneempowerment.screen.ModMenuTypes;

public class EldrinGeneratorMenu extends AbstractContainerMenu {
    private final EldrinGeneratorBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    // Client-side constructor
    public EldrinGeneratorMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(8));
    }

    // Server-side constructor
    public EldrinGeneratorMenu(int id, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.ELDRIN_GENERATOR_MENU.get(), id);
        checkContainerDataCount(data, 8);
        this.blockEntity = (EldrinGeneratorBlockEntity) entity;
        this.level = inv.player.level();
        this.data = data;

        // Track data slots
        addDataSlots(data);
    }

    public int getEnergy() { return this.data.get(0); }
    public int getFluid() { return this.data.get(1); }

    // Divide back by 100.0f to recover floating points for rendering
    public float getAffinityGen(int index) { return this.data.get(index) / 100.0f; }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY; // No item slots inside this generator setup
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player,
                blockEntity.getBlockState().getBlock()) && blockEntity.isStructureComplete();
    }

}