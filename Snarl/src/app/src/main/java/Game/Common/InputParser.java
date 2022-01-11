package Game.Common;

import Game.Model.Level;
import org.json.*;

import java.util.List;

public interface InputParser {
    String getInput();
    List<Level> parseInputToGameObject() throws Exception;
    JSONArray getLevelArray() throws Exception;
}
