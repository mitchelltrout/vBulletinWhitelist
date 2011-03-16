package com.mitchelltrout.vBulletinWhitelist;

import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;



/**
 * Handle events for all Player related events
 * @author Mitchell Trout
 */
public class VbwPlayerListener extends PlayerListener {
	
	
    public VbwPlayerListener(vBulletinWhitelist instance) {
    }

    public void onPlayerLogin(PlayerLoginEvent event)
    {    	
    	String playerName = event.getPlayer().getName();
    	
    	if(vBulletinWhitelist.isMember(playerName) == 0)
    	{
    		event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "You are not a registered user of " + vBulletinWhitelist.getForumPath());
    	}
    	else if(vBulletinWhitelist.isMember(playerName) == 2)
    	{
    		event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Error while verifing username at " + vBulletinWhitelist.getForumPath());
    	}
    }
}