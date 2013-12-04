package com.ben.chat;

import com.ben.chat.Server.ClientThread;

import java.io.IOException;
import java.util.concurrent.*;
public class KeepAlive  extends Thread {
	public Server s;
	public KeepAlive(Server server) {
		this.s = server;
	}
	public void run()
	{		
		while (true)
		{
		s.lock.readLock().lock();
		{
			 try {
				 for(int i = s.al.size(); --i >= 0;) {
					 ClientThread ct = s.al.get(i);
						
						
					
						ct.writeKeepAlive();
					 
					 
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
