

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Scanner;

import com.vmware.vim25.*;
import com.vmware.vim25.mo.*;

public class TrailVM 
{
	static ServiceInstance si;
	public static void main(String[] args) throws Exception
	{
		System.out.println("CMPE281 HW2 from QI LI");
		if(args.length != 3 || args.length == 0)
		{
			
			System.out.println("Enter the IP, username and password: (or would you like to use default?(Y))");
			
			String str = waitin();
			String[] command = str.split(" ");
			while (command.length != 3)
			{
				if(command[0].equals("Y"))
				{
					command = new String[]{"https://130.65.159.14/sdk", "cmpe281_sec3_student@vsphere.local", "cmpe-LXKN"};
					break;
				}
				System.out.println("Please input: java CreateVM <url> " +
	            "<username> <password>");
				
				str = waitin();
				command = str.split(" ");

			}

			startup(command[0], command[1], command[2]);
		}
		else{
			startup(args[0], args[1], args[2]);
		}
		
		Command cmd = new Command();
		String comm = waitin();
		while(!comm.equals("exit")){
			String str_cmd = cmd.command_correct(comm);
			if (str_cmd != null) {
				cmd.exe(str_cmd, si);
				printPrompt();
				comm = waitin();
			} else {
				System.out.println("no such command");
				printPrompt();
				comm = waitin();
			}
		}
		logout(si);
		
	}
	
	public static void startup(String url, String _name, String pwd) throws IOException
	{
		long start = System.currentTimeMillis();
		si = new ServiceInstance(new URL(url), _name, pwd, true);
		long end = System.currentTimeMillis();
		printPrompt();
	}
	
	public static void logout(ServiceInstance si)
	{
		si.getServerConnection().logout();
	}
	
	public static void printPrompt()
	{
		System.out.print("QILI-352>");
	}

	public static String waitin() throws IOException
	{
		InputStreamReader in = new InputStreamReader(System.in);
		BufferedReader bReader = new BufferedReader(in);
		return bReader.readLine();
	}
}
