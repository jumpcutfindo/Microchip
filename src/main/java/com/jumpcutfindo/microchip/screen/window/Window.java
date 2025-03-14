package com.jumpcutfindo.microchip.screen.window;

import com.jumpcutfindo.microchip.screen.Interactable;
import com.jumpcutfindo.microchip.screen.MicrochipScreen;
import com.jumpcutfindo.microchip.screen.ScreenUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import java.util.List;

public abstract class Window implements Interactable {
    protected MicrochipScreen screen;
    protected int x, y;
    protected int width, height;
    protected Text title;
    protected int titleX, titleY;
    public Window(MicrochipScreen screen, Text title, int width, int height, int x, int y) {
        this.screen = screen;
        this.title = title;

        this.titleX = 7;
        this.titleY = 9;

        this.width = width;
        this.height = height;

        this.x = x;
        this.y = y;
    }

    public void render(DrawContext context, int mouseX, int mouseY) {
        this.renderBackgroundGradient(context);
        this.renderBackground(context);
        this.renderContent(context, mouseX, mouseY);
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public abstract void renderBackground(DrawContext context);
    public abstract void renderContent(DrawContext context, int mouseX, int mouseY);

    public abstract void tick();

    public abstract boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount);

    public abstract boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY);

    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (!ScreenUtils.isWithin(mouseX, mouseY, x, y, width, height)) {
            screen.setActiveWindow(null);
            if (screen.isStandalone()) screen.close();
            return true;
        }
        return false;
    }

    public abstract boolean keyPressed(int keyCode, int scanCode, int modifiers);

    public abstract boolean charTyped(char chr, int modifiers);

    public abstract List<ClickableWidget> getWidgets();

    private void renderBackgroundGradient(DrawContext context) {
        screen.drawBackgroundGradient(context);
    }
}
