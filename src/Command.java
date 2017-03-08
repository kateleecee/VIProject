import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.mo.Datastore;
import com.vmware.vim25.mo.EventManager;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.Network;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

public class Command {
	private List<String> COMMAND = new ArrayList<String>();
	private String hname;
	private String vname;

	public Command() {
		COMMAND.add("help");
		COMMAND.add("host");
		COMMAND.add("host info");
		COMMAND.add("host datastore");
		COMMAND.add("host network");
		COMMAND.add("vm");
		COMMAND.add("vm info");
		COMMAND.add("vm on");
		COMMAND.add("vm off");
		COMMAND.add("vm shutdown");
	}

	public void command_help() {
		System.out.printf("%-25s%s", "exit", "Exit the program\n");
		System.out.printf("%-25s%s", "help", "Print out the usage\n");
		System.out.printf("%-25s%s", "host", "Enumerate all the hosts\n");
		System.out.printf("%-25s%s", "host hname info", "Show info of host hname\n");
		System.out.printf("%-25s%s", "host hname datastore", "Enumerate datastores of host hname\n");
		System.out.printf("%-25s%s", "host hname network", "Enumerate network of host hname\n");
		System.out.printf("%-25s%s","vm", "Enumerate all virtual machines\n");
		System.out.printf("%-25s%s","vm vname info", "Show info of VM vname\n");
		System.out.printf("%-25s%s","vm vname on", "Power on VM vname and wait until task completes\n");
		System.out.printf("%-25s%s","vm vname off", "Power off VM vname and wait until task completes\n");
		System.out.printf("%-25s%s","vm vname shutdown", "Shutdown guest of VM vname\n");
	}

	public void command_host(ServiceInstance si) throws InvalidProperty, RuntimeFault, RemoteException {
		Folder rootFolder = si.getRootFolder();
		ManagedEntity[] hosts = new InventoryNavigator(rootFolder)
				.searchManagedEntities(new String[][] { { "HostSystem", "name" }, }, true);
		for (int i = 0; i < hosts.length; i++) {
			System.out.println("host[" + i + "]: Name = " + hosts[i].getName());
		}
		if (hosts.length == 0) {
			System.out.println("No host");
		}
	}

	public void command_hinfo(ServiceInstance si) throws InvalidProperty, RuntimeFault, RemoteException {
		// System.out.println("command_that never reach");
		Folder rootFolder = si.getRootFolder();
		HostSystem host = (HostSystem) new InventoryNavigator(rootFolder).searchManagedEntity("HostSystem", hname);
		if (host == null) {
			System.out.println("Can't find the host");
		} else {
			System.out.println("Name = " + hname);
			System.out.println("ProductFullName = " + host.getConfig().product.fullName);
			System.out.println("Cpu cores = " + host.getHardware().cpuInfo.numCpuCores);
			System.out.println("RAM = " + host.getHardware().memorySize / 1024 / 1024 / 1024 + "GB");
		}
	}

	public void command_hdatastore(ServiceInstance si) throws InvalidProperty, RuntimeFault, RemoteException {
		Folder rootFolder = si.getRootFolder();
		HostSystem host = (HostSystem) new InventoryNavigator(rootFolder).searchManagedEntity("HostSystem", hname);
		if (host == null) {
			System.out.println("Can't find the host");
		} else {
			System.out.println("Name = " + hname);
			Datastore[] dStores = host.getDatastores();
			if (dStores.length == 0) {
				System.out.println("No datastore");
			} else {
				for (int i = 0; i < dStores.length; i++) {
					System.out.println("Datastore[" + i + "]: name = " + dStores[i].getName() + ", capacity = "
							+ dStores[i].getSummary().capacity / 1024 / 1024 / 1024 + "GB, freespace = "
							+ dStores[i].getSummary().freeSpace / 1024 / 1024 / 1024 + "GB");
				}
			}
		}
	}

	public void command_hnetwork(ServiceInstance si) throws InvalidProperty, RuntimeFault, RemoteException {
		Folder rootFolder = si.getRootFolder();
		HostSystem host = (HostSystem) new InventoryNavigator(rootFolder).searchManagedEntity("HostSystem", hname);
		if (host == null) {
			System.out.println("Can't find the host");
		} else {
			System.out.println("Name = " + hname);
			Network[] network = host.getNetworks();
			if (network.length == 0) {
				System.out.println("No network");
			} else {
				for (int i = 0; i < network.length; i++) {
					System.out.println("Network[" + i + "]: name = " + network[i].getName());
				}
			}
		}
	}

	public void command_vm(ServiceInstance si) throws InvalidProperty, RuntimeFault, RemoteException {
		Folder rootFolder = si.getRootFolder();
		ManagedEntity[] vms = new InventoryNavigator(rootFolder)
				.searchManagedEntities(new String[][] { { "VirtualMachine", "name" }, }, true);
		if (vms.length == 0) {
			System.out.println("No VM");
		} else {
			for (int i = 0; i < vms.length; i++) {
				System.out.println("vm[" + i + "]: Name = " + vms[i].getName());
			}
		}

	}

