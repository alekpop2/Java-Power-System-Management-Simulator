package cs116Project;

import java.util.Comparator;

public class PercentAppsUsedIncComparator implements Comparator<Appliance[]> {
	
	public int compare(Appliance[] a, Appliance[] b) {
		
		int counterA = 0;
		int counterB = 0;
		
		for (int i = 0; i < a.length; i++)
			if (a[i].getIsOn())
				counterA++;
		
		for (int i = 0; i < b.length; i++)
			if (b[i].getIsOn())
				counterB++;
		
		int percentA = (int)((counterA * 1000000.0) / a.length);
		int percentB = (int)((counterB * 1000000.0) / b.length);
		
		return percentA - percentB;
		
	}

}