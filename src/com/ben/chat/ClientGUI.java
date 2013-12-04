package com.ben.chat;

import com.ben.screenshot.SliceRectangleHandler;
import com.ben.screenshot.TransparentFrame;


import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTML;
import javax.swing.text.html.parser.AttributeList;
import javax.swing.text.html.parser.Element;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.SystemTray;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/*
 * The Client with its GUI
 */
public class ClientGUI extends JFrame implements ActionListener {

	private String _guiVersion = "0.6";
	
	private static final long serialVersionUID = 1L;
	// will first hold "Username:", later on "Enter message"
	private JLabel label,label_status,lblstatus,lbltyping,lblVersion,lbllatency;
	// to hold the Username and later on the messages
	private JTextField tf;
	// to hold the server address an the port number
	private JTextField tfServer, tfPort;
	// to Logout and get the list of the users
	private JButton login,  whoIsIn,Nudge;
	// for the chat room
	private JTextArea ta;
	// for the chat room
	private JTextArea ta_users;
	// if it is for connection
	private boolean connected;
	// the Client object
	private Client client;
	// the default port number
	private int defaultPort;
	private String defaultHost;
	private String name;
	private boolean typing;
	
	private JTextPane tp;
	private JColorComboBox JC;
	private JComboBox fontSize;
	private JMenuItem logout,screenshot;
	// Constructor connection receiving a socket number
	ClientGUI(String host, int port) throws BadLocationException {

		super("Chat Client");
		defaultPort = port;
		defaultHost = host;
		
		 JMenuBar menubar = new JMenuBar();
		 JMenu file = new JMenu("File");
		  logout = new JMenuItem("Logout");
		  logout.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent event) {
	            	client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
	    			lblstatus.setText("Status : Disconnected");
	            }
	        });
		  
		 screenshot = new JMenuItem("Send Screen Shot");
		 screenshot.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent event) {
	                screenshot_handler();
	            }
	        });
		 
		 
		 
		 
		  file.add(logout);
		  file.add(screenshot);
	     menubar.add(file);
	     setJMenuBar(menubar);
	        
		// The NorthPanel with:
		JPanel northPanel = new JPanel(new GridLayout(2,1));
		// the server name anmd the port number
		JPanel serverAndPort = new JPanel(new GridLayout(1,5, 1, 3));
		JPanel status = new JPanel(new GridLayout(1,2, 1, 3));
		// the two JTextField with default value for server address and port number
		tfServer = new JTextField(host);
		tfPort = new JTextField("" + port);
		tfPort.setHorizontalAlignment(SwingConstants.RIGHT);

		serverAndPort.add(new JLabel("Server Address:  "));
		serverAndPort.add(tfServer);
		//serverAndPort.add(new JLabel("Port Number:  "));
	//	serverAndPort.add(tfPort);
	//	serverAndPort.add(new JLabel(""));
		serverAndPort.add(new JLabel("Text Colour"));
		
		//Colour dropdown 
		
		Box box = new Box(BoxLayout.PAGE_AXIS);
	      JPanel panel = new JPanel();
	      JC =  new JColorComboBox(JColorComboBox.RECT,
                  new Color[] { Color.black, Color.red,     Color.green,   Color.blue,     Color.orange },
                  new String[] { "Black", "Red", "Green", "Blue", "Orange" });

	      
	      panel.add(JC);
	      box.add(panel);
		  serverAndPort.add(box);
		
		
		// adds the Server an port field to the GUI
		northPanel.add(serverAndPort);

		
		
		
		
		
		
		
		JPanel row2_2 = new JPanel(new GridLayout(1,2, 1, 0));
		JPanel row2_1 = new JPanel(new GridLayout(1,2, 1, 0));
		lblstatus = new JLabel("Status: Disconnected");
		lbllatency = new JLabel("Latency: ");
		status.add(lblstatus);
		status.add(lbllatency);
		row2_1.add(status);
		
		
		fontSize = new JComboBox();
		
		  for (int i=6;i<48;i++)
		  {
			  fontSize.addItem(i);
			  i++;
		  }
		  fontSize.setSelectedIndex(4);
		//  row2.add(new JLabel(""));
		 row2_2.add(new JLabel("Font Size")); 
		row2_2.add(fontSize);
		
		JPanel row2 = new JPanel(new GridLayout(1,1, 1, 0));
		row2.add(row2_1);
		row2.add(row2_2);
		northPanel.add(row2);

		//Adds listenter for when 
			

		
		if (System.getProperty("os.name").contains("Windows"))
		{
			 name =  "Ben";
		}
		else
		{
			 name = "KC";
		}
		tf = new JTextField(name);
		tf.setBackground(Color.WHITE);
	boolean _sentTyping = false;
		tf.getDocument().addDocumentListener(new DocumentListener() 
		{
			  public void changedUpdate(DocumentEvent e) {
				 
			  }
			  public void removeUpdate(DocumentEvent e) {
				
				  if (connected && tf.getText().length()>1 && !typing)
				  {
					  client.sendMessage(new ChatMessage(ChatMessage.TYPING, "TRUE"));		
					  typing=true;
					 
				  }
				  if (connected && tf.getText().length()<1)
				  {
					  client.sendMessage(new ChatMessage(ChatMessage.TYPING, "FALSE"));		
					  typing=false;
					 
				  }
			  }
			  public void insertUpdate(DocumentEvent e) {
				  if (connected && tf.getText().length()>1 && !typing)
				  {
					  client.sendMessage(new ChatMessage(ChatMessage.TYPING, "TRUE"));		
					  typing=true;
					 
				  }
				  if (connected && tf.getText().length()<1)
				  {
					  client.sendMessage(new ChatMessage(ChatMessage.TYPING, "FALSE"));		
					  typing=false;
					 
				  }
				  
				  
				  
			  }
		
		});
	
			

		add(northPanel, BorderLayout.NORTH);

		// The CenterPanel which is the chat room
		//ta = new JTextArea("Welcome - 喂 \n", 60, 60);
		
		  
		  
				tp = new JTextPane();
			tp.addMouseListener(new MouseAdapter(){
				@Override
				public void mouseClicked(MouseEvent e)
				{
					 javax.swing.text.Element ele = tp.getStyledDocument().getCharacterElement(tp.viewToModel(e.getPoint()));
			            AttributeSet as = ele.getAttributes();
			            HtmlListener fla = (HtmlListener)as.getAttribute("linkact");
			            if(fla != null)
			            {
			                fla.execute();
			            }
				}
				
			}
			);
		tp.setEditorKit(new WrapEditorKit());
	
		tp.setEditable(false);
		//tp.set
		StyledDocument doc = tp.getStyledDocument();
		
			doc.insertString(0, "Welcome - 喂 \n", null );
			
			
			JScrollPane jsp = new JScrollPane(tp);
	        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
	  	JPanel centerPanel = new JPanel(new GridLayout(1,2));
		centerPanel.add(jsp);
		add(centerPanel, BorderLayout.CENTER);
		
		
		add(centerPanel, BorderLayout.CENTER);
		
		
		login = new JButton("Login");
		login.addActionListener(this);
		
		
		logout.setEnabled(false);		// you have to login before being able to logout
		whoIsIn = new JButton("Who is in");
		whoIsIn.addActionListener(this);
		whoIsIn.setEnabled(false);
		
		// you have to login before being able to Who is in

		JPanel southPanel = new JPanel(new GridLayout(3,1));
		JPanel buttons = new JPanel(new GridLayout(1,4, 1, 3));
		
		
		label = new JLabel("Enter your username below", SwingConstants.LEFT);
		lbltyping = new JLabel("", SwingConstants.LEFT);
		
		JPanel nudgePanel = new JPanel(new GridLayout(1,4));
		Nudge = new JButton("Nudge");
		Nudge.addActionListener(this);
		Nudge.setEnabled(false);
		nudgePanel.add(label);
		nudgePanel.add(lbltyping);
		nudgePanel.add(Nudge);
		southPanel.add(nudgePanel);
		
	//	southPanel.add(label);
		southPanel.add(tf);
		buttons.add(login);
		buttons.add(whoIsIn);
	southPanel.add(buttons);
	
