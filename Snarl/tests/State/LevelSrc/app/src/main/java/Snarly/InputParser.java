package Snarly;

import java.util.Scanner;

class InputParser {

    private static InputParser singleton;

    public InputParser() {
    }

    public static InputParser getInstance() {
        if(singleton == null) {
            singleton = new InputParser();
        }
        return singleton;
    }

    String parseUserJson() {
        Scanner scanner = new Scanner(System.in);
        String readString = scanner.nextLine();
        StringBuilder sb = new StringBuilder();
        while (readString != null) {
            if (readString.isEmpty() || "END".equals(readString)) {
                break;
            }
            sb.append(readString);
            if (scanner.hasNextLine()) {
                readString = scanner.nextLine();
            } else {
                readString = null;
            }
        }
        return sb.toString();
    }
}
