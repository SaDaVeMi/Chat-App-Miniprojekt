package jat.appClasses;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.Security;
import java.util.ArrayList;
import java.util.TreeMap;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import com.sun.net.ssl.internal.ssl.Provider;
import jat.ServiceLocator;
import jat.abstractClasses.Model;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Copyright 2015, FHNW, Prof. Dr. Brad Richards. All rights reserved. This code
 * is licensed under the terms of the BSD 3-clause license (see the file
 * license.txt).
 * 
 * @author Brad Richards
 */

public class App_Model extends Model {
	String ipAddress = "147.86.8.31";
	int portNumber = 50001;
	private Socket socket;
	boolean secure = false;
	private BufferedReader socketIn;
	private OutputStreamWriter socketOut;
	private String token;
	public String username;
	private SimpleBooleanProperty loggedIn = new SimpleBooleanProperty(false);
	private volatile String newestMessage = "";
	private String latestResult = null;
	private final ObservableList<String> chatroomList = FXCollections.observableArrayList();
	private volatile TreeMap<String, ObservableList<String>> chatrooms = new TreeMap<>();
	private volatile TreeMap<String, ObservableList<String>> chatroomUserLists = new TreeMap<>();

	private final ArrayList<Thread> chatroomThread = new ArrayList<Thread>();
	private final Object newestMessageLock = new Object();

	ServiceLocator serviceLocator;

	// Constructor
	public App_Model() {
		serviceLocator = ServiceLocator.getServiceLocator();
		serviceLocator.getLogger().info("Application model initialized");
	}

	public void connect(String ipAddress, int portNumber, boolean secure) {
		this.ipAddress = ipAddress;
		this.portNumber = portNumber;
		this.secure = secure;

		this.socket = null;
		try {
			if (secure) {
				// Registering the JSSE provider
				Security.addProvider(new Provider());

				// Specifying the Truststore details. This is needed if you have created a
				// truststore, for example, for self-signed certificates
				System.setProperty("javax.net.ssl.trustStore", "truststore.ts");
				System.setProperty("javax.net.ssl.trustStorePassword", "trustme");

				// Creating Client Sockets
				SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
				socket = sslsocketfactory.createSocket(ipAddress, portNumber);

				// The next line is entirely optional !!
				// The SSL handshake would happen automatically, the first time we send data.
				// Or we can immediately force the handshaking with this method:
				((SSLSocket) socket).startHandshake();
			} else {
				this.socket = new Socket(ipAddress, portNumber);
			}

			// Open input/output stream
			try {
				socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				socketOut = new OutputStreamWriter(socket.getOutputStream());
				receiveIncomingMessage();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println(e);
			}
			System.out.println("Connected");

		} catch (Exception e) {
			e.printStackTrace();
		}
			
	}

