package Snarly;

public class Bounds {
    private Integer rows;
    private Integer columns;

    public Bounds() {
    }

    public Bounds(Integer rows, Integer columns) {
        this.rows = rows;
        this.columns = columns;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Integer getColumns() {
        return columns;
    }

    public void setCols(Integer columns) {
        this.columns = columns;
    }
}
