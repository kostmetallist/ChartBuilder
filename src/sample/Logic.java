package sample;

import java.util.*;

import javafx.util.Pair;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;
import static java.lang.Math.cos;

public class Logic {


    public List<Pair<Double, Double>> rungeKutter(String f,
                                                  String g,
                                                  InitialData initData,
                                                  Integer iterationsNum) {

        List<Pair<Double, Double>> result = new ArrayList<>();

        Double x = initData.getInitX();
        Double y = initData.getInitY();
        Double t = initData.getInitT();
        Double deltaT = initData.getDeltaT();

        Argument xArg = new Argument("x", x);
        Argument yArg = new Argument("y", y);
        Argument tArg = new Argument("t", t);

        Expression eF = new Expression(f, xArg, yArg, tArg);
        Expression eG = new Expression(g, xArg, yArg, tArg);

        result.add(new Pair<Double, Double>(x, y));

        for (Integer i = 0; i < iterationsNum; i++) {

            Double kF1 = eF.calculate()*deltaT;
            Double kG1 = eG.calculate()*deltaT;

            xArg.setArgumentValue(x + kF1/2);
            yArg.setArgumentValue(y + kG1/2);
            tArg.setArgumentValue(t + deltaT/2);

            Double kF2 = eF.calculate()*deltaT;
            Double kG2 = eG.calculate()*deltaT;

            xArg.setArgumentValue(x + kF2/2);
            yArg.setArgumentValue(y + kG2/2);
            // sic, we don't need to modify tArg here

            Double kF3 = eF.calculate()*deltaT;
            Double kG3 = eG.calculate()*deltaT;

            xArg.setArgumentValue(x + kF3);
            yArg.setArgumentValue(y + kG3);
            tArg.setArgumentValue(t + deltaT);

            Double kF4 = eF.calculate()*deltaT;
            Double kG4 = eG.calculate()*deltaT;

            x = x + (kF1 + 2.0*kF2 + 2.0*kF3 + kF4)/6.0;
            y = y + (kG1 + 2.0*kG2 + 2.0*kG3 + kG4)/6.0;
            t = t + deltaT;

            result.add(new Pair<Double, Double>(x, y));
            xArg.setArgumentValue(x);
            yArg.setArgumentValue(y);
            tArg.setArgumentValue(t);
    }

        return result;
    }

    private Double fFunc(Double x, Double y, Double t) {
        return y;
    }

    private Double gFunc(Double x, Double y, Double t) {
        return (-0.25)*y + 0.5*x*(1-x*x) + 0.5*cos(0.2*t);
    }



    public List<Pair<Double, Double>> eulerPolycurver(String f,
                                                      String g,
                                                      InitialData initData,
                                                      Integer iterationsNum) {

        List<Pair<Double, Double>> result = new ArrayList<>();

        Double x = initData.getInitX();
        Double y = initData.getInitY();
        Double t = initData.getInitT();
        Double deltaT = initData.getDeltaT();

        Argument xArg = new Argument("x", x);
        Argument yArg = new Argument("y", y);
        Argument tArg = new Argument("t", t);

        Expression eF = new Expression(f, xArg, yArg, tArg);
        Expression eG = new Expression(g, xArg, yArg, tArg);

        result.add(new Pair<Double, Double>(x, y));

        for (Integer i = 0; i < iterationsNum; i++) {

            x = xArg.getArgumentValue() + eF.calculate() * deltaT;
            y = yArg.getArgumentValue() + eG.calculate() * deltaT;
            t = t + deltaT;

            result.add(new Pair<Double, Double>(x, y));
            xArg.setArgumentValue(x);
            yArg.setArgumentValue(y);
            tArg.setArgumentValue(t);
        }

        return result;
    }

    public List<Pair<Double, Double>> arbitraryMapper(String f,
                                                      String g,
                                                      InitialData initData,
                                                      Integer iterationsNum) {

        List<Pair<Double, Double>> result = new ArrayList<>();

        Double x = initData.getInitX();
        Double y = initData.getInitY();

        Argument xArg = new Argument("x", x);
        Argument yArg = new Argument("y", y);

        Expression eF = new Expression(f, xArg, yArg);
        Expression eG = new Expression(g, xArg, yArg);

        result.add(new Pair<Double, Double>(x, y));

        for (Integer i = 0; i < iterationsNum; i++) {

            x = eF.calculate();
            y = eG.calculate();

            result.add(new Pair<Double, Double>(x, y));
            xArg.setArgumentValue(x);
            yArg.setArgumentValue(y);
        }

        return result;
    }

    public Pair<List<Pair<Double, Double>>, List<Short>> crBuilderTopSort(String f,
                                                                         String g,
                                                                         CellularArea initArea,
                                                                         Integer fragDepth,
                                                                          Integer dotsByCell) {
        if (fragDepth < 0) {

            System.err.println("crBuilder: fragmentation depth must be 0+");
            return null;
        }

        Expression eF = new Expression(f);
        Expression eG = new Expression(g);
        Argument xArg = new Argument("x", 0.0);
        Argument yArg = new Argument("y", 0.0);

        eF.addArguments(xArg, yArg);
        eG.addArguments(xArg, yArg);

        ComponentGraph cgInit = new ComponentGraph();

        initArea.doInitialFragmentation(cgInit);
        initArea.fillSymbolicImage(cgInit, eF, eG);
        cgInit.tarjan();
        initArea.markupEntireArea(cgInit);

        cgInit.printContent();

        for (Integer i = 0; i < fragDepth; i++) {

            ComponentGraph cg = new ComponentGraph();

            initArea.doRegularFragmentation(cg);
            initArea.fillSymbolicImage(cg, eF, eG);
            cg.tarjan();
            initArea.markupEntireArea(cg);

            cg.printContent();

            if (i == fragDepth-1) {

                ComponentGraph subGraph = cg.createConcentratedGraph();
                System.out.println("Concentrated: ");
                subGraph.printContent();
                List<ComponentGraph.Node> sortedReversed = subGraph.sortNodes();
                List<Integer> orderList = new ArrayList<>();

                for (ComponentGraph.Node node : sortedReversed) {
                    if (node.id < cg.getSccNumber())
                        orderList.add(0, node.id);
                }

                System.out.println("Order of SCC: ");
                for (Integer each : orderList) { System.out.println(each); }
            }
        }

        return initArea.getActiveColouredArea(dotsByCell);
    }


    public List<Pair<Double, Double>> crBuilderSimple(String f,
                                                     String g,
                                                     CellularArea initArea,
                                                     Integer fragDepth) {

        if (fragDepth < 0) {

            System.err.println("crBuilder: fragmentation depth must be 0+");
            return null;
        }

        Expression eF = new Expression(f);
        Expression eG = new Expression(g);
        Argument xArg = new Argument("x", 0.0);
        Argument yArg = new Argument("y", 0.0);

        eF.addArguments(xArg, yArg);
        eG.addArguments(xArg, yArg);

        ComponentGraph cgInit = new ComponentGraph();

        initArea.doInitialFragmentation(cgInit);
        initArea.fillSymbolicImage(cgInit, eF, eG);
        initArea.markAsDiscarded(cgInit.tarjan());

        cgInit.printContent();

        for (Integer i = 0; i < fragDepth; i++) {

            ComponentGraph cg = new ComponentGraph();

            initArea.doRegularFragmentation(cg);
            initArea.fillSymbolicImage(cg, eF, eG);
            initArea.markAsDiscarded(cg.tarjan());

            cg.printContent();
        }

        List<Pair<Double, Double>> result = initArea.getActiveArea(20);
        return result;
    }
}
