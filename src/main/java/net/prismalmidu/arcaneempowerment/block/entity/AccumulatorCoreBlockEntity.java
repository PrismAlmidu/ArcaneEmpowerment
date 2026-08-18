package net.prismalmidu.arcaneempowerment.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.prismalmidu.arcaneempowerment.block.ModBlocks;
import net.prismalmidu.arcaneempowerment.block.custom.AccumulatorCoreBlock;

public class AccumulatorCoreBlockEntity extends BlockEntity {

    private boolean isComplete = false;
    private int timer = 0; // Cooldown tracker

    public AccumulatorCoreBlockEntity(BlockPos pPos, BlockState pState) {
        super(ModBlockEntities.ACCUMULATOR_CORE_BE.get(), pPos, pState);
    }

    // This method is called 20 times per second by the block's ticker helper
    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        this.timer++;
        if (timer >= 20) { // Check once every 20 ticks (1 second)
            validateStructure(pLevel, pPos, pState);
            timer = 0; // Reset countdown
        }
    }

    public void validateStructure(Level world, BlockPos controllerPos, BlockState currentState) {

        // 1. Explicitly check all 9 relative positions
        boolean isValid =
                        // Row 1 (z = -1)
                        checkBlock(world, controllerPos.offset(-1, -1, -1), Blocks.POLISHED_ANDESITE) &&
                        checkBlock(world, controllerPos.offset(0, -1, -1),  Blocks.DIORITE)    &&
                        checkBlock(world, controllerPos.offset(1, -1, -1),  Blocks.POLISHED_ANDESITE) &&

                        // Row 2 (z = 0)
                        checkBlock(world, controllerPos.offset(-1, -1, 0),  Blocks.DIORITE)    &&
                        checkBlock(world, controllerPos.offset(0, -1, 0),   Blocks.BLACKSTONE) &&
                        checkBlock(world, controllerPos.offset(1, -1, 0),   Blocks.DIORITE)    &&

                        // Row 3 (z = +1)
                        checkBlock(world, controllerPos.offset(-1, -1, 1),  Blocks.POLISHED_ANDESITE) &&
                        checkBlock(world, controllerPos.offset(0, -1, 1),   Blocks.DIORITE)    &&
                        checkBlock(world, controllerPos.offset(1, -1, 1),   Blocks.POLISHED_ANDESITE);

        this.isComplete = isValid;
        setChanged();

        // 2. Fetch current block state and figure out the target value (1 if valid, 0 if invalid)
        int targetStateValue = isValid ? 1 : 0;

        // 4. If the structure is confirmed complete, scan the nearby area for the altar
        if (targetStateValue == 1) {
            if (findCraftingAltar(world, controllerPos, 16)) {
                targetStateValue = 2; // Elevate state value to 2 if the altar is found in range
            }
        }

        // 3. Check if your custom state property is present and needs updating
        // (Assuming your IntegerProperty is named 'STATE' or whatever you set in your Block class)
        if (currentState.hasProperty(AccumulatorCoreBlock.STATE) && currentState.getValue(AccumulatorCoreBlock.STATE) != targetStateValue) {

            // Flag 2: Sends the change to clients. Flag 3 (2 | 1): Re-renders and updates neighbors.
            // Using 2 or 3 is crucial for updating the texture instantly!
            world.setBlock(controllerPos, currentState.setValue(AccumulatorCoreBlock.STATE, targetStateValue), 3);
        }
    }

    /**
     * Scans a cubic radius centered around the controller block for the target custom block.
     * Uses BlockPos.betweenClosed for memory-friendly lookups.
     */
    private boolean findCraftingAltar(Level world, BlockPos controllerPos, int radius) {
        Iterable<BlockPos> scanArea = BlockPos.betweenClosed(
                controllerPos.offset(-radius, -radius, -radius),
                controllerPos.offset(radius, radius, radius)
        );

        for (BlockPos targetPos : scanArea) {
            // Replace 'ModBlocks.CRAFTING_ALTAR_T1.get()' with your actual block reference registry object/variable
            if (world.getBlockState(targetPos).is(ModBlocks.CRAFTING_ALTAR_T1.get())) {
                return true; // Match found, break search early
            }
        }
        return false; // Altar not found in range
    }

    private boolean checkBlock(Level world, BlockPos pos, Block expectedBlock) {
        return world.getBlockState(pos).is(expectedBlock);
    }

    public boolean isComplete() {
        return this.isComplete;
    }

}