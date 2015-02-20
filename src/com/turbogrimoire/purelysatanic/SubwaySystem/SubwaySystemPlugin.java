package com.turbogrimoire.purelysatanic.SubwaySystem;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.turbogrimoire.purelysatanic.SubwaySystem.Commands.GenerationCommand;
import com.turbogrimoire.purelysatanic.SubwaySystem.Exceptions.PluginNotFoundException;
import com.turbogrimoire.purelysatanic.SubwaySystem.Handlers.StationHandler;
import com.turbogrimoire.purelysatanic.SubwaySystem.Listeners.BlockListener;
import com.turbogrimoire.purelysatanic.SubwaySystem.Listeners.PlayerListener;
import com.turbogrimoire.purelysatanic.SubwaySystem.Util.Vector2;
import com.turbogrimoire.purelysatanic.SubwaySystem.Util.Vector3;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class SubwaySystemPlugin
  extends JavaPlugin
{
  private Logger log;
  private WorldEditPlugin we;
  private WorldGuardPlugin wg;
  public static Permission permissions;
  
  public void onEnable()
  {
    this.log = getLogger();
    
    ConfigurationSerialization.registerClass(Station.class);
    ConfigurationSerialization.registerClass(Vector2.class);
    ConfigurationSerialization.registerClass(Vector3.class);
    try
    {
      this.we = ((WorldEditPlugin)getPlugin("WorldEdit"));
      this.wg = ((WorldGuardPlugin)getPlugin("WorldGuard"));
      
      
      if(!setupPermissions()){
    	  System.out.println("SubwaySystem: Whoops! It looks like I couldn't initialize Vault! This means that permissions won't work.");
      }
      
      getCommand("Subway").setExecutor(new GenerationCommand(this));
      PluginManager pm = getServer().getPluginManager();
      
      pm.registerEvents(new PlayerListener(this), this);
      pm.registerEvents(new BlockListener(this), this);
      
      StationHandler.LoadStation(this);
    }
    catch (PluginNotFoundException e)
    {
      this.log.log(Level.SEVERE, e.getMessage(), e);
      getServer().getPluginManager().disablePlugin(this);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  public void onDisable() {}
  
  private Plugin getPlugin(String name)
    throws PluginNotFoundException
  {
    if (name == null) {
      throw new PluginNotFoundException(name);
    }
    Plugin p = getServer().getPluginManager().getPlugin(name);
    if (p == null) {
      throw new PluginNotFoundException(name);
    }
    return p;
  }
  
  private boolean setupPermissions()
  {
      RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
      if (permissionProvider != null) {
          permissions = permissionProvider.getProvider();
      }
      return (permissions != null);
  }
  
  public boolean hasPermission(Player player, String permission)
  {
	  return permissions.playerHas(player, permission);
  }
  
  public WorldEditPlugin getWorldEdit()
  {
    return this.we;
  }
  
  public WorldGuardPlugin getWorldGuard()
  {
    return this.wg;
  }
}
