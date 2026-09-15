package net.prismalmidu.arcaneempowerment.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.prismalmidu.arcaneempowerment.screen.EldrinGeneratorMenu;
import net.minecraft.world.entity.player.Inventory;

public class EldrinGeneratorScreen extends AbstractContainerScreen<EldrinGeneratorMenu> {

    // Points directly to your custom mod texture instead of vanilla files
    private static final ResourceLocation CUSTOM_TEXTURE =
            new ResourceLocation("arcaneempowerment", "textures/gui/eldrin_generator_gui.png");

    public EldrinGeneratorScreen(EldrinGeneratorMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        // Matching your custom Paint.NET graphical canvas layout window bounds
        this.imageWidth = 248;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // FIXED BLIT OVERLOAD: Specifies the exact source width/height and texture sheet dimensions
        // Parameters: (Texture, destX, destY, destWidth, destHeight, srcX, srcY, srcWidth, srcHeight, textureSheetWidth, textureSheetHeight)
        guiGraphics.blit(CUSTOM_TEXTURE, x, y, this.imageWidth, this.imageHeight, 0, 0, this.imageWidth, this.imageHeight, 248, 166);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Pushed text alignment vectors in by 3 pixels to perfectly adapt to the new boundary margins
        guiGraphics.drawString(this.font, this.title, 9, 15, 4210752, false);

        // Draw Resource metrics
        guiGraphics.drawString(this.font, "FE Storage: " + menu.getEnergy() + " / 50000 RF", 9, 35, 0x3f3f3f, false);
        guiGraphics.drawString(this.font, "Mana Fluid: " + menu.getFluid() + " mB / 4000 mB", 9, 49, 0x3f3f3f, false);

        // Draw Eldrin titles
        guiGraphics.drawString(this.font, "Active Eldrin Yields (per tick):", 9, 71, 0x6A0DAD, false);

        // Left Column positions (X = 23)
        guiGraphics.drawString(this.font, "Arcane: +" + menu.getAffinityGen(2), 9, 87, 0xAA00AA, false);
        guiGraphics.drawString(this.font, "Ender:  +" + menu.getAffinityGen(4), 9, 101, 0x4B0082, false);
        guiGraphics.drawString(this.font, "Water:  +" + menu.getAffinityGen(6), 9, 115, 0x00AAAA, false);

        // RIGHT COLUMN ADJUSTMENT FIX: Shifted from 139 down to 126 to pull the text away from the right border edge
        guiGraphics.drawString(this.font, "Earth:  +" + menu.getAffinityGen(3), 92, 87, 0x555500, false);
        guiGraphics.drawString(this.font, "Fire:   +" + menu.getAffinityGen(5), 92, 101, 0xFF5555, false);
        guiGraphics.drawString(this.font, "Wind:   +" + menu.getAffinityGen(7), 92, 115, 0xAAAAAA, false);
    }
}