package net.prismalmidu.arcaneempowerment.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.prismalmidu.arcaneempowerment.ArcaneEmpowerment;

public class BeaconT3Screen extends AbstractContainerScreen<BeaconT3Menu> {

    private TextScrollPanel scrollPanel;

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(ArcaneEmpowerment.MOD_ID, "textures/gui/beacon_t3_gui.png");

    public BeaconT3Screen(BeaconT3Menu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 222;
        this.imageWidth = 176;
    }

    @Override
    protected void init() {
        super.init();

        // Viewport boundaries where the text scrolling panel is confined
        int panelWidth = 150;
        int panelHeight = 80;
        int panelX = this.leftPos + 12;
        int panelY = this.topPos + 45;

        this.scrollPanel = new TextScrollPanel(this.minecraft, panelWidth, panelHeight, panelY, panelY + panelHeight, 12);
        this.scrollPanel.setLeftPos(panelX);

        // Register the panel to receive mouse wheel movements and drags
        this.addWidget(this.scrollPanel);

        // Center block title text relative to the GUI texture width
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    // Rebuilds the scrollable lines each tick based on updated container network variables
    private void refreshScrollableContent() {
        if (this.scrollPanel == null) return;
        this.scrollPanel.clearText();

        this.scrollPanel.addTextLine(Component.literal("Energy: " + this.menu.getEnergy() + " / 10000 FE"));
        this.scrollPanel.addTextLine(Component.literal("Drain Rate: " + this.menu.getDrainRate() + " FE/t"));
        this.scrollPanel.addTextLine(Component.literal("---------------------------"));
        this.scrollPanel.addTextLine(Component.literal("Active Buff Modifiers:"));

        // Push every single stat safely into the scroll feed (using the correct menu accessor methods)
        this.scrollPanel.addTextLine(Component.literal(" - Speed Amplification: " + this.menu.getSpeed()));
        this.scrollPanel.addTextLine(Component.literal(" - Strength Level: " + this.menu.getStrength()));
        this.scrollPanel.addTextLine(Component.literal(" - Resistance level: " + this.menu.getResistance()));
        this.scrollPanel.addTextLine(Component.literal(" - Regeneration level: " + this.menu.getRegeneration()));
        this.scrollPanel.addTextLine(Component.literal(" - Saturation level: " + this.menu.getSaturation()));
        this.scrollPanel.addTextLine(Component.literal(" - Haste level: " + this.menu.getHaste()));
        this.scrollPanel.addTextLine(Component.literal(" - Health Boost level: " + this.menu.getHealthBoost()));
        this.scrollPanel.addTextLine(Component.literal(" - Waterbreathing level: " + this.menu.getWaterBreathing()));
        this.scrollPanel.addTextLine(Component.literal(" - Dolphin's Grace level: " + this.menu.getDolphinsGrace()));
        this.scrollPanel.addTextLine(Component.literal(" - Luck level: " + this.menu.getLuck()));
        this.scrollPanel.addTextLine(Component.literal(" - Slow Falling level: " + this.menu.getSlowFalling()));
        this.scrollPanel.addTextLine(Component.literal(" - Night Vision level: " + this.menu.getNightVision()));
        this.scrollPanel.addTextLine(Component.literal(" - Jump Boost level: " + this.menu.getJumpBoost()));
        this.scrollPanel.addTextLine(Component.literal(" - Invisibility level: " + this.menu.getInvisibility()));
        this.scrollPanel.addTextLine(Component.literal(" - Fire Resistance level: " + this.menu.getFireResistance()));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 1. Re-populate live data metrics text strings
        this.refreshScrollableContent();

        // 2. Render standard background screen tint masking
        this.renderBackground(guiGraphics);

        // 3. Let Minecraft draw your container layout background (Calls renderBg & renderLabels)
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // 4. MANUAL CUSTOM RENDER: Limits drawing boundaries perfectly to your text box
        if (this.scrollPanel != null) {
            int panelX = this.leftPos + 12;
            int panelY = this.topPos + 45;
            int panelWidth = 150;
            int panelHeight = 80;

            // Enforce a strict clipping mask zone over your text panel
            guiGraphics.enableScissor(panelX, panelY, panelX + panelWidth, panelY + panelHeight);

            // Draw ONLY the list items, skipping the background code entirely
            this.scrollPanel.drawListOnly(guiGraphics, mouseX, mouseY, partialTick);

            guiGraphics.disableScissor();
        }

        // 5. Draw item slot hover tooltips over everything last
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Center Block Title text at top of layout window
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);

        // Player Inventory label pushed down safely right above the hotbar slots (Y=128 fits an imageHeight of 222)
        guiGraphics.drawString(this.font, this.playerInventoryTitle, 8, 128, 4210752, false);

        // REMOVED: Static modifier text fields have been entirely removed from here.
        // They are now handled dynamically inside the TextScrollPanel viewport context without clashing.
    }

    // Custom Scroll List Viewport widget definition handling mouse actions and scissored dimensions
    public class TextScrollPanel extends AbstractSelectionList<TextScrollPanel.TextEntry> {

        public TextScrollPanel(Minecraft minecraft, int width, int height, int top, int bottom, int itemHeight) {
            super(minecraft, width, height, top, bottom, itemHeight);
            this.setRenderBackground(false);
            this.setRenderHeader(false, 0);
        }

        public void drawListOnly(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            this.renderList(guiGraphics, mouseX, mouseY, partialTick);
        }

        public void addTextLine(Component text) {
            this.addEntry(new TextEntry(text));
        }

        public void clearText() {
            this.clearEntries();
        }

        @Override
        public int getRowWidth() {
            return this.width - 20;
        }

        @Override
        protected int getScrollbarPosition() {
            return this.x0 + this.width - 8;
        }

        // FIX: Implements the missing abstract narration method required by Minecraft 1.20+
        @Override
        public void updateNarration(net.minecraft.client.gui.narration.NarrationElementOutput narrationOutput) {
            // Leave empty if you don't need the system narrator to read out the scroll box text
        }

        // Object entry representing a single row rendered within scissored boundaries
        public class TextEntry extends Entry<TextEntry> {
            private final Component text;

            public TextEntry(Component text) {
                this.text = text;
            }

            @Override
            public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
                guiGraphics.drawString(Minecraft.getInstance().font, this.text, left, top + 2, 0x404040, false);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                return false;
            }
        }
    }
}
