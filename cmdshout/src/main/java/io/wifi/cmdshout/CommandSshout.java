package io.wifi.cmdshout;

import java.util.Iterator;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class CommandSshout implements CommandExecutor {
    Logger LOGGER = Logger.getLogger("cmdshout");

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 0) {
            BaseComponent[] message = new ComponentBuilder("Usage: /sshout <Messages>").color(ChatColor.RED).create();
            sender.spigot().sendMessage(message);
            return false;
        }
        String content = "";
        for (int i = 0; i < args.length; i++) {
            content += (content == "" ? "" : " ") + args[i];
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("io.wifi.cmd.sshout")) {
                sendChatMessageToAll(sender, content, true);
            } else {
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
                // (playerTeam, ModObj).getScore();

                if ((chatType & 4) != 0) {
                    // 禁止 shout
                    BaseComponent[] message = new ComponentBuilder("You cannot shout right now.").color(ChatColor.RED)
                            .create();
                    sender.spigot().sendMessage(message);
                    return false;
                }
                sendChatMessageToAll(player, content, false);
            }
        } else {
            sendChatMessageToAll(sender, content, true);
        }
        return true;
    }

    private void sendChatMessageToAll(CommandSender sender, String content, boolean op) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        BaseComponent[] message = null;
        if (player == null) {
            message = new ComponentBuilder("[SHOUT][OP] ").color(ChatColor.GOLD).append("CONSOLES")
                    .color(ChatColor.GREEN).append(": ").color(ChatColor.GRAY).append(content).color(ChatColor.WHITE)
                    .create();
            LOGGER.info("[SHOUT][CONSOLES]: " + content);

        } else {
            if (op) {
                message = new ComponentBuilder("[SHOUT][OP] ").color(ChatColor.GOLD).append(player.getDisplayName())
                        .color(ChatColor.GRAY).append(": ").color(ChatColor.GRAY).append(content).color(ChatColor.WHITE)
                        .create();
                LOGGER.info("[SHOUT][OP] " + player.getDisplayName() + ": " + content);
            } else {
                message = new ComponentBuilder("[SHOUT] ").color(ChatColor.GOLD).append(player.getDisplayName())
                        .color(ChatColor.GRAY).append(": ").color(ChatColor.GRAY).append(content).color(ChatColor.WHITE)
                        .create();
                LOGGER.info("[SHOUT] " + player.getDisplayName() + ": " + content);
            }

        }
        // sender.getServer()..broadcast(text, false);

        for (Player p : sender.getServer().getOnlinePlayers()) {
            p.spigot().sendMessage(message);
        }
    }

}