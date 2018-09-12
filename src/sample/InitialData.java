package sample;

public class InitialData {

    private Double initX;
    private Double initY;
    private Double initT;
    private Double deltaT;

    public InitialData(Double initX, Double initY, Double initT, Double deltaT) {
        this.initX = initX;
        this.initY = initY;
        this.initT = initT;
        this.deltaT = deltaT;
    }

    public Double getInitX() {
        return initX;
    }

    public void setInitX(Double initX) {
        this.initX = initX;
    }

    public Double getInitY() {
        return initY;
    }

    public void setInitY(Double initY) {
        this.initY = initY;
    }

    public Double getInitT() {
        return initT;
    }

    public void setInitT(Double initT) {
        this.initT = initT;
    }

    public Double getDeltaT() {
        return deltaT;
    }

    public void setDeltaT(Double deltaT) {
        this.deltaT = deltaT;
    }
}
