package com.turbogrimoire.purelysatanic.SubwaySystem.Schedulers;

import com.turbogrimoire.purelysatanic.SubwaySystem.Util.Vector2;
import org.bukkit.Location;

class LocationGrid
{
  private final Location location;
  private final Location receiving;
  private final Vector2 gridLocation;
  
  public LocationGrid(Location location, Location receiving, Vector2 gridLocation)
  {
    this.location = location;
    this.receiving = receiving;
    this.gridLocation = gridLocation;
  }
  
  public Location getLocation()
  {
    return this.location;
  }
  
  public Location getReceiving()
  {
    return this.receiving;
  }
  
  public Vector2 getGridLocation()
  {
    return this.gridLocation;
  }
}
