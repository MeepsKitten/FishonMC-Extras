package io.github.markassk.fishonmcextras.config;

import me.shedaniel.autoconfig.annotation.ConfigEntry;

public class FishEventsConfig {
    public static class Fishevents {

        @ConfigEntry.Gui.CollapsibleObject
        public RarityToggles rarityToggles = new RarityToggles();
        public static class RarityToggles {
            @ConfigEntry.Gui.Tooltip
            public boolean showSpecial = false;
        }

        @ConfigEntry.Gui.CollapsibleObject
        public VariantToggles variantToggles = new VariantToggles();
        public static class VariantToggles {
            @ConfigEntry.Gui.Tooltip
            public boolean showAlternate = false;
            @ConfigEntry.Gui.Tooltip
            public boolean showSpooky = false;
            @ConfigEntry.Gui.Tooltip
            public boolean showFrozen = true;
        }

        @ConfigEntry.Gui.CollapsibleObject
        public DryStreakToggles dryStreakToggles = new DryStreakToggles();
        public static class DryStreakToggles {
            public boolean showSpecial = true;
            public boolean showAlternate = true;
            public boolean showSpooky = true;
            public boolean showFrozen = true;
        }

    }
}