package io.wifi.cmdshout;

import java.util.Iterator;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scoreboard.Team;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class ChatListener implements Listener {
    Logger LOGGER = Logger.getLogger("cmdshout");

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        // 获取玩家和消息内容
        Player player = event.getPlayer();
        String msg = event.getMessage();
        Team team = player.getScoreboard().getEntryTeam(player.getName());
        String playerTeam = "#";
        if (team != null) {
            playerTeam = team.getName();
        }
        int chatType = 0;
        try {
            chatType = player.getScoreboard().getObjective(App.SCORE_NAME).getScore(playerTeam).getScore();
        } catch (Exception e) {
            chatType = 0;
        }

        if (chatType == 0 || chatType == 4) {
            event.setCancelled(false);
            return;
        } else {
            if (chatType == 3) {
                event.setCancelled(true);
                BaseComponent[] message = new ComponentBuilder("You cannot speak right now. \n" + //
                        "Try command instead: /sshout <Messages>").color(ChatColor.RED)
                        .create();
                player.spigot().sendMessage(message);
            } else if (chatType == 2 || chatType == 6) {
                event.setCancelled(true);

                LOGGER.info(
                        "[TEAM_ONLY] " + player.getDisplayName() + ": " + msg);
                sendMessageToOwnTeam(msg, player, playerTeam);
            } else if (chatType == 1 || chatType == 5) {
                event.setCancelled(true);
                if (team == null) {
                    sendMessageToOtherTeam(msg, player, playerTeam, "NORMAL");
                    LOGGER.info("[IN_TEAM_ONLY] " + player.getDisplayName() + ": "
                            + msg);
                } else {
                    Team team1 = (Team) team;
                    sendMessageToOtherTeam(msg, player, playerTeam, team1.getDisplayName());
                    LOGGER.info("[OTHER_TEAM_ONLY] " + player.getDisplayName() + ": "
                            + msg);

                }

            } else if (chatType == 7) {
                event.setCancelled(true);
                BaseComponent[] message = new ComponentBuilder("You cannot speak right now.").color(ChatColor.RED)
                        .create();
                player.spigot().sendMessage(message);
            }
            return;
        }
    }

    private void sendMessageToOwnTeam(String msg, Player player, String playerTeam) {
        BaseComponent[] message = new ComponentBuilder("[TEAM] ").color(ChatColor.GRAY)
                .append(player.getDisplayName() + ": ").color(ChatColor.GRAY).append(msg)
                .color(ChatColor.WHITE)
                .create();

        for (Player p : player.getServer().getOnlinePlayers()) {
            Team scoreTeam = p.getScoreboard().getEntryTeam(p.getName());
            String teamName = "#";
            if (scoreTeam != null)
                teamName = scoreTeam.getName();
            if (teamName == playerTeam) {
                p.spigot().sendMessage(message);
            }
        }
    }

    private void sendMessageToOtherTeam(String msg, Player player, String playerTeam, String teamDisplay) {
        BaseComponent[] message = new ComponentBuilder("[").color(ChatColor.GRAY).append(teamDisplay)
                .color(ChatColor.WHITE).append("] " + player.getDisplayName() + ": ").color(ChatColor.GRAY).append(msg)
                .color(ChatColor.WHITE)
                .create();

        // Iterator<? extends Player> it =
        // player.getServer().getOnlinePlayers().iterator();
        // sender.getServer().getOnlinePlayers().forEach((p) -> {
        // p.spigot().sendMessage(message);
        player.spigot().sendMessage(message);
        player.spigot().sendMessage(new ComponentBuilder("This message can't be viewed by your teammates!")
                .color(ChatColor.GRAY).italic(true)
                .create());
        for (Player p : player.getServer().getOnlinePlayers()) {
            Team scoreTeam = p.getScoreboard().getEntryTeam(p.getName());
            String teamName = "#";
            if (scoreTeam != null)
                teamName = scoreTeam.getName();
            if (teamName != playerTeam) {
                p.spigot().sendMessage(message);
            }
        }
    }
}