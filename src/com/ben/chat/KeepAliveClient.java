package com.ben.chat;

import java.util.Date;

import javax.swing.text.BadLocationException;

public class KeepAliveClient  extends Thread {
	public Client c;
	public KeepAliveClient(Client c) {
		this.c = c;
	}
	public void run()
	{		
		boolean run=true;
		while(run)
		{
			 Long lDateTime = new Date().getTime();
			 
			Long delta = (lDateTime - c.receivingTime)/1000;
			 if (delta > 10 && c.receivingTime!= 0)
			 {
				 try {
					c.display("Last message received over " +delta.toString()+" seconds ago");
					c.display("Attempting reconnect...");
					c.disconnect();
					c.login();
					run=false;
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
			
			 try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
				
					e.printStackTrace();
				}
			
			
			
		}
		
		
		
		
	}
	}