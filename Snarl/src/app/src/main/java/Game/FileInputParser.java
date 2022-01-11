package Game;

import Game.Common.InputParser;
import Game.Model.Level;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class FileInputParser implements InputParser {
    Scanner scanIn;

    public FileInputParser() throws FileNotFoundException {
        scanIn = new Scanner(new FileReader(new File("").getAbsolutePath()+"/snarl.levels"));
    }

    public FileInputParser(String fileName) throws FileNotFoundException {
        if (fileName == null) {
            fileName = new File("").getAbsolutePath()+"/snarl.levels";
        }
        scanIn = new Scanner(new FileReader(fileName));
    }

    @Override
    public String getInput() {
        StringBuilder sb = new StringBuilder();
        String test = "";
        while (scanIn.hasNextLine()) {
            test = scanIn.nextLine();
            if (test.equals("")) {
                sb.append("\n");
            }
            sb.append(test.trim());
        }
        scanIn.close();
        return sb.toString();
    }

    public JSONArray getLevelArray() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String stringToParse = getInput();
        String[] splitLevelsString = stringToParse.split("\n");
        int numLevels = Integer.parseInt(splitLevelsString[0]);
        if (splitLevelsString.length - 1 != numLevels) {
            throw new Exception("File format is poor");
        }
        JSONArray levels = new JSONArray();
        for(int i = 1; i < numLevels + 1; i++) {
            levels.put(new JSONObject(splitLevelsString[i]));
        }
        return levels;
    }

    @Override
    public List<Level> parseInputToGameObject() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String stringToParse = getInput();
        String[] splitLevelsString = stringToParse.split("\n");
        int numLevels = Integer.parseInt(splitLevelsString[0]);
        if (splitLevelsString.length - 1 != numLevels) {
            throw new Exception("File format is poor");
        }
        List<Level> levels = new LinkedList<>();
        for (int i = 1; i < numLevels; i++) {
            Level level = mapper.readValue(splitLevelsString[i], Level.class);
            levels.add(level);
        }
        return levels;
    }


    public static void main(String[] args) throws Exception {
        InputParser inputParser = new FileInputParser();
        inputParser.parseInputToGameObject();
    }
}
