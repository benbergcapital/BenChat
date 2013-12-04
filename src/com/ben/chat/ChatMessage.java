package com.ben.chat;
import java.awt.Color;
import java.io.*;
import java.util.Date;
/*
 * This class defines the different type of messages that will be exchanged between the
 * Clients and the Server. 
 * When talking from a Java Client to a Java Server a lot easier to pass Java objects, no 
 * need to count bytes or to wait for a line feed at the end of the frame
 */
public class ChatMessage implements Serializable {

	protected static final long serialVersionUID = 1112122200L;

	// The different types of message sent by the Client
	// WHOISIN to receive the list of the users connected
	// MESSAGE an ordinary message
	// LOGOUT to disconnect from the Server
	static final int WHOISIN = 0;

	public static final int MESSAGE = 1;

	static final int LOGOUT = 2;

	static final int NUDGE = 3;

	static final int PING=4;

	static final int TYPING=5;

	static final int VERSION=6;
	
	static final int IMAGE=7;
	private int type;
	private String message;
	private String colour;
	private String size;
	private byte[] image;
	private String name;
	private Long receivingTime;
	// constructor
	ChatMessage(int type, String message,String colour,String size) {
		this.type = type;
		this.message = message;
		this.colour = colour;
		this.size = size;
	}
	ChatMessage(int type, String message) {
		this.type = type;
		this.message = message;
		
	}
	ChatMessage(int type,String name,String colour, byte[] image) {
		this.type = type;
		this.name = name;
		this.colour =colour;
		this.image = image;
		
	}
	ChatMessage(int type,String name,String message) {
		this.type = type;
		this.name = name;
		this.message = message;
		
	}
	ChatMessage(int type,Long receivingTime)
	{
		this.type=type;
		this.receivingTime = receivingTime;
		
	}
	
	// getters
	int getType() {
		return type;
	}
	String getMessage() {
		return message;
	}
	String getColour() {
		return colour;
	}
	String getSize(){
		return size;
	}
	byte[] getImage(){
		return image;
	}
	String getName(){
		return name;
	
	}
	Long getTime()
	{
		return receivingTime;
	}
}


