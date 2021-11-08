package cs116Project;

import java.util.Comparator;

public class CurrentWattsDecComparator implements Comparator<Appliance> {
	
	public int compare(Appliance a, Appliance b) {
		
		return b.getCurrentWatts() - a.getCurrentWatts();
		
	}

}