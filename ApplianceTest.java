package cs116Project;

public class ApplianceTest {
	
	public static void main(String[] args) throws ApplianceInvalidException {
		
		Appliance a1 = new Appliance("toaster", 50, 0, 0.2, 101);
		System.out.println(a1);
		Appliance a2 = new Appliance(a1);
		System.out.println(a2);
		System.out.println(a1.getId());
		System.out.println(a1.getIsOn());
		System.out.println(a1.getLocation());
		System.out.println(a1.getOddsIsOn());
		System.out.println(a1.getOffWatts());
		System.out.println(a1.getOnWatts());
		System.out.println(a1.getType());
		a1.setLocation(202);
		a1.setOffWatts(10);
		a1.setOnWatts(200);
		System.out.println(a1);
		a1.setOddsIsOn(0.25);
		System.out.println(a1.getOddsIsOn());
		a1.turnOn();
		System.out.println(a1.getCurrentWatts());
		a1.turnOff();
		System.out.println(a1.getCurrentWatts());
		System.out.println(a1.compareTo(a2));
		
	}

}