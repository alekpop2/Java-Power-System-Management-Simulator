package cs116Project;

public class SmartAppliance extends Appliance {
	
	private double percentSaving;
	private boolean smartOn;
	
	public SmartAppliance(String t, int onW, int offW, double odds, int loc, double perSav)
			throws ApplianceInvalidException, SmartApplianceInvalidException {
		
		super(t, onW, offW, odds, loc);
		setPercentSaving(perSav);
		smartOn = true;
		
	}
	
	public SmartAppliance(SmartAppliance s) {
		
		super(s);
		this.percentSaving = s.percentSaving;
		this.smartOn = true;
		
	}
	
	public double getPercentSaving() {
		
		return percentSaving;
		
	}
	
	public boolean getSmartOn() {
		
		return smartOn;
		
	}
	
	public void setPercentSaving(double perSav) throws SmartApplianceInvalidException {
		
		if (perSav > 0 && perSav < 1)
			this.percentSaving = perSav;
		else
			throw new SmartApplianceInvalidException("percent must be between 0 and 1 exclusive");
		
	}
	
	public int getCurrentWatts() {
		
		if(getIsOn() && smartOn)
			return (int)(this.getOnWatts() * (1 - percentSaving));
		else if (getIsOn() && !smartOn)
			return this.getOnWatts();
		else
			return this.getOffWatts();
		
	}
	
	public void smartOn() {
		
		smartOn = true;
		
	}
	
	public void smartOff() {
		
		smartOn = false;
		
	}
	
	public String toString() {
		
		return super.toString() + " PercentSaving=" + percentSaving + " smartOn=" + smartOn;
		
	}

}