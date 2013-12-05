package com.ben.chat;


import java.net.*;
import java.awt.Color;
import java.awt.TrayIcon;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.*;
import java.util.*;

import javax.swing.text.BadLocationException;



/*
 * The Client that can be run both as a console or a GUI
 */
public class Client  {

	// for I/O
	private ObjectInputStream sInput;		// to read from the socket
	private ObjectOutputStream sOutput;		// to write on the socket
	public Socket socket;

	// if I use a GUI or not
	private ClientGUI cg;
	public static boolean Focus = false;
	// the server, the port and the username
	private String server, username;
	private int port;
     public Long  receivingTime=0L;	
	/*
	 *  Constructor called by console mode
	 *  server: the server address
	 *  port: the port number
	 *  username: the username
	 */
Client(String server, int port, String username) {
		// which calls the common constructor with the GUI set to null
		this(server, port, username, null);
	}

	/*
	 * Constructor call when used from a GUI
	 * in console mode the ClienGUI parameter is null
	 */
	Client(String server, int port, String username, ClientGUI cg) {
		this.server = server;
		this.port = port;
		this.username = username;
		// save if we are in GUI mode or not
		this.cg = cg;
	}
	
	/*
	 * To start the dialog
	 */
	public boolean start() throws BadLocationException {
	//	 System.getProperties().list(System.out);
		// try to connect to the server
		try {
			socket = new Socket(server, port);
		//	Socket socket = new Socket();
			//socket.connect(new InetSocketAddress(server, port), 1000);
		} 
		// if it failed not much I can so
		catch(Exception ec) {
			display("Error connectiong to server:" + ec);
			return false;
		}
		
		String msg = "Connection accepted";
		display(msg);
	
		/* Creating both Data Stream */
		try
		{
			sInput  = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException eIO) {
			display("Exception creating new Input/output Streams: " + eIO);
			return false;
		}

		// creates the Thread to listen from the server 
		new ListenFromServer().start();
		// Send our username to the server this is the only message that we
		// will send as a String. All other messages will be ChatMessage objects
		try
		{
			sOutput.writeObject(username);
		}
		catch (IOException eIO) {
			display("Exception doing login : " + eIO);
			disconnect();
			return false;
		}
		// success we inform the caller that it worked
		
		return true;
	}

	
	
	
	/*
	 * To send a message to the console or the GUI
	 */
	public void display(String msg) throws BadLocationException {
		if(cg == null)
			System.out.println(msg);      // println in console mode
		else
			cg.append(msg + "\n",null,0);		// append to the ClientGUI JTextArea (or whatever)
	}
	
	/*
	 * To send a message to the server
	 */
	public void sendMessage(ChatMessage msg)  {
		try {
			sOutput.writeObject(msg);
		}
		catch(IOException e) {
			
			try {
				display("Exception writing to server: " + e);
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	public boolean sendHeartbeat()  {
		 Long lDateTime = new Date().getTime();
		ChatMessage cm = new ChatMessage(ChatMessage.PING,lDateTime);
		
		try {
			sOutput.writeObject(cm);
			return true;
		}
		catch(IOException e) {
			
			try {
			//	display("Exception writing to server: " + e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return false;
		}
	}
	public void login()
	{
		
		cg.login(true);
	}

	/*
	 * When something goes wrong
	 * Close the Input/Output streams and disconnect not much to do in the catch clause
	 */
	public void disconnect() {
		try { 
			if(sInput != null) sInput.close();
		}
		catch(Exception e) {} // not much else I can do
		try {
			if(sOutput != null) sOutput.close();
		}
		catch(Exception e) {} // not much else I can do
        try{
			if(socket != null) socket.close();
		}
		catch(Exception e) {} // not much else I can do
		
		// inform the GUI
		if(cg != null)
			cg.connectionFailed();
			
	}
	/*
	 * To start the Client in console mode use one of the following command
	 * > java Client
	 * > java Client username
	 * > java Client username portNumber
	 * > java Client username portNumber serverAddress
	 * at the console prompt
	 * If the portNumber is not specified 1500 is used
	 * If the serverAddress is not specified "localHost" is used
	 * If the username is not specified "Anonymous" is used
	 * > java Client 
	 * is equivalent to
	 * > java Client Anonymous 1500 localhost 
	 * are eqquivalent
	 * 
	 * In console mode, if an error occurs the program simply stops
	 * when a GUI id used, the GUI is informed of the disconnection
	 */
	public static void main(String[] args) throws BadLocationException {
		
		// default values
		int portNumber = 1500;
		String serverAddress = "localhost";
		String userName = "Anonymous";

		// depending of the number of arguments provided we fall through
		switch(args.length) {
			// > javac Client username portNumber serverAddr
			case 3:
				serverAddress = args[2];
			// > javac Client username portNumber
			case 2:
				try {
					portNumber = Integer.parseInt(args[1]);
				}
				catch(Exception e) {
					System.out.println("Invalid port number.");
					System.out.println("Usage is: > java Client [username] [portNumber] [serverAddress]");
					return;
				}
			// > javac Client username
			case 1: 
				userName = args[0];
			// > java Client
			case 0:
				break;
			// invalid number of arguments
			default:
				System.out.println("Usage is: > java Client [username] [portNumber] {serverAddress]");
			return;
		}
		// create the Client object
		Client client = new Client(serverAddress, portNumber, userName);
		// test if we can start the connection to the Server
		// if it failed nothing we can do
		if(!client.start())
			return;
		
		// wait for messages from user
		Scanner scan = new Scanner(System.in);
		// loop forever for message from the user
		while(true) {
			System.out.print("> ");
			// read message from user
			String msg = scan.nextLine();
			// logout if message is LOGOUT
			if(msg.equalsIgnoreCase("LOGOUT")) {
				client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
				// break to do the disconnect
				break;
			}
			// message WhoIsIn
			else if(msg.equalsIgnoreCase("WHOISIN")) {
				client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));				
			}
			else {				// default to ordinary message
				client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msg));
			}
		}
		// done disconnect
		client.disconnect();	
	}