/*	JPanel statusPanel = new JPanel();
	statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
	statusPanel.setPreferredSize(new Dimension(this.getWidth(), 16));
	
	
	
	southPanel.add(statusPanel);
	*/
		add(southPanel, BorderLayout.SOUTH);
	
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(400, 400);
		setVisible(true);
		
		/*
		this.add(statusPanel, BorderLayout.SOUTH);
		
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
		JLabel statusLabel = new JLabel("status");
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		statusPanel.add(statusLabel);
*/
		

		
		
		tf.requestFocus();
			
		//Makes noise if window is out of focus.
		addWindowFocusListener(new WindowAdapter() {
		    public void windowLostFocus(WindowEvent e) {
		    	
		    	Client.Focus = false;
		    	//java.awt.Toolkit.getDefaultToolkit().beep();
		    }
		});
		addWindowFocusListener(new WindowAdapter() {
		    public void windowGainedFocus(WindowEvent e) {
		    	
		    	Client.Focus = true;
		    	//java.awt.Toolkit.getDefaultToolkit().beep();
		    }
		});
		
		
		
		
		
		
		
		
		
login(false);
	}
	 private int linkID = 0;
	// called by the Client to append text in the TextArea 
	void append(String str, String c,int size) throws BadLocationException {
		Style  style = tp.addStyle("I'm a Style", null);
		Color color = null;
		//Colour is sent from gui as string. If null, need style to be null too.
		if (!(c==null) )
		{
		 
		  switch (c){
		  case "Black" : color = color.black;
		  	break;
		  case "Red" : color = color.red;
		  	break;
		  case "Green" : color = color.green;
		  	break;	
		  case "Blue" : color = color.blue;
		  	break;	
		  case "Orange" : color = color.orange;
		  	break;
		  }
		  StyleConstants.setForeground(style, color);
		}
			int fot = Integer.parseInt(fontSize.getSelectedItem().toString());
	       
	        StyleConstants.setFontSize(style, fot);
	  
	       if (str.contains("www.")|| str.contains("http://") || str.contains("https://"))
	       {
	    	   String[] words = str.split(" ");
	    	 
	    	  for (String s : words)
	    	  {
	    		  if (s.startsWith("www.")|| s.startsWith("http://") || s.startsWith("https://"))
	    		  {
	    			  appendHyperlink(s);
	    			//  tp.getStyledDocument().insertString(tp.getStyledDocument().getLength(),, style);
	    		  }
	    		  else
	    		  {
	    			  tp.getStyledDocument().insertString(tp.getStyledDocument().getLength(),s+" ", style); 
	    		  }
	    	  }
	    	 
	    	   
	       }
	    
	       else
	       {
	       tp.getStyledDocument().insertString(tp.getStyledDocument().getLength(),str, style);
	       }
		//Sets curser to bottom of screen for autoscrolling
	       
	       String ff = tp.getStyledDocument().getText(tp.getStyledDocument().getLength()-1, 1);
		     
	       
	       
		tp.setCaretPosition(tp.getStyledDocument().getLength());
		  String ff2 = tp.getStyledDocument().getText(tp.getStyledDocument().getLength(), 1);
		
		  
		  if(!(tp.getStyledDocument().getText(tp.getStyledDocument().getLength()-1, 1).equals("\n")))
		  {
	//		  tp.setCaretPosition(tp.getStyledDocument().getLength()-2);
		  }
		  
		
		  
		  
	}
	
	private void  appendHyperlink(String str) throws BadLocationException
	{
		Style html = tp.addStyle("html", StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE));
	 
        StyleConstants.setUnderline(html, true);
        StyleConstants.setForeground(html, Color.blue);
       html.addAttribute("linkact", new HtmlListener(str));
        
     
        tp.getStyledDocument().insertString(tp.getStyledDocument().getLength(), str, html);
        
	}
	public void mouseClicked(MouseEvent e)
    {
        javax.swing.text.Element ele = tp.getStyledDocument().getCharacterElement(tp.viewToModel(e.getPoint()));
        AttributeSet as = ele.getAttributes();
        HtmlListener fla = (HtmlListener)as.getAttribute("linkact");
        if(fla != null)
        {
            fla.execute();
        }
    }
	
	void appendImage(String name,String colour,byte[] image) throws BadLocationException
	{
		append(name+" sent an image\n",colour,0);
		ImageIcon pictureImage = new ImageIcon(image);

		StyledDocument doc = (StyledDocument)tp.getDocument();

	    // The image must first be wrapped in a style
	    Style style = doc.addStyle("StyleName", null);
	   
	    StyleConstants.setIcon(style, new ImageIcon(image));

	    // Insert the image at the end of the text
	    doc.insertString(doc.getLength(), "ignored text", style);
		
		
       // tp.insertIcon(pictureImage);
        append("\n",colour,0);
	//	tp.add(l);
		
	}
	void setTyping(String name, String message)
	{
	
			if (!this.name.equals(name) && message.contains("TRUE"))
			{
				lbltyping.setText(name+" is typing...");
			}
			else
			{
				lbltyping.setText("");
			}	
		}
		
	
	void showNewVersionAvailable(String str)
	{
		
		int reply=JOptionPane.showConfirmDialog(null,new JLabel(str) , "Update", JOptionPane.YES_NO_OPTION);
		 if (reply == JOptionPane.YES_OPTION) {
			 try {
					String url = "http://ben512.no-ip.org/";
			         java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		 }
	}
	
	
	
	// called by the GUI is the connection failed
	// we reset our buttons, label, textfield
	void connectionFailed() {
		login.setEnabled(true);
		logout.setEnabled(false);
		whoIsIn.setEnabled(false);
		Nudge.setEnabled(false);
		label.setText("Enter your username");
		
		tf.setText(name);
		// reset port number and host name as a construction time
		tfPort.setText("" + defaultPort);
		tfServer.setText(defaultHost);
		// let the user change them
		tfServer.setEditable(false);
		tfPort.setEditable(false);
		// don't react to a <CR> after the username
		tf.removeActionListener(this);
		connected = false;
		lblstatus.setText("Status : Disconnected");
	}
		
	/*
	* Button or JTextField clicked
	*/
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		// if it is the Logout button
		if(o == logout) {
			client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
			lblstatus.setText("Status : Disconnected");
			return;
		}
		// if it the who is in button
		if(o == whoIsIn) {
			client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));				
			return;
		}

		if(o == Nudge) {
		
			
			client.sendMessage(new ChatMessage(ChatMessage.NUDGE, ""));				
			return;
		}
		
		
		
		
		
		
		// ok it is coming from the JTextField
		if(connected) {
			
			if (tf.getText().length() >0 && !tf.getText().equals(" "))
			{

			client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, tf.getText(),JC.getSelectedItem().toString(),fontSize.getSelectedItem().toString()));				
			tf.setText("");
			}
			return;
		}
		

		if(o == login) {
			login(false);
			
		}
	}
	public void login(boolean reconnect)
	{
			// ok it is a connection request
			name = tf.getText().trim();
			
			// empty username ignore it
			if(name.length() == 0)
				return;
			// empty serverAddress ignore it
			String server = tfServer.getText().trim();
			if(server.length() == 0)
				return;
			// empty or invalid port numer, ignore it
			String portNumber = tfPort.getText().trim();
			if(portNumber.length() == 0)
				return;
			int port = 0;
			try {
				port = Integer.parseInt(portNumber);
			}
			catch(Exception en) {
				return;   // nothing I can do if port number is not valid
			}

			// try creating a new Client with GUI
			
			client = new Client(server, port, name, this);
			// test if we can start the Client
			try {
				if(!client.start()) 
					return;
			} catch (BadLocationException e) {
				
				e.printStackTrace();
			}
		//	client.start_heartbeat();
			
			//Check gui version
			
		//	client.sendMessage(new ChatMessage(ChatMessage.VERSION, _guiVersion));
			
			
			
			tf.setText("");
			label.setText("Enter your message below");
			connected = true;
			
			// disable login button
			login.setEnabled(false);
			// enable the 2 buttons
			logout.setEnabled(true);
			whoIsIn.setEnabled(true);
			Nudge.setEnabled(true);
			// disable the Server and Port JTextField
			tfServer.setEditable(false);
			tfPort.setEditable(false);
			// Action listener for when the user enter a message
			tf.addActionListener(this);
			
			if (connected && !reconnect)
			{
				client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));
				lblstatus.setText("Status : Connected");
				KeepAliveClient k = new KeepAliveClient(client);
				k.start();
			}
			}
		

	

	// to start the whole thing the server
	public static void main(String[] args) {
	try{
	
		new ClientGUI("localhost", 5124);
		
	}
	catch (Exception e)
	{}
	}
	public static TransparentFrame TRANSPARENT_FRAME;
	
	void screenshot_handler()
	{
		screenshot(this);
	}
	
	void screenshot(ClientGUI g)
	{
	//	SwingUtilities.invokeLater(new Runnable() {

	//		@Override
	//		public void run() {
		
		
				SliceRectangleHandler sliceRectangleHandler = new SliceRectangleHandler(g);
		
				TRANSPARENT_FRAME = new TransparentFrame(sliceRectangleHandler);

		
	}
	public void sendImage(BufferedImage _image)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write( _image, "png", baos );
			baos.flush();
			byte[] imageInByte = baos.toByteArray();
			client.sendMessage(new ChatMessage(ChatMessage.IMAGE,null,JC.getSelectedItem().toString(), imageInByte));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

public void updateLatency(Long delta)
{
	lbllatency.setText("Latency : "+delta.toString()+"ms");
	
}

}

