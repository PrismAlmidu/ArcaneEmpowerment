package net.prismalmidu.arcaneempowerment.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.prismalmidu.arcaneempowerment.ArcaneEmpowerment;
import net.prismalmidu.arcaneempowerment.capability.PlayerPerks;
import net.prismalmidu.arcaneempowerment.capability.PlayerPerksProvider;
import net.prismalmidu.arcaneempowerment.networking.C2SRespecPacket;
import net.prismalmidu.arcaneempowerment.networking.C2SUnlockPerkPacket;
import net.prismalmidu.arcaneempowerment.networking.ModMessages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerkShrineScreen extends AbstractContainerScreen<PerkShrineMenu> {
    private static final ResourceLocation FRAME_TEXTURE = new ResourceLocation(ArcaneEmpowerment.MOD_ID, "textures/gui/perk_frame.png");
    private static final ResourceLocation GRID_BACKGROUND = new ResourceLocation(ArcaneEmpowerment.MOD_ID, "textures/gui/perk_grid_tile.png");

    // Camera Translation states
    private double scrollX = 0;
    private double scrollY = 0;
    private float zoomScale = 1.0f;
    private boolean isDragging = false;

    // Define the boundaries of the black box inside your frame texture where rendering belongs
    private static final int INNER_X = 6;
    private static final int INNER_Y = 17;
    private static final int INNER_WIDTH = 238;
    private static final int INNER_HEIGHT = 227;

    public PerkShrineScreen(PerkShrineMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 256;
        this.imageHeight = 256;

        // FIX 1: Shifting default labels out of bounds so they don't block your screen layout
        this.titleLabelX = 12;
        this.titleLabelY = 6;
        this.inventoryLabelY = -999; // Sends "Inventory" text completely off the visible screen
    }

    private Button respecButton;

    @Override
    protected void init() {
        super.init();
        // Places a 60x20 pixel button at the top left corner of the silver frame wrapper
        this.respecButton = Button.builder(Component.literal("Respec"), button -> {
                    ModMessages.sendToServer(new C2SRespecPacket());
                })
                .bounds(this.leftPos + 13, this.topPos + 23, 60, 20)
                .build();

        // Registers the widget component into the screen rendering hierarchy
        this.addRenderableWidget(this.respecButton);
    }

    private static final int CELL_SIZE = 12; // Adjust this to change how far apart nodes are spaced

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        guiGraphics.enableScissor(x + INNER_X, y + INNER_Y, x + INNER_X + INNER_WIDTH, y + INNER_Y + INNER_HEIGHT);
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        poseStack.translate(x + INNER_X + (INNER_WIDTH / 2.0f), y + INNER_Y + (INNER_HEIGHT / 2.0f), 0);
        poseStack.scale(zoomScale, zoomScale, 1.0f);
        poseStack.translate(scrollX, scrollY, 0);

        // 1. FIRST PASS: Draw clean orthogonal grid lines between all parents and children
        for (PerkNode node : PERK_NODES) {
            // Loop through every parent ID registered to this node
            for (String parentId : node.parentIds()) {
                PerkNode parent = findNodeById(parentId);
                if (parent != null) {
                    int startX = parent.getCanvasX(CELL_SIZE);
                    int startY = parent.getCanvasY(CELL_SIZE);
                    int endX = node.getCanvasX(CELL_SIZE);
                    int endY = node.getCanvasY(CELL_SIZE);

                    int lineThickness = 1;

                    // Step A: Draw Horizontal segment
                    int xMin = Math.min(startX, endX) - lineThickness;
                    int xMax = Math.max(startX, endX) + lineThickness;
                    guiGraphics.fill(xMin, startY - lineThickness, xMax, startY + lineThickness, 0xFF808080);

                    // Step B: Draw Vertical segment
                    int yMin = Math.min(startY, endY) - lineThickness;
                    int yMax = Math.max(startY, endY) + lineThickness;
                    guiGraphics.fill(endX - lineThickness, yMin, endX + lineThickness, yMax, 0xFF808080);
                }
            }
        }

        // 2. SECOND PASS: Draw the actual clickable nodes on top of the lines
        for (PerkNode node : PERK_NODES) {
            int nodeX = node.getCanvasX(CELL_SIZE);
            int nodeY = node.getCanvasY(CELL_SIZE);

            int nodeColor;

            if (isNodePurchased(node.id())) {
                nodeColor = 0xFF00FF00; // Green if owned / unlocked
            } else {
                // Check if the node is available to purchase (No parents, or at least one purchased parent)
                boolean isAvailable = node.parentIds().isEmpty();
                for (String parentId : node.parentIds()) {
                    if (isNodePurchased(parentId)) {
                        isAvailable = true;
                        break;
                    }
                }

                // Gold/Yellow if ready to buy, Red if completely gated behind prerequisites
                nodeColor = isAvailable ? 0xFFDFB914 : 0xFFFF0000;
            }

            // Draw a neat 8x8 square box for the node emblem
            guiGraphics.fill(nodeX - 4, nodeY - 4, nodeX + 4, nodeY + 4, nodeColor);
        }

        poseStack.popPose();
        guiGraphics.disableScissor();

        // Render your silver window frame asset over top
        RenderSystem.setShaderTexture(0, FRAME_TEXTURE);
        guiGraphics.blit(FRAME_TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 1. Render the background dim overlay and the base screen components
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Grab the point total directly out of client capability memory cache
        int currentPoints = this.minecraft.player.getCapability(PlayerPerksProvider.PLAYER_PERKS)
                .map(PlayerPerks::getPerkPoints).orElse(0);

        int totalClaimed = this.minecraft.player.getCapability(PlayerPerksProvider.PLAYER_PERKS)
                .map(PlayerPerks::getTotalPointsClaimed).orElse(0);

        // Render text onto screen (Gold when points are available, gray when empty)
        int textColor = currentPoints > 0 ? 0xFFDFB914 : 0xFF888888;
        guiGraphics.drawString(this.font, "Perk Points: " + currentPoints, this.leftPos + 12, this.topPos + 238, textColor);
        guiGraphics.drawString(this.font, "Total Unlocked: " + totalClaimed + "/64", this.leftPos + 140, this.topPos + 238, 0xFF888888);

        // =========================================================================
        // NEW: DRAW ACTIVE EFFECTS SIDEBAR PANE
        // =========================================================================
        // Establish coordinates anchored 8 pixels right off the primary frame wall border
        int sidebarX = this.leftPos + this.imageWidth + 8;
        int sidebarY = this.topPos + 17;
        int sidebarWidth = 140;
        int sidebarHeight = 227;

        // 1. Draw a dark semi-translucent slate container box for the background panel layout
        guiGraphics.fill(sidebarX, sidebarY, sidebarX + sidebarWidth, sidebarY + sidebarHeight, 0xD0101010);
        // Draw a neat gold/gray perimeter dividing border frame line stroke accent layout
        guiGraphics.fill(sidebarX - 1, sidebarY, sidebarX, sidebarY + sidebarHeight, 0xFF5A5A5A);

        // 2. Render a localized panel text title section header component
        guiGraphics.drawString(this.font, "Active Bonuses", sidebarX + 8, sidebarY + 8, 0xFFDFB914);
        guiGraphics.fill(sidebarX + 8, sidebarY + 19, sidebarX + sidebarWidth - 8, sidebarY + 20, 0xFF5A5A5A);

        // 3. Loop through and print out the compiled numeric modifications
        Map<String, Double> activeStats = calculateStackedEffects();
        int currentTextRowY = sidebarY + 26;

        if (activeStats.isEmpty()) {
            guiGraphics.drawString(this.font, "No buffs active.", sidebarX + 8, currentTextRowY, 0xFF888888);
        } else {
            for (Map.Entry<String, Double> statEntry : activeStats.entrySet()) {
                // Prevent data text rendering from bleeding past the bottom height clipping box bounds
                if (currentTextRowY > sidebarY + sidebarHeight - 14) break;

                String statLabel = statEntry.getKey();
                double totalValue = statEntry.getValue();

                String displayedText;
                if (totalValue == 0.0) {
                    displayedText = "§7• " + statLabel; // Non-numeric features format wrapper
                } else {
                    // If it's a fractional modifier value, format as percentage, otherwise show clean int lines
                    String valueString = (totalValue == (int) totalValue) ? String.valueOf((int) totalValue) : String.format("%.1f", totalValue);
                    String prefix = totalValue > 0 ? "+" : "";

                    displayedText = "§a" + prefix + valueString + "§7 " + statLabel;
                }

                // Draw line record metrics out onto screen panel
                guiGraphics.drawString(this.font, displayedText, sidebarX + 8, currentTextRowY, 0xFFFFFFFF);
                currentTextRowY += 12; // Advance row layout pointer down by line text margins spacing height
            }
        }
        // =========================================================================


        this.renderTooltip(guiGraphics, mouseX, mouseY);

        // 2. Calculate the canvas pivot point matching your renderBg settings
        double centerX = this.leftPos + INNER_X + (INNER_WIDTH / 2.0f);
        double centerY = this.topPos + INNER_Y + (INNER_HEIGHT / 2.0f);

        // 3. Reverse the zoom and drag translations to turn the raw mouse coordinates into canvas coordinates
        double localCanvasX = ((mouseX - centerX) / zoomScale) - scrollX;
        double localCanvasY = ((mouseY - centerY) / zoomScale) - scrollY;

        // 4. Loop through the nodes to check if the mouse is sitting within a node's bounding box
        for (PerkNode node : PERK_NODES) {
            int nX = node.getCanvasX(CELL_SIZE);
            int nY = node.getCanvasY(CELL_SIZE);

            // Using a slightly wider detection box (6 pixels out from center) to make hovering comfortable for the player
            if (localCanvasX >= nX - 6 && localCanvasX <= nX + 6 &&
                    localCanvasY >= nY - 6 && localCanvasY <= nY + 6) {

                net.minecraft.network.chat.MutableComponent statusText;
                if (isNodePurchased(node.id())) {
                    statusText = Component.literal("Unlocked & Active").withStyle(net.minecraft.ChatFormatting.GREEN);
                } else {
                    // Check if the node is at least available via OR logic
                    boolean isAvailable = node.parentIds().isEmpty();
                    for (String parentId : node.parentIds()) {
                        if (isNodePurchased(parentId)) {
                            isAvailable = true;
                            break;
                        }
                    }

                    if (isAvailable) {
                        statusText = Component.literal("Available to Purchase").withStyle(net.minecraft.ChatFormatting.YELLOW);
                    } else {
                        statusText = Component.literal("Locked (Prerequisite Required)").withStyle(net.minecraft.ChatFormatting.RED);
                    }
                }

                // 5. Build a list containing each line of your tooltip text layout
                List<Component> tooltipLines = List.of(
                        node.name().copy().withStyle(net.minecraft.ChatFormatting.GOLD, net.minecraft.ChatFormatting.BOLD),
                        statusText,
                        Component.empty(), // Acts as a clean blank spacer line
                        node.desc().copy().withStyle(net.minecraft.ChatFormatting.GRAY)
                );

                // 6. Draw the structured tooltip box directly underneath the user's cursor pointer
                guiGraphics.renderTooltip(this.font, tooltipLines, java.util.Optional.empty(), mouseX, mouseY);

                // Break out of the loop immediately so we don't accidentally check or overlay multiple tooltips at once
                break;
            }
        }
    }

    // --- Input Listener Handlers for Mouse Controls ---

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (pButton == 0) { // Left Click
            // 1. Calculate screen center pivot adjustments
            double centerX = this.leftPos + INNER_X + (INNER_WIDTH / 2.0f);
            double centerY = this.topPos + INNER_Y + (INNER_HEIGHT / 2.0f);

            // 2. Reverse translation math to find exactly where the mouse clicked on the infinite moving grid
            double localCanvasX = ((pMouseX - centerX) / zoomScale) - scrollX;
            double localCanvasY = ((pMouseY - centerY) / zoomScale) - scrollY;

            // 3. Loop through your defined nodes to see if the click landed directly on a node circle
            for (PerkNode node : PERK_NODES) {
                int nX = node.getCanvasX(CELL_SIZE);
                int nY = node.getCanvasY(CELL_SIZE);

                // Check if click bounding box hits within a 6-pixel radius of the node center point
                if (localCanvasX >= nX - 6 && localCanvasX <= nX + 6 &&
                        localCanvasY >= nY - 6 && localCanvasY <= nY + 6) {

                    // Trigger the dependency calculation check and exit early
                    tryPurchasePerk(node);
                    return true;
                }
            }
        }

        // 4. Try widgets (like your Respec Button). We call children components directly
        // so the container screen's default background layer doesn't steal the input loop
        if (this.getChildAt(pMouseX, pMouseY).map(listener -> listener.mouseClicked(pMouseX, pMouseY, pButton)).orElse(false)) {
            return true;
        }

        // 5. Fallback: If they clicked completely empty space on your grid view, activate camera dragging!
        if (pButton == 0) {
            this.isDragging = true;
            return true;
        }

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    private void tryPurchasePerk(PerkNode node) {
        if (isNodePurchased(node.id())) return;

        // If the node has listed prerequisites, evaluate them
        if (!node.parentIds().isEmpty()) {
            boolean hasAnyValidParent = false;
            //boolean hasStarterExemption = false;

            for (String parentId : node.parentIds()) {
                // Apply your logical OR check (||)
                if (isNodePurchased(parentId)) {
                    hasAnyValidParent = true;
                    break; // One match is enough! Stop checking.
                }
            }

            // If the player lacks a purchased parent AND doesn't qualify for the starter bypass, block them
            if (!hasAnyValidParent) {
                if (this.minecraft.player != null) {
                    this.minecraft.player.displayClientMessage(Component.literal("§cYou must unlock a previous connected perk first!"), true);
                }
                return;
            }
        }

        // Send unlock packet to server
        BlockPos bePos = this.menu.getBlockEntity().getBlockPos();
        ModMessages.sendToServer(new C2SUnlockPerkPacket(node.id(), bePos));
    }

    // Quick lookup loop to find a node object by its String ID string
    private PerkNode findNodeById(String id) {
        return PERK_NODES.stream().filter(n -> n.id().equals(id)).findFirst().orElse(null);
    }

    // Update this: Screen nodes only draw green if the player spent currency to buy them!
    private boolean isNodePurchased(String id) {
        if (this.minecraft.player != null) {
            return this.minecraft.player.getCapability(PlayerPerksProvider.PLAYER_PERKS)
                    .map(perks -> perks.hasPerk(id))
                    .orElse(false);
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (pButton == 0) {
            this.isDragging = false;
        }
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (this.isDragging) {
            this.scrollX += pDragX / zoomScale;
            this.scrollY += pDragY / zoomScale;
            return true;
        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (pDelta > 0) {
            this.zoomScale = Math.min(2.0f, this.zoomScale + 0.1f);
        } else if (pDelta < 0) {
            this.zoomScale = Math.max(0.5f, this.zoomScale - 0.1f);
        }
        return true;
    }

    public record PerkNode(
            String id,
            int gridX,
            int gridY,
            List<String> parentIds, // <-- Changed from String to List<String>
            Component name,
            Component desc
    ) {
        public int getCanvasX(int cellSize) { return this.gridX * cellSize; }
        public int getCanvasY(int cellSize) { return this.gridY * cellSize; }
    }



    private final List<PerkNode> PERK_NODES = List.of(
            // ---- START NODES (No parent required) ----
            new PerkNode("ranged_start", 0, -5, List.of(), Component.literal("Ranged Mastery"), Component.literal("Arrow Damage +10%")),
            new PerkNode("melee_start", -5, 0, List.of(), Component.literal("Melee Mastery"), Component.literal("Attack Damage +10%")),
            new PerkNode("mining_start", 0, 5, List.of(), Component.literal("Mining Mastery"), Component.literal("Experience Gained +10%")),
            new PerkNode("magic_start", 5, 0, List.of(), Component.literal("Magic Mastery"), Component.literal("Spell Damage + 10%")),

            // ---- MAGIC BRANCH (Example mapping from your spreadsheet) ----
            new PerkNode("a2", 9, 1, List.of("magic_start"), Component.literal("A2: Placeholder"), Component.literal("Spell Efficiency +5%")),
            new PerkNode("a3", 9, -1, List.of("magic_start"), Component.literal("A3: Placeholder"), Component.literal("Cast Speed +5%")),
            new PerkNode("a4", 11, 2, List.of("a2", "a6"), Component.literal("A4: Placeholder"), Component.literal("Spell Efficiency +5%")),
            new PerkNode("a5", 11, -2, List.of("a3", "a7"), Component.literal("A5: Placeholder"), Component.literal("Cast Speed +5%")),
            new PerkNode("a6", 13, 1, List.of("a4", "a8", "a9", "a10"), Component.literal("A6: Placeholder"), Component.literal("Spell Efficiency +5%")),
            new PerkNode("a7", 13, -1, List.of("a5", "a8", "a9", "a11"), Component.literal("A7: Placeholder"), Component.literal("Cast Speed +5%")),
            new PerkNode("a8", 11, 0, List.of("a6", "a7"), Component.literal("A8: Placeholder"), Component.literal("Mana Regen +10%")),
            new PerkNode("a9", 14, 0, List.of("a6", "a7", "a10", "a11"), Component.literal("A9: Placeholder"), Component.literal("Mana Regen +10%")),
            new PerkNode("a10", 14, 2, List.of("a6", "a9", "a12"), Component.literal("A10: Placeholder"), Component.literal("Spell Efficiency +5%")),
            new PerkNode("a11", 14, -2, List.of("a7", "a9", "a13"), Component.literal("A11: Placeholder"), Component.literal("Cast Speed +5%")),
            new PerkNode("a12", 15, 3, List.of("a10", "a14"), Component.literal("A12: Placeholder"), Component.literal("Spell Efficiency +5%")),
            new PerkNode("a13", 15, -3, List.of("a11", "a15"), Component.literal("A13: Placeholder"), Component.literal("Cast Speed +5%")),
            new PerkNode("a14", 17, 3, List.of("a12", "a16", "a18"), Component.literal("A14: Placeholder"), Component.literal("Spell Damage +5%")),
            new PerkNode("a15", 17, -3, List.of("a13", "a17", "a19"), Component.literal("A15: Placeholder"), Component.literal("Spell Damage +5%")),
            new PerkNode("a16", 18, 2, List.of("a14", "a18", "a23"), Component.literal("A16: Max Health Minor"), Component.literal("Max Health +4")),
            new PerkNode("a17", 18, -2, List.of("a15", "a19", "a23"), Component.literal("A17: Max Health Minor"), Component.literal("Max Health +4")),
            new PerkNode("a18", 19, 3, List.of("a14", "a16", "a20"), Component.literal("A18: Armor Toughness Minor"), Component.literal("Armor Toughness +2")),
            new PerkNode("a19", 19, -3, List.of("a15", "a17", "a21"), Component.literal("A19: Armor Toughness Minor"), Component.literal("Armor Toughness +2")),
            new PerkNode("a20", 20, 2, List.of("a18", "a24", "a25"), Component.literal("A20: Placeholder"), Component.literal("Spell Efficiency +5%")),
            new PerkNode("a21", 20, -2, List.of("a19", "a24", "a27"), Component.literal("A21: Placeholder"), Component.literal("Cast Speed +5%")),
            new PerkNode("a22", 16, 0, List.of("a9", "a23"), Component.literal("A22: Placeholder"), Component.literal("Max Mana +100")),
            new PerkNode("a23", 18, 0, List.of("a16", "a17", "a22", "a24"), Component.literal("A23: Max Health Major"), Component.literal("Max Health +12")),
            new PerkNode("a24", 21, 0, List.of("a20", "a21", "a23"), Component.literal("A24: Placeholder"), Component.literal("Max Mana +100")),
            new PerkNode("a25", 22, 2, List.of("a20", "a24"), Component.literal("A25: Placeholder"), Component.literal("Spell Damage +5%")),
            new PerkNode("a26", 23, 0, List.of("a24"), Component.literal("A26: Placeholder"), Component.literal("Spell Efficiency +10%")),
            new PerkNode("a27", 22, -2, List.of("a21", "a24"), Component.literal("A27: Placeholder"), Component.literal("Cast Speed +10%")),
            new PerkNode("a28", 24, 4, List.of("a25"), Component.literal("A28: Placeholder"), Component.literal("Spell Damage +10%")),
            new PerkNode("a29", 25, 0, List.of("a26"), Component.literal("A29: Placeholder"), Component.literal("Spell Damage +10%")),
            new PerkNode("a30", 24, -4, List.of("a27"), Component.literal("A30: Placeholder"), Component.literal("Spell Damage +10%")),

            // ---- RANGED BRANCH (Example mapping from your spreadsheet) ----
            new PerkNode("b2", 1, -9, List.of("ranged_start"), Component.literal("B2: Placeholder"), Component.literal("Draw Speed +5%")),
            new PerkNode("b3", -1, -9, List.of("ranged_start"), Component.literal("B3: Placeholder"), Component.literal("Arrow Velocity +5%")),
            new PerkNode("b4", 2, -11, List.of("b2", "b6"), Component.literal("B4: Placeholder"), Component.literal("Draw Speed +5%")),
            new PerkNode("b5", -2, -11, List.of("b3", "b7"), Component.literal("B5: Placeholder"), Component.literal("Arrow Velocity +5%")),
            new PerkNode("b6", 1, -13, List.of("b4", "b8", "b9", "b10"), Component.literal("B6: Placeholder"), Component.literal("Draw Speed +5%")),
            new PerkNode("b7", -1, -13, List.of("b5", "b8", "b9", "b11"), Component.literal("B7: Placeholder"), Component.literal("Arrow Velocity +5%")),
            new PerkNode("b8", 0, -11, List.of("b6", "b7"), Component.literal("B8: Placeholder"), Component.literal("Mana Regen +10%")),
            new PerkNode("b9", 0, -14, List.of("b6", "b7", "b10", "b11"), Component.literal("B9: Placeholder"), Component.literal("Mana Regen +10%")),
            new PerkNode("b10", 2, -14, List.of("b6", "b9", "b12"), Component.literal("B10: Placeholder"), Component.literal("Draw Speed +5%")),
            new PerkNode("b11", -2, -14, List.of("b7", "b9", "b13"), Component.literal("B11: Placeholder"), Component.literal("Arrow Velocity +5%")),
            new PerkNode("b12", 3, -15, List.of("b10", "b14"), Component.literal("B12: Placeholder"), Component.literal("Draw Speed +5%")),
            new PerkNode("b13", -3, -15, List.of("b11", "b15"), Component.literal("B13: Placeholder"), Component.literal("Arrow Velocity +5%")),
            new PerkNode("b14", 3, -17, List.of("b12", "b16", "b18"), Component.literal("B14: Placeholder"), Component.literal("Arrow Damage +5%")),
            new PerkNode("b15", -3, -17, List.of("b13", "b17", "b19"), Component.literal("B15: Placeholder"), Component.literal("Arrow Damage +5%")),
            new PerkNode("b16", 2, -18, List.of("b14", "b18", "b23"), Component.literal("B16: Max Health Minor"), Component.literal("Max Health +4")),
            new PerkNode("b17", -2, -18, List.of("b15", "b19", "b23"), Component.literal("B17: Max Health Minor"), Component.literal("Max Health +4")),
            new PerkNode("b18", 3, -19, List.of("b14", "b16", "b20"), Component.literal("B18: Armor Toughness Minor"), Component.literal("Armor Toughness +2")),
            new PerkNode("b19", -3, -19, List.of("b15", "b17", "b21"), Component.literal("B19: Armor Toughness Minor"), Component.literal("Armor Toughness +2")),
            new PerkNode("b20", 2, -20, List.of("b18", "b24", "b25"), Component.literal("B20: Placeholder"), Component.literal("Draw Speed +5%")),
            new PerkNode("b21", -2, -20, List.of("b19", "b24", "b27"), Component.literal("B21: Placeholder"), Component.literal("Arrow Velocity +5%")),
            new PerkNode("b22", 0, -16, List.of("b9", "b23"), Component.literal("B22: Placeholder"), Component.literal("Max Mana +100")),
            new PerkNode("b23", 0, -18, List.of("b16", "b17", "b22", "b24"), Component.literal("B23: Max Health Major"), Component.literal("Max Health +12")),
            new PerkNode("b24", 0, -21, List.of("b20", "b21", "b23"), Component.literal("B24: Placeholder"), Component.literal("Max Mana +100")),
            new PerkNode("b25", 2, -22, List.of("b20", "b24"), Component.literal("B25: Placeholder"), Component.literal("Draw Speed +10%")),
            new PerkNode("b26", 0, -23, List.of("b24"), Component.literal("B26: Placeholder"), Component.literal("Arrow Damage +5%")),
            new PerkNode("b27", -2, -22, List.of("b21", "b24"), Component.literal("B27: Placeholder"), Component.literal("Arrow Velocity +10%")),
            new PerkNode("b28", 4, -24, List.of("b25"), Component.literal("B28: Placeholder"), Component.literal("Arrow Damage +10%")),
            new PerkNode("b29", 0, -25, List.of("b26"), Component.literal("B29: Placeholder"), Component.literal("Arrow Damage +10%")),
            new PerkNode("b30", -4, -24, List.of("b27"), Component.literal("B30: Placeholder"), Component.literal("Arrow Damage +10%")),

            // ---- MELEE BRANCH (Example mapping from your spreadsheet) ----
            new PerkNode("c2", -9, -1, List.of("melee_start"), Component.literal("C2: Armor Plus Minor"), Component.literal("Armor +1")),
            new PerkNode("c3", -9, 1, List.of("melee_start"), Component.literal("C3: Attack Speed Minor"), Component.literal("Attack Speed +5%")),
            new PerkNode("c4", -11, -2, List.of("c2", "c6"), Component.literal("C4: Armor Plus Minor"), Component.literal("Armor +1")),
            new PerkNode("c5", -11, 2, List.of("c3", "c7"), Component.literal("C5: Attack Speed Minor"), Component.literal("Attack Speed +5%")),
            new PerkNode("c6", -13, -1, List.of("c4", "c8", "c9", "c10"), Component.literal("C6: Armor Plus Minor"), Component.literal("Armor +1")),
            new PerkNode("c7", -13, 1, List.of("c5", "c8", "c9", "c11"), Component.literal("C7: Attack Speed Minor"), Component.literal("Attack Speed +5%")),
            new PerkNode("c8", -11, 0, List.of("c6", "c7"), Component.literal("C8: Placeholder"), Component.literal("Mana Regen +10%")),
            new PerkNode("c9", -14, 0, List.of("c6", "c7", "c10", "c11"), Component.literal("C9: Placeholder"), Component.literal("Mana Regen +10%")),
            new PerkNode("c10", -14, -2, List.of("c6", "c9", "c12"), Component.literal("C10: Armor Plus Minor"), Component.literal("Armor +1")),
            new PerkNode("c11", -14, 2, List.of("c7", "c9", "c13"), Component.literal("C11: Attack Speed Minor"), Component.literal("Attack Speed +5%")),
            new PerkNode("c12", -15, -3, List.of("c10", "c14"), Component.literal("C12: Armor Plus Minor"), Component.literal("Armor +1")),
            new PerkNode("c13", -15, 3, List.of("c11", "c15"), Component.literal("C13: Attack Speed Minor"), Component.literal("Attack Speed +5%")),
            new PerkNode("c14", -17, -3, List.of("c12", "c16", "c18"), Component.literal("C14: Attack Damage Minor"), Component.literal("Attack Damage +5%")),
            new PerkNode("c15", -17, 3, List.of("c13", "c17", "c19"), Component.literal("C15: Attack Damage Minor"), Component.literal("Attack Damage +5%")),
            new PerkNode("c16", -18, -2, List.of("c14", "c18", "c23"), Component.literal("C16: Max Health Minor"), Component.literal("Max Health +4")),
            new PerkNode("c17", -18, 2, List.of("c15", "c19", "c23"), Component.literal("C17: Max Health Minor"), Component.literal("Max Health +4")),
            new PerkNode("c18", -19, -3, List.of("c14", "c16", "c20"), Component.literal("C18: Armor Toughness Minor"), Component.literal("Armor Toughness +2")),
            new PerkNode("c19", -19, 3, List.of("c15", "c17", "c21"), Component.literal("C19: Armor Toughness Minor"), Component.literal("Armor Toughness +2")),
            new PerkNode("c20", -20, -2, List.of("c18", "c24", "c25"), Component.literal("C20: Armor Plus Minor"), Component.literal("Armor +1")),
            new PerkNode("c21", -20, 2, List.of("c19", "c24", "c27"), Component.literal("C21: Attack Speed Minor"), Component.literal("Attack Speed +5%")),
            new PerkNode("c22", -16, 0, List.of("c9", "c23"), Component.literal("C22: Placeholder"), Component.literal("Max Mana +100")),
            new PerkNode("c23", -18, 0, List.of("c16", "c17", "c22", "c24"), Component.literal("C23: Max Health Major"), Component.literal("Max Health +12")),
            new PerkNode("c24", -21, 0, List.of("c20", "c21", "c23"), Component.literal("C24: Placeholder"), Component.literal("Max Mana +100")),
            new PerkNode("c25", -22, -2, List.of("c20", "c24"), Component.literal("C25: Armor Plus Major"), Component.literal("Armor +2")),
            new PerkNode("c26", -23, 0, List.of("c24"), Component.literal("C26: Attack Damage Minor"), Component.literal("Attack Damage +5%")),
            new PerkNode("c27", -22, 2, List.of("c21", "c24"), Component.literal("C27: Attack Speed Major"), Component.literal("Attack Speed +10%")),
            new PerkNode("c28", -24, -4, List.of("c25"), Component.literal("C28: Attack Damage Major"), Component.literal("Attack Damage +10%")),
            new PerkNode("c29", -25, 0, List.of("c26"), Component.literal("C29: Attack Damage Major"), Component.literal("Attack Damage +10%")),
            new PerkNode("c30", -24, 4, List.of("c27"), Component.literal("C30: Attack Damage Major"), Component.literal("Attack Damage +10%")),

            // ---- MINING BRANCH (Example mapping from your spreadsheet) ----
            new PerkNode("d2", -1, 9, List.of("mining_start"), Component.literal("D2: Underwater Mining Minor"), Component.literal("Submerged Mining Speed +0.1")),
            new PerkNode("d3", 1, 9, List.of("mining_start"), Component.literal("D3: Fall Distance Minor"), Component.literal("Safe Fall Distance +1")),
            new PerkNode("d4", -2, 11, List.of("d2", "d6"), Component.literal("D4: Underwater Mining Minor"), Component.literal("Submerged Mining Speed +0.1")),
            new PerkNode("d5", 2, 11, List.of("d3", "d7"), Component.literal("D5: Fall Distance Minor"), Component.literal("Safe Fall Distance +1")),
            new PerkNode("d6", -1, 13, List.of("d4", "d8", "d9", "d10"), Component.literal("D6: Underwater Mining Minor"), Component.literal("Submerged Mining Speed +0.1")),
            new PerkNode("d7", 1, 13, List.of("d5", "d8", "d9", "d11"), Component.literal("D7: Fall Distance Minor"), Component.literal("Safe Fall Distance +1")),
            new PerkNode("d8", 0, 11, List.of("d6", "d7"), Component.literal("D8: Placeholder"), Component.literal("Mana Regen +10%")),
            new PerkNode("d9", 0, 14, List.of("d6", "d7", "d10", "d11"), Component.literal("D9: Placeholder"), Component.literal("Mana Regen +10%")),
            new PerkNode("d10", -2, 14, List.of("d6", "d9", "d12"), Component.literal("D10: Underwater Mining Minor"), Component.literal("Submerged Mining Speed +0.1")),
            new PerkNode("d11", 2, 14, List.of("d7", "d9", "d13"), Component.literal("D11: Fall Distance Minor"), Component.literal("Safe Fall Distance +1")),
            new PerkNode("d12", -3, 15, List.of("d10", "d14"), Component.literal("D12: Underwater Mining Minor"), Component.literal("Submerged Mining Speed +0.1")),
            new PerkNode("d13", 3, 15, List.of("d11", "d15"), Component.literal("D13: Fall Distance Minor"), Component.literal("Safe Fall Distance +1")),
            new PerkNode("d14", -3, 17, List.of("d12", "d16", "d18"), Component.literal("D14: EXP Up Minor"), Component.literal("Experience Gained +5%")),
            new PerkNode("d15", 3, 17, List.of("d13", "d17", "d19"), Component.literal("D15: EXP Up Minor"), Component.literal("Experience Gained +5%")),
            new PerkNode("d16", -2, 18, List.of("d14", "d18", "d23"), Component.literal("D16: Max Health Minor"), Component.literal("Max Health +4")),
            new PerkNode("d17", 2, 18, List.of("d15", "d19", "d23"), Component.literal("D17: Max Health Minor"), Component.literal("Max Health +4")),
            new PerkNode("d18", -3, 19, List.of("d14", "d16", "d20"), Component.literal("D18: Armor Toughness Minor"), Component.literal("Armor Toughness +2")),
            new PerkNode("d19", 3, 19, List.of("d15", "d17", "d21"), Component.literal("D19: Armor Toughness Minor"), Component.literal("Armor Toughness +2")),
            new PerkNode("d20", -2, 20, List.of("d18", "d24", "d25"), Component.literal("D20: Underwater Mining Minor"), Component.literal("Submerged Mining Speed +0.1")),
            new PerkNode("d21", 2, 20, List.of("d19", "d24", "d27"), Component.literal("D21: Fall Distance Minor"), Component.literal("Safe Fall Distance +1")),
            new PerkNode("d22", 0, 16, List.of("d9", "d23"), Component.literal("D22: Placeholder"), Component.literal("Max Mana +100")),
            new PerkNode("d23", 0, 18, List.of("d16", "d17", "d22", "d24"), Component.literal("D23: Max Health Major"), Component.literal("Max Health +12")),
            new PerkNode("d24", 0, 21, List.of("d20", "d21", "d23"), Component.literal("D24: Placeholder"), Component.literal("Max Mana +100")),
            new PerkNode("d25", -2, 22, List.of("d20", "d24"), Component.literal("D25: Underwater Mining Major"), Component.literal("Submerged Mining Speed +0.2")),
            new PerkNode("d26", 0, 23, List.of("d24"), Component.literal("D26: EXP Up Minor"), Component.literal("Experience Gained +5%")),
            new PerkNode("d27", 2, 22, List.of("d21", "d24"), Component.literal("D27: Fall Distance Major"), Component.literal("Safe Fall Distance +2")),
            new PerkNode("d28", -4, 24, List.of("d25"), Component.literal("D28: EXP Up Major"), Component.literal("Experience Gained +10%")),
            new PerkNode("d29", 0, 25, List.of("d26"), Component.literal("D28: EXP Up Major"), Component.literal("Experience Gained +10%")),
            new PerkNode("d30", 4, 24, List.of("d27"), Component.literal("D28: EXP Up Major"), Component.literal("Experience Gained +10%")),

            //MAGIC/RANGED BRIDGE
            new PerkNode("e1", 13, -4, List.of("a11", "a13", "e2"), Component.literal("E1: Placeholder"), Component.literal("Max Mana +100")),
            new PerkNode("e2", 11, -6, List.of("e1", "e2"), Component.literal("E2: Placeholder"), Component.literal("Max Mana +100")),
            new PerkNode("e3", 9, -6, List.of("e2", "e4"), Component.literal("E3: Placeholder"), Component.literal("Crit Damage +25%")),
            new PerkNode("e4", 7, -4, List.of("e3", "e5"), Component.literal("E4: Placeholder"), Component.literal("Life Steal +1%")),
            new PerkNode("e5", 5, -3, List.of("e4", "e6"), Component.literal("E5: Placeholder"), Component.literal("Life Steal +1%")),
            new PerkNode("e6", 3, -5, List.of("e5", "e7"), Component.literal("E6: Placeholder"), Component.literal("Crit Chance +2%")),
            new PerkNode("e7", 4, -7, List.of("e6", "e8"), Component.literal("E7: Placeholder"), Component.literal("Crit Chance +2%")),
            new PerkNode("e8", 6, -9, List.of("e7", "e9"), Component.literal("E8: Placeholder"), Component.literal("Crit Damage +25%")),
            new PerkNode("e9", 6, -11, List.of("e8", "e10"), Component.literal("E9: Placeholder"), Component.literal("Crit Chance +2%")),
            new PerkNode("e10", 4, -13, List.of("e9", "b10", "b12"), Component.literal("E10: Placeholder"), Component.literal("Crit Chance +2%")),
            new PerkNode("e11", 6, -6, List.of("e5", "e6"), Component.literal("E11: Placeholder"), Component.literal("Crit Damage +10%")),
            new PerkNode("e12", 8, -8, List.of("e11"), Component.literal("E12: Placeholder"), Component.literal("Cold Damage +5")),

            //RANGED/MELEE BRIDGE
            new PerkNode("f1", -4, -13, List.of("b11", "b13", "f2"), Component.literal("F1: Placeholder"), Component.literal("Crit Chance +2%")),
            new PerkNode("f2", -6, -11, List.of("f1", "f2"), Component.literal("F2: Placeholder"), Component.literal("Crit Chance +2%")),
            new PerkNode("f3", -6, -9, List.of("f2", "f4"), Component.literal("F3: Placeholder"), Component.literal("Life Steal 5%")),
            new PerkNode("f4", -4, -7, List.of("f3", "f5"), Component.literal("F4: Placeholder"), Component.literal("Max Mana +100")),
            new PerkNode("f5", -3, -5, List.of("f4", "f6"), Component.literal("F5: Placeholder"), Component.literal("Max Mana +100")),
            new PerkNode("f6", -5, -3, List.of("f5", "f7"), Component.literal("F6: Solid Footing"), Component.literal("Knockback Resistance +0.2")),
            new PerkNode("f7", -7, -4, List.of("f6", "f8"), Component.literal("F7: Solid Footing"), Component.literal("Knockback Resistance +0.2")),
            new PerkNode("f8", -9, -6, List.of("f7", "f9"), Component.literal("F8: Placeholder"), Component.literal("Life Steal 5%")),
            new PerkNode("f9", -11, -6, List.of("f8", "f10"), Component.literal("F9: Placeholder"), Component.literal("Life Steal +1%")),
            new PerkNode("f10", -13, -4, List.of("f9", "c10", "c12"), Component.literal("F10: Placeholder"), Component.literal("Life Steal +1%")),
            new PerkNode("f11", -6, -6, List.of("f5", "f6"), Component.literal("F11: Placeholder"), Component.literal("Life Steal +1%")),
            new PerkNode("f12", -8, -8, List.of("f11"), Component.literal("F12: Placeholder"), Component.literal("Fire Damage +5")),

            //MELEE/MINING BRIDGE
            new PerkNode("g1", -13, 4, List.of("c11", "c13", "g2"), Component.literal("G1: Placeholder"), Component.literal("Life Steal +1%")),
            new PerkNode("g2", -11, 6, List.of("g1", "g2"), Component.literal("G2: Placeholder"), Component.literal("Life Steal +1%")),
            new PerkNode("g3", -9, 6, List.of("g2", "g4"), Component.literal("G3: Heavy Cleave Major"), Component.literal("Sweeping Damage Ratio +0.2")),
            new PerkNode("g4", -7, 4, List.of("g3", "g5"), Component.literal("G4: Solid Footing"), Component.literal("Knockback Resistance +0.2")),
            new PerkNode("g5", -5, 3, List.of("g4", "g6"), Component.literal("G5: Solid Footing"), Component.literal("Knockback Resistance +0.2")),
            new PerkNode("g6", -3, 5, List.of("g5", "g7"), Component.literal("G6: Placeholder"), Component.literal("Crit Chance +2%")),
            new PerkNode("g7", -4, 7, List.of("g6", "g8"), Component.literal("G7: Placeholder"), Component.literal("Crit Chance +2%")),
            new PerkNode("g8", -6, 9, List.of("g7", "g9"), Component.literal("G8: Heavy Cleave Major"), Component.literal("Sweeping Damage Ratio +0.2")),
            new PerkNode("g9", -6, 11, List.of("g8", "g10"), Component.literal("G9: Blast Resist"), Component.literal("Explosion Resistance +0.2")),
            new PerkNode("g10", -4, 13, List.of("g9", "d10", "d12"), Component.literal("G10: Blast Resist"), Component.literal("Explosion Resistance +0.2")),
            new PerkNode("g11", -6, 6, List.of("g5", "g6"), Component.literal("G11: Heavy Cleave Minor"), Component.literal("Sweeping Damage Ratio +0.1")),
            new PerkNode("g12", -8, 8, List.of("g11"), Component.literal("G12: Extra Reach"), Component.literal("Block Interaction Range +1")),

            //MINING/MAGIC BRIDGE
            new PerkNode("h1", 4, 13, List.of("d11", "d13", "h2"), Component.literal("H1: Blast Resist"), Component.literal("Explosion Resistance +0.2")),
            new PerkNode("h2", 6, 11, List.of("h1", "h2"), Component.literal("H2: Blast Resist"), Component.literal("Explosion Resistance +0.2")),
            new PerkNode("h3", 6, 9, List.of("h2", "h4"), Component.literal("H3: Placeholder"), Component.literal("Max Mana +200")),
            new PerkNode("h4", 4, 7, List.of("h3", "h5"), Component.literal("H4: Placeholder"), Component.literal("Crit Chance +2%")),
            new PerkNode("h5", 3, 5, List.of("h4", "h6"), Component.literal("H5: Placeholder"), Component.literal("Crit Chance +2%")),
            new PerkNode("h6", 5, 3, List.of("h5", "h7"), Component.literal("H6: Placeholder"), Component.literal("Max Mana +100")),
            new PerkNode("h7", 7, 4, List.of("h6", "h8"), Component.literal("H7: Placeholder"), Component.literal("Max Mana +100")),
            new PerkNode("h8", 9, 6, List.of("h7", "h9"), Component.literal("H8: Placeholder"), Component.literal("Max Mana +200")),
            new PerkNode("h9", 11, 6, List.of("h8", "h10"), Component.literal("H9: Placeholder"), Component.literal("Max Mana +100")),
            new PerkNode("h10", 13, 4, List.of("h9", "a10", "a12"), Component.literal("H10: Placeholder"), Component.literal("Max Mana +100")),
            new PerkNode("h11", 6, 6, List.of("h5", "h6"), Component.literal("H11: Placeholder"), Component.literal("Max Mana +100")),
            new PerkNode("h12", 8, 8, List.of("h11"), Component.literal("H12: Placeholder"), Component.literal("Max Mana Multiplier Final +20%")),

            //INNER WHEEL
            new PerkNode("i1", 3, -3, List.of("e5", "e6", "i2", "i16"), Component.literal("I1: Max Health Major"), Component.literal("Max Health +12")),
            new PerkNode("i2", 1, -3, List.of("i1", "i3", "i4"), Component.literal("I2: Max Health Minor"), Component.literal("Max Health +4")),
            new PerkNode("i3", 0, -2, List.of("i2", "i4", "i17"), Component.literal("I3: Max Health Minor"), Component.literal("Max Health +4")),
            new PerkNode("i4", -1, -3, List.of("i2", "i3", "i5"), Component.literal("I4: Armor Plus Minor"), Component.literal("Armor +1")),
            new PerkNode("i5", -3, -3, List.of("i4", "i6", "f5", "f6"), Component.literal("I5: Placeholder"), Component.literal("Max Mana +200")),
            new PerkNode("i6", -3, -1, List.of("i5", "i7", "i8"), Component.literal("I6: Placeholder"), Component.literal("Max Mana +100")),
            new PerkNode("i7", -2, 0, List.of("i6", "i8", "i17"), Component.literal("I7: Placeholder"), Component.literal("Max Mana +100")),
            new PerkNode("i8", -3, 1, List.of("i6", "i7", "i9"), Component.literal("I8: Armor Plus Minor"), Component.literal("Armor +1")),
            new PerkNode("i9", -3, 3, List.of("i8", "i10", "g5", "g6"), Component.literal("I9: Max Health Major"), Component.literal("Max Health +12")),
            new PerkNode("i10", -1, 3, List.of("i9", "i11", "i12"), Component.literal("I10: Max Health Minor"), Component.literal("Max Health +4")),
            new PerkNode("i11", 0, 2, List.of("i10", "i12", "i17"), Component.literal("I11: Max Health Minor"), Component.literal("Max Health +4")),
            new PerkNode("i12", 1, 3, List.of("i10", "i11", "i13"), Component.literal("I12: Armor Plus Minor"), Component.literal("Armor +1")),
            new PerkNode("i13", 3, 3, List.of("i12", "i14", "h5", "h6"), Component.literal("I13: Placeholder"), Component.literal("Max Mana +200")),
            new PerkNode("i14", 3, 1, List.of("i13", "i15", "i16"), Component.literal("I14: Placeholder"), Component.literal("Max Mana +100")),
            new PerkNode("i15", 2, 0, List.of("i14", "i16", "i17"), Component.literal("I15: Placeholder"), Component.literal("Max Mana +100")),
            new PerkNode("i16", 3, -1, List.of("i14", "i15", "i1"), Component.literal("I16: Armor Plus Minor"), Component.literal("Armor +1")),
            new PerkNode("i17", 0, 0, List.of("i3", "i7", "i11", "i15"), Component.literal("I17: Placeholder"), Component.literal("???"))

            // ... Continue mapping b, c, and d coordinates similarly from your chart


    );

    private Map<String, Double> calculateStackedEffects() {
        Map<String, Double> stackedStats = new HashMap<>();

        if (this.minecraft.player == null) return stackedStats;

        this.minecraft.player.getCapability(PlayerPerksProvider.PLAYER_PERKS).ifPresent(perks -> {
            for (PerkNode node : PERK_NODES) {
                if (perks.hasPerk(node.id())) {
                    // Read the description string text out of the node component
                    String descText = node.desc().getString();

                    // Parse out numerical configurations (e.g., "+5%", "+12", "+0.2")
                    java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("([+-]?\\d*\\.?\\d+)").matcher(descText);
                    if (matcher.find()) {
                        try {
                            double val = Double.parseDouble(matcher.group(1));
                            // Clean up text characters to isolate the attribute label key name
                            String cleanKey = descText.replaceAll("[+-]?\\d*\\.?\\d+%?", "").trim();

                            // Append and accumulate calculations onto the tracking hash list
                            stackedStats.put(cleanKey, stackedStats.getOrDefault(cleanKey, 0.0) + val);
                        } catch (NumberFormatException ignored) {}
                    } else {
                        // Fallback for custom nodes that contain no readable raw numeric fields
                        stackedStats.put(node.name().getString(), 0.0);
                    }
                }
            }
        });

        return stackedStats;
    }
}