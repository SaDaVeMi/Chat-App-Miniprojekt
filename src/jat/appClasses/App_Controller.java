package jat.appClasses;

import java.util.Optional;
import jat.JavaFX_App_Template;
import jat.ServiceLocator;
import jat.abstractClasses.Controller;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.WindowEvent;

public class App_Controller extends Controller<App_Model, App_View> {
	ServiceLocator serviceLocator;

	public App_Controller(final JavaFX_App_Template main, App_Model model, App_View view) {
		super(model, view);

		// register ourselves to handle window-closing event
		view.getStage().setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				Platform.exit();
			}
		});

		serviceLocator = ServiceLocator.getServiceLocator();
		serviceLocator.getLogger().info("Application controller initialized");

		// EventHandler part

		// Connecting client w/ server
		view.connect.setOnAction(event -> {
			String ipAddress = view.ipAddress.getText();
			int port = Integer.parseInt(view.port.getText());
			boolean secure = (view.secure.isSelected()) ? true : false;
			model.connect(ipAddress, port, secure);
			view.changeRoot(1); // '1' stands for the Login_View
		});

		// Switch window to SignUp_View
		view.createLogin.setOnAction(event -> {
			view.changeRoot(2);
			view.errorLogin.setVisible(false);
			view.username.clear();
			view.password.clear();
			view.newUsername.clear();
			view.newPassword.clear();
			view.repeatPassword.clear();
			view.errorSignUpUser.setVisible(false);
			view.errorSignUpPassword.setVisible(false);
		});

		// Switch window to Login_View
		view.buttonBack.setOnAction(event -> {
			view.changeRoot(1);
			view.newUsername.clear();
			view.newPassword.clear();
			view.repeatPassword.clear();
			view.errorSignUpUser.setVisible(false);
			view.errorSignUpPassword.setVisible(false);
		});

		// Create Account
		view.signUp.setOnAction(event -> {
			if (model.createLogin(view.newUsername.getText(), view.newPassword.getText())) {
				view.changeRoot(1);
				view.errorSignUpUser.setVisible(false);
				view.errorSignUpPassword.setVisible(false);
			} else {
				view.errorSignUpUser.setVisible(true);
			}
		});

		// Log in
		view.login.setOnAction(event -> {
			if (model.logIn(view.username.getText(), view.password.getText())) {
				view.username.clear();
				view.password.clear();
				model.listChatrooms();
				view.changeRoot(3);
				view.errorLogin.setVisible(false);
			} else {
				view.errorLogin.setVisible(true);
			}

		});

		// Log out
		view.menuAccountLogout.setOnAction(event -> {
			if (model.logOut()) {
				view.changeRoot(1);
			}
		});

		// Change password
		view.menuAccountChangePassword.setOnAction(event -> showChangePasswordDialog());

		// Delete account
		view.menuAccountDelete.setOnAction(event -> showDeleteAccountDialog());

		// Create chatroom
		view.addChatroom.setOnAction(event -> showChatroomDialog());

		// Join chatroom
		view.joinChatroom.setOnAction(event -> {
			if (view.chatroomList.getSelectionModel().getSelectedItem() != null) {
				joinChatroom(view.chatroomList.getSelectionModel().getSelectedItem(), main);
			}
		});
		// Also with double click
		view.chatroomList.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				joinChatroom(view.chatroomList.getSelectionModel().getSelectedItem(), main);
			}
		});

		// Refresh chatroom list
		view.refreshChatroom.setOnAction(event -> model.listChatrooms());
		
		// Change design
		view.menuDesignDark.setOnAction(event -> view.changeDesign(view.menuDesignDark.getText()));
		view.menuDesignLight.setOnAction(event -> view.changeDesign(view.menuDesignLight.getText()));

		
		// Listener part

		// Disable account menu when user logout
		model.getLoggedInProperty().addListener((observable, oldValue, newValue) -> {
			if (model.getLoggedIn()) {
				view.menuAccountChangePassword.setDisable(false);
				view.menuAccountLogout.setDisable(false);
				view.menuAccountDelete.setDisable(false);
			} else {
				view.menuAccountChangePassword.setDisable(true);
				view.menuAccountLogout.setDisable(true);
				view.menuAccountDelete.setDisable(true);
			}

		});

		// Disable logIn-button if txtfield empty
		view.username.textProperty().addListener((observable, oldValue, newValue) -> {
			if (view.username.getText().isEmpty()) {
				view.login.setDisable(true);
			} else if (!(view.username.getText().isEmpty()) && !(view.password.getText().isEmpty())) {
				view.login.setDisable(false);
			}
		});
		view.password.textProperty().addListener((observable, oldValue, newValue) -> {
			if (view.password.getText().isEmpty()) {
				view.login.setDisable(true);
			} else if (!(view.password.getText().isEmpty()) && !(view.username.getText().isEmpty())) {
				view.login.setDisable(false);
			}
		});

		// Disable signUp-button if passwords don't match
		view.newPassword.textProperty().addListener((observable, oldValue, newValue) -> validatePassword(newValue));
		view.repeatPassword.textProperty().addListener((observable, oldValue, newValue) -> validatePassword(newValue));

	}

	// Disable signUp-button if passwords don't match
	private void validatePassword(String newValue) {
		if (newValue.equals(view.newPassword.getText()) && newValue.equals(view.repeatPassword.getText())
				&& !(newValue.isEmpty()) && view.newPassword.getText().length() > 1) {
			view.signUp.setDisable(false);
			view.errorSignUpPassword.setVisible(false);
		} else {
			view.signUp.setDisable(true);
			view.errorSignUpPassword.setVisible(true);
		}
	}

	private void showDeleteAccountDialog() {
		Optional<ButtonType> result = view.deleteAccountDialog.showAndWait();
		if (result.isPresent() && result.get() == view.yesDelete) {
			if (model.deleteLogin()) {
				view.changeRoot(1);
			}
		}
	}

	// Change password in dialog window
	private void showChangePasswordDialog() {
		// EventFilter for changePassword button
		final Button changeBtn = (Button) view.changePasswordDialog.getDialogPane().lookupButton(view.changePassword);
		changeBtn.addEventFilter(ActionEvent.ACTION, event -> {
			if (view.changePasswordField.getText().equals(view.repeatChangePasswordField.getText())
					&& !view.changePasswordField.getText().isEmpty()) {
				if (model.changePassword(view.changePasswordField.getText())) {
					view.errorChangePassword.setVisible(false);
				} else {
					event.consume();
				}
			} else {
				view.errorChangePassword.setVisible(true);
				event.consume();
			}

		});
		Optional<ButtonType> result = view.changePasswordDialog.showAndWait();
		if (result.isPresent() && result.get() == view.changePassword) {
			view.changePasswordField.clear();
			view.repeatChangePasswordField.clear();
		} else if (result.isPresent() && result.get() == view.cancelPassword) {
			view.changePasswordField.clear();
			view.repeatChangePasswordField.clear();
		}
	}

	// Create chatroom in dialog window
	private void showChatroomDialog() {
		// EventFilter for CreateChatroom button
		final Button createBtn = (Button) view.createChatroomDialog.getDialogPane().lookupButton(view.createChatroom);
		createBtn.addEventFilter(ActionEvent.ACTION, event -> {
			if (!view.chatroomName.getText().isEmpty()) {
				String isPublic = (view.publicChat.isSelected()) ? "true" : "false";
				if (model.createChatroom(view.chatroomName.getText(), isPublic)) {
					view.errorChatroomName.setVisible(false);
				} else {
					view.errorChatroomName.setVisible(true);
					event.consume();
				}
			} else {
				event.consume();
			}

		});
		Optional<ButtonType> result = view.createChatroomDialog.showAndWait();
		if (result.isPresent() && result.get() == view.createChatroom) {
			view.chatroomName.clear();
		} else if (result.isPresent() && result.get() == view.cancelChatroom) {
			view.chatroomName.clear();
		}
	}

	// Join chatroom
	private void joinChatroom(String chatroom, JavaFX_App_Template main) {
		if (model.getChatroomThread(chatroom) == null) {
			if (model.joinChatroom(chatroom)) {
				main.startChatroom(chatroom);
			}
		}
	}
}
