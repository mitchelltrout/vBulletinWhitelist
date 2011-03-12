package com.mitchelltrout.vBulletinWhitelist;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

/**
* vBulletinWhitelist plugin for Bukkit
*
* @author Mitchell Trout
*/
public class vBulletinWhitelist extends JavaPlugin {
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    public static Logger Log = Logger.getLogger("Minecraft");
    private final VbwPlayerListener playerListener = new VbwPlayerListener(this);
    public static PluginDescriptionFile pdfFile;
    public static String forumpath = "";

    public void onDisable() {
    	pdfFile = this.getDescription();
        //System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled!" );
        Log.log(Level.INFO, "[" + pdfFile.getName() + "] version " + pdfFile.getVersion() + " is disabled!");
    }

    public void onEnable() {
    	
    	pdfFile = this.getDescription();
    	BufferedWriter Writer = null;
    	
    	if(!new File("plugins/vBulletinWhitelist/vBulletinWhitelist.properties").exists())
        {
            try
            {
            	 new File("plugins/vBulletinWhitelist/").mkdir();
            	new File("plugins/vBulletinWhitelist/vBulletinWhitelist.properties").createNewFile();
                Writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("plugins/vBulletinWhitelist/vBulletinWhitelist.properties")));
                Writer.write("# The path to your forums directory. Please include the trailing slash.\r\nforumpath=http://www.yourwebsite.com/path/to/forum/");
                Writer.flush();
                Writer.close();
                Log.log(Level.INFO, "[" + pdfFile.getName() + "] vBulletinWhitelist.properties created");
            }
            catch(Exception e)
            {
                Log.log(Level.WARNING, "[" + pdfFile.getName() + "] Exception while creating vBulletinWhitelist.properties");
                e.printStackTrace();
            }
        }
    	
    	try
        {
            Scanner scan = new Scanner(new FileInputStream("plugins/vBulletinWhitelist/vBulletinWhitelist.properties"));
            String input, var, val;
            while(scan.hasNextLine())
            {
                input = scan.nextLine();

                if(input.startsWith("#")) continue;

                var = input.substring(0, input.indexOf("=")).toLowerCase();
                val = input.substring(input.indexOf("=") + 1);

                if(var.equals("forumpath"))
                {
                    forumpath = val;
                }
            }
            
            scan.close();
        }
        catch(Exception e)
        {
            Log.log(Level.WARNING, "[" + pdfFile.getName() + "] Exception while reading vBulletinWhitelist.properties");
            e.printStackTrace();
        }
    	
    	
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_LOGIN, playerListener, Priority.Normal, this);

        //System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
        Log.log(Level.INFO, "[" + pdfFile.getName() + "] version " + pdfFile.getVersion() + " is enabled!");
    }
    
    public static boolean isMember(String playername)
    {
    	boolean member = false;
    	
    	Log.log(Level.INFO, "[" + pdfFile.getName() + "] " + playername + " is attempting to join server...");
    	
    	try {
    		
    		URL address = new URL(forumpath + "mcwhitelist.php?user=" + playername);
			URLConnection yc = address.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				
				if(inputLine.equals("true"))
				{
					member = true;
				}
				else
				{
					member = false;
				}

			}
			in.close();
		} catch (Exception e) {
			//System.out.print("vBulletinWhitelist: Could not connect to feverclan.com");
			Log.log(Level.WARNING, "[" + pdfFile.getName() + "] Exception while trying to read " + forumpath + "mcwhitelist.php");
			e.printStackTrace();
		}

    	
    	if(member)// || playername.equals("quarkman")
    	{
    		Log.log(Level.INFO, "[" + pdfFile.getName() + "] " + playername + " is registered.");
    		return true;
    	}
    	else
    	{
    		Log.log(Level.INFO, "[" + pdfFile.getName() + "] " + playername + " is not registered. DENIED!");
    		return false;
    	}
    }
    
    public static Logger getLogger()
    {
    	return Log;
    }
    
    public static PluginDescriptionFile getPdf()
    {
    	return pdfFile;
    }
    
    public static String getForumPath()
    {
    	return forumpath;
    }

    public boolean isDebugging(final Player player) {
        if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else {
            return false;
        }
    }

    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
    }
}