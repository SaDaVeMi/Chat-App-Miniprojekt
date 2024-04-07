package jat.appClasses;

import java.util.Locale;
import java.util.logging.Logger;
import jat.ServiceLocator;
import jat.abstractClasses.View;
import jat.commonClasses.Translator;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Copyright 2015, FHNW, Prof. Dr. Brad Richards. All rights reserved. This code
 * is licensed under the terms of the BSD 3-clause license (see the file
 * license.txt).
 * 
 * @author Brad Richards
 */

public class App_View extends View<App_Model> {
	private Menu menuFile, menuFileLanguage, menuHelp, menuAccount, menuDesign;
	protected MenuItem menuAccountChangePassword, menuAccountLogout, menuAccountDelete, menuDesignDark, menuDesignLight;

	private BorderPane root;

	// Connect_View
	protected TextField ipAddress, port;
	protected RadioButton secure, notSecure;
	private ToggleGroup tg;
	protected Button connect;
	private Label ipAddressLabel, portLabel, secureLabel;
	private HBox connectView;

	// Login_View
	private Label usernameLabel, passwordLabel;
	protected Label errorLogin;
	protected TextField username, password;
	protected Button createLogin, login;
	private GridPane rootLogin;

	// SignUp_View
	protected TextField newUsername, newPassword, repeatPassword;
	protected Button buttonBack, signUp;
	private Label newUsernameLabel, newPasswordLabel, repeatPasswordLabel;
	protected Label errorSignUpUser, errorSignUpPassword;
	private GridPane rootSignUp;

	// ChatroomList_View
	protected ListView<String> chatroomList;
	private final Image img = new Image("jat/appClasses/group_chat_icon.png", 60, 60, true, false);
	protected Button joinChatroom, addChatroom, refreshChatroom;
	private VBox rootListView;

	// Chatroom_View
	protected ListView<String> chatroom;
	protected TextField msgInput;
	protected Button send;

	// Custom dialog
	protected Dialog<ButtonType> createChatroomDialog;
	protected TextField chatroomName;
	private Label chatroomNameLabel;
	protected Label errorChatroomName;
	protected ButtonType createChatroom, cancelChatroom;
	protected RadioButton publicChat, privateChat;
	private ToggleGroup dtg;

	protected Dialog<ButtonType> changePasswordDialog;
	protected ButtonType changePassword, cancelPassword;
	private Label changePasswordLabel, repeatChangePasswordLabel;
	protected PasswordField changePasswordField, repeatChangePasswordField;
	protected Label errorChangePassword;

	protected Dialog<ButtonType> deleteAccountDialog;
	protected ButtonType yesDelete, noDelete;

	public App_View(Stage stage, App_Model model) {
		super(stage, model);
		ServiceLocator.getServiceLocator().getLogger().info("Application view initialized");
	}

