package cs116Project;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.Scanner;

public class EventSimulator {
	
	private static PowerUsageSystem appsList;
	private static int intervalCounter;
	private static int minute;
	private static int[][] locsEffectedCounters;
	private static BufferedWriter bf;
	
	
	/* The lowerWatts method will manage the appliances using the following logic:
	 *
	 * Sort the locations by the percent of the appliances that are on at each location.
	 * For the locations that only have 1/3 or less of their appliances turned on, sort
	 * their appliances by current wattage being used. Starting with the location with
	 * the lowest percent of appliances turned on, turn off the smart appliance that is
	 * using the most watts at each location until we are below the target wattage.
	 * We are assuming that the people at the locations with 1/3 or less of their
	 * appliances turned on are not home. That being said, we still do not want to
	 * turn off all of the smart appliances at one location since we can never be 100%
	 * sure that nobody is home and for the fact that some people may have left specific
	 * smart appliances on, on purpose even though they are not home. If we turn off the
	 * highest watt consuming smart appliance at each one of these locations and our
	 * target wattage still is not met, keep turning off the next highest watt consuming
	 * smart appliance at these locations. If we turn off all the smart appliances at all
	 * of these locations where we assume nobody is home and it is still not enough, we
	 * will start repeating the process for the rest of the locations in the same manner.
	 * The majority of people do not use close to all of their appliances at once, so the
	 * ones who are producing a high amount of watts most likely need to be doing so at
	 * that time. Therefore, we would like to leave these locations alone for as long as
	 * we can. If all the smart appliances in our system are turned off, and we are still
	 * over the target wattage, we will begin cutting the power to certain locations
	 * (a brown out) using the same sorting/priority as before.
	 */
	
	private static void lowerWatts(int maxWatts) throws IOException {
		
		if (appsList.totalCurrentWatts() < maxWatts) {
			updateFileAndSummary();
			return;
		}
		
		int initialWatts = appsList.totalCurrentWatts();
		ArrayList<Appliance> effectedApps = new ArrayList<Appliance>();
		int[] locs = appsList.getLocs();
		Appliance[][] priorityList = new Appliance[locs.length][2];
		for (int i = 0; i < priorityList.length; i++) {
			priorityList[i] = appsList.getSmartAppsAtLoc(locs[i]);
			Arrays.sort(priorityList[i], new CurrentWattsDecComparator());
		}
		Arrays.sort(priorityList, new PercentAppsUsedIncComparator());
		
		int thresholdIndex = 0;
		for (int i = 0; i < priorityList.length; i++) {
			if (priorityList[i].length == 0)
				continue;
			int location = priorityList[i][0].getLocation();
			if (appsList.percentAppsOnAtLoc(location) > 1.0 / 3) {
				thresholdIndex = i;
				break;
			}
		}
		
		int i = 0;
		int availableLocs = thresholdIndex;
		int[] currentIndices = new int[thresholdIndex];
		while (availableLocs > 0) {
			if (appsList.totalCurrentWatts() < maxWatts) {
				updateFileAndSummary(effectedApps, initialWatts);
				return;
			} else if (i == thresholdIndex)
				i = 0;
			else if (currentIndices[i] == -1)
				i++;
			else if (currentIndices[i] == priorityList[i].length) {
				currentIndices[i] = -1;
				availableLocs--;
				i++;
			} else if (priorityList[i][currentIndices[i]].getIsOn()) {
				priorityList[i][currentIndices[i]].turnOff();
				effectedApps.add(priorityList[i][currentIndices[i]]);
				currentIndices[i]++;
				i++;
			} else {
				currentIndices[i]++;
			}
		}
		
		int j = thresholdIndex;
		int availableLocs2 = priorityList.length - thresholdIndex;
		int[] currentIndices2 = new int[availableLocs2];
		while (availableLocs2 > 0) {
			if (appsList.totalCurrentWatts() < maxWatts) {
				updateFileAndSummary(effectedApps, initialWatts);
				return;
			} else if (j == priorityList.length)
				j = thresholdIndex;
			else if (currentIndices2[j - thresholdIndex] == -1)
				j++;
			else if (currentIndices2[j - thresholdIndex] == priorityList[j].length) {
				currentIndices2[j - thresholdIndex] = -1;
				availableLocs2--;
				j++;
			} else if (priorityList[j][currentIndices2[j - thresholdIndex]].getIsOn()) {
				priorityList[j][currentIndices2[j - thresholdIndex]].turnOff();
				effectedApps.add(priorityList[j][currentIndices2[j - thresholdIndex]]);
				currentIndices2[j - thresholdIndex]++;
				j++;
			} else {
				currentIndices2[j - thresholdIndex]++;
			}
		}
		
		Appliance[][] priorityList2 = new Appliance[locs.length][2];
		for (int k = 0; k < priorityList2.length; k++)
			priorityList2[k] = appsList.getAppsAtLoc(locs[k]);
		Arrays.sort(priorityList2, new PercentAppsUsedIncComparator());
		
		for (int r = 0; r < priorityList2.length; r++)
			if (appsList.totalCurrentWatts() < maxWatts) {
				updateFileAndSummary(effectedApps, initialWatts);
				return;
			} else {
				for (int c = 0; c < priorityList2[r].length; c++) {
					if (priorityList2[r][c].getIsOn()) {
						priorityList2[r][c].turnOff();
						effectedApps.add(priorityList2[r][c]);
					}
				}
			}
		
		updateFileAndSummary(effectedApps, initialWatts);
		
	}
	
