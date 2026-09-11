package net.prismalmidu.arcaneempowerment.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.prismalmidu.arcaneempowerment.ArcaneEmpowerment;

public class CraftingAltarT1Screen extends AbstractContainerScreen<CraftingAltarT1Menu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(ArcaneEmpowerment.MOD_ID, "textures/gui/crafting_altar_t1.png");
    public CraftingAltarT1Screen(CraftingAltarT1Menu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) /2;

        pGuiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, mouseX, mouseY, delta);
        renderTooltip(pGuiGraphics, mouseX, mouseY);
    }


    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);

        // Call the clean getters instead of raw data array requests
        int currentEnergy = this.menu.getClientEnergy();
        int maxEnergy = this.menu.getClientMaxEnergy();

        String energyString = currentEnergy + " / " + maxEnergy + " Mana";

        // Renders the string cleanly inside the GUI layout boundary box
        guiGraphics.drawString(this.font, energyString, 90, 20, 0x000000, false);

        // NEW: Fetch and render the active core tracking count from the menu
        int activeCores = this.menu.getActiveCoresCount();
        String coresString = "Active Cores: " + activeCores + " / 8";

        // Rendered slightly above the energy bar text line (y=45) using a dark gray hex color code
        guiGraphics.drawString(this.font, coresString, 75, 72, 0x000000, false);
    }
}
