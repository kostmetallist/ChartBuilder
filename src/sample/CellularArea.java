package sample;

import java.util.*;
import javafx.util.Pair;
import static java.lang.Math.floor;

public class CellularArea {

    private Double startX;
    private Double startY;

    private Double finishX;
    private Double finishY;

    private Integer cellsX;
    private Integer cellsY;

    private Double cellWidth;
    private Double cellHeight;



    public CellularArea(Double startX, Double startY,
                        Double finishX, Double finishY,
                        Integer cellsX, Integer cellsY) {

        this.startX = startX;
        this.startY = startY;
        this.finishX = finishX;
        this.finishY = finishY;
        this.cellsX = cellsX;
        this.cellsY = cellsY;

        this.cellWidth  = (this.finishX-this.startX) / this.cellsX;
        this.cellHeight = (this.finishY-this.startY) / this.cellsY;
    }

    public CellularArea(Pair<Double, Double> startCoords,
                        Pair<Double, Double> finishCoords,
                        Pair<Integer, Integer> cellsNumbers) {

        this.startX = startCoords.getKey();
        this.startY = startCoords.getValue();
        this.finishX = finishCoords.getKey();
        this.finishY = finishCoords.getValue();
        this.cellsX = cellsNumbers.getKey();
        this.cellsY = cellsNumbers.getValue();

        this.cellWidth  = (this.finishX-this.startX) / this.cellsX;
        this.cellHeight = (this.finishY-this.startY) / this.cellsY;
    }

    public Double getStartX() {
        return startX;
    }

    public void setStartX(Double startX) {
        this.startX = startX;
        this.cellWidth  = (this.finishX-this.startX) / this.cellsX;
    }

    public Double getStartY() {
        return startY;
    }

    public void setStartY(Double startY) {
        this.startY = startY;
        this.cellHeight  = (this.finishY-this.startY) / this.cellsY;
    }

    public Double getFinishX() {
        return finishX;
    }

    public void setFinishX(Double finishX) {
        this.finishX = finishX;
        this.cellWidth  = (this.finishX-this.startX) / this.cellsX;
    }

    public Double getFinishY() {
        return finishY;
    }

    public void setFinishY(Double finishY) {
        this.finishY = finishY;
        this.cellHeight  = (this.finishY-this.startY) / this.cellsY;
    }

    public Integer getCellsX() {
        return cellsX;
    }

    public void setCellsX(Integer cellsX) {
        this.cellsX = cellsX;
        this.cellWidth  = (this.finishX-this.startX) / this.cellsX;
    }

    public Integer getCellsY() {
        return cellsY;
    }

    public void setCellsY(Integer cellsY) {
        this.cellsY = cellsY;
        this.cellHeight  = (this.finishY-this.startY) / this.cellsY;
    }

    public Double getCellWidth() {
        return cellWidth;
    }

    public Double getCellHeight() {
        return cellHeight;
    }

    private boolean checkDotBounds(Double x, Double y) {
        if (x < this.startX || x > this.finishX ||
            y < this.startY || y > this.finishY)

            return false;

        return true;
    }

    /**
     *  Gets the (i,j) pair
     *  of cell indices (counting according to x,y growth), i.e.
     *
     *  ________________
     *  |    |    |    |
     *  |0, 1|1, 1|2, 1|
     *  |____|____|____|
     *  |    |    |    |
     *  |0, 0|1, 0|2, 0|
     *  |____|____|____|
     *
     *  for specified dot.
     *
     */

    public Pair<Integer, Integer> getDotCell(Double x, Double y) {

        if (!checkDotBounds(x, y)) { return new Pair<Integer, Integer>(-1, -1); }

        Integer i = (int) floor((x - this.startX) / cellWidth);
        Integer j = (int) floor((y - this.startY) / cellHeight);

        return new Pair<Integer, Integer>(i, j);
    }

    /**
     *  Gets the cell number
     *  (counting up to down and left to right), i.e.
     *
     *  ________________
     *  |    |    |    |
     *  |  0 |  1 |  2 |
     *  |____|____|____|
     *  |    |    |    |
     *  |  3 |  4 |  5 |
     *  |____|____|____|
     *
     *  for specified pair of indices got from getDotCell().
     *
     */

    public Integer getCellNumber(Pair<Integer, Integer> indices) {

        return (this.cellsY - 1 - indices.getValue())*this.cellsX + indices.getKey();
    }

    public Integer getCellNumber(Double x, Double y) {

        if (!checkDotBounds(x, y)) { return -1; }

        Integer i = (int) floor((x - this.startX) / cellWidth);
        Integer j = (int) floor((y - this.startY) / cellHeight);

        return (this.cellsY - 1 - j)*this.cellsX + i;
    }
}
