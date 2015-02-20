package com.turbogrimoire.purelysatanic.SubwaySystem.Handlers;

import com.turbogrimoire.purelysatanic.SubwaySystem.SubwaySystemPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MessageHandler
{
  public static void SendMessage(CommandSender sender, String message)
  {
    if (sender != null) {
      sender.sendMessage(ChatColor.AQUA + message);
    }
  }
  
  public static void SendErrorMessage(CommandSender sender, String message, boolean toConsole)
  {
    if (sender != null) {
      sender.sendMessage(ChatColor.RED + "ERROR: " + message);
    }
    if (toConsole) {
      System.out.println(ChatColor.RED + "ERROR: " + message);
    }
  }
  
  public static void SendDebugMessage(CommandSender sender, String message, boolean toConsole)
  {
    if (sender != null) {
      sender.sendMessage(ChatColor.BLUE + "DEBUG: " + message);
    }
    if (toConsole) {
      System.out.println(ChatColor.BLUE + "DEBUG: " + message);
    }
  }
  
  public static void SendServerMessage(SubwaySystemPlugin plugin, String message)
  {
    plugin.getServer().broadcastMessage(
      ChatColor.DARK_PURPLE + "SERVER: " + message);
  }
}
