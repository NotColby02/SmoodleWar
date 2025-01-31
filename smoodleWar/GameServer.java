package smoodleWar;

import java.awt.*;
import javax.swing.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

public class GameServer extends AbstractServer {
	// Data fields for this chat server.
	private JTextArea log;
	private JLabel status;
	private boolean running = false;
	private Database database;

	private boolean isDrawer;
	
	// Constructor for initializing the server with default settings.
	public GameServer() {
		super(12345);
		this.setTimeout(500);
		isDrawer = false;
	}

	void setDatabase(Database database) {
		this.database = database;
	}

	// Getter that returns whether the server is currently running.
	public boolean isRunning() {
		return running;
	}

	// Setters for the data fields corresponding to the GUI elements.
	public void setLog(JTextArea log) {
		this.log = log;
	}

	public void setStatus(JLabel status) {
		this.status = status;
	}

	// When the server starts, update the GUI.
	public void serverStarted() {
		running = true;
		status.setText("Listening");
		status.setForeground(Color.GREEN);
		log.append("Server started\n");
	}

	// When the server stops listening, update the GUI.
	public void serverStopped() {
		status.setText("Stopped");
		status.setForeground(Color.RED);
		log.append("Server stopped accepting new clients - press Listen to start accepting new clients\n");
	}

	// When the server closes completely, update the GUI.
	public void serverClosed() {
		running = false;
		status.setText("Close");
		status.setForeground(Color.RED);
		log.append("Server and all current clients are closed - press Listen to restart\n");
	}

	// When a client connects or disconnects, display a message in the log.
	public void clientConnected(ConnectionToClient client) {
		log.append("Client " + client.getId() + " connected\n");
	}

	// When a message is received from a client, handle it.
	public void handleMessageFromClient(Object arg0, ConnectionToClient arg1) {
		System.out.println(arg0.getClass());
		// If we received LoginData, verify the account information.
		if (arg0 instanceof LoginData) {
			// Check the username and password with the database.
			LoginData data = (LoginData) arg0;
			Object result;

			if (database.verifyAccount(data.getUsername(), data.getPassword())) {
				result = "LoginSuccessful";
				log.append("Client " + arg1.getId() + " successfully logged in as " + data.getUsername() + "\n");
			} else {
				result = new Error("The username and password are incorrect.", "Login");
				log.append("Client " + arg1.getId() + " failed to log in\n");
			}
			
			// We determine who will be assigned the drawer and guesser role initially
			// here.
			if(!isDrawer) {
				isDrawer = true;
				result = result + "," + "drawer" + "," + data.getUsername();
				
				try {
					arg1.sendToClient(result);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			else {
				isDrawer = false;
				result = result + "," + "guesser" + "," + data.getUsername();
			}
			// Send the result to the client.
			try {
				arg1.sendToClient(result);
			} catch (IOException e) {
				return;
			}
		}

		// If we received CreateAccountData, create a new account.
		else if (arg0 instanceof CreateAccountData) {
			// Try to create the account.
			CreateAccountData data = (CreateAccountData) arg0;
			Object result;

			if (database.createNewAccount(data.getUsername(), data.getPassword())) {
				result = "CreateAccountSuccessful";
				log.append("Client " + arg1.getId() + " created a new account called " + data.getUsername() + "\n");
			} else {
				result = new Error("The username is already in use.", "CreateAccount");
				log.append("Client " + arg1.getId() + " failed to create a new account\n");
			}

			// Send the result to the client.
			try {
				arg1.sendToClient(result);
			} catch (IOException e) {
				return;
			}
			
		}else if (arg0 instanceof ArrayList<?>) {
			ArrayList<Point> coordinates = (ArrayList<Point>) arg0;
			if (coordinates.isEmpty()) {
				log.append("Arraylist is empty.\n");
			}
			log.append("Server Recieved Drawing Coords List from client " + arg1.getId() + "\n");

			super.sendToAllClients(coordinates);
			
		}else if (arg0 instanceof DeleteAccountData) {
			DeleteAccountData data = (DeleteAccountData) arg0;
			database.deleteAccount(data.getUsername(), data.getPassword());
			Object result = "AccountDeletionSuccesful";
			log.append("Client " + arg1.getId() + " deleted account called " + data.getUsername() + "\n");
			try {
				arg1.sendToClient(result);
			} catch (IOException e) {
				return;
			}
		}
		else if (arg0 instanceof String) {
			
			String message = (String)arg0;
			
			if (message.equals("getPrompt")) {
				ArrayList<String> promptData = new ArrayList<String>();
				
		        try {
		            promptData = database.getPrompt();
		        } catch (SQLException e) {
		            // TODO Auto-generated catch block
		            e.printStackTrace();
		        }
		        ArrayList<String> prompts = promptData;
		        
		        Random r = new Random();
				int randomWordIndex = r.nextInt(prompts.size());
		        
		    
				super.sendToAllClients("Prompt," + prompts.get(randomWordIndex));
				
			}
			else if (message.equals("Opponent Failed") || message.equals("Opponent Guessed Correctly")) {
				super.sendToAllClients("RoundEnd," + message);
			}
			else if (message.equals("GameOver")) {
				super.sendToAllClients("GameEnd," + message);
			}
		
		}
	}
	// Method that handles listening exceptions by displaying exception information.
	public void listeningException(Throwable exception) {
		running = false;
		status.setText("Exception occurred while listening");
		status.setForeground(Color.RED);
		log.append("Listening exception: " + exception.getMessage() + "\n");
		log.append("Press Listen to restart server\n");
	}
}
