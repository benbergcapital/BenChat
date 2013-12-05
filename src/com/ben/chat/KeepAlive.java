package com.ben.chat;

import com.ben.chat.Server.ClientThread;

import java.io.IOException;
import java.util.concurrent.*;

import javax.swing.text.BadLocationException;
public class KeepAlive  extends Thread {
	public Server s;
	private boolean run = true;
	public KeepAlive(Server server) {
		this.s = server;
	}
	public void run()
	{		
		while (run)
		{
		s.lock.readLock().lock();
		{
			 try {
				 for(int i = s.al.size(); --i >= 0;) {
					 ClientThread ct = s.al.get(i);
						
					 try {
						 ct.writeKeepAlive();
						} catch (Exception e1) {
							
					
							run=false;
						}
											
					 
					 
				 }
		        } finally {
		            s.lock.readLock().unlock();
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