	private static void updateFileAndSummary() throws IOException {
		
		String str = "Interval " + intervalCounter + ", Minute " + minute
				+ "\n\t0 locations effected";
		
		bf.write(str + "\n");
		bf.flush();
		
		int watts = appsList.totalCurrentWatts();
		System.out.println("Interval " + intervalCounter + ", Minute " + minute
				+ "\n\tStart: " + watts + " W, End: " + watts + " W"
				+ "\n\t0 locations effected");
		
	}
	
	private static void updateFileAndSummary(ArrayList<Appliance> apps, int initialWattage)
			throws IOException {
		
		String str = "Interval " + intervalCounter + ", Minute " + minute;
		
		ArrayList<Integer> locs = new ArrayList<Integer>();
		for (Appliance a : apps) {	
			for (int i = 0; i < locsEffectedCounters[0].length; i++)
				if (locsEffectedCounters[0][i] == a.getLocation()) {
					locsEffectedCounters[1][i]++;
					break;
				}
			if (!locs.contains(a.getLocation()))
				locs.add(a.getLocation());
		}
		
		Collections.sort(locs);
		for (int loc : locs) {
			str += "\n\tLocation " + loc;
			for (Appliance a : apps)
				if (a.getLocation() == loc)
					str += "\n\t\t" + a.getId() + " " + a.getType();
		}
		
		bf.write(str + "\n");
		bf.flush();
		
		System.out.println("Interval " + intervalCounter + ", Minute " + minute
				+ "\n\tStart: " + initialWattage + " W, End: " + appsList.totalCurrentWatts() + " W"
				+ "\n\t" + locs.size() + " locations effected");
		
	}
	
