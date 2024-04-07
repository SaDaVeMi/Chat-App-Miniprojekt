package jat;

import java.util.Scanner;

import jat.appClasses.App_Model;

public class TestApp_Model {

	public static void main(String[] args) {
		App_Model model = new App_Model();
		Scanner scan = new Scanner(System.in);
		model.connect("147.86.8.31", 50001, false);
		model.logIn("flo600", "1234");
		model.joinChatroom("Bier");
		scan.nextLine();
		model.closeChatroomThread("Bier");
		scan.nextLine();

	}

}
