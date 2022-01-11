package Game.Model;

public class Bounds {
    private Integer rows;
    private Integer columns;

    public Bounds() {
    }

    public Bounds(Integer rows, Integer columns) {
        this.rows = rows;
        this.columns = columns;
    }

    /**
     * Get rows
     * @return Integer
     */
    public Integer getRows() {
        return rows;
    }

    /**
     * Get columns
     * @return Integer
     */
    public Integer getColumns() {
        return columns;
    }
}
