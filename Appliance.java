package cs116Project;

public class Appliance implements Comparable<Appliance> {

	private String type;
	private boolean isOn;
	private int onWattage;
	private int offWattage;
	private double oddsIsOn;
	private int location;
	private int id;
	private static int count = 0;
	
	public Appliance(String t, int onW, int offW, double odds, int loc)
			throws ApplianceInvalidException {
		
		type = t;
		isOn = false;
		setOnWatts(onW);
		setOffWatts(offW);
		setOddsIsOn(odds);
		setLocation(loc);
		count++;
		id = count;
		
	}
	
	public Appliance(Appliance a) {
		
		this.type = a.type;
		this.isOn = false;
		this.onWattage = a.onWattage;
		this.offWattage = a.offWattage;
		this.oddsIsOn = a.oddsIsOn;
		this.location = a.location;
		count++;
		id = count;
		
	}
	
	public String getType() {
		
		return type;
		
	}
	
	public boolean getIsOn () {
		
		return isOn;
		
	}
	
	public int getOnWatts() {
		
		return onWattage;
		
	}
	
	public int getOffWatts() {
		
		return offWattage;
		
	}
	
	public int getId() {
		
		return id;
		
	}
	
	public double getOddsIsOn() {
		
		return oddsIsOn;
		
	}
	
	public int getLocation() {
		
		return location;
		
	}
	
	public void setOnWatts(int onW) throws ApplianceInvalidException {
		
		if (onW >= 0)
			this.onWattage = onW;
		else
			throw new ApplianceInvalidException("wattages must be positive");
		
	}
	
	public void setOffWatts(int offW) throws ApplianceInvalidException {
		
		if (offW >= 0)
			this.offWattage = offW;
		else
			throw new ApplianceInvalidException("wattages must be positive");
		
	}
	
	public void setOddsIsOn(double odds) throws ApplianceInvalidException {
		
		if (odds >= 0 && odds <= 1)
			this.oddsIsOn = odds;
		else
			throw new ApplianceInvalidException("odds must be between 0 and 1 inclusive");
		
	}
	
	public void setLocation(int loc) throws ApplianceInvalidException {
		
		if (loc >= 0)
			this.location = loc;
		else
			throw new ApplianceInvalidException("location must be positive");
		
	}
	
	public int getCurrentWatts() {
		
		return isOn ? onWattage : offWattage;
		
	}
	
	public void turnOn() {
		
		isOn = true;
		
	}
	
	public void turnOff() {
		
		isOn = false;
		
	}
	
	public String toString () {
		
		return id + " type=" + type + " loc=" + location + " currentW=" + getCurrentWatts() +
				" on?=" + isOn + " OnW=" + onWattage + " OffW=" + offWattage;
		
	}
	
	public int compareTo(Appliance a) {
		
		if (this.onWattage == a.onWattage)
			return this.offWattage - a.offWattage;
		return a.onWattage - this.onWattage;
		
	}
	
}