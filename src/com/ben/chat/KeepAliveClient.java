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
		while (run)
		{
				
			
			 Long lDateTime = new Date().getTime();
				Long delta = (lDateTime - c.receivingTime)/1000;
			if (delta > 10 && c.receivingTime!= 0)
			{
			try {
				c.display("A response fromt the server has no been received for "+delta+" seconds");
			
				 c.display("Reconnecting...");
				 c.disconnect();
				 c.login();
				 run=false;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		
			
			 if(!c.sendHeartbeat())
				 {
					
				 
				 }
			 else
			 {
			 try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 }
		 
		
		 }
			
		}
		
		
		
		
	}
