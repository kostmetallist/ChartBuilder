package sample;

import java.util.*;
import javafx.util.Pair;
import static java.lang.Math.floor;
import static java.lang.Math.random;

public class CellularArea {

    public enum CellStatus { ACTIVE, DISCARDED }

    private Double startX;
    private Double startY;

    private Double finishX;
    private Double finishY;

    private Integer cellsX;
    private Integer cellsY;

    private Double cellWidth;
    private Double cellHeight;

    private CellStatus status = CellStatus.ACTIVE;
    private List<Integer> id;
    private List<CellularArea> children = new ArrayList<>();


    public CellularArea(Double startX, Double startY,
                        Double finishX, Double finishY,
                        Integer cellsX, Integer cellsY,
                        List<Integer> id) {

        this.startX = startX;
        this.startY = startY;
        this.finishX = finishX;
        this.finishY = finishY;
        this.cellsX = cellsX;
        this.cellsY = cellsY;

        this.cellWidth  = (this.finishX-this.startX) / this.cellsX;
        this.cellHeight = (this.finishY-this.startY) / this.cellsY;
        this.id = id;

        // if we really have a fragmentation
        if (cellsX != 1 || cellsY != 1) { this.initializeChildren(); }
    }

    public CellularArea(Pair<Double, Double> startCoords,
                        Pair<Double, Double> finishCoords,
                        Pair<Integer, Integer> cellsNumbers,
                        List<Integer> id) {

        this.startX = startCoords.getKey();
        this.startY = startCoords.getValue();
        this.finishX = finishCoords.getKey();
        this.finishY = finishCoords.getValue();
        this.cellsX = cellsNumbers.getKey();
        this.cellsY = cellsNumbers.getValue();

        this.cellWidth  = (this.finishX-this.startX) / this.cellsX;
        this.cellHeight = (this.finishY-this.startY) / this.cellsY;
        this.id = id;

        // if we really have a fragmentation
        if (this.cellsX != 1 || this.cellsY != 1) { this.initializeChildren(); }
    }

    public Double getStartX() {
        return startX;
    }

    public Double getStartY() {
        return startY;
    }

    public Double getFinishX() {
        return finishX;
    }

    public Double getFinishY() {
        return finishY;
    }

    public Integer getCellsX() {
        return cellsX;
    }

    // actually, method can be invoked
    // only when this cell w/o children
    public void setCellsXY(Integer cellsX, Integer cellsY) {

        if (this.cellsX == 1 && this.cellsY == 1 &&
                (cellsX != 1 || cellsY != 1)) {

            this.cellsX = cellsX;
            this.cellsY = cellsY;
            this.cellWidth = (this.finishX - this.startX) / this.cellsX;
            this.cellHeight  = (this.finishY - this.startY) / this.cellsY;

            this.initializeChildren();
        }
    }

    public Integer getCellsY() {
        return cellsY;
    }

    public Double getCellWidth() {
        return cellWidth;
    }

    public Double getCellHeight() {
        return cellHeight;
    }

    public CellStatus getStatus() {
        return status;
    }

    public void setStatus(CellStatus status) {
        this.status = status;
    }

    public List<Integer> getId() {
        return id;
    }

    public List<CellularArea> getChildren() {
        return children;
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

    private void initializeChildren() {

        for (int j = 0; j < this.cellsY; j++) {
            for (int i = 0; i < this.cellsX; i++) {

                Double childStartX = this.startX + i*this.cellWidth;
                Double childStartY = this.startY + (cellsY-j-1)*this.cellHeight;

                Double childFinishX = this.startX + (i+1)*this.cellWidth;
                Double childFinishY = this.startY + (cellsY-j)*this.cellHeight;

                List<Integer> childId = new ArrayList<>(this.id);
                childId.add(j*this.cellsX+i);

                CellularArea child = new CellularArea(childStartX, childStartY,
                                                        childFinishX, childFinishY,
                                                        1, 1, childId);
                this.children.add(child);

                //System.out.println("Child id " + child.getId().toString() + " bounds X: " + child.startX + " to " + child.finishX);
                //System.out.println("             bounds Y: " + child.startY + " to " + child.finishY);
            }
        }
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


    public void doFragmentation(ComponentGraph cg) {

        if (this.children.isEmpty() && this.getStatus() == CellStatus.ACTIVE) {

            setCellsXY(2, 2);

            for (CellularArea frag: this.children) {

                ComponentGraph.Node node = cg.createNode(frag.getId());
                cg.addNode(node);
                System.out.println("New fragment " + frag.id.toString());
            }

            return;
        }

        for (CellularArea each : this.children) { each.doFragmentation(cg); }

        //for (CellularArea each : this.children) {

        //    System.out.println(each);
        //}
    }

    /**
     *
     * @param amount how many points it will get
     * @param delta value of extension of this area
     * @param generalList modifiable list forming general sequence
     */
    public void getRandomPoints(Integer amount, Double delta,
                                List<Pair<Double, Double>> generalList) {

        if (this.children.isEmpty() && this.getStatus() == CellStatus.ACTIVE) {

            List<Pair<Double, Double>> areaDots = new ArrayList<>();
            Double areaWidth = this.finishX - this.startX;
            Double areaHeight = this.finishY - this.startY;

            //System.out.println("Generating dots for " + this.getId().toString() + "...");

            for (Integer i = 0; i < amount; i++) {

                Double randX = this.startX + random() * areaWidth;
                Double randY = this.startY + random() * areaHeight;
                Pair<Double, Double> dot = new Pair<Double, Double>(randX, randY);

                areaDots.add(dot);
            }

            generalList.addAll(areaDots);
            return;
        }

        for (CellularArea each : this.children) { each.getRandomPoints(amount, delta, generalList); }
    }

    public List<Pair<Double, Double>> getActiveArea() {

        List<Pair<Double, Double>> result = new ArrayList<>();

        this.getRandomPoints(200, 0.0, result);
        return result;
    }
}
