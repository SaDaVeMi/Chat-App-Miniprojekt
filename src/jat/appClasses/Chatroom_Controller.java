package jat.appClasses;

import jat.abstractClasses.Controller;

public class Chatroom_Controller extends Controller<App_Model, Chatroom_View> {

	public Chatroom_Controller(App_Model model, Chatroom_View view) {
		super(model, view);

		// Send message
		view.send.setOnAction(event -> {
			if (!view.msgInput.getText().isEmpty()) {
			model.sendMessage(view.msgInput.getText(), view.chatroomName);
			view.msgInput.clear();
			}
		});

		// Leave chatroom
		view.menuChatroomLeave.setOnAction(event -> {
			if (model.leaveChatroom(view.chatroomName)) {
				model.closeChatroomThread(view.chatroomName);
				view.stop();
			}
		});

		// Refresh user list
		view.menuChatroomListUsers.setOnAction(event -> model.startChatroomUserList(view.chatroomName));

		// Close thread
		view.getStage().setOnCloseRequest(value -> model.closeChatroomThread(view.chatroomName));

		// Change design
		view.menuDesignDark.setOnAction(event -> view.changeDesign(view.menuDesignDark.getText()));
		view.menuDesignLight.setOnAction(event -> view.changeDesign(view.menuDesignLight.getText()));

		// Stop running chatroom-thread and leave all joined chatroom when user logout
		model.getLoggedInProperty().addListener((observable, oldValue, newValue) -> {
			if (!model.getLoggedIn()) {
				model.leaveChatroom(view.chatroomName);
				model.closeChatroomThread(view.chatroomName);
				view.stop();
			}
		});

	}

}
