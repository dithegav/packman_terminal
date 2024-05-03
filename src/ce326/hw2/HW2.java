package ce326.hw2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class HW2 {
    public static List<String> moveHistory = new ArrayList<>(); //save the history here and then turn it into an JSONObject
    public String filePath;//keep an active file name in order to help with jump move

    public Board createBoardFromJson (String filePath) throws IOException, JSONException {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        JSONObject json = new JSONObject(content);
    
        int rows = json.getInt("rows");
        int cols = json.getInt("columns");
        int energy = json.getInt("energy");
        Board board = new Board(rows, cols);

        board.setNumRows(rows);
        board.setNumCols(cols);
    
        JSONArray boardContent = json.getJSONArray("init");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                String[] elementInfo = boardContent.getString(i * cols + j).split(",");
                char symbol = elementInfo[0].charAt(0);
    
                if (symbol == '#') {
                    board.setElement(i, j, new Obstacle());
                } else if (symbol == 'v') {
                    board.setElement(i, j, new Vegetable());
                } else if (symbol == 'f') {
                    board.setElement(i, j, new Fish());
                } else if (symbol == 'm') {
                    board.setElement(i, j, new Meat());
                } else if (symbol == '@') {
                    board.setElement(i, j, new Ghost());
                    board.NumGhosts++;
                    ((Ghost) board.getElement(i, j)).setCurrentPosition(new int[]{i, j});
                } else if (symbol == 'X') {
                    board.setElement(i, j, new Player());
                    ((Player) board.getElement(i, j)).setCurrentPosition(new int[]{i, j});
                    ((Player) board.getElement(i, j)).setEnergy(energy);
                }
            }
        }
    
        return board;
    }

    public JSONObject showMoveHistory(Board board) {
        JSONObject moveHistoryJSON = new JSONObject();
        JSONObject movesJSON = new JSONObject();

        // Populate movesJSON with move history
        for (int i = 0; i < moveHistory.size(); i++) {
            movesJSON.put(String.valueOf(i), moveHistory.get(i));
        }

        moveHistoryJSON.put("moves", movesJSON);

        // Manually print the move history JSON
        System.out.println("{");
        System.out.print("  \"moves\": {");

        // Iterate over each move entry and print as JSON
        //Cant use toString cuase it hashes and when i have more than 10 moves its not sorted
        boolean isFirst = true;
        for (int i = 0; i < moveHistory.size(); i++) {
            if (!isFirst) {
                System.out.print(",");
            }
            System.out.print("\n    \"" + i + "\": \"" + moveHistory.get(i) + "\"");
            isFirst = false;
        }

        System.out.println("\n  }");
        System.out.println("}");

        return moveHistoryJSON;
    }

    private static void handleGameMenu(HW2 hw2, Scanner scanner, Board board) {
        // Handle interactions within the game menu
        boolean debugDijkstra = false;
        JSONObject moveHistoryJSON = new JSONObject();

        int flag = 0;

        while (true) {

            String option;

            if (flag == 1){
                System.out.println("\n- Load Game      (L/l)");
                System.out.println("- Debug Dijkstra (D/d)");
                System.out.println("- Show History   (H/h)");
                System.out.println("- Jump to move   (J/j)");
                System.out.println("- Continue game  (C/c)");
                System.out.println("- Quit           (Q/q)\n");

                System.out.print("Your option: ");

                option = scanner.nextLine().trim();
            }
            else {
                option = "c";
            }
            flag = 1;

            switch (option.toLowerCase()) {
                case "l":
                    System.out.print("Enter input filename: ");
                    String fileName = scanner.nextLine().trim();

                    try {
                        board = hw2.createBoardFromJson(fileName);

                        
                        if (board != null) {
                            System.out.println("Game loaded successfully!\n");
                            moveHistory = new ArrayList<>();
                            hw2.filePath = fileName;
                        }

                        board.printBoard();
                        
                    } catch (Exception e) {
                        System.out.println("Error loading game: " + e.getMessage());
                    }

                    break;

                case "d":

                    debugDijkstra = !debugDijkstra;
                    System.out.println("Debug Dijkstra is " + (debugDijkstra ? "enabled" : "disabled"));

                    break;

                case "h":
                    //keep the history to use for a jump 
                    moveHistoryJSON = hw2.showMoveHistory(board);

                    break;

                case "j":
                    //the user has given a place where he wants to back
                    System.out.print("Enter move number: ");
                    int moveTo = Integer.parseInt(scanner.nextLine().trim());
                    JSONObject moves = moveHistoryJSON.getJSONObject("moves");

                    if (moveTo > moves.length()){
                        System.out.println("\nWrong number");
                        break;
                    }

                    try {    
                        board = hw2.createBoardFromJson(hw2.filePath);
                    } catch (Exception e) {
                        System.out.println("Error loading game: " + e.getMessage());
                    }
                    moveHistory = new ArrayList<>();

                    if (moveTo > 1) {
                        //if the number is even then go one player move back
                        //if the number is odd go to the prev player move and then go one more back
                        if (moveTo % 2 == 1){
                            moveTo--;
                        }

                        //go to the prev player move 
                        moveTo -= 1;

                        //now do every move before that and wait for the user 
                        for (int i=0; i < moves.length(); i+=2) {

                            if (moveTo < i){
                                break;
                            }

                            String moveValue = moves.getString(String.valueOf(i));
                            moveValue = moveValue.substring(3);

                            board.HandlePlayerMove(moveValue, false, moveHistory);
                        }
                    }

                    board.printBoard();
                    break;

                case "c":
                    
                    while (true){
                        System.out.print("\nEnter next move (e.g., A1): ");
                        String move = scanner.nextLine().trim();

                        if (move.equalsIgnoreCase("z")) {
                            System.out.println("Pausing the game...");
                            break;
                        }

                        if (isValidMove(move)) {

                            int gameStatus = board.HandlePlayerMove(move, debugDijkstra, moveHistory);

                            if (gameStatus== -42){
                                System.out.println("\nGAME OVER");
                                break;
                            }
                            else if (gameStatus == 42){
                                System.out.println("\nYOU WON");
                                break;
                            }

                            // Print updated board
                            board.printBoard();

                        } else {
                            System.out.println("Invalid input. Try again...");
                        }

                    }

                    break;
                    
                case "q":
                    System.out.println("Exiting the program...");
                    scanner.close();
                    System.exit(0);

                default:
                    System.out.println("Invalid option");
                    break;
            }
        }
    }

    private static boolean isValidMove(String move) {
        // Implement validation for the input move (e.g., "A1")
        return move.matches("[A-Za-z][0-9]+");
    }

    public static void main(String[] args) {
        HW2 hw2 = new HW2();

        Scanner scanner = new Scanner(System.in);
        Board board = null; // Initialize the board

        // Main menu loop
        while (true) {
            System.out.println("\n- Load Game      (L/l)");
            System.out.println("- Quit           (Q/q)");
            System.out.print("Your option: ");

            String option = scanner.nextLine().trim();

            switch (option.toLowerCase()) {
                case "l":
                    System.out.print("Enter input filename: ");
                    hw2.filePath = scanner.nextLine().trim();

                    try {
                        board = hw2.createBoardFromJson(hw2.filePath);
                        
                        if (board != null) {
                            System.out.println("Game loaded successfully!\n");
                        }
                        
                        board.printBoard();

                    } catch (Exception e) {
                        System.out.println("Error loading game: " + e.getMessage());
                    }
                    break;

                case "q":
                    System.out.println("Exiting the program...");
                    scanner.close();
                    System.exit(0);

                default:
                    System.out.println("Invalid option");
                    break;
            }

            if (board != null) {
                handleGameMenu(hw2, scanner, board);
                return;
            }
        }
    }
}

