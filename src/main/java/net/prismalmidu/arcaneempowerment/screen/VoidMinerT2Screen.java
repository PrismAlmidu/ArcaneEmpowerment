package net.prismalmidu.arcaneempowerment.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.prismalmidu.arcaneempowerment.ArcaneEmpowerment;
import net.prismalmidu.arcaneempowerment.block.entity.VoidMinerT2BlockEntity;

public class VoidMinerT2Screen extends AbstractContainerScreen<VoidMinerT2Menu> {
    // Points directly to your image asset layout location
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(ArcaneEmpowerment.MOD_ID, "textures/gui/void_miner_t2.png");

    public VoidMinerT2Screen(VoidMinerT2Menu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        // 🌟 FIX: Forces left alignment with a standard 8-pixel margin from the edge
        this.titleLabelX = 8;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Draw base generic screen window background panel
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // Render progress arrow if actively updating
        renderProgressArrow(guiGraphics, x, y);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if (menu.getScaledProgress() > 0) {
            // Drawing coordinates mapping inside your custom texture sheet
            // Parameters: Texture, screenX, screenY, sourceImageTextureX, sourceImageTextureY, width, height
            guiGraphics.blit(TEXTURE, x + 102, y + 37, 176, 0, menu.getScaledProgress(), 14);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY); // Shows hover information labels
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // 1. Draw the block title automatically at the top center
        super.renderLabels(guiGraphics, mouseX, mouseY);

        // 2. Fetch structural modifier counts from your menu data channels
        int speed = menu.getSpeedMods();
        int eff = menu.getEfficiencyMods();
        int prod = menu.getProductionMods();
        int energy = menu.getEnergy();
        int maxEnergy = menu.getMaxEnergy();

        // 3. Replicate the balancing math to find the net modifier change
        int penaltyCost = (speed * 8) + (prod * 12);
        int savingsCost = (eff * 3);
        int netChange = penaltyCost - savingsCost;

        // 4. Format strings dynamically based on the balance outcome
        String speedText = String.format("Speed Boost: +%d%% (%d)", speed * 10, speed);
        String prodText = String.format("Double Output: %d%% (%d)", (int)(prod * 15), prod);
        String energyText = String.format("%,d / %,d FE", energy, maxEnergy);

        String effText;
        int effTextColor;

        if (netChange > 0) {
            // Costs outweigh savings -> Energy Penalty! (Using a warning red/orange color: 0xFF5555)
            effText = String.format("Energy Penalty: +%d FE (%d)", netChange, eff);
            effTextColor = 0xFF5555;
        } else if (netChange < 0) {
            // Savings outweigh costs -> Energy Discount! (Using a positive cyan color: 0x55FFFF)
            effText = String.format("Energy Discount: -%d FE (%d)", Math.abs(netChange), eff);
            effTextColor = 0x55FFFF;
        } else {
            // Net change is exactly 0 -> Neutral (Using default dark grey: 0x404040)
            effText = String.format("Energy Discount: 0 FE (%d)", eff);
            effTextColor = 0x404040;
        }

        // 5. Draw text lines smoothly onto your interface layout card sheet
        int drawX = 8;

        // Draw Speed Line
        guiGraphics.drawString(this.font, speedText, drawX, 36, 0x404040, false);

        // Draw Dynamic Efficiency/Penalty Line (Changes text and color automatically!)
        guiGraphics.drawString(this.font, effText, drawX, 47, effTextColor, false);

        // Draw Double Production Line
        guiGraphics.drawString(this.font, prodText, drawX, 58, 0x404040, false);

        // Draw Right-Aligned Energy Line
        int energyX = this.imageWidth - this.font.width(energyText) - 8;
        guiGraphics.drawString(this.font, energyText, 88, 72, 0x404040, false);
    }
}