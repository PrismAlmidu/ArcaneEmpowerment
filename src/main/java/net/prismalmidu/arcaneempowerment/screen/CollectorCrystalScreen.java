package net.prismalmidu.arcaneempowerment.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.prismalmidu.arcaneempowerment.ArcaneEmpowerment;
import org.jetbrains.annotations.NotNull;

public class CollectorCrystalScreen extends AbstractContainerScreen<CollectorCrystalMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(ArcaneEmpowerment.MOD_ID, "textures/gui/collector_crystal_gui.png");

    public CollectorCrystalScreen(CollectorCrystalMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);

        this.imageWidth = 176;
        this.imageHeight = 222;
    }

    @Override
    protected void init() {
        super.init();

        this.imageWidth = 176;
        this.imageHeight = 222;

        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        // Draw the block name at the top
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, 6, 0x404040, false);

        // Draw the "Inventory" text right above your custom grid (Y=125 aligns with Y=136 grid)
        guiGraphics.drawString(this.font, this.playerInventoryTitle, 8, 130, 0x404040, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        // Blit the image using 256x256 texture map proportions to prevent stretching or color distortion
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);

        int amount = this.menu.getFluidAmount();
        int max = this.menu.getFluidCapacity();

        // Render the fluid tracking text centered inside that spacious upper panel area
        String fluidText = amount + " / " + max + " mB";
        int textX = this.leftPos + (this.imageWidth - this.font.width(fluidText)) / 2;
        int textY = this.topPos + 50;

        guiGraphics.drawString(this.font, fluidText, textX, textY, 0x404040, false);
    }
}