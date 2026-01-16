package io.github.markassk.fishonmcextras.handler;

import io.github.markassk.fishonmcextras.config.FishOnMCExtrasConfig;
import io.github.markassk.fishonmcextras.util.TextHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.text.Style;

public class AutoTippingHandler {
	private static AutoTippingHandler INSTANCE;
	private long timestamp = 0L;

	public static AutoTippingHandler instance() {
		if (INSTANCE == null) {
			INSTANCE = new AutoTippingHandler();
		}
		return INSTANCE;
	}

	public void onReceiveMessage(Text message) {
		if (!LoadingHandler.instance().isOnServer) {
			return;
		}

		String plain = message.getString();

		Pattern p = Pattern.compile("REACTIONS »\\s*([0-9A-Za-z_]{3,16})\\b");
		Matcher m = p.matcher(plain);

		if (m.find()) {
			String username = m.group(1);
			onReceiveReactions(username, message);
		}

		else {
			p = Pattern.compile("BOOSTER »[\\s\\S]*\\(\\/tipall\\)");
			m = p.matcher(plain);
			if (m.find()) {
				onReceiveBooster(message);
			}
		}
	}

	private void onReceiveReactions(String username, Text message) {
		MinecraftClient client = MinecraftClient.getInstance();
		client.execute(() -> {
			if (client.player == null) {
				return;
			}

			if (client.inGameHud == null) {
				return;
			}

			PlayerListEntry playerListEntry = client.getNetworkHandler() != null
					? client.getNetworkHandler().getPlayerListEntry(username)
					: null;

			if (playerListEntry == null) {
				return;
			}

			if (client.player.getName().getString().equals(username)) {
				return;
			}

			if (timestamp + 5000L > System.currentTimeMillis()) {
				return;
			}
			timestamp = System.currentTimeMillis();

			int amount = FishOnMCExtrasConfig.getConfig().autoTip.reactionTipAmount;
			boolean autoTip = FishOnMCExtrasConfig.getConfig().autoTip.autoTipReactions;
			String payCommand = "/pay " + username + " " + amount;

			if (!autoTip) {
				client.inGameHud.getChatHud().addMessage(TextHelper.concat(
						Text.literal("FoE ").formatted(Formatting.DARK_GREEN, Formatting.BOLD),
						Text.literal("| ").formatted(Formatting.DARK_GRAY),
						Text.literal("[Tip " + username + " $" + amount + "]")
								.setStyle(
										Style.EMPTY
												.withClickEvent(
														new ClickEvent(ClickEvent.Action.RUN_COMMAND, payCommand))
												.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
														Text.literal(payCommand))))));
				return;
			}

			if (client.player.networkHandler != null) {
				client.inGameHud.getChatHud().addMessage(TextHelper.concat(
						Text.literal("FoE ").formatted(Formatting.DARK_GREEN, Formatting.BOLD),
						Text.literal("| ").formatted(Formatting.DARK_GRAY),
						Text.literal("Tipping " + username + " $" + amount).formatted(Formatting.GREEN)));
				client.player.networkHandler.sendChatCommand("pay " + username + " " + amount);
			}
		});
	}


	private void onReceiveBooster(Text message) {
		MinecraftClient client = MinecraftClient.getInstance();
		client.execute(() -> {
			if (client.player == null) {
				return;
			}

			if (client.inGameHud == null) {
				return;
			}

			/* Disabled due to Staff disallowing automatic /tipall usage for non Admirals
			boolean autoTip = FishOnMCExtrasConfig.getConfig().autoTip.autoTipall;
			*/
			boolean autoTip = false;
			String payAllCommand = "/tipall";

			if (!autoTip) {
				client.inGameHud.getChatHud().addMessage(TextHelper.concat(
						Text.literal("FoE ").formatted(Formatting.DARK_GREEN, Formatting.BOLD),
						Text.literal("| ").formatted(Formatting.DARK_GRAY),
						Text.literal("[Tipall]")
								.setStyle(
										Style.EMPTY
												.withClickEvent(
														new ClickEvent(ClickEvent.Action.RUN_COMMAND, payAllCommand))
												.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
														Text.literal(payAllCommand))))));
				return;
			}
			
			/* Disabled due to Staff disallowing automatic /tipall usage for non Admirals
			if (client.player.networkHandler != null) {
				client.inGameHud.getChatHud().addMessage(TextHelper.concat(
						Text.literal("FoE ").formatted(Formatting.DARK_GREEN, Formatting.BOLD),
						Text.literal("| ").formatted(Formatting.DARK_GRAY),
						Text.literal("Tipping Boosters").formatted(Formatting.GREEN)));
				client.player.networkHandler.sendChatCommand("tipall");
			}
			*/
		});
	}
	
}
