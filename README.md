# Chat Miniproject

(Please read the more detailed file with pictures uploaded on moodle)

# Basic features

The program has all basic features

All basic functions:
*	Set server address and port
*	Create new login
*	Login
*	Logout
*	Delete login
*	Change password
*	List, join, add and leave (public) chatrooms
*	List users in chatrooms
*	Receive and send messages
*	Switch language

# Optional: GUI features

* Smooth GUI and responsive controls

* Dynamic error message e.g. when password is wrong

* Controls like buttons or menu are enabled/disabled when it makes sense

* Change the design dynamically in dark or light mode

# Optional: Chat room usability features

* Join and chat in multiple chatrooms
simultaneously thanks to multi-thread

_Code snippet:_

```
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
```

* As long as the user does not leave the chatroom, it saves all messages (while the program is running)

# Libraries (model and controller)

* java.io.BufferedReader;
* java.io.IOException;
* java.io.InputStreamReader;
* java.io.OutputStreamWriter;
* java.net.Socket;
* java.security.Security;
* java.util.ArrayList;
* java.util.TreeMap;
* javax.net.ssl.SSLSocket;
* javax.net.ssl.SSLSocketFactory;
* com.sun.net.ssl.internal.ssl.Provider;
* jat.ServiceLocator;
* jat.abstractClasses.Model;
* javafx.application.Platform;
* javafx.beans.property.SimpleBooleanProperty;
* javafx.collections.FXCollections;
* javafx.collections.ObservableList;


* java.util.Optional;
* jat.JavaFX_App_Template;
* jat.ServiceLocator;
* jat.abstractClasses.Controller;
* javafx.application.Platform;
* javafx.event.ActionEvent;
* javafx.event.EventHandler;
* javafx.scene.control.Button;
* javafx.scene.control.ButtonType;
* javafx.stage.WindowEvent;

