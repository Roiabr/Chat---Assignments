package Server_Client;

import javax.swing.*;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.SwingPropertyChangeSupport;

import org.omg.CosNaming.NamingContextPackage.NotFoundReasonHelper;
import org.omg.PortableInterceptor.ClientRequestInterceptor;

import java.awt.*;
import java.awt.event.*;
import Server_Client.*;


/*
 * The Client with its GUI
 */
public class ClientGUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	// will first hold "Username:", later on "Enter message"
	private JLabel label;
	// to hold the Username and later on the messages
	private JTextField tf,user_send;
	// to hold the server address an the port number
	private JTextField tfServer, tfPort,username1;
	// to Logout and get the list of the users
	private JButton login, logout, whoIsIn;	// for the chat room
	private JTextArea ta;
	// if it is for connection
	private boolean connected;
	// the Client object
	private Client client;

	// the default port number
	private int defaultPort;
	private String defaultHost;

	/**
	 * @param host - the server to connect
	 * @param port - the logic port in the server we connect to 
	 */	ClientGUI(String host, int port) {

		 super("Chat Client");
		 defaultPort = port;
		 defaultHost = host;
		 // The NorthPanel with:
		 JPanel northPanel = new JPanel(new GridLayout(6,2)); //deteminte the long of space beetween the panel user name 
		 // the server name anmd the port number
		 JPanel serverAndPort = new JPanel(new GridLayout(1,1, 1, 3));  //deteminte the long of space beetween the panel server and port 
		 // the two JTextField with default value for server address and port number 
		 tfServer = new JTextField(host);
		 tfPort = new JTextField("" + port); //
		 tfPort.setHorizontalAlignment(SwingConstants.LEFT); //select where put the numer of the port in the windoes 



		 username1= new JTextField("");

		 serverAndPort.add(new JLabel("Server Address:  ")); 
		 serverAndPort.add(tfServer);
		 serverAndPort.add(new JLabel("  Port Number:  "));
		 serverAndPort.add(tfPort);
		 serverAndPort.add(new JLabel(""));

		 // adds the Server an port field to the GUI
		 northPanel.add(serverAndPort);
		 serverAndPort.add(new JLabel("               user:")); 
		 serverAndPort.add(username1);

		 username1.addActionListener(this);
		 //username1.setEditable(false);
		 // the Label and the TextField
		 label = new JLabel("Enter your username below", SwingConstants.CENTER); //where to put the labael on the chat
		 northPanel.add(label);
		 tf = new JTextField("Insert username"); //the text in the label
		 northPanel.add(tf); //put in the up panel ;
		 add(northPanel, BorderLayout.NORTH); //determine where to put the filed of port user name and server



		 // The CenterPanel which is the chat room
		 ta = new JTextArea("Welcome to the Chat room\n", 80, 80); //the long of the fild chat
		 JPanel centerPanel = new JPanel(new GridLayout(1,1)); //dtermine the size of the windoes for the button
		 centerPanel.add(new JScrollPane(ta));
		 ta.setEditable(false);
		 add(centerPanel, BorderLayout.CENTER);
		 user_send=new JTextField("");


		 user_send.addActionListener(this);


		 northPanel.add(new JLabel("for private messeage write the username"));
		 northPanel.add(user_send);

		 username1.setEditable(false);



		 // the 3 buttons
		 login = new JButton("Login");
		 login.addActionListener(this);
		 logout = new JButton("Logout");
		 logout.addActionListener(this);
		 logout.setEnabled(false);		// you have to login before being able to logout
		 whoIsIn = new JButton("show Online");
		 whoIsIn.addActionListener(this);
		 whoIsIn.setEnabled(false);		// you have to login before being able to Who is in
		 login.setBackground(Color.GREEN);
		 logout.setBackground(Color.yellow);
		 whoIsIn.setBackground(Color.BLUE);

		 ////font
		 Font myFont = new Font("ariel", Font.BOLD, 16);
		 ta.setFont(myFont);
		 login.setFont(myFont);
		 logout.setFont(myFont);
		 whoIsIn.setFont(myFont);
		 tf.setFont(myFont);
		 username1.setFont(myFont);
		 tfPort.setFont(myFont);
		 tfServer.setFont(myFont);
		 
		 
		 ///////////////////

		 JPanel southPanel = new JPanel();
		 southPanel.add(login); //add the buttons.
		 southPanel.add(logout);
		 southPanel.add(whoIsIn);
		 add(southPanel, BorderLayout.SOUTH); //and put him in the outline down 

		 setDefaultCloseOperation(EXIT_ON_CLOSE);
		 setSize(750, 700);
		 setVisible(true);
		 tf.requestFocus();

	 }

	 // called by the Client to append text in the TextArea 
	 /**
	  * to display message on the screen
	  * @param str - the message the client send
	  */
	 void append(String str) {
		 ta.append(str);
		 ta.setCaretPosition(ta.getText().length() - 1);
	 }


	 // called by the GUI is the connection failed
	 // we reset our buttons, label, textfield
	 /**
	  * if the connection failed reset our buttons, labels and textfield
	  */
	 void connectionFailed() {
		 login.setEnabled(true);
		 logout.setEnabled(false);
		 whoIsIn.setEnabled(false);
		 label.setText("Enter your username below");
		 tf.setText("Anonymous");
		 // reset port number and host name as a construction time
		 tfPort.setText("" + defaultPort);
		 tfServer.setText(defaultHost);
		 // let the user change them
		 tfServer.setEditable(false);
		 tfPort.setEditable(false);
		 // don't react to a <CR> after the username
		 tf.removeActionListener(this);
		 connected = false;
	 }

	 /**
	  * Button or JTextField clicked
	  **/
	 public void actionPerformed(ActionEvent e) {
		 Object o = e.getSource();
		 // if it is the Logout button
		 if(o == logout) {
			 client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
			 return;
		 }
		 // if it the who is in button
		 if(o == whoIsIn) {
			 client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));				
			 return;
		 }



		 // ok it is coming from the JTextField
		 if(connected) {
			 String PrivateUser= user_send.getText();
			 if (PrivateUser.equals("")){
				 client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, tf.getText()));				
				 tf.setText("");
				 return;
			 }

			 else{
				 client.sendMessage(new ChatMessage(ChatMessage.SENDPRIVATE, tf.getText(), PrivateUser));
				 tf.setText("");
				 return;
			 }

		 }
		 if(o == login) {
			 // ok it is a connection request
			 String username = tf.getText().trim();
			 // empty username ignore it
			 if(username.length() == 0)
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
			 client = new Client(server, port, username, this);
			 // test if we can start the Client
			 if(!client.start()) 
				 return;
			 tf.setText("");
			 label.setText("Enter your message below");
			 connected = true;
			 username1.setText(""+username);
			 username1.setEnabled(false);

			 // disable login button
			 login.setEnabled(false);


			 // enable the 2 buttons
			 logout.setEnabled(true);
			 whoIsIn.setEnabled(true);
			 // disable the Server and Port JTextField
			 tfServer.setEditable(false); 
			 tfPort.setEditable(false);
			 // Action listener for when the user enter a message
			 tf.addActionListener(this);
		 }

	 }

	 public JTextField getuser_send() {
		 return user_send;
	 }

	 // to start the whole thing the server
	 public static void main(String[] args) {
		 new ClientGUI("localhost", 1500);
	 }

}
