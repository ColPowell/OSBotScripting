package nezz.org.chocolatebuyer.api;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;

import org.osbot.script.Script;
import org.osbot.script.mouse.RectangleDestination;
import org.osbot.script.rs2.model.Item;

/**
 * 
 * @author LiveRare
 * 
 * This class is designed to store the details for every item
 * within a shop interface. There should be only 40 instances
 * of this object stored in an array because there's a maximum
 * of 40 available item slots in a shop interface.
 * 
 */
public class ShopItem {

	public static final Stroke STROKE = new BasicStroke(0.655f);
	public static final Color FOREGROUND_COLOR = new Color(255, 255, 255, 150);

	public static final Point ITEM_STARTING_POSITION = new Point(80, 70);
	public static final Dimension ITEM_BOUNDS = new Dimension(30, 25);
	public static final Dimension SPACE_MARGIN = new Dimension(17, 23);

	private final Script script;
	private final int slot;
	private final int slotColumn;
	private final int slotRow;
	private final Rectangle slotBounds;
	private final RectangleDestination slotDestination;

	private int id;
	private String name;
	private int amount;

	public ShopItem(Script script, int slot) {
		this.script = script;
		this.slot = slot;
		this.slotColumn = (slot % 8);
		this.slotRow = (int) (slot / (double) 8);
		this.slotBounds = new Rectangle(
				ITEM_STARTING_POSITION.x + (ITEM_BOUNDS.width + SPACE_MARGIN.width) * (slotColumn),
				ITEM_STARTING_POSITION.y + (ITEM_BOUNDS.height + SPACE_MARGIN.height) * (slotRow),
				ITEM_BOUNDS.width,
				ITEM_BOUNDS.height);
		this.slotDestination = new RectangleDestination(slotBounds);
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null
				&& obj instanceof ShopItem
				&& ((ShopItem) obj).getSlot() == getSlot();
	}

	@Override
	public String toString() {
		return "[Slot: " + slot + " | Name: " + name + " | Item ID: " + id + " | Amount: " + amount + "]";
	}
	
	/*
	 * Item profile values
	 */

	public int getSlot() {
		return slot;
	}

	public int getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setID(int id) {
		this.id = id;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getSlotColumn() {
		return slotColumn;
	}
	
	public void setDate(Item item) {
		if (item != null) {
			this.id = item.getId();
			this.name = item.getName();
			this.amount = item.getAmount();
		} else {
			this.id = -1;
			this.name = "null";
			this.amount = -1;
		}
	}
	
	/*
	 * Other methods
	 */

	public int getSlotRow() {
		return slotRow;
	}

	public Rectangle getSlotBounds() {
		return slotBounds;
	}

	public RectangleDestination getSlotDestination() {
		return slotDestination;
	}
	
	public boolean interact(String interact) throws InterruptedException {
		script.client.moveMouse(getSlotDestination(), false);
		return script.selectOption(null, getSlotDestination(), interact);	
	}
	
	public boolean purchaseOne() throws InterruptedException {
		return interact("Buy 1");
	}
	
	public boolean purchaseFive() throws InterruptedException {
		return interact("Buy 5");
	}
	
	public boolean purchaseTen() throws InterruptedException {
		return interact("Buy 10");
	}
	
	public void draw(Graphics2D g) {
		
		Rectangle r = new Rectangle(slotBounds.x - 4, slotBounds.y - 6, slotBounds.width + 4, slotBounds.height + 10);
		
		{ // Draw bounding box
			g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
			g.setStroke(STROKE);
			g.setColor(FOREGROUND_COLOR);
			g.draw(r);
			g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
		}
		
		{ // Draw text
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			FontMetrics fm = g.getFontMetrics();
			String id = String.valueOf(this.id);
			int width = (int) fm.getStringBounds(id, g).getWidth();
			Point p = new Point(r.x + ((r.width - width) / 2), r.y + 32);
			g.setColor(Color.BLACK);
			g.drawString(id, p.x - 1, p.y - 1);
			g.drawString(id, p.x - 1, p.y + 1);
			g.drawString(id, p.x + 1, p.y - 1);
			g.drawString(id, p.x + 1, p.y + 1);
			g.setColor(Color.WHITE);
			g.drawString(id, p.x, p.y);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		}
	}

}

