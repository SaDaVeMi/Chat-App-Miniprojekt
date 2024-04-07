package jat.appClasses;

import java.util.Locale;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import jat.ServiceLocator;
import jat.abstractClasses.View;
import jat.commonClasses.Translator;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Chatroom_View extends View<App_Model> {
	protected ListView<String> chatroom;
	protected ListView<String> chatroomUserList;
	private Label userLabel, chatroomLabel;
	protected String chatroomName;
	protected TextField msgInput;
	protected Button send;
	private VBox rootChatroom;
	private Menu menuFile;
	private Menu menuFileLanguage, menuHelp, menuChatroom, menuDesign;
	protected MenuItem menuChatroomLeave, menuChatroomListUsers, menuDesignDark, menuDesignLight;

	public Chatroom_View(Stage stage, App_Model model) {
		super(stage, model);
	}

	@Override
	public void start(String chatroom) {
		this.chatroom.setItems(model.createChatroom(chatroom));
		this.chatroomUserList.setItems(model.createChatroomUserList(chatroom));
		chatroomUserList.setPrefWidth(120);
		chatroomUserList.setPrefHeight(90);
		chatroomName = chatroom;
		stage.setWidth(320);
		stage.setHeight(650);
		stage.setTitle(chatroom);
		stage.show();
	}

	@Override
	protected Scene create_GUI() {
		ServiceLocator sl = ServiceLocator.getServiceLocator();
		Logger logger = sl.getLogger();

		// Menu bar
		MenuBar menuBar = new MenuBar();
		menuFile = new Menu();
		menuHelp = new Menu();
		menuChatroom = new Menu();
		menuChatroomLeave = new MenuItem();
		menuChatroomListUsers = new MenuItem();
		menuDesign = new Menu();
		menuDesignDark = new MenuItem();
		menuDesignLight = new MenuItem();
		menuFileLanguage = new Menu();
		menuFile.getItems().add(menuFileLanguage);
		menuDesign.getItems().addAll(menuDesignDark, menuDesignLight);
		menuChatroom.getItems().addAll(menuChatroomLeave, menuChatroomListUsers);

		for (Locale locale : sl.getLocales()) {
			MenuItem language = new MenuItem(locale.getLanguage());
			menuFileLanguage.getItems().add(language);
			language.setOnAction(event -> {
				sl.getConfiguration().setLocalOption("Language", locale.getLanguage());
				sl.setTranslator(new Translator(locale.getLanguage()));
				updateTexts();
			});
		}

		menuBar.getMenus().addAll(menuFile, menuChatroom, menuDesign, menuHelp);

		// Chat_View
		msgInput = new TextField();
		send = new Button();
		chatroom = new ListView<>();
		userLabel = new Label();
		chatroomLabel = new Label();
		chatroomUserList = new ListView<>();
		chatroomUserList.setId("userList");

		chatroom.setCellFactory(param -> new ListCell<String>() {
			@Override
			public void updateItem(String msg, boolean empty) {
				super.updateItem(msg, empty);
				if (empty) {
					setGraphic(null);
					setId("chatroomListCell");
				} else {
					setId("chatroomListCell");

					/*
					 * Add new line if message string exceeds 15 char so the label does not stretch
					 * too much (top answer)
					 * https://stackoverflow.com/questions/7528045/large-string-split-into-lines-
					 * with-maximum-length-in-java
					 * 
					 */

					StringTokenizer tok = new StringTokenizer(msg, " ");
					StringBuilder formattedMsg = new StringBuilder(msg.length());
					int lineLength = 0;

					while (tok.hasMoreTokens()) {
						String word = tok.nextToken() + " ";

						if (word.length() > 15) {
							if (lineLength > 0) {
								formattedMsg.append("\n");
							}
							for (int i = 0; i < word.length(); i += 15) {
								formattedMsg.append(word.substring(i, Math.min(i + 15, word.length())) + "\n");
							}
							lineLength = 0;
						} else if (lineLength + word.length() > 15) {
							formattedMsg.append("\n");
							formattedMsg.append(word);
							lineLength = 0;
						} else {
							formattedMsg.append(word);
							lineLength += word.length();
						}
					}
					Label messageLabel = new Label(formattedMsg.toString());
					messageLabel.setMinHeight(35);
					messageLabel.setWrapText(true);

					String[] msgSplit = msg.split(":");
					if (msgSplit[0].equals(model.username)) {
						messageLabel.setId("messageUser");
						setGraphic(messageLabel);
						setAlignment(Pos.CENTER_RIGHT);
					} else {
						messageLabel.setId("messageOtherUser");
						setGraphic(messageLabel);
						setAlignment(Pos.CENTER_LEFT);

					}
				}
			}
		});
		HBox container = new HBox(msgInput, send);
		container.setId("sendView");
		rootChatroom = new VBox(menuBar, userLabel, chatroomUserList, chatroomLabel, chatroom, container);
		rootChatroom.setId("chatroomView");
		updateTexts();
		Scene scene = new Scene(rootChatroom);
		scene.getStylesheets().add(getClass().getResource("app.css").toExternalForm());
		return scene;
	}
	
	protected void changeDesign(String design) {
		if (design.equals("Dark") || design.equals("Dunkel")) {
			scene.getStylesheets().clear();
			scene.getStylesheets().add(getClass().getResource("app.css").toExternalForm());
		} else {
			scene.getStylesheets().clear();
			scene.getStylesheets().add(getClass().getResource("appLight.css").toExternalForm());
		}
	}

	protected void updateTexts() {
		Translator t = ServiceLocator.getServiceLocator().getTranslator();

		// The menu entries
		menuFile.setText(t.getString("program.menu.file"));
		menuChatroom.setText(t.getString("program.menu.chatroom"));
		menuFileLanguage.setText(t.getString("program.menu.file.language"));
		menuDesign.setText(t.getString("program.menu.design"));
		menuDesignDark.setText(t.getString("program.menu.design.dark"));
		menuDesignLight.setText(t.getString("program.menu.design.light"));
		menuHelp.setText(t.getString("program.menu.help"));
		menuChatroomLeave.setText(t.getString("program.menu.chatroom.leave"));
		menuChatroomListUsers.setText(t.getString("program.menu.chatroom.listusers"));

		// Other controls
		send.setText(t.getString("label.send"));
		userLabel.setText(t.getString("label.userLabel"));
		chatroomLabel.setText(t.getString("label.chatroomLabel"));
	}
}