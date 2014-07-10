package nezz.org.chocolatebuyer.api;
import java.awt.Graphics2D;
import java.util.HashSet;
import java.util.Set;

import org.osbot.script.MethodProvider;
import org.osbot.script.Script;
import org.osbot.script.mouse.RectangleDestination;
import org.osbot.script.rs2.Client;
import org.osbot.script.rs2.model.Item;
import org.osbot.script.rs2.model.NPC;
import org.osbot.script.rs2.ui.RS2Interface;
import org.osbot.script.rs2.ui.RS2InterfaceChild;

/**
 * 
 * @author LiveRare
 * 
 * Shop class that provides simple access mechanisms
 * to purchase items from a shop interface.
 * 
 */
public class Shop {
	
	/*
	 * CONSTANT VARIABLES
	 */
	
	/**
	 * Common parent ID of most (if not all) shops.
	 */
	public static final int COMMON_PARENT_ID = 300;
	
	/**
	 * Common child ID for the component that contains the data
	 * for the items.
	 */
	public static final int COMMON_CONTENT_CHILD_ID = 75;
	
	/**
	 * Common child ID for the shop's title.
	 */
	public static final int COMMON_TITLE_CHILD_ID = 76;
	
	/**
	 * Common child ID for the shop's close button.
	 */
	public static final int COMMON_CLOSE_CHILD_ID = 92;
	
	/**
	 * Maximum amount of slots available in a shop interface
	 */
	public static final int MAXIMUM_SLOT_AMOUNT = 40;
	
	/*
	 * DYNAMIC VARIABLES
	 */
	
	/**
	 * Script instance that will be accessing the interfaces from.
	 */
	private final Script script;
	
	/**
	 * A cache of 40 available items.
	 * 
	 * Note: 40 Is the maximum amount of item slots a shop can hold.
	 */
	private final ShopItem[] items;
	
	/**
	 * ID values for the parent interface and child components.
	 */
	private final int parentID, childContentID, childTitleID, childCloseID;
	
	/**
	 * Parent instance that will be accessed elsewhere in this class.
	 */
	private RS2Interface parent;
	
	/**
	 * Name of the clerk to interact with.
	 */
	private String npcName;
	
	/*
	 * Constructors
	 */
	
	public Shop(Script script, String npcName, int parentID, int childContentID, int childTitleID, int childCloseID) {
		this.script = script;
		this.npcName = npcName;
		this.parentID = parentID;
		this.childContentID = childContentID;
		this.childTitleID = childTitleID;
		this.childCloseID = childCloseID;
		this.items = new ShopItem[MAXIMUM_SLOT_AMOUNT];
		
		init();
	}
	
	public Shop(Script script, String npcName) {
		this(script, npcName, COMMON_PARENT_ID, COMMON_CONTENT_CHILD_ID, COMMON_TITLE_CHILD_ID, COMMON_CLOSE_CHILD_ID);
	}
	
	/*
	 * Analytics methods
	 */
	
	/**
	 * This class provides a property for an NPC name. This will
	 * allow the {@link Shop#open(String)} function to execute with
	 * a viable NPC name.
	 * 
	 * @param npcName
	 * 		Name of the NPC to find
	 */
	public void setNPCName(String npcName) {
		this.npcName = npcName;
	}
	
	/**
	 * This method will initialise the ShopItem 2D array that
	 * can contain up to 40 ({@link Shop#MAXIMUM_SLOT_AMOUNT}).
	 */
	public void init() {
		for (int i = 0; i < MAXIMUM_SLOT_AMOUNT; i++)
			items[i] = new ShopItem(script, i);
	}
	
