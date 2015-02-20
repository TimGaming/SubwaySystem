package com.turbogrimoire.purelysatanic.SubwaySystem.Schedulers;

import com.sk89q.worldedit.data.DataException;
import com.turbogrimoire.purelysatanic.SubwaySystem.Handlers.GenerationHandler;
import com.turbogrimoire.purelysatanic.SubwaySystem.Handlers.MessageHandler;
import com.turbogrimoire.purelysatanic.SubwaySystem.Handlers.StationHandler;
import com.turbogrimoire.purelysatanic.SubwaySystem.Runnables.SubwayGenerationRunnable;
import com.turbogrimoire.purelysatanic.SubwaySystem.SubwaySystemPlugin;
import com.turbogrimoire.purelysatanic.SubwaySystem.Util.Vector2;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import org.bukkit.Location;
import org.bukkit.World;

public class SubwayGenerationScheduler
  implements Runnable
{
  private final SubwaySystemPlugin plugin;
  private final Location start;
  private final Location receivingStart;
  private final World world;
  private final int radius;
  private final int total;
  private final LocationGrid[] locations;
  private final File schematic;
  private int minimumWaitPeriod = 15000;
  private long timeAtLastGeneration = 0L;
  private int currentGenerationID = -1;
  private int count = 0;
  
  public SubwayGenerationScheduler(SubwaySystemPlugin plugin, File schematic, Location start, Location receivingStart, int radius)
  {
    this.plugin = plugin;
    this.schematic = schematic;
    this.start = start;
    this.receivingStart = receivingStart;
    this.world = start.getWorld();
    this.radius = radius;
    this.total = ((int)Math.pow(2 * radius + 1, 2.0D));
    this.locations = new LocationGrid[this.total];
    calculateLocations();
  }
  
  public void run()
  {
    if ((!this.plugin.getServer().getScheduler().isCurrentlyRunning(this.currentGenerationID)) && 
      (System.currentTimeMillis() - this.timeAtLastGeneration > this.minimumWaitPeriod))
    {
      if (this.count == this.total)
      {
        try
        {
          StationHandler.SaveStations(this.plugin);
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
        MessageHandler.SendServerMessage(this.plugin, 
          "Subway generation has completed.");
        GenerationHandler.ClearGeneration();
        this.plugin.getServer().getScheduler()
          .cancelTasks(this.plugin);
        return;
      }
      if (
        StationHandler.GetStationByVector(this.locations[this.count]
        .getGridLocation()) != null) {
        this.count = findFirstNullStation();
      }
      if (this.count < this.total)
      {
        DecimalFormat format = new DecimalFormat("#.##");
        
        String percent = format
          .format((this.count + 1) / this.total * 100.0D) + 
          "%";
        
        MessageHandler.SendServerMessage(this.plugin, 
          "Subway Generation: " + (this.count + 1) + "/" + 
          this.total + " (" + percent + ")");
        try
        {
          this.currentGenerationID = this.plugin
            .getServer()
            .getScheduler()
            .scheduleSyncDelayedTask(
            this.plugin, 
            new SubwayGenerationRunnable(
            this.plugin, 
            this.schematic, 
            "Station-" + 
            this.locations[this.count]
            .getGridLocation()
            .getX() + 
            "," + 
            this.locations[this.count]
            .getGridLocation()
            .getZ() + "", 
            this.world, 
            this.locations[this.count]
            .getLocation(), 
            this.locations[this.count]
            .getReceiving(), 
            this.locations[this.count]
            .getGridLocation(), 
            false));
          StationHandler.SaveStations(this.plugin);
        }
        catch (DataException|IOException e)
        {
          e.printStackTrace();
        }
        this.timeAtLastGeneration = System.currentTimeMillis();
        this.count += 1;
      }
    }
  }
  
  private int findFirstNullStation()
  {
    for (int i = 0; i < this.total; i++) {
      if (StationHandler.GetStationByVector(this.locations[i]
        .getGridLocation()) == null) {
        return i;
      }
    }
    return this.total;
  }
  
  private void calculateLocations()
  {
    int count = 0;
    for (int i = 0; i <= this.radius; i++) {
      for (int x = -i; x <= i; x++) {
        for (int z = -i; z <= i; z++) {
          if ((x == i) || (x == -i) || (z == i) || (z == -i))
          {
            Location location = new Location(this.start.getWorld(), 
              this.start.getBlockX() + x * 1000, 
              this.start.getBlockY(), this.start.getBlockZ() + 
              z * 1000);
            Location receiving = new Location(
              this.receivingStart.getWorld(), 
              this.receivingStart.getBlockX() + x * 1000, 
              this.receivingStart.getBlockY(), 
              this.receivingStart.getBlockZ() + z * 1000);
            
            this.locations[count] = new LocationGrid(location, 
              receiving, new Vector2(x, -z));
            count++;
          }
        }
      }
    }
  }
}
