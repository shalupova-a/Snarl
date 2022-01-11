import org.json.*;

import java.util.*;

class TestRoom {

    private static final Set<Integer> VALID_MARKERS = new HashSet<>(Arrays.asList(1, 2));
    private static final Set<Integer> INVALID_MARKERS = new HashSet<>(Collections.singletonList(0));
    private static int cols, rows;

    private enum Cardinals {
        UP(-1, 0), DOWN(0, -1), RIGHT(1, 0), LEFT(0, 1);
        int x, y;

        Cardinals(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public static JSONArray getCardinalValuesForPoint(Coordinate p) {
            JSONArray up = new JSONArray(Arrays.asList(p.x + UP.x, p.y + UP.y));
            JSONArray down = new JSONArray(Arrays.asList(p.x + DOWN.x, p.y + DOWN.y));
            JSONArray right = new JSONArray(Arrays.asList(p.x + RIGHT.x, p.y + RIGHT.y));
            JSONArray left = new JSONArray(Arrays.asList(p.x + LEFT.x, p.y + LEFT.y));
            return new JSONArray(Arrays.asList(up, down, left, right));
        }
    }

    private static class Coordinate {
        public Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        private final int x;
        private final int y;

        @Override
        public String toString() {
            return new JSONArray(Arrays.asList(x, y)).toString();
        }
    }

    private static void printTraversablePoints(boolean isEmpty, JSONArray o, JSONArray p, JSONArray traversablePoints) {
        if (isEmpty) {
            JSONArray jsonArray =
                    new JSONArray(Arrays.asList("Failure: Point ", p, " is not in room at ", o));
            System.out.println(jsonArray.toString());
        } else {
            JSONArray jsonArray =
                    new JSONArray(Arrays.asList("Success: Traversable points from ", p, " in room at ", o, " are ", traversablePoints));
            System.out.println(jsonArray.toString());
        }
    }

    private static JSONArray filterValidPoints(JSONArray trueCardinals, JSONArray layout) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < trueCardinals.length(); i++) {
            int x = trueCardinals.getJSONArray(i).getInt(0);
            int y = trueCardinals.getJSONArray(i).getInt(1);
            if (x < 0 || y < 0 || x > rows - 1 || y > cols - 1) {
                continue;
            }
            if (VALID_MARKERS.contains(layout.getJSONArray(x).getInt(y))) {
                jsonArray.put(trueCardinals.getJSONArray(i));
            }
        }
        return jsonArray;
    }

    private static JSONArray addOrigins(JSONArray truePoints, JSONArray o) throws JSONException {
        JSONArray arrayToRet = new JSONArray();
        for (int i = 0; i < truePoints.length(); i++) {
            arrayToRet.put(Arrays.asList(truePoints.getJSONArray(i).getInt(0) + o.getInt(0),
                    truePoints.getJSONArray(i).getInt(1) + o.getInt(1)));
        }
        return arrayToRet;
    }

    private static JSONArray getTraversablePoints(Coordinate truePoint, JSONArray o, JSONArray layout) throws JSONException {

        JSONArray trueCardinals = Cardinals.getCardinalValuesForPoint(truePoint);

        return addOrigins(filterValidPoints(trueCardinals, layout), o);
    }

    private static String parseUserJson() {
        Scanner scanner = new Scanner(System.in);
        String readString = scanner.nextLine();
        StringBuilder sb = new StringBuilder();
        while (readString != null) {
            if (readString.isEmpty()) {
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

    public static void main(String[] args) throws JSONException {
        while (true) {
            String userJsonString = parseUserJson();
            if (userJsonString.isEmpty() || userJsonString.equals("exit")) {
                break;
            }
            JSONArray jsonArray = new JSONArray(userJsonString);
            JSONObject room = jsonArray.getJSONObject(0);
            JSONArray point = jsonArray.getJSONArray(1);
            JSONArray layout = (JSONArray) room.get("layout");
            JSONObject bounds = room.getJSONObject("bounds");
            rows = bounds.getInt("rows");
            cols = bounds.getInt("columns");
            JSONArray origin = room.getJSONArray("origin");
            Coordinate truePoint
                    = new Coordinate(point.getInt(0) - origin.getInt(0), point.getInt(1) - origin.getInt(1));
            if (truePoint.x < 0 || truePoint.y < 0 || truePoint.x + 1 > rows || truePoint.y + 1 > cols) {
                printTraversablePoints(true, origin, point, null);
                return;
            }
            JSONArray points = getTraversablePoints(truePoint, origin, layout);
            printTraversablePoints(false, origin, point, points);
        }

    }
}
