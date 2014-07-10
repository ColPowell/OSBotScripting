package nezz.org.chocolatebuyer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;

import nezz.org.chocolatebuyer.api.PricedItem;
import nezz.org.chocolatebuyer.api.Shop;
import nezz.org.chocolatebuyer.api.ShopItem;
import nezz.org.chocolatebuyer.gui.GUI;
import nezz.org.chocolatebuyer.gui.Settings;

import org.osbot.script.MethodProvider;
import org.osbot.script.Script;
import org.osbot.script.ScriptManifest;
import org.osbot.script.mouse.MinimapTileDestination;
import org.osbot.script.mouse.MouseDestination;
import org.osbot.script.mouse.RectangleDestination;
import org.osbot.script.rs2.map.Position;
import org.osbot.script.rs2.model.Item;
import org.osbot.script.rs2.model.NPC;
import org.osbot.script.rs2.model.Player;
import org.osbot.script.rs2.model.RS2Object;
import org.osbot.script.rs2.utility.Area;

@ScriptManifest(author = "Nezz", info = "Buys chocolate bars in Nardah",name = "Nezz's Nardah Chocolate Buyer", version = 0.1)

public class ChocolateBuyer extends Script {
	/*
	 * TODO:
	 * Antiban features:
	 * 	rotate camera
	 * 	different paths to shop
	 */
	public Area BANK_AREA = new Area(3427,2894,3430,2889);
	public Area SHOP_AREA = new Area(3428,2916,3433,2909);
	Settings s = new Settings();
	GUI gui = new GUI(s);
	private long script_start_time = 0;
	State state;
	Shop shop;
	PricedItem chocolateBar;
	boolean shopEmpty = false;
	String paintURL = "http://puu.sh/8udkh.png";
	private final Image img1 = getImage(paintURL);
	
	Position[] bankToShop1 = new Position[]{
		new Position(3429,2892,0),
		new Position(3433,2898,0),
		new Position(3432,2911,0)
	};
	Position[] bankToShop2 = new Position[]{
			new Position(3428,2892,0),
			new Position(3434,2903,0),
			new Position(3429,2913,0)
	};
	Position[] bankToShop3 = new Position[]{
			new Position(3429,2892,0),
			new Position(3432,2906,0),
			new Position(3432,2912,0)
	};
	
	Position[][] paths = new Position[][]{
			bankToShop1,bankToShop2,bankToShop3
	};
	
	private Image getImage(String url) {
		try {
			return ImageIO.read(new URL(url));
		} catch(IOException e) {
			return null;
		}
	}
	
	private enum State {
		BUY, WALK_TO_BANK, WALK_TO_SHOP, BANK
	};
	
	private State getState() throws InterruptedException {
		if(client.getCurrentWorld() == 385 || client.getCurrentWorld() == 386){
			log("You're in a bot world! OH SHIT RUNN");
			hopWorlds();
		}
		if(client.getInventory().isFull() || client.getInventory().getAmount("Coins") < 1000){
			if(BANK_AREA.contains(myPlayer()))
				return State.BANK;
			else{
				if(shop != null && shop.isOpen())
					shop.close();
				return State.WALK_TO_BANK;
			}
		}
		else{
			if(SHOP_AREA.contains(myPlayer()))
				return State.BUY;
			else
				return State.WALK_TO_SHOP;
		}
	}
	
	public boolean hopWorlds() throws InterruptedException{
		while(shop != null && shop.isOpen()){
			shop.close();
			sleep(random(345,654));
		}
		while(client.getBank().isOpen()){
			client.getBank().close();
			sleep(random(345,654));
		}
		//301 - 378
		//the worlds 307, 315, 323, 324, 331, 332, 347, 348, 355, 356, 363, 364, 371, 372 do not exist.
		//PVP/wilderness: 308 or 316, 325, 337
		int curr_world = client.getCurrentWorld();
		int[] worlds = {303, 304, 305, 306, 309, 310, 311, 312, 313, 314, 317, 
						319, 320, 321, 322, 326, 327, 328,
		                329,330,333,334,335,336,338,341,342,343,344,
		                345,346,349,350,351,352,353,354,357,358,359,360,361,
		                362,365,366,367,368,370,373,375,376,378};
		int next_world = 0;
		next_world = worlds[random(0,worlds.length-1)];
		while(next_world == curr_world){
			next_world = worlds[random(0,worlds.length-1)];
		}
		worldHopper.hopWorld(next_world);
		sleep(MethodProvider.random(1000,2000));
		return true;
		
	}
		
