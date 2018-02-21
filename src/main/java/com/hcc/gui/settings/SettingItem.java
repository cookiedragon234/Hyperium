/*
 *     Hypixel Community Client, Client optimized for Hypixel Network
 *     Copyright (C) 2018  HCC Dev Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.hcc.gui.settings;

import com.hcc.utils.HCCFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import java.awt.*;
import java.util.function.Consumer;

public class SettingItem extends GuiButton {
    private static final HCCFontRenderer fontRenderer =  new HCCFontRenderer("Arial", Font.PLAIN, 12);
    private int hoverColor = new Color(0, 0, 0, 30).getRGB();
    private int color = new Color(0, 0, 0, 0).getRGB();
    private int textColor = new Color(255, 255, 255, 255).getRGB();
    private int textHoverColor = new Color(255, 255, 255, 255).getRGB();
    private String displayString;
    private Consumer<Integer> callback;

    public SettingItem(int id, int width, String displayString, Consumer<Integer> callback) {
        super(id, 0, 0, width, 15, displayString);
        this.displayString = displayString;
        this.callback = callback;
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        boolean pressed = super.mousePressed(mc, mouseX, mouseY);
        if(pressed)
            callback.accept(id);
        return pressed;
    }

    public void drawItem(Minecraft mc, int mouseX, int mouseY, int x, int y) {
        if (this.visible) {
            this.hovered = mouseX >= x && mouseY >= y && mouseX < this.xPosition + this.width && mouseY < y + this.height;
            this.mouseDragged(mc, mouseX, mouseY);

            // TODO RECT COLORS
            if (this.hovered) {
                drawRect(x, y,
                        x + this.width, y + this.height,
                        hoverColor);
            } else {
                drawRect(x, y,
                        x + this.width, y + this.height,
                        color);
            }
            int j = textColor;

            if (!this.enabled) {
                j = 10526880;
            } else if (this.hovered) {
                j = textHoverColor;
            }
            fontRenderer.drawString(this.displayString, x + 4, y + (this.height - 8) / 2, j);
            fontRenderer.drawString(">", x + width - 6, y + (this.height - 8) / 2, j);
        }

    }
}