	public static void main(String[] args) 
			throws IOException, ApplianceInvalidException, SmartApplianceInvalidException {

		Scanner scan = new Scanner(System.in);
		String command = "";
		do {
			
			System.out.print("Possible commands for power usage system...\n"
					+ "(01) new system\n"
					+ "(02) new default system\n"
					+ "(03) new system from file\n"
					+ "(04) add appliance\n"
					+ "(05) add appliances from file\n"
					+ "(06) delete appliance\n"
					+ "(07) find appliance\n"
					+ "(08) get appliances at location\n"
					+ "(09) get appliances of type\n"
					+ "(10) view summary\n"
					+ "(11) start simulation\n"
					+ "(12) stop\n"
					+ "Please input a command: ");
			command = scan.nextLine();
			System.out.println();

			if (command.equalsIgnoreCase("new system")) {
				
				boolean flag = false;
				do {
					try {
						System.out.print("What is the size of your system? ");
						int size = scan.nextInt();
						appsList = new PowerUsageSystem(size);
						System.out.println("system created");
						flag = true;
						scan.nextLine();
					} catch (NegativeArraySizeException nase) {
						System.out.println(nase.getMessage());
					} catch (InputMismatchException ime) {
						System.out.println("size must be an integer");
						scan.nextLine();
					}
				} while (!flag);
				
			} else if (command.equalsIgnoreCase("new default system")) {
				
				appsList = new PowerUsageSystem();
				System.out.println("system created");
				
			} else if (command.equalsIgnoreCase("new system from file")) {
				
				boolean flag = false;
				do {
					try {
						System.out.print("What is the size of your system? ");
						int size = scan.nextInt();
						scan.nextLine();
						System.out.print("What is the filename? ");
						String filename = scan.nextLine();
						appsList = new PowerUsageSystem(size, filename);
						System.out.println("system created");
						flag = true;
					} catch (NegativeArraySizeException nase) {
						System.out.println(nase.getMessage());
					} catch (InputMismatchException ime) {
						System.out.println("size must be an integer");
						scan.nextLine();
					} catch (FileNotFoundException fnfe) {
						System.out.println("file by that name not found");
					}
				} while (!flag);
				
			} else if (command.equalsIgnoreCase("add appliance")) {
				
				if (appsList == null)
					System.out.println("create a system first");
				else if (appsList.isFull())
					System.out.println("system full, appliance not added");
				else {
					String smart;
					do {
						System.out.print("(yes/no) Is it a smart appliance? ");
						smart = scan.nextLine();
					} while (!smart.equalsIgnoreCase("yes") && !smart.equalsIgnoreCase("no"));
					if (smart.equalsIgnoreCase("yes")) {
						boolean flag = false;
						do {
							try {
								System.out.print("What type of smart appliance is it? ");
								String t = scan.nextLine();
								System.out.print("What location is it at? ");
								int loc = scan.nextInt();
								System.out.print("What is the on wattage? ");
								int onW = scan.nextInt();
								System.out.print("What is the off wattage? ");
								int offW = scan.nextInt();
								System.out.print("At any time, what are the odds this smart "
										+ "appliance is on? ");
								double odds = scan.nextDouble();
								System.out.print("What is the percent saving? ");
								double perSav = scan.nextDouble();
								SmartAppliance s1 = new SmartAppliance(t, onW, offW, odds, loc, perSav);
								appsList.insert(s1);
								System.out.println("smart appliance added");
								flag = true;
							} catch (InputMismatchException ime) {
								System.out.println("please enter an integer or real number when "
										+ "appropriate");
								scan.nextLine();
							} catch (ApplianceInvalidException aie) {
								System.out.println(aie.getMessage());
								scan.nextLine();
							} catch (SmartApplianceInvalidException saie) {
								System.out.println(saie.getMessage());
								scan.nextLine();
							}
						} while (!flag);	
					} else {
						boolean flag = false;
						do {
							try {
								System.out.print("What type of appliance is it? ");
								String t = scan.nextLine();
								System.out.print("What location is it at? ");
								int loc = scan.nextInt();
								System.out.print("What is the on wattage? ");
								int onW = scan.nextInt();
								System.out.print("What is the off wattage? ");
								int offW = scan.nextInt();
								System.out.print("At any time, what are the odds this "
										+ "appliance is on? ");
								double odds = scan.nextDouble();
								Appliance a1 = new Appliance(t, onW, offW, odds, loc);
								appsList.insert(a1);
								System.out.println("appliance added");
								flag = true;
							} catch (InputMismatchException ime) {
								System.out.println("please enter an integer or real number when "
										+ "appropriate");
								scan.nextLine();
							} catch (ApplianceInvalidException aie) {
								System.out.println(aie.getMessage());
								scan.nextLine();
							}
						} while (!flag);
					}
					scan.nextLine();
				}
				
			} else if (command.equalsIgnoreCase("add appliances from file")) { 
				
				if (appsList == null)
					System.out.println("create a system first");
				else if (appsList.isFull())
					System.out.println("system full, appliances not added");
				else {
					boolean flag = false;
					do {
						try {
							System.out.print("What is the filename? ");
							String filename = scan.nextLine();
							boolean allAdded = appsList.insert(filename);
							if (allAdded)
								System.out.println("appliances added");
							else
								System.out.println("system capacity reached, not all appliances "
										+ "were added");
							flag = true;
						} catch (FileNotFoundException fnfe) {
							System.out.println("file by that name not found");
						}
					} while (!flag);
				}
				
			} else if (command.equalsIgnoreCase("delete appliance")) {
				
				if (appsList == null)
					System.out.println("create a system first");
				else if (appsList.isEmpty())
					System.out.println("system empty");
				else {
					boolean flag = false;
					do {
						try {
							System.out.print("What is the appliance ID? ");
							int id = scan.nextInt();
							boolean deleted = appsList.delete(id);
							if (deleted)
								System.out.println("appliance deleted");
							else
								System.out.println("appliance with that ID not found");
							scan.nextLine();
							flag = true;
						} catch (InputMismatchException ime) {
							System.out.println("ID must be an integer");
							scan.nextLine();
						}
					} while (!flag);
				}
				
			} else if (command.equalsIgnoreCase("find appliance")) {
				
				if (appsList == null)
					System.out.println("create a system first");
				else if (appsList.isEmpty())
					System.out.println("system empty");
				else {
					boolean flag = false;
					do {
						try {
							System.out.print("What is the appliance ID? ");
							int id = scan.nextInt();
							Appliance a1 = appsList.isThere(id);
							if (a1 == null)
								System.out.println("appliance with that ID not found");
							else
								System.out.println(a1);
							scan.nextLine();
							flag = true;
						} catch (InputMismatchException ime) {
							System.out.println("ID must be an integer");
							scan.nextLine();
						}
					} while (!flag);
				}
				
			} else if (command.equalsIgnoreCase("get appliances at location")) {
				
				if (appsList == null)
					System.out.println("create a system first");
				else if (appsList.isEmpty())
					System.out.println("system empty");
				else {
					boolean flag = false;
					do {
						try {
							System.out.print("What location do want the appliances from? ");
							int loc = scan.nextInt();
							Appliance[] apps = appsList.getAppsAtLoc(loc);
							if (apps.length == 0)
								System.out.println("location not found");
							else
								for (int i = 0; i < apps.length; i++)
									System.out.println(apps[i]);
							scan.nextLine();
							flag = true;
						} catch (InputMismatchException ime) {
							System.out.println("location must be an integer");
							scan.nextLine();
						}
					} while (!flag);
				}
				
			} else if (command.equalsIgnoreCase("get appliances of type")) {
				
				if (appsList == null)
					System.out.println("create a system first");
				else if (appsList.isEmpty())
					System.out.println("system empty");
				else {
					System.out.print("What type of appliances do you want? ");
					String t = scan.nextLine();
					Appliance[] apps = appsList.getAppsOfType(t);
					if (apps.length == 0)
						System.out.println("appliances of this type not found");
					else
						for (int i = 0; i < apps.length; i++)
							System.out.println(apps[i]);
				}
				
			} else if (command.equalsIgnoreCase("view summary")) {
				
				if (appsList == null)
					System.out.println("create a system first");
				else if (appsList.isEmpty())
					System.out.println("system empty");
				else
					System.out.println(appsList.summary());
				
			} else if (command.equalsIgnoreCase("start simulation")) {
			
				if (appsList == null)
					System.out.println("create a system first");
				else if (appsList.isEmpty())
					System.out.println("system empty");
				else {
				
					intervalCounter = 1;
					minute = 0;
					locsEffectedCounters = new int[2][appsList.getLocs().length];
					locsEffectedCounters[0] = appsList.getLocs();
					bf = new BufferedWriter(new FileWriter("report.txt", false));
					
					String changeHours;
					do {
						System.out.print("(yes/no) Do you want to change the default "
								+ "simulation length of 24 hours? ");
						changeHours = scan.nextLine();
					} while (!changeHours.equalsIgnoreCase("yes") &&
							!changeHours.equalsIgnoreCase("no"));
					
					int simulationLength = 24;
					if (changeHours.equalsIgnoreCase("yes")) {
						boolean flag = false;
						do {
							try {
								System.out.print("How many hours long is the simulation? ");
								simulationLength = scan.nextInt();
							} catch (InputMismatchException ime) {
								System.out.println("hours must be an integer");
								scan.nextLine();
								continue;
							}
							if (simulationLength <= 0)
								System.out.println("hours must be positive");
							else {
								flag = true;
								scan.nextLine();
							}
						} while (!flag);
					}
					simulationLength *= 60;
					
					String changeMinutes;
					do {
						System.out.print("(yes/no) Do you want to change the default "
								+ "interval length of 5 minutes? ");
						changeMinutes = scan.nextLine();
					} while (!changeMinutes.equalsIgnoreCase("yes") &&
							!changeMinutes.equalsIgnoreCase("no"));
					
					int intervalLength = 5;
					if (changeMinutes.equalsIgnoreCase("yes")) {
						boolean flag = false;
						do {
							try {
								System.out.print("how many minutes long is each interval? ");
								intervalLength = scan.nextInt();
							} catch (InputMismatchException ime) {
								System.out.println("minutes must be an integer");
								scan.nextLine();
								continue;
							}
							if (intervalLength <= 0)
								System.out.println("minutes must be positive");
							else
								flag = true;
						} while (!flag);
					}
					
					int maxWatts = 0;
					boolean flag = false;
					do {
						try {
							System.out.print("What is the system's maximum wattage? ");
							maxWatts = scan.nextInt();
						} catch (InputMismatchException ime) {
							System.out.println("wattage must be an integer");
							scan.nextLine();
							continue;
						}
						if (intervalLength < 0)
							System.out.println("wattage must be positive");
						else
							flag = true;
					} while (!flag);
					System.out.println();
					
					appsList.turnOffAllApps();
					if (appsList.totalCurrentWatts() > maxWatts)
					System.out.println("The desired wattage is too low to be attained even"
							+ " with all the appliances in our system turned off.\n");
					
					while (minute < simulationLength) {
						appsList.turnOffAllApps();
						appsList.turnOnByPercent();
						lowerWatts(maxWatts);
						minute += intervalLength;
						intervalCounter++;
					}
					
					int maxIndex = 0;
					for (int i = 1; i < locsEffectedCounters[1].length; i++)
						if (locsEffectedCounters[1][i] > locsEffectedCounters[1][maxIndex])
							maxIndex = i;
					if (locsEffectedCounters[1][maxIndex] == 0)
						System.out.println("0 locations were effected");
					else
						System.out.println("max effected location: " + locsEffectedCounters[0][maxIndex]);
					
					bf.close();
					scan.nextLine();
					
				}
			
			} else if (command.equalsIgnoreCase("stop")) {
				
				System.out.println("good bye");
				continue;
				
			} else {
				
				System.out.println("please input a valid command");
				
			}
			
			System.out.println();
			
		} while (!command.equalsIgnoreCase("stop"));
		
		scan.close();
		
	}

}