	@Override
	public void onStart() {
		gui.setVisible(true);
		while(!s.started){
			try {
				sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		chocolateBar = new PricedItem("Chocolate bar", client);
		script_start_time = System.currentTimeMillis();
		log("Nezz's Chocolate Bar Buyer - for Adept");
	}
	
	public void traversePath(Position[] pathToTravel, boolean reversed) throws InterruptedException{
		
		if (!reversed) {
			int tries = 0;
			for (int i = 1; i < pathToTravel.length; i++){
				i-=walkTile(pathToTravel[i]);
				tries++;
				if(tries - i > 3){
					return;
				}
			}
					//i--;
		} else {
			int tries = pathToTravel.length-1;
			for (int i = pathToTravel.length-2; i >= 0; i--){
				i+=walkTile(pathToTravel[i]);
				tries++;
				if(tries - i > 3)
					return;
			}
					//i++;
		}
	}	
	
	public int walkTile(Position p) throws InterruptedException{
		if(client.getCameraPitch() < random(150,250)){
			client.rotateCameraPitch(random(250,400));
		}
		if (client.getRunEnergy() > random(5,90) && client.getConfig(173) == 0) {
			client.getInterface(548).getChild(93).interact("Toggle Run");
		}
		if(!canReach(p))
			return 1;
		if (p != null){
			MinimapTileDestination mmtd = new MinimapTileDestination(bot,p);
			if(client.moveMouse(mmtd,false)){
				sleep(random(100,150));
				client.pressMouse();
			}
		}
		else{
			log("Null position!");
			sleep(random(345,567));
			return 1;
		}
		int failsafe = 0;
		while (failsafe < 10 && myPlayer().getPosition().distance(p) > random(2,5)) {
			if(s.rightClickHover){
				int rand = random(0,6000);
				if(rand < random(0,173)){
					List<NPC> localNPC = client.getLocalNPCs();
					List<Player> localPlayer = client.getLocalPlayers();
					switch((random(0,4)%2)){
					case 0:
						if(!localNPC.isEmpty()){
							NPC hoverNPC = localNPC.get(random(0,localNPC.size()-1));
							if(hoverNPC != null && hoverNPC.isVisible()){
								MouseDestination NPCmd = hoverNPC.getMouseDestination();
								if(NPCmd != null){
									switch((random(0,4)%2)){
									case 0:
										log("Right clicking NPC");
										client.moveMouseTo(NPCmd, false, true, true);
										sleep(random(345,876));
									break;
									case 1:
										log("Hovering over NPC");
										client.moveMouseTo(NPCmd, false, false,false);
										sleep(random(345,876));
									break;
									}
								}
							}
						}
					break;
					case 1:
						if(!localPlayer.isEmpty()){
							Player hoverPlayer = localPlayer.get(random(0,localPlayer.size()-1));
							if(hoverPlayer != null && hoverPlayer.isVisible()){
								MouseDestination NPCmd = hoverPlayer.getMouseDestination();
								if(NPCmd != null){
									switch((random(0,4)%2)){
									case 0:
										if(hoverPlayer != myPlayer()){
											log("Right clicking player");
											client.moveMouseTo(NPCmd, false, true, true);
											sleep(random(345,876));
										}
									break;
									case 1:
										if(hoverPlayer != myPlayer()){
											log("Hovering over player");
											client.moveMouseTo(NPCmd, false, false,false);
											sleep(random(345,876));
										}
									break;
									}
									
								}
							}
						}
					break;
					}
				}
				else
					sleep(100);
			}
			else
				sleep(100);
			failsafe++;
			if (myPlayer().isMoving())
				failsafe = 0;
		}
		if (failsafe == 10)
			return 1;
		if(s.variedSleep && random(0,1000) < random(0,100)){
			int rand = random(0,random(2000,5000));
			log("Sleeping for: " + rand + " ms before moving again");
			while(myPlayer().isMoving())
				sleep(50);
			sleep(rand);
		}
		return 0;
	}
	
	@Override
	public int onLoop() throws InterruptedException {
		state = getState();
		AntiBan();
		switch (state) {
		case BUY:
			while(myPlayer().isMoving())
				sleep(100);
			if(!shopEmpty){
				NPC rokuh = closestNPCForName("Rokuh");
				if(rokuh == null){
					log("Problems with finding Rokuh!");
				}
				else if(rokuh.isVisible()){
					shop = new Shop(this, rokuh.getName());
					if(shop.isOpen() && shop.validate()){
						ShopItem bar = shop.getItemByName("Chocolate bar");
						if(bar != null){
							int amtInStore = bar.getAmount();
							int buy = 0;
							if(amtInStore < client.getInventory().getEmptySlots()){
								buy = amtInStore;
								if(client.getInventory().getEmptySlots() - amtInStore >3)
									shopEmpty = true;
							}
							else buy = client.getInventory().getEmptySlots();
							log("Attempting to purchase");
							shop.tryPurchase(bar, buy);
							
						}
					}
					else{
						log("Opening shop");
						shop.tryOpen();
					}
				}
				else{
					log("Walking to Rokuh");
					walk(rokuh);
				}
			}
			if(shopEmpty){
				if(client.getInventory().getEmptySlots() > 3){
					log("Hopping worlds");
					if(shop != null && shop.isOpen()){
						shop.close();
						sleep(random(456,876));
					}
					shopEmpty = false;
					hopWorlds();
				}
				else{
					sleep(random(555,888));
					shopEmpty = false;
				}
			}
			sleep(random(345,765));
		break;
		case BANK:
			while(myPlayer().isMoving())
				sleep(200);
			RS2Object bank = closestObjectForName("Bank booth");
			if(bank != null){
				if(!client.getBank().isOpen()){
					bank.interact("Bank");
					sleep(random(634,743));
				}
				else{
					client.getBank().depositAllExcept("Coins");
					sleep(random(567,765));
					//check for minimum # of coins
					if(client.getInventory().getAmount("Coins") < random(1000,4000)){
						Item sb = client.getBank().getItemForName("Coins");
						if(sb != null){
							int sbID = sb.getId();
							client.getBank().withdraw(sbID,5000);
						}
						else{
							log("Out of steel bars!");
							stop();
						}
					}
					if(shopEmpty){
						log("Shop is empty, hopping worlds!");
						if(client.getBank().isOpen()){
							client.getBank().close();
							sleep(random(456,654));
						}
						shopEmpty = false;
						hopWorlds();
					}
				}
			}
			while(myPlayer().isMoving())
				sleep(200);
			
		break;
		case WALK_TO_BANK:
			while(myPlayer().isMoving())
				sleep(200);
			if(s.randomPaths){
				int rand = random(0,paths.length-1);
				log("Taking path: " + (rand+1));
				traversePath(paths[rand],true);
			}
			else
				traversePath(bankToShop1,true);
			while(myPlayer().isMoving())
				sleep(200);
		break;
		case WALK_TO_SHOP:
			while(myPlayer().isMoving())
				sleep(200);
			if(s.randomPaths){
				int rand = random(0,paths.length-1);
				log("Taking path: " + (rand+1));
				traversePath(paths[rand],false);
			}
			else
				traversePath(bankToShop3, false);
			while(myPlayer().isMoving())
				sleep(200);
		break;
		}
		return random(200, 300);
	}
	
	public void AntiBan() throws InterruptedException{
		int rand = random(0,random(5000,10000));
		if(rand == random(0,random(0,100)) && s.rotateCamera){
			log("Rotating camera!");
			switch(random(0,4)%2){
			case 0:
				client.rotateCameraToAngle(random(0,3000));
			break;
			case 1:
				client.rotateCameraPitch(random(60,400));
			break;
			}
			//move camear
		}
		else if(rand == random(random(0,100),random(100,200)) && s.rightClickHover){
			List<NPC> localNPC = client.getLocalNPCs();
			List<Player> localPlayer = client.getLocalPlayers();
			switch((random(0,4)%2)){
			case 0:
				if(!localNPC.isEmpty()){
					NPC hoverNPC = localNPC.get(random(0,localNPC.size()-1));
					if(hoverNPC != null && hoverNPC.isVisible()){
						MouseDestination NPCmd = hoverNPC.getMouseDestination();
						if(NPCmd != null){
							switch((random(0,4)%2)){
							case 0:
								log("Right clicking NPC!");
								client.moveMouseTo(NPCmd, false, true, true);
								sleep(random(345,876));
							break;
							case 1:
								log("Hovering over NPC");
								client.moveMouseTo(NPCmd, false, false,false);
								sleep(random(345,876));
							break;
							}
						}
					}
				}
			break;
			case 1:
				if(!localPlayer.isEmpty()){
					Player hoverPlayer = localPlayer.get(random(0,localPlayer.size()-1));
					if(hoverPlayer != null && hoverPlayer.isVisible()){
						MouseDestination NPCmd = hoverPlayer.getMouseDestination();
						if(NPCmd != null){
							switch((random(0,4)%2)){
							case 0:
								if(hoverPlayer != myPlayer()){
									log("Right clicking player!");
									client.moveMouseTo(NPCmd, false, true, true);
									sleep(random(345,876));
								}
							break;
							case 1:
								if(hoverPlayer != myPlayer()){
									log("Hovering over player!");
									client.moveMouseTo(NPCmd, false, false,false);
									sleep(random(345,876));
								}
							break;
							}
							
						}
					}
				}
			break;
			}
		}
		else if(rand == random(random(0,200),random(200,300)) && s.changeMouseSpeed){
			log("Changing mouse speed");
			client.setMouseSpeed(random(3,7));
			//change mouse speed
		}
		else if(rand == random(random(0,300),random(300,400)) && s.variedSleep){
			log("Varied sleep");
			sleep(random(0,5000));
		}
		else if(rand == 10000 && s.idleToLogout){
			log("Idling to log out!");
			//idle to log out
			sleep(random(310000,380000));
		}
		
	}
	
	@Override
	public void onMessage(String message){
		if(message.contains("out of stock")){
			log("The store has run out of stock! Setting shopEmpty to true");
			shopEmpty = true;
		}
	}
	
	public int getPerHour(long amt){
		return (int)((amt*3600000)/(System.currentTimeMillis() - script_start_time));
	}
	
	@Override
	public void onPaint(Graphics g) {
		chocolateBar.update(client);
		//draw paint
		//Graphics2D gr = (Graphics2D)g;
		//gr.drawImage(img1, 0, 338, null);
		//timer
		g.setColor(Color.WHITE);
		long time_passed = System.currentTimeMillis() - script_start_time;
		long time_in_seconds = time_passed/1000;
		int time_in_minutes = 0;
		int time_in_hours = 0;
		String seconds = "00";
		String minutes = "00";
		String hours = "00";
		if(time_in_seconds >= 60){
			time_in_minutes = (int)time_in_seconds/60;
			time_in_seconds = (int)time_in_seconds%60;
		}
		if(time_in_minutes >= 60){
			time_in_hours = time_in_minutes/60;
			time_in_minutes = time_in_minutes%60;
		}
		if(time_in_seconds < 10){
			seconds = "0" + time_in_seconds;
		}
		else
			seconds = ""+time_in_seconds;
		if(time_in_minutes < 10){
			minutes = "0" + time_in_minutes;
		}
		else
			minutes = ""+time_in_minutes;
		if(time_in_hours < 10){
			hours = "0" + time_in_hours;
		}
		else
			hours = ""+ time_in_hours;
		String runtime = hours + ":" + minutes + ":" + seconds;
		if(runtime != null)
			g.drawString(runtime,5,250);
			//g.drawString(runtime, 60, 425);
		//balls, balls/hr
		g.drawString("Bars: "+chocolateBar.getAmount(), 5,260);//115, 448);
		g.drawString("Bars/hr: "+getPerHour(chocolateBar.getAmount()), 5,270);//140,470);
		//profit, profit/hr
		g.drawString("Profit: "+chocolateBar.getAmount()*chocolateBar.getPrice(),5,280);// 340,447);
		g.drawString("Profit/hr: "+getPerHour(chocolateBar.getAmount()*chocolateBar.getPrice()), 5,290);//333,470);
	}
}

