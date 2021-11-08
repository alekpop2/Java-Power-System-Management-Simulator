package cs116Project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class PowerUsageSystem {
	
	private int numItems;
	private Appliance[] list;
	
	public PowerUsageSystem() {
		
		numItems = 0;
		list = new Appliance[10000];
		
	}
	
	public PowerUsageSystem(int size) {
		
		numItems = 0;
		if (size > 0)
			list = new Appliance[size];
		else
			throw new NegativeArraySizeException("size must be positive");
		
	}
	
	public PowerUsageSystem(int size, String filename)
			throws IOException, ApplianceInvalidException, SmartApplianceInvalidException {
		
		numItems = 0;
		if (size > 0)
			list = new Appliance[size];
		else
			throw new NegativeArraySizeException("size must be positive");
		
		insert(filename);
		
	}
	
	public boolean isEmpty() {
		
		return list[0] == null;
		
	}
	
	public boolean isFull() {
		
		return list[list.length - 1] != null;
		
	}
	
	public int length() {
		
		return numItems;
		
	}
	
	public Appliance isThere(int id) {
		
		for (int i = 0; i < numItems; i++)
			if (list[i].getId() == id)
				return list[i];
		return null;
		
	}
	
	public boolean insert(Appliance a) {
		
		if (isFull())
			return false;
		
		for (int i = 0; i < numItems; i++)
			if (list[i].getId() == a.getId())
				return false;
		
		list[numItems] = a;
		int i = numItems;
		while (i > 0 && list[i].compareTo(list[i - 1]) < 0) {
			Appliance temp = list[i];
			list[i] = list[i - 1];
			list[i - 1] = temp;
			i--;
		}
		numItems++;
		return true;
		
	}
	
	public boolean insert(String filename)
			throws FileNotFoundException, ApplianceInvalidException, SmartApplianceInvalidException {
		
		Scanner scan = new Scanner(new File(filename));
		scan.useDelimiter(",");
		while (scan.hasNext()) {
			if (isFull()) {
				scan.close();
				return false;
			}
			int loc = scan.nextInt();
			String t = scan.next();
			int onW = scan.nextInt();
			int offW = scan.nextInt();
			double odds = scan.nextDouble();
			boolean smart = scan.nextBoolean();
			double perSav = Double.parseDouble(scan.nextLine().substring(1));
			if (smart) {
				SmartAppliance s1 = new SmartAppliance(t, onW, offW, odds, loc, perSav);
				insert(s1);
			} else {
				Appliance a1 = new Appliance(t, onW, offW, odds, loc);
				insert(a1);
			}
		}
		scan.close();
		return true;
		
	}
	
	public boolean delete(int id) {
		
		if (isEmpty())
			return false;
		
		for (int i = 0; i < numItems; i++) {
			if (list[i].getId() == id) {
				numItems--;
				for (int j = i; j < numItems; j++)
					list[j] = list[j + 1];
				list[numItems] = null;
				return true;
			}
		}
		
		return false;
		
	}
	
	public Appliance getAppliance(int index) {
		
		if (index < numItems && index >= 0)
			return list[index];
		return null;
		
	}
	
	public Appliance[] getAppsAtLoc(int loc) {
		
		int counter1 = 0;
		for (int i = 0; i < numItems; i++)
			if (list[i].getLocation() == loc)
				counter1++;
		Appliance[] apps = new Appliance[counter1];
		
		int counter2 = 0;
		for (int i = 0; i < numItems; i++)
			if (list[i].getLocation() == loc)
				apps[counter2++] = list[i];
		return apps;
		
	}
	
	public Appliance[] getSmartAppsAtLoc(int loc) {
		
		int counter1 = 0;
		for (int i = 0; i < numItems; i++)
			if (list[i].getLocation() == loc && list[i] instanceof SmartAppliance)
				counter1++;
		Appliance[] apps = new Appliance[counter1];
		
		int counter2 = 0;
		for (int i = 0; i < numItems; i++)
			if (list[i].getLocation() == loc && list[i] instanceof SmartAppliance)
				apps[counter2++] = list[i];
		return apps;
		
	}
	
	public Appliance[] getAppsOfType(String t) {
		
		int counter1 = 0;
		for (int i = 0; i < numItems; i++)
			if (list[i].getType().equalsIgnoreCase(t))
				counter1++;
		Appliance[] apps = new Appliance[counter1];
		
		int counter2 = 0;
		for (int i = 0; i < numItems; i++)
			if (list[i].getType().equalsIgnoreCase(t))
				apps[counter2++] = list[i];
		return apps;
		
	}
	
	public int[] getLocs() {
		
		ArrayList<Integer> locsList = new ArrayList<Integer>();
		for (int i = 0; i < numItems; i++)
			if (!locsList.contains(list[i].getLocation()))
				locsList.add(list[i].getLocation());
		
		int[] locsArray = new int[locsList.size()];
		for (int i = 0; i < locsArray.length; i++)
			locsArray[i] = locsList.get(i).intValue();
		
		Arrays.sort(locsArray);
		return locsArray;
		
	}
	
	public int currentWattsAtLoc(int loc) {
		
		Appliance[] apps = getAppsAtLoc(loc);
		int sum = 0;
		for (int i = 0; i < apps.length; i++)
			sum += apps[i].getCurrentWatts();
		return sum;
		
	}
	
	public int totalCurrentWatts() {
		
		int[] locs = getLocs();
		int sum = 0;
		for (int i = 0; i < locs.length; i++)
			sum += currentWattsAtLoc(locs[i]);
		return sum;
		
	}
	
	public double percentAppsOnAtLoc(int loc) {
		
		Appliance[] apps = getAppsAtLoc(loc);
		int counter = 0;	
		for (int i = 0; i < apps.length; i++)
			if (apps[i].getIsOn())
				counter++;
		return (counter * 1.0) / apps.length;
		
	}
	
	public void turnOnByPercent() {
		
		for (int i = 0; i < numItems; i++)
			if (Math.random() <= list[i].getOddsIsOn())
				list[i].turnOn();
		
	}
	
	public void turnOffAllApps() {
		
		for (int i = 0; i < numItems; i++)
			list[i].turnOff();
		
	}
	
	public String summary() {
		
		String str = "This system contains the following:\n";
		str += getLocs().length + " total locations\n";
		
		ArrayList<String> types = new ArrayList<String>();
		for (int i = 0; i < numItems; i++)
			if (!types.contains(list[i].getType()))
				types.add(list[i].getType());
		
		int[] counters = new int[types.size()];
		for (int i = 0; i < numItems; i++)
			counters[types.indexOf(list[i].getType())]++;
		
		for (int i = 0; i < counters.length; i++)
			str += counters[i] + "x " + types.get(i) + "(s)\n";
		
		return str;
		
	}
	
	public String toString() {
		
		String str = "";
		for (int i = 0; i < numItems; i++)
			str += list[i] + "\n";
		return str;
		
	}

}