import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.application.Platform;

public class main extends Application {
	private int row = 0;
	private int col = 0;
	private int p = 0;
	private int q = 0;
	private int hitCountPlayer = 0;
	private int hitCountCPU = 0;
	private char[][] boardPlayer = new char[10][10];
	private char[][] boardCPU = new char[10][10];
	private Button[][] buttonCPU = new Button[10][10];
	private Button[][] buttonPlayer = new Button[10][10];
	Label playerLabel = new Label("Player Messages: " + "\n" + "Open PLAYER.txt!");
	Label enemyLabel = new Label("CPU Messages: " + "\n" + "Open CPU.txt!");
	private char[] shipPlayer = { 'P', 'D', 'S', 'B', 'C' };
	private char[] shipCPU = { 'P', 'D', 'S', 'B', 'C' };
	private boolean player = false;
	private boolean winner = false;

	public static char[][] loadFile(char[][] board, String fileName) { // loads txt files into 2D arrays (the game
																		// boards)
		try {
			Scanner file = new Scanner(new File(fileName));
			for (int p = 0; p < 10; p++) {
				for (int q = 0; q < 10; q++) {
					board[p][q] = file.next().charAt(0);
				}
			}
		} catch (FileNotFoundException exception) {
			System.out.println("Error opening file");
		}
		return board;
	}

	public static void main(String[] args) {
		launch(args);
	}

	public static char[][] fire(char[][] boardPlayer, int row, int col, Label enemyLabel) { // receives a board and
																							// coords, then places a
																							// hit/miss marker on the
																							// target
		if (boardPlayer[row][col] == '*') {
			boardPlayer[row][col] = 'M';
			enemyLabel.setText("Enemy Messages: " + "\n" + "The CPU MISSED!");
		} else {
			boardPlayer[row][col] = 'H';
			enemyLabel.setText("Enemy Messages: " + "\n" + "The CPU HIT!");
		}
		return boardPlayer;
	}

	public static char[] sink(char[][] board, char[] ship, boolean player, Label label) {
		boolean sunk;
		String[] shipName = { "patrol", "destroyer", "submarine", "battleship", "carrier" };

		for (int i = 0; i < 5; i++) {
			sunk = true;
			for (int p = 0; p < 10; p++) {
				for (int q = 0; q < 10; q++) {
					if (board[p][q] == ship[i] || ship[i] == 'E') {
						sunk = false;
					}
				}
			}
			if (sunk) {
				label.setText(
						(player ? "Player messages: \n You HIT! The enemy " : "CPU messages: \n The CPU HIT! Your ")
								+ shipName[i] + " has sunk!");
				ship[i] = 'E';
			}
		}
		return ship;
	}