	public void command_vinfo(ServiceInstance si) throws InvalidProperty, RuntimeFault, RemoteException {
		Folder rootFolder = si.getRootFolder();
		VirtualMachine vm = (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine",
				vname);
		if (vm == null) {
			System.out.println("Can't find the VM");
		} else {
			System.out.println("Name = " + vm.getName());
			System.out.println("Guest full name = " + vm.getConfig().guestFullName);
			System.out.println("Guest state = " + vm.getGuest().guestState);
			System.out.println("IP addr = " + vm.getGuest().ipAddress);
			System.out.println("Tool running status = " + vm.getGuest().toolsRunningStatus);
			System.out.println("Power state = " + vm.getRuntime().powerState);
		}
	}

	public void command_von(ServiceInstance si)
			throws InvalidProperty, RuntimeFault, RemoteException, InterruptedException {
		Folder rootFolder = si.getRootFolder();
		VirtualMachine vm = (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine",
				vname);
		String stat = null;
		if (vm == null) {
			System.out.println("Can't find the VM");
		} else {
			System.out.println("Name = " + vm.getName());

			Task task = null;
			task = vm.powerOnVM_Task(null);
			task.waitForTask();
			stat = task.getTaskInfo().state.toString();
			if (stat.equalsIgnoreCase("error")) {
				stat = task.getTaskInfo().getError().localizedMessage;
			}
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			System.out.println("Power on VM: status = " + stat + ", completion time = "
					+ sdf.format(task.getTaskInfo().completeTime.getTime()));

		}
	}

	public void command_voff(ServiceInstance si)
			throws InvalidProperty, RuntimeFault, RemoteException, InterruptedException {
		String stat = null;
		Folder rootFolder = si.getRootFolder();
		VirtualMachine vm = (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine",
				vname);
		if (vm == null) {
			System.out.println("Can't find the VM");
		} else {
			System.out.println("Name = " + vm.getName());

			Task task = null;
			task = vm.powerOffVM_Task();
			task.waitForTask();
			stat = task.getTaskInfo().state.toString();
			if (stat.equalsIgnoreCase("error")) {
				stat = task.getTaskInfo().getError().localizedMessage;
			}
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			System.out.println("Power off VM: status = " + stat + ", completion time = "
					+ sdf.format(task.getTaskInfo().completeTime.getTime()));
		}
	}

	public void command_vshutdown(ServiceInstance si)
			throws InvalidProperty, RuntimeFault, RemoteException, InterruptedException {
		Folder rootFolder = si.getRootFolder();
		int timeout = 0;

		VirtualMachine vm = (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine",
				vname);
		if (vm == null) {
			System.out.println("Can't find the VM");
		} else {
			System.out.println("Name = " + vm.getName());
			try {
				vm.shutdownGuest();
			} catch (Exception e) {
			}
			while (true) {
				Thread.sleep(2000);
				timeout++;
				vm = (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine", vname);
				if (vm.getRuntime().powerState == VirtualMachinePowerState.poweredOff) {
					break;
				}
				if (timeout % 90 == 0) {
					break;
				}
			}
		}
		if (timeout % 90 == 0) {
			System.out.println("Graceful shutdown failed. Now try a hard power off.");
			Task task = vm.powerOffVM_Task();
			task.waitForTask();
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			System.out.println("Power off VM: status = " + task.getTaskInfo().state + ", completion time = "
					+ sdf.format(task.getTaskInfo().completeTime.getTime()));
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			System.out.println("Shutdown guest: completed, time = " + sdf.format(new Date()));
		}
	}

	public String command_correct(String in) {
		in = in.trim();
		Iterator<String> it = COMMAND.iterator();
		while (it.hasNext()) {
			String el = it.next();
			if (el.equals(in)) {
				return el;
			}
			if (in.contains("host") && in.contains("info")) {
				hname = in.split(" ")[1];
				return COMMAND.get(2);
			}
			if (in.contains("host") && in.contains("datastore")) {
				hname = in.split(" ")[1];
				return COMMAND.get(3);
			}
			if (in.contains("host") && in.contains("network")) {
				hname = in.split(" ")[1];
				return COMMAND.get(4);
			}
			if (in.contains("vm") && in.contains("info")) {
				vname = in.split(" ")[1];
				return COMMAND.get(6);
			}
			if (in.contains("vm") && in.contains("on")) {
				vname = in.split(" ")[1];
				return COMMAND.get(7);
			}
			if (in.contains("vm") && in.contains("off")) {
				vname = in.split(" ")[1];
				return COMMAND.get(8);
			}
			if (in.contains("vm") && in.contains("shutdown")) {
				vname = in.split(" ")[1];
				return COMMAND.get(9);
			}
		}

		return null;
	}

	public void exe(String in, ServiceInstance si)
			throws InvalidProperty, RuntimeFault, RemoteException, InterruptedException {
		switch (in) {
		case "host info":
			command_hinfo(si);
			break;
		case "help":
			command_help();
			break;
		case "host":
			command_host(si);
			break;
		case "host datastore":
			command_hdatastore(si);
			break;
		case "host network":
			command_hnetwork(si);
			break;
		case "vm":
			command_vm(si);
			break;
		case "vm info":
			command_vinfo(si);
			break;
		case "vm on":
			command_von(si);
			break;
		case "vm off":
			command_voff(si);
			break;
		case "vm shutdown":
			command_vshutdown(si);
			break;

		default:
			break;
		}
	}
}
