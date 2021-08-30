package tictactoe;

import java.util.*;

public class Main {

    private static char[] board = new char[9];
    private static int count = 0;
    enum State {
        ONGOING,
        DRAW,
        X_WINS,
        O_WINS
    }
    enum Mode {
        USER,
        EASY,
        MEDIUM,
        HARD;

        public static boolean exists(String name) {
            for (Mode m : Mode.values()) {
                if (m.name().equals(name.toUpperCase())) {
                    return true;
                }
            }
            return false;
        }
    }
    static class Player {
        private char symbol;
        private Mode mode;

        public Player(char symbol, Mode mode) {
            this.symbol = symbol;
            this.mode = mode;
        }

        public char getSymbol() {
            return symbol;
        }

        public Mode getMode() { return  mode; }
    }
    public static Player[] players = new Player[2];
    static class Move {
        public int score, index;
        public Move(int value) {
            score = index = value;
        }
    }

    public static void main(String[] args) {
        while (true) {
            State gameState = State.ONGOING;
            Arrays.fill(board, ' ');  // Ensure that board is initially empty
            count = 0;                      // Reset count variable
            int i = 0;                      // Player 0 always starts
            menuLoop();
            printBoard();
            while (gameState == State.ONGOING) {
                switch (players[i].getMode()) {
                    case USER:
                        makeMoveUser(players[i].getSymbol());
                        break;
                    case EASY:
                        System.out.println("Making move level \"easy\"");
                        makeMoveAIEasy(players[i].getSymbol());
                        break;
                    case MEDIUM:
                        System.out.println("Making move level \"medium\"");
                        makeMoveAIMedium(players[i].getSymbol());
                        break;
                    case HARD:
                        System.out.println("Making move level \"hard\"");
                        makeMoveAIHard(players[i].getSymbol());
                    default:
                        break;
                }
                printBoard();
                if (count >= 9) {
                    gameState = State.DRAW;
                }
                if (checkWin(board, players[i].getSymbol())) {
                    gameState = State.valueOf(players[i].getSymbol() + "_WINS");
                }
                i = i == 0 ? 1 : 0;
                switch (gameState) {
                    case X_WINS:
                        System.out.println("X wins");
                        break;
                    case O_WINS:
                        System.out.println("O wins");
                        break;
                    case DRAW:
                        System.out.println("Draw");
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public static boolean checkWin(char[] board, char sym) {
        return ((board[0] == sym && board[1] == sym && board[2] == sym) ||
                (board[3] == sym && board[4] == sym && board[5] == sym) ||
                (board[6] == sym && board[7] == sym && board[8] == sym) ||
                (board[0] == sym && board[3] == sym && board[6] == sym) ||
                (board[1] == sym && board[4] == sym && board[7] == sym) ||
                (board[2] == sym && board[5] == sym && board[8] == sym) ||
                (board[0] == sym && board[4] == sym && board[8] == sym) ||
                (board[2] == sym && board[4] == sym && board[6] == sym));

    }

    public static ArrayList<Integer> emptyIndices(char[] board) {
        ArrayList<Integer> availSpots = new ArrayList<Integer>();
        for (int i = 0; i < 9; i++) {
            if (isEmpty(i)) {
                availSpots.add(i);
            }
        }
        return availSpots;
    }

    public static boolean isEmpty(int index) {
        return (board[index] != 'X' && board[index] != 'O');
    }

    public static void makeMoveAIEasy(char sym) {
        while (true) {
            Random rand = new Random();
            int index = rand.nextInt(9);
            if (isEmpty(index)){
                board[index] = sym;
                count++;
                break;
            }
        }
    }

    public static void makeMoveAIMedium(char sym) {
        char opp = sym == 'X' ? 'O' : 'X';         // Opponent's symbol
        for (int i = 0; i < 9; i++) {
            if (isEmpty(i)) {
                board[i] = sym;
                if (checkWin(board, sym)) {    // Check if AI can win with move
                    count++;
                    return;
                }
                board[i] = opp;
                if (checkWin(board, opp)) {    // Check if opponent can win with move
                    board[i] = sym;
                    count++;
                    return;
                }
                board[i] = ' ';
            }
        }
        makeMoveAIEasy(sym);                // If no move has been made, make random (easy mode) move.
    }

    public static void makeMoveAIHard(char sym) {
        board[minimax(board, sym, sym).index] = sym;
        count++;
    }

    public static void makeMoveUser(char sym) {
        Scanner sc = new Scanner(System.in);
        int index = 0;
        boolean bError = true;
        do {
            try {
                System.out.print("Enter the coordinates: > ");
                index = (Integer.parseInt(sc.next()) - 1) * 3 + (Integer.parseInt(sc.next()) - 1);
                sc.nextLine();
                if (index < 0 || index > 9){
                    System.out.println("Coordinates should be from 1 to 3!");
                    continue;
                } else if (!isEmpty(index)) {
                    System.out.println("This cell is occupied! Choose another one!");
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("You should enter numbers!");
                sc.nextLine();
                continue;
            }
            bError = false;
        } while (bError);
        board[index] = sym;
        count++;
    }

    public static void menuLoop() {
        Scanner sc = new Scanner(System.in);
        String modeX = "", modeO = "";
        boolean bError = true;
        do {
            System.out.print("Input command: > ");
            Scanner sd = new Scanner(sc.nextLine());
            switch (sd.next()) {
                case "start":
                    if (sd.hasNext()) {
                        modeX = sd.next();
                        if (sd.hasNext()) {
                            modeO = sd.next();
                            if (Mode.exists(modeX) && Mode.exists(modeO)) {
                                players[0] = new Player('X', Mode.valueOf(modeX.toUpperCase()));
                                players[1] = new Player('O', Mode.valueOf(modeO.toUpperCase()));
                                bError = false;
                                break;
                            }
                        }
                    }
                    System.out.println("Bad parameters!");
                    sd.close();
                    break;
                case "exit":
                    System.exit(0);
                    break;
                default:
                    System.out.println("Unknown command!");
                    break;
            }
        } while (bError);
    }

    public static Move minimax(char newBoard[], char sym, char constant) {
        char opp_1 = sym == 'X' ? 'O' : 'X', opp_2 = constant == 'X' ? 'O' : 'X';
        ArrayList<Integer> availSpots = emptyIndices(newBoard);
        if (checkWin(newBoard, sym)) {
            return new Move(10);
        } else if (checkWin(newBoard, opp_2)) {
            return new Move(-10);
        } else if (availSpots.size() == 0){
            return new Move(0);
        }
        ArrayList<Move> moves = new ArrayList<Move>();
        for (int i = 0; i < availSpots.size(); i++) {
            Move move = new Move(availSpots.get(i));
            newBoard[move.index] = sym;
            move.score = minimax(newBoard, opp_1, constant).score;
            newBoard[move.index] = ' ';
            moves.add(move);
        }
        if (sym == constant) {
            return moves.stream().max(Comparator.comparingInt(x -> x.score)).get();
        } else {
            return moves.stream().min(Comparator.comparingInt(x -> x.score)).get();
        }
    }

    public static void printBoard() {
        System.out.println("---------");
        for(int i = 0; i < 3; i++){
            System.out.print("| ");
            for(int j = 0; j < 3; j++){
                System.out.print(board[i * 3 + j] + " ");
            }
            System.out.print("|");
            System.out.println();
        }
        System.out.println("---------");
    }

}