	public void start(Stage primaryStage) {
		GridPane layout = new GridPane();

		layout.setConstraints(playerLabel, 2, 13);
		layout.setConstraints(enemyLabel, 2, 14);

		// row heights
		for (int i = 0; i < 14; i++) {
			RowConstraints rowWidth = new RowConstraints((i == 14 ? 160 : 40));
			layout.getRowConstraints().add(rowWidth);
		}

		// creates dropdown menu
		Menu file = new Menu("file");
		MenuItem open = new MenuItem("Open");
		MenuItem restart = new MenuItem("Restart");
		MenuItem exit = new MenuItem("Exit");

		// exit button
		exit.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				System.exit(0);
			}
		});

		// restart button
		restart.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent h) {
				boardPlayer = new char[10][10];
				boardCPU = new char[10][10];
				hitCountPlayer = 0;
				hitCountCPU = 0;
				char[] shipPlayer = { 'P', 'D', 'S', 'B', 'C' };
				char[] shipCPU = { 'P', 'D', 'S', 'B', 'C' };

				playerLabel.setText("CPU Messages: " + "\n" + "Open CPU.txt!");
				enemyLabel.setText("Player Messages: " + "\n" + "Open PLAYER.txt!");
				start(primaryStage);
			}
		});

		// Open button
		open.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent h) {
				boardPlayer = loadFile(boardPlayer, "PLAYER.txt");
				boardCPU = loadFile(boardCPU, "CPU.txt");
				playerLabel.setText("Player Messages: " + "\n" + "files opened!");
				enemyLabel.setText("Player messages:" + "\n" + "files opened!");
				start(primaryStage);

				for (int p = 0; p < 10; p++) {
					for (int q = 0; q < 10; q++) {
						buttonPlayer[p][q].setDisable(false);
						buttonCPU[p][q].setDisable(false);
					}
				}
			}
		});

		file.getItems().addAll(open, restart, exit);

		MenuBar dropDown = new MenuBar();
		dropDown.getMenus().add(file);

		// add message boxes and dropdown menu
		layout.getChildren().addAll(playerLabel, enemyLabel, dropDown);

		// button factory
		for (int i = 6; i < 20; i = i + 13) {
			for (p = 0; p < 10; p++) {
				for (q = 0; q < 10; q++) {
					// set letters at the start of each row
					Label letterOrdinate = new Label(String.valueOf((char) (q + 65)));
					letterOrdinate.setPrefHeight(20);
					letterOrdinate.setPrefWidth(20);

					if (i == 19) {
						letterOrdinate.setText("              " + String.valueOf((char) (q + 65)) + "   ");
						letterOrdinate.setPrefWidth(65);
					}
					layout.setConstraints(letterOrdinate, i + 1, q + 2);

					// set numbers on top of each board
					Label numberOrdinate = new Label("     " + Integer.toString(q));
					numberOrdinate.setPrefHeight(20);
					numberOrdinate.setPrefWidth(40);
					layout.setConstraints(numberOrdinate, i + q + 2, 1);

					layout.getChildren().addAll(letterOrdinate, numberOrdinate);

					if (i == 6) {
						final int b = p;
						final int d = q;
						buttonCPU[p][q] = new Button();
						buttonPlayer[p][q] = new Button();

						// position of CPU buttons
						buttonCPU[p][q].setPrefHeight(40);
						buttonCPU[p][q].setPrefWidth(40);
						layout.setConstraints(buttonCPU[p][q], 21 + q, 2 + p);

						// position of player buttons
						buttonPlayer[p][q].setPrefHeight(40);
						buttonPlayer[p][q].setPrefWidth(40);
						layout.setConstraints(buttonPlayer[p][q], 8 + q, 2 + p);
						layout.getChildren().addAll(buttonCPU[p][q], buttonPlayer[p][q]);

						// set player button ships
						switch (boardPlayer[p][q]) {
							case 'P':
								buttonPlayer[p][q].setText("P");
								break;
							case 'B':
								buttonPlayer[p][q].setText("B");
								break;
							case 'C':
								buttonPlayer[p][q].setText("C");
								break;
							case 'D':
								buttonPlayer[p][q].setText("D");
								break;
							case 'S':
								buttonPlayer[p][q].setText("S");
								break;
						}

						// CPU buttons with ships (player fires)
						if (boardCPU[p][q] == 'P' || boardCPU[p][q] == 'B' || boardCPU[p][q] == 'S'
								|| boardCPU[p][q] == 'D' || boardCPU[p][q] == 'C') {
							buttonCPU[p][q].setOnAction((ActionEvent x) -> {
								hitCountPlayer++;
								buttonCPU[b][d].setText("H");
								boardCPU[b][d] = 'H';
								playerLabel.setText("Player Messages: " + "\n" + "You HIT!");

								// did you sink an enemy ship?
								sink(boardCPU, shipCPU, player = true, playerLabel);

								// Player win check
								if (hitCountPlayer == 17) {
									for (int p = 0; p < 10; p++) {
										for (int q = 0; q < 10; q++) {
											buttonPlayer[p][q].setDisable(true);
											buttonCPU[p][q].setDisable(true);
											winner = true;
										}
									}
								}

								// CPU fires after player fires
								if (!winner) {
									do {
										row = (int) (Math.random() * (10 - 0));
										col = (int) (Math.random() * (10 - 0));
									} while ((boardPlayer[row][col] == 'M') || (boardPlayer[row][col] == 'H'));

									boardPlayer = fire(boardPlayer, row, col, enemyLabel);
								}

								// if CPU hits
								if (boardPlayer[row][col] == 'H') {
									hitCountCPU++;
									buttonPlayer[row][col].setText("H");
									buttonPlayer[row][col].setDisable(true);
									sink(boardPlayer, shipPlayer, player = false, enemyLabel);
								} else {
									buttonPlayer[row][col].setText("M");
									buttonPlayer[row][col].setDisable(true);
								}

								// CPU win check
								if (hitCountCPU == 17) {
									for (int p = 0; p < 10; p++) {
										for (int q = 0; q < 10; q++) {
											buttonPlayer[p][q].setDisable(true);
											buttonCPU[p][q].setDisable(true);
										}
									}
								}

								buttonCPU[b][d].setDisable(true);
							});
						} else {
							buttonCPU[p][q].setOnAction((ActionEvent y) -> {
								buttonCPU[b][d].setText("M");
								playerLabel.setText("Player Messages: " + "\n" + "You MISSED!");

								// CPU fires after player fires
								do {
									row = (int) (Math.random() * (10 - 0));
									col = (int) (Math.random() * (10 - 0));
								} while ((boardPlayer[row][col] == 'M') || (boardPlayer[row][col] == 'H'));

								boardPlayer = fire(boardPlayer, row, col, enemyLabel);

								// if CPU hits
								if (boardPlayer[row][col] == 'H') {
									hitCountCPU++;
									buttonPlayer[row][col].setText("H");
									buttonPlayer[row][col].setDisable(true);
									sink(boardPlayer, shipPlayer, player = false, enemyLabel);
								} else {
									buttonPlayer[row][col].setText("M");
									buttonPlayer[row][col].setDisable(true);
								}

								// CPU win check
								if (hitCountCPU == 17) {
									for (int p = 0; p < 10; p++) {
										for (int q = 0; q < 10; q++) {
											buttonPlayer[p][q].setDisable(true);
											buttonCPU[p][q].setDisable(true);
										}
									}
								}

								buttonCPU[b][d].setDisable(true);
							});
						}
						buttonPlayer[b][d].setDisable(true);
						buttonCPU[b][d].setDisable(true);
					}
				}
			}
		}

		Scene scene = new Scene(layout, 1200, 600);
		primaryStage.setTitle("Battleship!");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}