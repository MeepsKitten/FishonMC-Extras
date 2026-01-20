package io.github.markassk.fishonmcextras.handler;

import io.github.markassk.fishonmcextras.config.FishOnMCExtrasConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.text.Text;

public class TimerHandler {
    private static TimerHandler INSTANCE = new TimerHandler();
    private final FishOnMCExtrasConfig config = FishOnMCExtrasConfig.getConfig();

    public long baitShopOffset = 60 * 60 * 1000;
    public final long baitShopTotalTime = 60 * 60 * 1000 * 4; // 4h
    public long baitShopTimer = 0L;
    public long baitShopAlertTime = 0L;

    public final long contestTotalTime =  60 * 60 * 1000; // 1h
    public long contestTimer = 0L;

    public final long contestEndTotalTime =  60 * 60 * 1000; // 1h
    public long contestEndTimer = 0L;

    // Moon timer variables
    public long moonTimer = 0L;
    public final long moonCycleTime = 160L * 60L * 1000L; // 160 minutes in milliseconds
    public long moonOffset = calculateMoonOffset(); // Calculate once at initialization

    public static TimerHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new TimerHandler();
        }
        return INSTANCE;
    }

    public void tick() {
        long currentTime = System.currentTimeMillis();

        long baitShopTime = baitShopTotalTime;
        this.baitShopTimer = baitShopTime - ((currentTime + baitShopOffset + config.timerTracker.hiddenOffsetBaitShop) % baitShopTime);

        long contestOffset = 0L;
        long contestTime = contestTotalTime;
        this.contestTimer = contestTime - ((currentTime + contestOffset) % contestTime);

        long contestEndOffset = 60 * 30 * 1000L; // 30m
        long contestEndTime = contestEndTotalTime;
        this.contestEndTimer = contestEndTime - ((currentTime + contestEndOffset) % contestEndTime);

        // Calculate moon timer using EXACTLY the same approach as bait shop
        this.moonTimer = moonCycleTime - ((currentTime + moonOffset) % moonCycleTime);
    }

    public boolean onReceiveMessage(Text text) {
        if(text.getString().startsWith("TACKLE SHOP »") && text.getString().contains("Tackle items restocked!")) {
            baitShopAlertTime = System.currentTimeMillis();

            if(baitShopTimer >= 1000L && baitShopTimer <= 600000L) {
                config.timerTracker.hiddenOffsetBaitShop = baitShopTimer - 1000L;
                AutoConfig.getConfigHolder(FishOnMCExtrasConfig.class).save();
            }
        }
        return false;
    }

    private long calculateMoonOffset() {
        // Use UTC (not timezone-dependent, avoids DST issues)
        java.time.ZoneOffset utc = java.time.ZoneOffset.UTC;
        java.time.Instant now = java.time.Instant.now();
        java.time.ZonedDateTime utcNow = now.atZone(utc);
        
        // Get midnight today in UTC
        java.time.ZonedDateTime midnightUTC = utcNow.toLocalDate().atStartOfDay(utc);
        
        // First moon event of the day is at 01:03 UTC (per moon times chart)
        java.time.ZonedDateTime firstMoonToday = midnightUTC.plusHours(1).plusMinutes(3);
        
        // Convert to milliseconds
        long firstMoonMillis = firstMoonToday.toInstant().toEpochMilli();
        long currentMillis = System.currentTimeMillis();
        
        // Find the next moon event from the first one today
        long timeSinceFirst = currentMillis - firstMoonMillis;
        
        // If we're before the first event today, use it directly
        long nextMoonMillis;
        if (timeSinceFirst < 0) {
            nextMoonMillis = firstMoonMillis;
        } else {
            // Calculate how many cycles have passed and find the next one
            long cyclesElapsed = timeSinceFirst / moonCycleTime;
            long nextCycleMillis = firstMoonMillis + ((cyclesElapsed + 1) * moonCycleTime);
            nextMoonMillis = nextCycleMillis;
        }
        
        // Calculate offset so that: moonCycleTime - ((currentMillis + offset) % moonCycleTime) 
        // equals the time remaining until nextMoonMillis
        long timeUntilNext = nextMoonMillis - currentMillis;
        
        // We want: moonCycleTime - ((currentMillis + offset) % moonCycleTime) = timeUntilNext
        // Therefore: (currentMillis + offset) % moonCycleTime = moonCycleTime - timeUntilNext
        // So: offset = (moonCycleTime - timeUntilNext - currentMillis) % moonCycleTime
        
        long baseOffset = moonCycleTime - timeUntilNext - (currentMillis % moonCycleTime);
        
        // For the timers to tick in sync, we need:
        // moonOffset % 1000 == (baitShopOffset + hiddenOffsetBaitShop) % 1000
        // This ensures both timers cross second boundaries at the same time
        long targetMod = (baitShopOffset + config.timerTracker.hiddenOffsetBaitShop) % 1000L;
        long currentMod = baseOffset % 1000L;
        
        // Adjust to match the target modulo
        long adjustment = (targetMod - currentMod + 1000L) % 1000L;
        
        return baseOffset + adjustment;
    }
}