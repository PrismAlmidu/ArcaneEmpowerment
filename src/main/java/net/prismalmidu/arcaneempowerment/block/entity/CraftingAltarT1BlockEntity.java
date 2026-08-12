package net.prismalmidu.arcaneempowerment.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.prismalmidu.arcaneempowerment.item.ModItems;
import net.prismalmidu.arcaneempowerment.screen.CraftingAltarT1Menu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CraftingAltarT1BlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(10);

    private static final int INPUT_SLOT_1 = 0;
    private static final int INPUT_SLOT_2 = 1;
    private static final int INPUT_SLOT_3 = 2;
    private static final int INPUT_SLOT_4 = 3;
    private static final int INPUT_SLOT_5 = 4;
    private static final int INPUT_SLOT_6 = 5;
    private static final int INPUT_SLOT_7 = 6;
    private static final int INPUT_SLOT_8 = 7;
    private static final int INPUT_SLOT_9 = 8;
    private static final int OUTPUT_SLOT = 9;

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    //protected final ContainerData data;
    //private int progress = 0;
    //private int maxProgress = 78;
    // unnecessary? not using duration crafting, other stuff at 10:30

    public CraftingAltarT1BlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.CRAFTING_ALTAR_T1_BE.get(), pPos, pBlockState);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    //@Override
    //public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
    //    return super.getCapability(cap);
    //} if direction is unnecessary?


    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.arcaneempowerment.crafting_altar_t1");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new CraftingAltarT1Menu(pContainerId, pPlayerInventory, this, new SimpleContainerData(10));//intentional 39:10
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemHandler.serializeNBT());
        //pTag.putInt("almiducustommachineprogresslabelhere", progress);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        //progress = pTag.getInt("almiducustommachineprogresslabelhere");
    }

    //public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
       // if (hasRecipe()) { 22:30 timestamp
         //   increaseCraftingProgress();
           // SetChanged(pLevel, pPos, pState);

            //if(hasProgressFinished()) {
              //  craftItem();
                //resetProgress();
            //}
        //} else {
          //  resetProgress();
        //}
    //}

    //private void craftItem() {
      //  ItemStack result = new ItemStack(ModItems.ARCANIUM_SHARD.get(), 1);
        //this.itemHandler.extractItem(INPUT_SLOT_1, 1, false);

        //this.itemHandler.getStackInSlot(OUTPUT_SLOT, new ItemStack(result.getItem(),
          //      this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + result.getCount()));
    //}

    //private boolean hasRecipe() {
        //boolean hasCraftingItem = this.itemHandler.getStackInSlot(INPUT_SLOT_1).getItem() == ModItems.LIQUID_MANA_BUCKET.get();
        //ItemStack result = new ItemStack(ModItems.ARCANIUM_SHARD.get());

      //  return hasCraftingItem && canInsertAmountIntoItemSlot(result.getCount()) && canInsertItemIntoOutputSlot(result.getItem());
    //}

    //private boolean canInsertItemIntoOutputSlot(Item item) {
      //  return this.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() || this.itemHandler.getStackInSlot(OUTPUT_SLOT).is(item);
    //}

    //private boolean canInsertAmountIntoOutputSlot(int count) {
     //   return this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + count <= this.itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
    //}


}