	/*
	 * a class that waits for the message from the server and append them to the JTextArea
	 * if we have a GUI or simply System.out.println() it in console mode
	 */
	class ListenFromServer  extends Thread {

		public void run(){
			while(true) {
				try {
					ChatMessage cm = (ChatMessage) sInput.readObject();
				//	cm = (ChatMessage) sInput.readObject();
					
					
				/*
					String[] message = (String[]) sInput.readObject();
					String msg = message[0];
					
					String colour = message[1];
					int size=0;
					int j = message.length;
					*/				
					// if console mode print the message and add back the prompt
					if(cg == null) {
						System.out.println(cm.toString());
						System.out.print("> ");
					}
					else {
						String message = cm.getMessage();
						String colour = cm.getColour();
						String size = cm.getSize();
						String name = cm.getName();
						byte[] image = cm.getImage();
						Long _receivingTime = cm.getTime();
						switch(cm.getType()) {

						case ChatMessage.NUDGE:
											
						//	String[] array = msg.split(";"); 
							
							cg.append(message,null,0);
							java.awt.Toolkit.getDefaultToolkit().beep();
							java.awt.Toolkit.getDefaultToolkit().beep();
							try
							{
							 final int originalX = cg.getLocationOnScreen().x; 
						      final int originalY = cg.getLocationOnScreen().y; 
						      for(int i = 0; i < 10; i++) { 
						        Thread.sleep(10); 
						        cg.setLocation(originalX, originalY + 12); 
						        Thread.sleep(10); 
						        cg.setLocation(originalX, originalY - 12);
						        Thread.sleep(10); 
						        cg.setLocation(originalX + 12, originalY);
						        Thread.sleep(10); 
						        cg.setLocation(originalX, originalY); 
						      }
							}
						      catch(Exception ex)
						      {
						    	  
						    	  
						      }
							
						break;
						case ChatMessage.TYPING:						
						
							cg.setTyping(name,message);
							break;
							
						
					/*
						else if (msg.contains("SYSTEM-VERSION"))
						{
							msg.substring(msg.indexOf(";")+1);
							String[] array = msg.split(";"); 
							cg.showNewVersionAvailable("A newer version ("+array[1]+") is availalbe. Download now");
							
							
						}
						*/
						
				case ChatMessage.MESSAGE:
							cg.append(message,colour,0);
							
							if(Focus==false)
							{
							java.awt.Toolkit.getDefaultToolkit().beep();
							}
							break;
				case ChatMessage.IMAGE:
					cg.appendImage(name,colour,image);
					
					break;		
				case ChatMessage.PING:
					receivingTime = _receivingTime;
					Long lDateTime = new Date().getTime();
					Long delta =  lDateTime - _receivingTime;
									
					cg.updateLatency(delta);
										
					
					break;	
					}
					}
				}
				catch(IOException e) {
					
					try {
						display("Server has closed the connection");
					} catch (BadLocationException e1) {
						
						e1.printStackTrace();
					}
					if(cg != null) 
						cg.connectionFailed();
					break;
				}
				// can't happen with a String object but need the catch anyhow
				catch(ClassNotFoundException e2) {
				} catch (BadLocationException e) {
					
					e.printStackTrace();
				}
			}
		}
	}
	class ClientHeartbeat  extends Thread {

		public void run(){
			while(true) {
				try {
				//	cg.append(receivingTime.toString(),null,0);
					
					
				}
				catch(Exception e)
				{
					
				}
				}
		}
	}
}