	@Override
	protected Scene create_GUI() {
		ServiceLocator sl = ServiceLocator.getServiceLocator();
		Logger logger = sl.getLogger();

		root = new BorderPane();

		// Connect_View
		ipAddressLabel = new Label();
		portLabel = new Label();
		secureLabel = new Label();
		ipAddress = new TextField("147.86.8.31");
		port = new TextField("50001");
		secure = new RadioButton();
		notSecure = new RadioButton();
		connect = new Button();
		tg = new ToggleGroup();
		secure.setToggleGroup(tg);
		notSecure.setToggleGroup(tg);
		notSecure.setSelected(true);

		// Login_View
		usernameLabel = new Label();
		username = new TextField();
		passwordLabel = new Label();
		password = new PasswordField();
		login = new Button();
		login.setDisable(true);
		createLogin = new Button();
		errorLogin = new Label();
		errorLogin.setId("errorLabel");
		errorLogin.setVisible(false);

		// SignUp_View
		newUsernameLabel = new Label();
		newUsername = new TextField();
		newPasswordLabel = new Label();
		newPassword = new PasswordField();
		repeatPasswordLabel = new Label();
		repeatPassword = new PasswordField();
		signUp = new Button();
		signUp.setDisable(true);
		buttonBack = new Button();
		errorSignUpUser = new Label();
		errorSignUpPassword = new Label();
		errorSignUpUser.setId("errorLabel");
		errorSignUpUser.setVisible(true);
		errorSignUpPassword.setId("errorLabel");
		errorSignUpPassword.setVisible(true);

		// ChatroomList_View
		joinChatroom = new Button();
		addChatroom = new Button();
		refreshChatroom = new Button();
		chatroomList = new ListView<String>(model.getChatroomList());

		chatroomList.setCellFactory(param -> new ListCell<String>() {
			ImageView chatIcon = new ImageView(img);

			@Override
			public void updateItem(String name, boolean empty) {
				super.updateItem(name, empty);
				if (empty) {
					setText(null);
					setGraphic(null);
				} else {
					setText(name);
					setGraphic(chatIcon);
				}
			}
		});

		// Custom Dialog changePassword
		changePasswordDialog = new Dialog<>();
		changePassword = ButtonType.OK;
		cancelPassword = ButtonType.CANCEL;
		changePasswordLabel = new Label();
		repeatChangePasswordLabel = new Label();
		changePasswordField = new PasswordField();
		repeatChangePasswordField = new PasswordField();
		errorChangePassword = new Label();
		errorChangePassword.setId("errorLabel");
		errorChangePassword.setVisible(false);
		GridPane container1 = new GridPane();
		container1.setId("grid-pane");
		container1.add(changePasswordLabel, 0, 0);
		container1.add(changePasswordField, 0, 1);
		container1.add(repeatChangePasswordLabel, 0, 3);
		container1.add(repeatChangePasswordField, 0, 4);
		container1.add(errorChangePassword, 0, 2);
		changePasswordDialog.getDialogPane().getButtonTypes().addAll(changePassword, cancelPassword);
		changePasswordDialog.getDialogPane().setContent(container1);
		changePasswordDialog.getDialogPane().setPrefSize(300, 300);
		changePasswordDialog.getDialogPane().getScene().getWindow().setOnCloseRequest(e -> changePasswordDialog.setResult(cancelPassword));
		changePasswordDialog.getDialogPane().getStylesheets().add(
				   getClass().getResource("app.css").toExternalForm());
		
		// Custom Dialog deleteAccount
		deleteAccountDialog = new Dialog<>();
		yesDelete = ButtonType.YES;
		noDelete = ButtonType.NO;
		deleteAccountDialog.getDialogPane().getButtonTypes().addAll(yesDelete, noDelete);
		deleteAccountDialog.getDialogPane().setPrefSize(200, 200);
		deleteAccountDialog.getDialogPane().getScene().getWindow().setOnCloseRequest(e -> deleteAccountDialog.setResult(noDelete));
		deleteAccountDialog.getDialogPane().getStylesheets().add(
				   getClass().getResource("app.css").toExternalForm());

		// Custom Dialog createChatroom
		createChatroomDialog = new Dialog<>();
		createChatroom = ButtonType.FINISH;
		cancelChatroom = ButtonType.CANCEL;
		chatroomName = new TextField();
		chatroomNameLabel = new Label(); 
		errorChatroomName = new Label();
		errorChatroomName.setId("errorLabel");
		errorChatroomName.setVisible(false);
		publicChat = new RadioButton();
		privateChat = new RadioButton();
		dtg = new ToggleGroup();
		publicChat.setToggleGroup(dtg);
		privateChat.setToggleGroup(dtg);
		publicChat.setSelected(true);
		HBox rbContainer = new HBox(publicChat, privateChat);
		rbContainer.setSpacing(10);
		GridPane container2 = new GridPane();
		container2.setId("grid-pane");
		container2.add(chatroomNameLabel, 0, 0);
		container2.add(chatroomName, 0, 1);
		container2.add(errorChatroomName, 0, 2);
		container2.add(rbContainer, 0, 3);
		container2.setMinSize(200, 200);
		createChatroomDialog.getDialogPane().getButtonTypes().addAll(createChatroom, cancelChatroom);
		createChatroomDialog.getDialogPane().setContent(container2);
		createChatroomDialog.getDialogPane().setPrefSize(300, 300);
		createChatroomDialog.getDialogPane().getScene().getWindow().setOnCloseRequest(e -> createChatroomDialog.setResult(cancelChatroom));
		createChatroomDialog.getDialogPane().getStylesheets().add(
				   getClass().getResource("app.css").toExternalForm());
	
		// Menu bar
		MenuBar menuBar = new MenuBar();
		menuFile = new Menu();
		menuHelp = new Menu();
		menuAccount = new Menu();
		menuAccountChangePassword = new MenuItem();
		menuAccountChangePassword.setDisable(true);
		menuAccountLogout = new MenuItem();
		menuAccountLogout.setDisable(true);
		menuAccountDelete = new MenuItem();
		menuAccountDelete.setDisable(true);
		menuDesign = new Menu();
		menuDesignDark = new MenuItem();
		menuDesignLight = new MenuItem();
		menuFileLanguage = new Menu();
		menuDesign.getItems().addAll(menuDesignDark, menuDesignLight);
		menuFile.getItems().add(menuFileLanguage);
		menuAccount.getItems().addAll(menuAccountChangePassword, menuAccountLogout, menuAccountDelete);

		for (Locale locale : sl.getLocales()) {
			MenuItem language = new MenuItem(locale.getLanguage());
			menuFileLanguage.getItems().add(language);
			language.setOnAction(event -> {
				sl.getConfiguration().setLocalOption("Language", locale.getLanguage());
				sl.setTranslator(new Translator(locale.getLanguage()));
				updateTexts();
			});
		}

		// Root for Connect_View
		connectView = new HBox(ipAddressLabel, ipAddress, portLabel, port, secureLabel, secure, notSecure, connect);
		connectView.setId("connectView");
		stage.setWidth(950);
		stage.setHeight(150);

		// Root for Login_View
		rootLogin = new GridPane();
		rootLogin.setId("loginView");
		rootLogin.add(usernameLabel, 0, 0);
		rootLogin.add(username, 0, 1);
		rootLogin.add(passwordLabel, 0, 3);
		rootLogin.add(password, 0, 4);
		rootLogin.add(login, 0, 7);
		rootLogin.add(createLogin, 0, 8);
		rootLogin.add(errorLogin, 0, 2);
		GridPane.setHalignment(usernameLabel, HPos.CENTER);
		GridPane.setHalignment(passwordLabel, HPos.CENTER);
		GridPane.setHalignment(login, HPos.CENTER);
		GridPane.setHalignment(createLogin, HPos.CENTER);
		GridPane.setHalignment(errorLogin, HPos.CENTER);
		GridPane.setHalignment(username, HPos.CENTER);
		GridPane.setHalignment(password, HPos.CENTER);

		// Root for SignUp_View
		rootSignUp = new GridPane();
		rootSignUp.setId("signUpView");
		rootSignUp.add(newUsernameLabel, 0, 0);
		rootSignUp.add(newUsername, 0, 1);
		rootSignUp.add(newPasswordLabel, 0, 3);
		rootSignUp.add(newPassword, 0, 4);
		rootSignUp.add(repeatPasswordLabel, 0, 5);
		rootSignUp.add(repeatPassword, 0, 6);
		rootSignUp.add(signUp, 0, 9);
		rootSignUp.add(buttonBack, 0, 10);
		rootSignUp.add(errorSignUpUser, 0, 2);
		rootSignUp.add(errorSignUpPassword, 0, 7);
		GridPane.setHalignment(newUsernameLabel, HPos.CENTER);
		GridPane.setHalignment(newUsername, HPos.CENTER);
		GridPane.setHalignment(newPasswordLabel, HPos.CENTER);
		GridPane.setHalignment(newPassword, HPos.CENTER);
		GridPane.setHalignment(repeatPasswordLabel, HPos.CENTER);
		GridPane.setHalignment(repeatPassword, HPos.CENTER);
		GridPane.setHalignment(signUp, HPos.CENTER);
		GridPane.setHalignment(buttonBack, HPos.CENTER);
		GridPane.setHalignment(errorSignUpUser, HPos.CENTER);
		GridPane.setHalignment(errorSignUpPassword, HPos.CENTER);

		// Root for ChatroomList_View
		rootListView = new VBox(chatroomList, joinChatroom, addChatroom, refreshChatroom);
		rootListView.setId("listView");

		menuBar.getMenus().addAll(menuFile, menuAccount, menuDesign, menuHelp);

		root.setTop(menuBar);
		root.setCenter(connectView);

		updateTexts();

		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("app.css").toExternalForm());
		return scene;
	}

	protected void changeRoot(int input) {
		switch (input) {
		case 1: // Login
			root.setCenter(rootLogin);
			stage.setWidth(500);
			stage.setHeight(500);
			break;
		case 2: // SignUp
			root.setCenter(rootSignUp);
			stage.setWidth(500);
			stage.setHeight(500);
			break;
		case 3: // ChatroomList
			root.setCenter(rootListView);
			stage.setWidth(270);
			stage.setHeight(570);
			break;
		}
	}

	protected void changeDesign(String design) {
		if (design.equals("Dark") || design.equals("Dunkel")) {
			scene.getStylesheets().clear();
			createChatroomDialog.getDialogPane().getStylesheets().clear();
			changePasswordDialog.getDialogPane().getStylesheets().clear();
			deleteAccountDialog.getDialogPane().getStylesheets().clear();
			scene.getStylesheets().add(getClass().getResource("app.css").toExternalForm());
			createChatroomDialog.getDialogPane().getStylesheets().add(getClass().getResource("app.css").toExternalForm());
			changePasswordDialog.getDialogPane().getStylesheets().add(getClass().getResource("app.css").toExternalForm());
			deleteAccountDialog.getDialogPane().getStylesheets().add(getClass().getResource("app.css").toExternalForm());
		} else {
			scene.getStylesheets().clear();
			createChatroomDialog.getDialogPane().getStylesheets().clear();
			changePasswordDialog.getDialogPane().getStylesheets().clear();
			deleteAccountDialog.getDialogPane().getStylesheets().clear();
			scene.getStylesheets().add(getClass().getResource("appLight.css").toExternalForm());
			createChatroomDialog.getDialogPane().getStylesheets().add(getClass().getResource("appLight.css").toExternalForm());
			changePasswordDialog.getDialogPane().getStylesheets().add(getClass().getResource("appLight.css").toExternalForm());
			deleteAccountDialog.getDialogPane().getStylesheets().add(getClass().getResource("appLight.css").toExternalForm());
		}
	}
	
	protected void updateTexts() {
		Translator t = ServiceLocator.getServiceLocator().getTranslator();

		// The menu entries
		menuFile.setText(t.getString("program.menu.file"));
		menuAccount.setText(t.getString("program.menu.account"));
		menuFileLanguage.setText(t.getString("program.menu.file.language"));
		menuHelp.setText(t.getString("program.menu.help"));
		menuDesign.setText(t.getString("program.menu.design"));
		menuDesignDark.setText(t.getString("program.menu.design.dark"));
		menuDesignLight.setText(t.getString("program.menu.design.light"));
		menuAccountChangePassword.setText(t.getString("program.menu.account.changepassword"));
		menuAccountLogout.setText(t.getString("program.menu.account.logout"));
		menuAccountDelete.setText(t.getString("program.menu.account.delete"));

		// Other controls
		errorSignUpUser.setText(t.getString("label.errorsignupuser"));//
		errorSignUpPassword.setText(t.getString("label.errrorsignuppassword"));//
		createLogin.setText(t.getString("button.createLogin"));
		login.setText(t.getString("button.login"));
		errorLogin.setText(t.getString("label.errorLogin"));//
		connect.setText(t.getString("button.connect"));

		ipAddressLabel.setText(t.getString("label.ipAdress"));
		portLabel.setText(t.getString("label.port"));
		secureLabel.setText(t.getString("label.secure"));
		secure.setText(t.getString("radiobutton.secure"));
		notSecure.setText(t.getString("radiobutton.notsecure"));

		joinChatroom.setText(t.getString("button.joinchatroom"));
		addChatroom.setText(t.getString("button.addchatroom"));
		refreshChatroom.setText(t.getString("button.refreshchatroom"));

		((Button) changePasswordDialog.getDialogPane().lookupButton(changePassword))
				.setText(t.getString("button.changepassword"));
		((Button) changePasswordDialog.getDialogPane().lookupButton(cancelPassword))
				.setText(t.getString("button.cancelpassword"));
		changePasswordLabel.setText(t.getString("label.changepasswordlabel"));
		repeatChangePasswordLabel.setText(t.getString("label.repeatchangepasswordlabel"));
		errorChangePassword.setText(t.getString("label.errorchangepassword"));
		changePasswordDialog.setTitle(t.getString("dialog.title.changepassword"));

		((Button) deleteAccountDialog.getDialogPane().lookupButton(yesDelete)).setText(t.getString("button.yesdelete"));
		((Button) deleteAccountDialog.getDialogPane().lookupButton(noDelete)).setText(t.getString("button.nodelete"));
		deleteAccountDialog.setContentText(t.getString("dialog.content.deleteaccount"));
		deleteAccountDialog.setTitle(t.getString("dialog.title.deleteaccount"));

		((Button) createChatroomDialog.getDialogPane().lookupButton(createChatroom))
				.setText(t.getString("button.createchatroom"));
		((Button) createChatroomDialog.getDialogPane().lookupButton(cancelChatroom))
				.setText(t.getString("button.cancelchatroom"));
		chatroomNameLabel.setText(t.getString("label.chatroomnamelabel"));
		errorChatroomName.setText(t.getString("label.errorchatroomname"));
		publicChat.setText(t.getString("radiobutton.publicchat"));
		privateChat.setText(t.getString("radiobutton.privatchat"));
		createChatroomDialog.setTitle(t.getString("dialog.title.createchatroom"));

		usernameLabel.setText(t.getString("label.usernameLabel"));
		passwordLabel.setText(t.getString("label.passwordLabel"));
		newUsernameLabel.setText(t.getString("label.newUsernameLabel"));
		newPasswordLabel.setText(t.getString("label.newPasswordLabel"));
		repeatPasswordLabel.setText(t.getString("label.repeatPasswordLabel"));
		signUp.setText(t.getString("button.signUp"));
		buttonBack.setText(t.getString("button.buttonBack"));

		stage.setTitle(t.getString("program.name"));
	}

}
