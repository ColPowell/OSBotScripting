package nezz.org.chocolatebuyer.api;

import org.osbot.script.rs2.Client;

public class PricedItem {
	private String name;
	private int lastCount = 0;
	private int amount = 0;
	private int price = 0;
	private int id = 0;

	
	public PricedItem(String name, Client c){
		this.name = name;
		if(c.getInventory().contains(name))
			lastCount = (int) c.getInventory().getAmount(name);
		if(name.contains("Clue scroll")||name.contains("Tooth"))
			price = 100000;
		else{
			price = PriceGrab.getInstance().getPrice(name, 2);	
		}
	}
	
	public PricedItem(String name, int id , Client c){
		this.name = name;
		this.setId(id);
		if(c.getInventory().contains(name))
			lastCount = (int) c.getInventory().getAmount(name);
		if(name.contains("Clue scroll")||name.contains("Tooth"))
			price = 100000;
		else{
			price = PriceGrab.getInstance().getPrice(name, 2);	
		}
	}
	
	public void update(Client c){
		int increase = 0;
		if(id==0)
			increase =  (int) (c.getInventory().getAmount(name)- lastCount);
		else
			increase =  (int) (c.getInventory().getAmount(id)- lastCount);
		if(increase < 0)
			increase = 0;
		amount = amount + increase; 
		if(id==0)
			lastCount = (int) c.getInventory().getAmount(name);
		else
			lastCount = (int) c.getInventory().getAmount(id);
	}
	
	public String getName(){
		return name;
	}
	public int getAmount(){
		return amount;
	}
	
	public int getPrice(){
		return price;
	}
	public int getValue(){
		return amount * price;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
