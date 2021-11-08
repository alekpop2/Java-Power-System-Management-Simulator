package cs116Project;

import java.io.IOException;
import java.util.Arrays;

public class PowerSystemTest {
	
	public static void main(String[] args)
			throws IOException, ApplianceInvalidException, SmartApplianceInvalidException {
		
		System.out.println("PUS 1");
		PowerUsageSystem pus1 = new PowerUsageSystem();
		System.out.println(pus1.isEmpty());
		pus1.insert(new Appliance("oven", 90, 0, 0.5, 101));
		System.out.println(pus1.length());
		System.out.println(pus1.isThere(1));
		System.out.println();
		
		System.out.println("PUS 2");
		PowerUsageSystem pus2 = new PowerUsageSystem(20);
		pus2.insert("testTableData.txt");
		System.out.println(pus2.length());
		System.out.println(pus2.isFull());
		pus2.delete(5);
		System.out.println(pus2.length());
		System.out.println(pus2.getAppliance(0));
		System.out.println();
		System.out.println(pus2.summary());
		System.out.println();
		System.out.println(pus2);
		
		System.out.println("PUS 3");
		PowerUsageSystem pus3 = new PowerUsageSystem(2000, "testTableData.txt");
		System.out.println(pus3.length());
		System.out.println();
		Appliance[] apps = pus3.getAppsAtLoc(10000001);
		for (Appliance a : apps)
			System.out.println(a);
		System.out.println();
		apps = pus3.getSmartAppsAtLoc(10000001);
		for (Appliance a : apps)
			System.out.println(a);
		System.out.println();
		apps = pus3.getAppsOfType("oven");
		for (Appliance a : apps)
			System.out.println(a);
		System.out.println();
		System.out.println(Arrays.toString(pus3.getLocs()));
		System.out.println(pus3.currentWattsAtLoc(10000002));
		pus3.turnOnByPercent();
		System.out.println(pus3.currentWattsAtLoc(10000002));
		pus3.turnOffAllApps();
		System.out.println(pus3.currentWattsAtLoc(10000002));
		pus3.turnOnByPercent();
		System.out.println(pus3.totalCurrentWatts());
		System.out.println(pus3.percentAppsOnAtLoc(10000003));
		
	}

}