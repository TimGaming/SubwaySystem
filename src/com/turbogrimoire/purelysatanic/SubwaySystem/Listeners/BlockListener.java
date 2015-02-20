package com.turbogrimoire.purelysatanic.SubwaySystem.Listeners;

import java.util.Collection;

import com.turbogrimoire.purelysatanic.SubwaySystem.Handlers.MessageHandler;
import com.turbogrimoire.purelysatanic.SubwaySystem.Handlers.PlayerHandler;
import com.turbogrimoire.purelysatanic.SubwaySystem.Handlers.StationHandler;
import com.turbogrimoire.purelysatanic.SubwaySystem.Station;
import com.turbogrimoire.purelysatanic.SubwaySystem.SubwayPlayer;
import com.turbogrimoire.purelysatanic.SubwaySystem.SubwaySystemPlugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

public class BlockListener
  implements Listener
{
  private final SubwaySystemPlugin plugin;
  
  public BlockListener(SubwaySystemPlugin plugin)
  {
    this.plugin = plugin;
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void onBlockRedstoneChange(BlockRedstoneEvent e)
  {
    if (e.getBlock().getType().equals(Material.DETECTOR_RAIL))
    {
      Block[] blocks = new Block[4];
      
      blocks[0] = e.getBlock().getRelative(1, -1, 0);
      blocks[1] = e.getBlock().getRelative(-1, -1, 0);
      blocks[2] = e.getBlock().getRelative(0, -1, 1);
      blocks[3] = e.getBlock().getRelative(0, -1, -1);
      for (Block block : blocks) {
        if (block.getType().equals(Material.WALL_SIGN))
        {
          Sign sign = (Sign)block.getState();
          if ((sign.getLine(0).equalsIgnoreCase("[SS:Send]")) && (sign.getLine(1).equalsIgnoreCase("Send")))
          {
            Player player = findPlayerAbove(e.getBlock());
            if (player != null)
            {
              SubwayPlayer subwayPlayer = PlayerHandler.GetPlayer(player.getName());
              if (subwayPlayer != null)
              {
                Station dest = subwayPlayer.getDestination();
                teleportMinecart(subwayPlayer, player, dest);
                break;
              }
              MessageHandler.SendErrorMessage(player, "You do not have a destination. Rerouting.", false);
              teleportMinecart(subwayPlayer, player, StationHandler.GetNearestStation(player.getLocation()));
              break;
            }
          }
        }
      }
    }
  }
  
  private Player findPlayerAbove(Block block)
  {
    Location location = block.getLocation();
    
    Collection<? extends Player> players = this.plugin.getServer().getOnlinePlayers();
    for (Player player : players) {
      if (((player.getVehicle() instanceof Minecart)) && (location.distance(player.getLocation()) < 5.0D)) {
        return player;
      }
    }
    return null;
  }
  
  private boolean teleportMinecart(SubwayPlayer subwayPlayer, Player player, Station subway)
  {
    if ((player != null) && (subway != null))
    {
      Location track = subway.getReceiving();
      track = track.getWorld().getHighestBlockAt(track).getLocation().add(0.0D, 1.0D, 0.0D);
      
      track.getWorld().loadChunk(track.getBlockX(), track.getBlockZ());
      
      Vehicle vehicle = (Vehicle)player.getVehicle();
      player.leaveVehicle();
      vehicle.remove();
      player.teleport(track);
      if (subwayPlayer != null) {
        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, subwayPlayer, 1L);
      } else {
        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, 
          new SubwayPlayer(player, subway), 1L);
      }
      PlayerHandler.RemovePlayer(subwayPlayer);
      return true;
    }
    return false;
  }
}
