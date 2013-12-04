package com.ben.chat;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.AbstractAction;

class HtmlListener extends AbstractAction
{
    private String textLink;

    HtmlListener(String textLink)
    {
        this.textLink = textLink.trim();
    }

    protected void execute()
    {
    	if(Desktop.isDesktopSupported())
    	{
    	  try {
    	
			Desktop.getDesktop().browse(new URI(textLink));

    	  		}
    	  catch (Exception e) {
		System.out.println(e.toString());
    	}
    	}
    	else
    	{
    		 Runtime runtime = Runtime.getRuntime();
    		  String[] args = { "osascript", "-e", "open location \"" + textLink + "\"" };
    		  try
    		  {
    		    Process process = runtime.exec(args);
    		  }
    		  catch (IOException e)
    		  {
    		    // do what you want with this
    		  }
    		
    	}
    }



	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		execute();
	}
}