	/**
	 * @Deprecated this method is really inefficient usage of the
	 * item cache acquired from the shop interface. Since only one
	 * index is required, the rest are disregarded. This method
	 * should only be regarded if it's absolutely necessary!
	 * 
	 * @param slot
	 * 		Slot to validate
	 * @return
	 * 		<tt>Validation was successful</tt>
	 */
	@Deprecated
	public boolean validate(int slot) {
		try {
			Item[] items = getItems();
			this.items[slot].setDate(slot < items.length ? items[slot] : null);
			return true;
		} catch (Exception e) {
			this.items[slot].setDate(null);
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * This method will analyse the shop's contents and stores
	 * the data in the 2D ShopItem array.
	 * 
	 * @return
	 * 		<tt>Successfully validated the shop's content</tt>
	 */
	public boolean validate() {
		try {
			
			Item[] items = getItems();
			
			for (int i = 0; i < MAXIMUM_SLOT_AMOUNT; i++)
				this.items[i].setDate(i < items.length ? items[i] : null);
			
			return true;

		} catch (Exception e) {
			
			//If error occurs reset all ShopItem cache
			init();
			
			e.printStackTrace();
			
			return false;
		}
	}
	
	/**
	 * 
	 * @return <tt>Shop interface is valid, thus; shop is open</tt>
	 */
	public boolean isOpen() {
		return (parent = script.client.getInterface(parentID)) != null && parent.isValid() && parent.isVisible();
	}
	
	/**
	 * 
	 * @return Shop's title
	 */
	public String getTitle() {
		return parent.getChild(childTitleID).getMessage();
	}
	
	/**
	 * 
	 * @return Items in the shop
	 */
	public Item[] getItems() {
		return parent.getItems(childContentID);
	}
	
	/**
	 * Acquire an item based on its slot in the shop interface.
	 * 
	 * ONLY USE THIS METHOD IF YOU <u>KNOW</u> FOR CERTAIN
	 * THE SLOT'S ITEM WILL BE CONSISTENT, OTHERWISE USE ANOTHER
	 * GETTER.
	 * 
	 * @param slot
	 * 		Slot position within the shop
	 * @return
	 * 		ShopItem by index
	 */
	public ShopItem getItemBySlot(int slot) {
		return slot >= 0 && slot < MAXIMUM_SLOT_AMOUNT ? this.items[slot] : null;
	}
	
	/**
	 * Acquire an item based on item ID.
	 * 
	 * @param ids
	 * 		IDs of the item to search for
	 * @return
	 * 		First shop item with a corresponding ID
	 */
	public ShopItem getItemByID(int... ids) {
		if (ids != null && ids.length > 0)
			for (ShopItem nextItem : items)
				for (int nextID : ids)
					if (nextItem.getID() == nextID)
						return nextItem;
		return null;
	}
	
	public ShopItem[] getItemsByID(int... ids) {
		Set<ShopItem> cache = new HashSet<>();
		if (ids != null && ids.length > 0)
			for (ShopItem nextItem : items)
				for (int nextID : ids)
					if (nextItem.getID() == nextID)
						cache.add(nextItem);
		return cache.isEmpty() ? null : cache.toArray(new ShopItem[cache.size()]);
	}
	
	/**
	 * Acquire an item based on item name.
	 * 
	 * @param names
	 * 		Names of the item to search for
	 * @return
	 * 		First shop item with a corresponding name
	 */
	public ShopItem getItemByName(String... names) {
		if (names != null && names.length > 0)
			for (ShopItem nextItem : items) {
				String name = nextItem.getName();
				if (!isStringValid(name)){
					
					continue;
				}
				else
					for (String nextName : names)
						if (isStringValid(nextName) && name.equalsIgnoreCase(nextName))
							return nextItem;
			}
		return null;
	}
	
	public ShopItem[] getItemsByName(String... names) {
		Set<ShopItem> cache = new HashSet<>();
		if (names != null && names.length > 0)
			for (ShopItem nextItem : items) {
				String name = nextItem.getName();
				if (!isStringValid(name))
					continue;
				else
					for (String nextName : names)
						if (isStringValid(nextName) && name.equalsIgnoreCase(nextName))
							cache.add(nextItem);
			}
		return cache.isEmpty() ? null : cache.toArray(new ShopItem[cache.size()]);
	}
	
	public void paint(Graphics2D g) {
		for (ShopItem next : items)
			next.draw(g);
	}
	
	/*
	 * Interact methods
	 */
	
	public int tryPurchase(ShopItem item, int amount) throws InterruptedException {

		if (item != null && amount > 0 && this.isOpen()) {

			this.validate();

			int total = amount, onesPurchase = 0, fivesPurchase = 0, tensPurchase = 0;
			
			if (total >= 10) { tensPurchase = total / 10; total -= tensPurchase * 10; }
			if (total >= 5) { fivesPurchase = 1; total -= 5; }
			
			onesPurchase = total;
			
			int count = 0;

			try {

				for (int i = 0; i < tensPurchase; i++)
					if (purchase(item, 10))
						count += 10;
				for (int i = 0; i < fivesPurchase; i++)
					if (purchase(item, 5))
						count += 5;
				for (int i = 0; i < onesPurchase; i++){
					if(item.getAmount() > 1){
						if (purchase(item, 5)){
							count += onesPurchase;
							break;
						}
					}
					else{
						if (purchase(item, 1)){
							count += 1;
						}
					}
				}
			
			} catch (RuntimeException e) {
				
				script.log(e.getMessage());
								
			}
			
			return amount - count;
		}
		return -1;
	}

	private boolean purchase(ShopItem item, int amount)
			throws InterruptedException {
		final int oldID = item.getID();
		validate(item.getSlot());
		if (oldID != item.getID())
			throw new RuntimeException("Inconsistent item! Required: " + oldID + ", but found: " + item.getID());
		else if (item.getAmount() <= 0)
			return false;
		switch (amount) {
		case 1:
			return item.purchaseOne();
		case 5:
			return item.purchaseFive();
		case 10:
			return item.purchaseTen();
		}
		return false;
	}
	
	/**
	 * This method requires a pre-initialised <i>valid</i> NPC name or
	 * one needs to be provided on the constructor.
	 * 
	 * @param altNPCName
	 * 		Alternative NPC to search for
	 * @return
	 * 		<tt>Shop is open</tt>
	 */
	public boolean tryOpen(String altNPCName) throws InterruptedException {
		
		boolean open = isOpen();
		
		try {
			if (open)
				return true;
			
			NPC npc = script.closestNPCForName(isStringValid(altNPCName) ? altNPCName : npcName);
			if (npc != null && npc.exists() && npc.interact("Trade"))
				script.sleep(MethodProvider.random(350, 600));
			
			return open = isOpen();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (open)
				validate();
		}
	}
	
	public boolean tryOpen() throws InterruptedException {
		return tryOpen(null);
	}
	
	public boolean close() throws InterruptedException {
		if (!isOpen()) // Prevent unnecessary re-closing
			return false;
		RS2InterfaceChild child = parent.getChild(childCloseID);
		if(child != null){
			Client c = script.client;
			return c.moveMouseTo(new RectangleDestination(child.getRectangle()),false, true, false);
		}
		else
			return false;
	}
	
	/*
	 * Static methods
	 */
	
	private static boolean isStringValid(String aString) {
		return aString != null && !aString.isEmpty();
	}
	
	
}
