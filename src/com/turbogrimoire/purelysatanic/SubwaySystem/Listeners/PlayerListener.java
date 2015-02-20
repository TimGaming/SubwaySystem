package com.turbogrimoire.purelysatanic.SubwaySystem.Listeners;

import com.sk89q.worldedit.data.DataException;
import com.turbogrimoire.purelysatanic.SubwaySystem.Handlers.GenerationHandler;
import com.turbogrimoire.purelysatanic.SubwaySystem.Handlers.MessageHandler;
import com.turbogrimoire.purelysatanic.SubwaySystem.Handlers.PlayerHandler;
import com.turbogrimoire.purelysatanic.SubwaySystem.Handlers.StationHandler;
import com.turbogrimoire.purelysatanic.SubwaySystem.Runnables.SubwayGenerationRunnable;
import com.turbogrimoire.purelysatanic.SubwaySystem.Schedulers.SubwayGenerationScheduler;
import com.turbogrimoire.purelysatanic.SubwaySystem.Station;
import com.turbogrimoire.purelysatanic.SubwaySystem.SubwayPlayer;
import com.turbogrimoire.purelysatanic.SubwaySystem.SubwaySystemPlugin;
import com.turbogrimoire.purelysatanic.SubwaySystem.Util.Vector2;
import java.io.IOException;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerListener
  implements Listener
{
  private final SubwaySystemPlugin plugin;
  
  public PlayerListener(SubwaySystemPlugin plugin)
  {
    this.plugin = plugin;
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void onPlayerInteract(PlayerInteractEvent e)
  {
    if ((!e.isCancelled()) && (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)))
    {
      Player player = e.getPlayer();
      
      Block block = e.getClickedBlock();
      if (block.getType().equals(Material.POWERED_RAIL))
      {
        Player generator = GenerationHandler.GetPlayer();
        if ((generator != null) && (generator.getName().equalsIgnoreCase(player.getName()))) {
          if ((!GenerationHandler.IsActive()) && (!GenerationHandler.IsCustom()))
          {
            SubwayGenerationScheduler scheduler = new SubwayGenerationScheduler(this.plugin, GenerationHandler.GetSchematic(), GenerationHandler.GetLocation(), e.getClickedBlock().getLocation(), GenerationHandler.GetRadius());
            
            this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, scheduler, 0L, 20L);
            MessageHandler.SendMessage(e.getPlayer(), "Subway generation has started.");
            
            GenerationHandler.SetActive(true);
          }
          else if ((!GenerationHandler.IsActive()) && (GenerationHandler.IsCustom()))
          {
            try
            {
              SubwayGenerationRunnable scheduler = new SubwayGenerationRunnable(this.plugin, GenerationHandler.GetSchematic(), "Station - (" + GenerationHandler.GetName() + ")", GenerationHandler.GetPlayer().getWorld(), GenerationHandler.GetLocation(), e.getClickedBlock().getLocation(), new Vector2(0, 0), true);
              scheduler.run();
              MessageHandler.SendMessage(e.getPlayer(), "Subway generation complete.");
              StationHandler.SaveStations(this.plugin);
            }
            catch (IOException|DataException e1)
            {
              e1.printStackTrace();
            }
            GenerationHandler.ClearGeneration();
          }
        }
      }
      if (block.getType().equals(Material.STONE_BUTTON))
      {
        Station subway = StationHandler.GetStationByLocation(block.getLocation());
        if ((subway != null) && (subway.isCustom())) {
          subway = StationHandler.GetNearestStation(subway.getReceiving());
        }
        Sign sign = null;
        if (subway != null)
        {
          Block sign1 = block.getRelative(2, 0, 0);
          Block sign2 = block.getRelative(-2, 0, 0);
          Block sign3 = block.getRelative(0, 0, 2);
          Block sign4 = block.getRelative(0, 0, -2);
          
          Block[] signs = { sign1, sign2, sign3, sign4 };
          for (Block s : signs) {
            if ((s.getState() instanceof Sign)) {
              sign = (Sign)s.getState();
            }
          }
          if ((sign != null) && 
            (sign.getLine(0).equalsIgnoreCase("[SS:Send]"))) {
            if (sign.getLine(1).equalsIgnoreCase("Reset"))
            {
              PlayerHandler.RemovePlayer(PlayerHandler.GetPlayer(player.getName()));
              MessageHandler.SendMessage(player, "Destination reset.");
            }
            else if (PlayerHandler.ContainsPlayer(player.getName()))
            {
              PlayerHandler.GetPlayer(player.getName()).update(this.plugin, sign);
            }
            else
            {
              SubwayPlayer subwayPlayer = new SubwayPlayer(player, subway);
              PlayerHandler.AddPlayer(subwayPlayer);
              subwayPlayer.update(this.plugin, sign);
            }
          }
        }
      }
    }
  }
}
