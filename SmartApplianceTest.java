package cs116Project;

public class SmartApplianceTest {
	
	public static void main(String[] args)
			throws ApplianceInvalidException, SmartApplianceInvalidException {
		
		SmartAppliance s1 = new SmartAppliance("toaster", 100, 5, 0.25, 101, 0.5);
		System.out.println(s1);
		SmartAppliance s2 = new SmartAppliance(s1);
		System.out.println(s2);
		System.out.println(s1.getPercentSaving());
		System.out.println(s1.getSmartOn());
		s1.turnOn();
		s1.setPercentSaving(0.75);
		System.out.println(s1.getCurrentWatts());
		s1.smartOff();
		System.out.println(s1.getCurrentWatts());
		s1.smartOn();
		System.out.println(s1);
		
	}
	
}