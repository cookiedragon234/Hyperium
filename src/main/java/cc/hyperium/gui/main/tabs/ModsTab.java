package cc.hyperium.gui.main.tabs;

import cc.hyperium.config.Category;
import cc.hyperium.config.SelectorSetting;
import cc.hyperium.config.SliderSetting;
import cc.hyperium.config.ToggleSetting;
import cc.hyperium.gui.GuiBlock;
import cc.hyperium.gui.Icons;
import cc.hyperium.gui.main.HyperiumMainGui;
import cc.hyperium.gui.main.HyperiumOverlay;
import cc.hyperium.gui.main.components.AbstractTab;
import cc.hyperium.gui.main.components.OverlaySelector;
import cc.hyperium.gui.main.components.OverlaySlider;
import cc.hyperium.gui.main.components.SettingItem;
import net.minecraft.client.gui.Gui;
import org.apache.commons.lang3.ArrayUtils;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModsTab extends AbstractTab {
    private final HyperiumOverlay autotip = new HyperiumOverlay("Autotip");
    private final HashMap<Field, Consumer<Object>> callback = new HashMap<>();
    private final HashMap<Field, Supplier<String[]>> customStates = new HashMap<>();
    private GuiBlock block;
    private int y, w;

    public ModsTab(int y, int w) {
        block = new GuiBlock(0, w, y, y + w);
        this.y = y;
        this.w = w;
        items.add(new SettingItem(() -> HyperiumMainGui.INSTANCE.setOverlay(autotip), Icons.SETTINGS.getResource(), "Autotip", "Autotip Settings \n /autotip", "Click to configure", 0, 0));
        try {
            for (Object o : HyperiumMainGui.INSTANCE.getSettingsObjects()) {
                for (Field f : o.getClass().getDeclaredFields()) {
                    ToggleSetting ts = f.getAnnotation(ToggleSetting.class);
                    SelectorSetting ss = f.getAnnotation(SelectorSetting.class);
                    SliderSetting sliderSetting = f.getAnnotation(SliderSetting.class);
                    if (ts == null && ss == null && sliderSetting == null)
                        continue;
                    Consumer<Object> objectConsumer = callback.get(f);
                    if (ts != null) {
                        if (ts.mods()) {
                            getCategory(ts.category()).addToggle(ts.name(), f, objectConsumer, ts.enabled(), o);
                        }

                    } else if (ss != null) {
                        if (!ss.mods())
                            continue;
                        try {
                            Supplier<String[]> supplier = customStates.get(f);
                            Supplier<String[]> supplier1 = supplier != null ? supplier : ss::items;
                            String current = String.valueOf(f.get(o));
                            if (!ArrayUtils.contains(supplier1.get(), current))
                                current = supplier1.get()[0];
                            getCategory(ss.category()).getComponents().add(new OverlaySelector<>(ss.name(), current, si -> {
                                if (objectConsumer != null)
                                    objectConsumer.accept(si);
                                try {
                                    f.set(null, si);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }, supplier1, ss.enabled()));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }

                    } else if (sliderSetting != null) {
                        if (sliderSetting.mods())
                            try {
                                Double value = Double.valueOf(f.get(o).toString());
                                getCategory(sliderSetting.category()).getComponents().add(new OverlaySlider(sliderSetting.name(), sliderSetting.min(), sliderSetting.max(),
                                        value.floatValue(), aFloat -> {
                                    if (objectConsumer != null)
                                        objectConsumer.accept(aFloat);
                                    try {
                                        f.set(null, aFloat);
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    }
                                }, sliderSetting.round(), sliderSetting.enabled()));

                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    private HyperiumOverlay getCategory(Category settingsCategory) {
        switch (settingsCategory) {
            case AUTOTIP:
                return autotip;
        }
        throw new IllegalArgumentException(settingsCategory + " Cannot be used in mods!");
    }

    @Override
    public void drawTabIcon() {
        Icons.FA_WRENCH.bind();
        Gui.drawScaledCustomSizeModalRect(5, y + 5, 0, 0, 144, 144, w - 10, w - 10, 144, 144);

    }

    @Override
    public GuiBlock getBlock() {
        return block;
    }

    @Override
    public void drawHighlight(float s) {
        Gui.drawRect(0, (int) (y + s * (s * w / 2)), 3, (int) (y + w - s * (w / 2)), Color.WHITE.getRGB());
    }

    @Override
    public String getTitle() {
        return "Mods";
    }

    @Override
    public void draw(int mouseX, int mouseY, int topX, int topY, int containerWidth, int containerHeight) {
        super.draw(mouseX, mouseY, topX, topY, containerWidth, containerHeight);
    }
}
