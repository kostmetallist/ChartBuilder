package sample;

import java.util.*;
import javafx.util.Pair;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;
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
    // TODO make a check OR modify CellularArea.initializeChildren for overwriting 'children' list
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
     *  for specified dot.
     *
     */
    public Integer getCellNumber(Double x, Double y) {

        if (!checkDotBounds(x, y)) {

            System.err.println("Warning: CellularArea.getCellNumber given dot is out of bounds");
            return -1;
        }

        Integer i = (int) floor((x - this.startX) / cellWidth);
        Integer j = (int) floor((y - this.startY) / cellHeight);

        return (this.cellsY - 1 - j)*this.cellsX + i;
    }

    public CellularArea getCellById(List<Integer> id) {

        CellularArea parent = this;

        for (int i = 0; i < id.size(); i++) {

            if (parent.getChildren().isEmpty()) {

                if (i < id.size()-1) {
                    System.err.println("Warning: CellularArea.getCellById reached end of fragmentation but id is not over");
                }

                return parent;
            }

            parent = parent.getChildren().get(id.get(i));
        }

        // TODO remove?
        return parent;
    }

    /**
     * Note: this method returns appropriate CellularArea
     * even if that cell is DISCARDED. You should check that
     * case in the wrapping methods.
     */
    public CellularArea getCellByDot(Double x, Double y) {

        if (this.getChildren().isEmpty()) {
            return this;
        }

        CellularArea child = this.getChildren().get(this.getCellNumber(x, y));
        return child.getCellByDot(x, y);
    }

    // cg must be root CellularArea
    public void doInitialFragmentation(ComponentGraph cg) {

        for (CellularArea frag: this.children) {

            ComponentGraph.Node node = cg.createNode(frag.getId());
            cg.addNode(node);
            System.out.println("New fragment " + frag.id.toString());
        }
    }


    public void doRegularFragmentation(ComponentGraph cg) {

        if (this.children.isEmpty() && this.getStatus() == CellStatus.ACTIVE) {

            setCellsXY(2, 2);

            for (CellularArea frag: this.children) {

                ComponentGraph.Node node = cg.createNode(frag.getId());
                cg.addNode(node);
                System.out.println("New fragment " + frag.id.toString());
            }

            return;
        }

        // even if we have terminate DISCARDED cell as 'this',
        // it won't fragment further because of absence of children
        for (CellularArea each : this.children) { each.doRegularFragmentation(cg); }
    }

    /**
     *
     * @param amount how many points it will get
     * @param generalList modifiable list forming general sequence
     */
    public void getRandomPoints(Integer amount,
                                List<Pair<Double, Double>> generalList) {

        if (this.children.isEmpty() && this.getStatus() == CellStatus.ACTIVE) {

            List<Pair<Double, Double>> areaDots = new ArrayList<>();
            Double areaWidth = this.finishX - this.startX;
            Double areaHeight = this.finishY - this.startY;

            for (Integer i = 0; i < amount; i++) {

                Double randX = this.startX + random() * areaWidth;
                Double randY = this.startY + random() * areaHeight;
                Pair<Double, Double> dot = new Pair<Double, Double>(randX, randY);

                areaDots.add(dot);
            }

            generalList.addAll(areaDots);
            return;
        }

        for (CellularArea each : this.children) { each.getRandomPoints(amount, generalList); }
    }

    /**
     *
     * @param amount how many points it will get
     * @param generalList modifiable list forming general sequence
     * @param deltaPercent percentage of extension of this area
     */
    public void getRandomPoints(Integer amount,
                                List<Pair<Double, Double>> generalList,
                                Double deltaPercent) {

        if (this.children.isEmpty() && this.getStatus() == CellStatus.ACTIVE) {

            List<Pair<Double, Double>> areaDots = new ArrayList<>();
            Double areaWidth = this.finishX - this.startX;
            Double areaHeight = this.finishY - this.startY;
            Double deltaX = areaWidth * deltaPercent;
            Double deltaY = areaHeight * deltaPercent;

            for (Integer i = 0; i < amount; i++) {

                Double randX = (this.startX - deltaX) + random() * (areaWidth + 2*deltaX);
                Double randY = (this.startY - deltaY) + random() * (areaHeight + 2*deltaY);
                Pair<Double, Double> dot = new Pair<Double, Double>(randX, randY);

                areaDots.add(dot);
            }

            generalList.addAll(areaDots);
            return;
        }

        for (CellularArea each : this.children) { each.getRandomPoints(amount, generalList, deltaPercent); }
    }

    public List<Pair<Double, Double>> getActiveArea() {

        List<Pair<Double, Double>> result = new ArrayList<>();

        this.getRandomPoints(20, result);
        return result;
    }

    public void fillSymbolicImage(ComponentGraph cg,
                                    Expression eF,
                                    Expression eG) {

//        Argument xArg = new Argument("x", 0.0);
//        Argument yArg = new Argument("y", 0.0);
//
//        eF.addArguments(xArg, yArg);
//        eG.addArguments(xArg, yArg);

        Argument xArg = eF.getArgument("x");
        Argument yArg = eG.getArgument("y");

        for (List<Integer> id : cg.getNodes().keySet()) {

            CellularArea cArea = this.getCellById(id);
            ComponentGraph.Node node = cg.getNodes().get(id);
            HashSet<ComponentGraph.Node> adjacentNodes = new HashSet<>();
            List<Pair<Double, Double>> cellDots = new ArrayList<>();

            cArea.getRandomPoints(100, cellDots);

            for (Pair<Double, Double> dot : cellDots) {

                xArg.setArgumentValue(dot.getKey());
                yArg.setArgumentValue(dot.getValue());

                Double newX = eF.calculate();
                Double newY = eG.calculate();

                // in this case we do not registrating such link in graph
                if (!this.checkDotBounds(newX, newY)) { break; }

                // 'remoteCell' = cell which contains mapped dot
                CellularArea remoteCell = this.getCellByDot(newX, newY);

                if (remoteCell.getStatus() == CellStatus.DISCARDED) { break; }

                ComponentGraph.Node remoteNode = cg.getNodes().get(remoteCell.getId());
                adjacentNodes.add(remoteNode);
            }

            cg.getLinks().get(node).addAll(adjacentNodes);
        }
    }

    public void markAsDiscarded(Set<ComponentGraph.Node> nodes) {

        for (ComponentGraph.Node node : nodes) {
            this.getCellById(node.content).setStatus(CellStatus.DISCARDED);
        }
    }
}
