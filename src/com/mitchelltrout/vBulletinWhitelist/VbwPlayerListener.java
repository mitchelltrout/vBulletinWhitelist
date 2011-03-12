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
    	
    	if(!vBulletinWhitelist.isMember(playerName))
    	{
    		event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "You are not a registered user of " + vBulletinWhitelist.getForumPath());
    	}
    	
    	
    }
}