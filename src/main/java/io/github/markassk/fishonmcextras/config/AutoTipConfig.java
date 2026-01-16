package io.github.markassk.fishonmcextras.config;

import me.shedaniel.autoconfig.annotation.ConfigEntry;

public class AutoTipConfig {
    public static class AutoTip {
        @ConfigEntry.Gui.Tooltip
        public boolean autoTipReactions = false;
        public int reactionTipAmount = 1000;
        /* Disabled due to Staff disallowing automatic /tipall usage for non Admirals 
        public boolean autoTipall = false;
        */
    }
}