	// Send command from client to server
	private void sendCommand(String command) {
		try {
			socketOut.write(command + "\n");
			socketOut.flush();
			serviceLocator.getLogger().info("Send: " + command);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}

	// Read all message from server
	private void receiveIncomingMessage() {

		// Create thread to read incoming messages
		Runnable r = new Runnable() {
			@Override
			public void run() {
				while (true) {
					String msg;
					try {
						msg = socketIn.readLine();
						System.out.println("Received: " + msg);
						// Splits response in two categories: "Result" and "MessageText"
						String[] response = msg.split("\\|");

						if (response[0].equals("Result")) {
							latestResult = msg;
						}

						if (response[0].equals("MessageText")) {
							newestMessage = msg;
						}

					} catch (IOException e) {
						break;
					}
				}
			}

		};
		Thread t = new Thread(r);
		t.setDaemon(true);
		t.start();
	}

	// Receive all message type "result" from server
	private String[] processReceivedResult() {
		// Respond time server
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// In case server did not respond
		String[] result = null;
		if (latestResult != null) {
			result = latestResult.split("\\|");
		} else {
			socket = null;
			System.out.println("Server did not respond.");
		}
		return result;
	}

	// Create Login
	public boolean createLogin(String username, String password) {
		sendCommand("CreateLogin|" + username + "|" + password);
		String[] result = processReceivedResult();
		boolean accountCreated = Boolean.parseBoolean(result[2]);

		return accountCreated;
	}

	// Log in
	public boolean logIn(String username, String password) {
		sendCommand("Login|" + username + "|" + password);
		String[] result = processReceivedResult();
		setLoggedIn(Boolean.parseBoolean(result[2]));
		// Save token
		if (getLoggedIn()) {
			token = result[3];
			this.username = username;
			serviceLocator.getLogger().info("Log in: " + username);
		}
		return getLoggedIn();
	}
	
	// Log out
	public boolean logOut() {
		sendCommand("Logout|" + token);
		String[] result = processReceivedResult();
		setLoggedIn(!Boolean.parseBoolean(result[2]));
		if (!getLoggedIn()) {
			serviceLocator.getLogger().info("Log out: " + username);
			username = null;
			token = null;
		}
		return !getLoggedIn();
	}
	
	public boolean deleteLogin() {
		sendCommand("DeleteLogin|" + token);
		String[] result = processReceivedResult();
		boolean loginDeleted = Boolean.parseBoolean(result[2]);
		setLoggedIn(!loginDeleted);
		if (loginDeleted) {
			serviceLocator.getLogger().info("Delete login: " + username);
			username = null;
			token = null;
		}
		return loginDeleted;
	}
	
	public boolean changePassword(String newPassword) {
		sendCommand("ChangePassword|" + token + "|" + newPassword);
		String[] result = processReceivedResult();
		boolean passwordChanged = Boolean.parseBoolean(result[2]);
		if (passwordChanged) {
			serviceLocator.getLogger().info("Change password: " + newPassword);
		}
		return passwordChanged;
	}

	// Create list of users chatrooms
	public void listChatrooms() {
		// In case the list needs to refresh
		if (!getChatroomList().isEmpty()) {
			getChatroomList().clear();
		}
		sendCommand("ListChatrooms|" + token);
		String[] result = processReceivedResult();

		for (int i = 3; i < result.length; i++) {
			this.chatroomList.add(result[i]);
		}
	}

	// Getter for chatroomList
	public ObservableList<String> getChatroomList() {
		return chatroomList;
	}

	// Create users chatroom in observableList
	public boolean createChatroom(String chatroom, String isPublic) {
		sendCommand("CreateChatroom|" + token + "|" + chatroom + "|" + isPublic);
		String[] result = processReceivedResult();
		boolean chatCreated = Boolean.parseBoolean(result[2]);
		// Refresh list
		if (chatCreated) {
			listChatrooms();
		}
		return chatCreated;
	}

	// Join chatroom
	public boolean joinChatroom(String chatroom) {
		sendCommand("JoinChatroom|" + token + "|" + chatroom + "|" + username);
		String[] result = processReceivedResult();
		boolean chatroomJoined = Boolean.parseBoolean(result[2]);
		if (chatroomJoined) {
			serviceLocator.getLogger().info("Chatroom: Joined " + chatroom);
			startChatroom(chatroom);
			createChatroomUserList(chatroom);
			startChatroomUserList(chatroom);
		}
		return chatroomJoined;
	}

	// Send Message
	public boolean sendMessage(String message, String target) {
		sendCommand("SendMessage|" + token + "|" + target + "|" + message);
		String[] result = processReceivedResult();
		boolean messageSent = Boolean.parseBoolean(result[2]);
		return messageSent;
	}

	/**
	 * Creates chatroom (if not already exists) add it in TreeMap and return it, or
	 * return existing one
	 **/
	public ObservableList<String> createChatroom(String chatroom) {
		ObservableList<String> chat = getChatroom(chatroom);
		if (chat == null) {
			chat = FXCollections.observableArrayList();
			chatrooms.put(chatroom, chat);
			return chatrooms.get(chatroom);
		} else {
			return chat;
		}
	}

	// Getter chatroom in a TreeMap
	private ObservableList<String> getChatroom(String chatroom) {
		return chatrooms.get(chatroom);
	}

	// Chatroom-Thread
	private void startChatroom(String chatroom) {
		Runnable r = new Runnable() {

			@Override
			public void run() {
				while (!(getChatroomThread(chatroom).isInterrupted()) && getLoggedIn()) {
					synchronized (newestMessageLock) {

						if (!newestMessage.equals("") && newestMessage.contains(chatroom)) {
							String[] newestMessageSplit = newestMessage.split("\\|");
							String msg = newestMessageSplit[1] + ": " + newestMessageSplit[3];

							Platform.runLater(() -> chatrooms.get(newestMessageSplit[2]).add(msg));

							newestMessage = "";
						}
					}
				}
				serviceLocator.getLogger().info("Thread: " + chatroom + "-Thread stopped");
			}

		};
		Thread t = new Thread(r);
		t.setDaemon(true);
		t.setName(chatroom);
		chatroomThread.add(t);
		t.start();
	}

	// Leave chatroom
	public boolean leaveChatroom(String chatroom) {
		sendCommand("LeaveChatroom|" + token + "|" + chatroom + "|" + username);
		String[] result = processReceivedResult();
		boolean chatroomLeft = Boolean.parseBoolean(result[2]);
		if (chatroomLeft) {
			chatrooms.remove(chatroom);
			serviceLocator.getLogger().info("Chatroom: " + "Left " + chatroom);
		}
		return chatroomLeft;
	}

	// Getter for chatroom threads in ArrayList
	public Thread getChatroomThread(String chatroom) {
		for (int i = 0; i < chatroomThread.size(); i++) {
			if (chatroomThread.get(i).getName().equals(chatroom)) {
				return chatroomThread.get(i);
			}
		}
		return null;
	}

	// Interrupts specific chatroom thread in array
	public void closeChatroomThread(String chatroom) {
		for (int i = 0; i < chatroomThread.size(); i++) {
			if (chatroomThread.get(i).getName().equals(chatroom)) {
				chatroomThread.get(i).interrupt();
				serviceLocator.getLogger().info("Thread: " + chatroom + "-Thread interrupted");
				chatroomThread.remove(i);
			}
		}
	}

	private static boolean validateIpAddress(String ipAddress) {
		boolean formatOK = false;
		// Check for validity (not complete, but not bad)
		String ipPieces[] = ipAddress.split("\\."); // Must escape (see
													// documentation)
		// Must have 4 parts
		if (ipPieces.length == 4) {
			// Each part must be an integer 0 to 255
			formatOK = true; // set to false on the first error
			int byteValue = -1;
			for (String s : ipPieces) {
				byteValue = Integer.parseInt(s); // may throw
													// NumberFormatException
				if (byteValue < 0 | byteValue > 255)
					formatOK = false;
			}
		}
		return formatOK;
	}

	private static boolean validatePortNumber(String portText) {
		boolean formatOK = false;
		try {
			int portNumber = Integer.parseInt(portText);
			if (portNumber >= 1024 & portNumber <= 65535) {
				formatOK = true;
			}
		} catch (NumberFormatException e) {
		}
		return formatOK;
	}
	// Create user list in TreeMap
	public ObservableList<String> createChatroomUserList(String chatroom) {
		ObservableList<String> userList = getChatroomUserList(chatroom);
		if (userList == null) {
			userList = FXCollections.observableArrayList();
			chatroomUserLists.put(chatroom, userList);
			return chatroomUserLists.get(chatroom);
		} else {
			return userList;
		}
	}
	// Add items in user list
	public void startChatroomUserList(String chatroom) {

		// In case the list needs to refresh
		if (getChatroomUserList(chatroom) != null) {
			if (!getChatroomUserList(chatroom).isEmpty()) {
				getChatroomUserList(chatroom).clear();
			}
		}

		sendCommand("ListChatroomUsers|" + token + "|" + chatroom);
		String[] result = this.processReceivedResult();
		boolean chatroomUserList = Boolean.parseBoolean(result[2]);
		if (chatroomUserList) {
			serviceLocator.getLogger().info("ChatroomUserList: " + "Refreshed");
		}
		for (int i = 3; i < result.length; i++) {
			getChatroomUserList(chatroom).add(result[i]);
		}
	}

	private ObservableList<String> getChatroomUserList(String chatroom) {
		return chatroomUserLists.get(chatroom);
	}
	
	// Getter and setter BooleanProperty "loggedIn"
	public boolean getLoggedIn() {
		return loggedIn.get();
	}
	
	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn.set(loggedIn);
	}
	
	public SimpleBooleanProperty getLoggedInProperty() {
		return loggedIn;
	}
}
