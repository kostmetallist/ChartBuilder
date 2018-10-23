package sample;

import java.util.*;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class ComponentGraph {

    public class Node {
        // index and lowLink for tarjan -- node is unvisited by default;
        // id represents some additional info about node, e.g. cluster index for which this node belongs,
        // initial value -1 as default non-marked node.
        public int index = -1, lowLink = -1, id = -1;
        public double weight = -1;
        public List<Integer> content;

        public Node(List<Integer> content) {
            this.content = content;
        }

        public String toString() {
            return content.toString();
        }
    }

    // graph internal data
    HashMap<List<Integer>, Node> nodes = new LinkedHashMap<>();
    HashMap<Node, List<Node>>    links = new HashMap<>();

    // tarjan related data
    private int index = 0, clusterIndex = 0, sccN = 0;
    private ArrayDeque<Node> visited = new ArrayDeque<>();
    private HashSet<List<Integer>> stack = new HashSet<>();

    // contains scc clusters (until sccN) and transit (after sccM) nodes
    private List<Set<Node>> concentratedNodes = new ArrayList<>();

    // topological sorting data
    private Stack<Node>  greyNodes = new Stack<>();
    private List<Node> blackListed = new ArrayList<>();

    // whether the graph is SCC itself with continuously numbered nodes from 0
    // let SCC+such_numeration = Well Organized Graph
    private boolean isWOG = false;


    // Node fabricating
    public Node createNode(List<Integer> content) {
        return new Node(content);
    }

    public void addNode(Node node) {

        this.nodes.put(node.content, node);
        this.links.put(node, new ArrayList<>());
    }

    // TODO add addLinkAll()
    public void addLink(Node from, Node to) {
        this.links.get(from).add(to);
    }

    public HashMap<List<Integer>, Node> getNodes() {
        return this.nodes;
    }

    public HashMap<Node, List<Node>> getLinks() {
        return this.links;
    }

    public List<Set<Node>> getConcentratedNodes() { return this.concentratedNodes; }

    public int getSccNumber() { return this.sccN; }

    // returns set of transit nodes (not scc)
    public Set<Node> tarjan() {

        for (Node n : links.keySet()) {
            if (n != null && n.index == -1) {
                strongConnect(n);
            }
        }

        Set<Node> transitNodes = new HashSet<>();
        sccN = clusterIndex;

        for (Node each : links.keySet()) {
            // if it hasn't been marked with cluster id
            if (each.id == -1) {

                each.id = clusterIndex++;
                concentratedNodes.add(new HashSet<>(Arrays.asList(each)));
                transitNodes.add(each);
            }
        }

        return transitNodes;
    }

    private void strongConnect(Node node) {

        node.index = index;
        node.lowLink = index;
        index += 1;

        visited.push(node);
        stack.add(node.content);

        List<Node> neighbours = links.get(node);

        if (neighbours != null) {

            neighbours.forEach(n -> {

                if (n.index == -1) {
                    strongConnect(n);
                    node.lowLink = Math.min(node.lowLink, n.lowLink);
                }

                else if (stack.contains(n.content)) {
                    node.lowLink = Math.min(node.lowLink, n.index);
                }
            });
        }

        if (node.lowLink == node.index) {

            HashSet<Node> cycle = new HashSet<>();

            while (true) {

                Node p = visited.pop();
                stack.remove(p.content);
                cycle.add(p);

                if (p == node) {
                    break;
                }
            }

            // second condition marks auto-looping nodes as SCCs too
            if (cycle.size() > 1 ||
                    links.get(node).contains(node)) {

                for (Node elem : cycle) {
                    elem.id = clusterIndex;
                }

                clusterIndex++;
                concentratedNodes.add(cycle);
                System.out.println("SCC detected with size " + cycle.size());
            }
        }
    }

    // creates graph where each node corresponds to cluster in concentratedNodes of graph-caller
    public ComponentGraph createConcentratedGraph() {

        // newContentsStorage is for accelerating search by lists as elements of Map.keyset()
        List<List<Integer>> newContentsStorage = new ArrayList<>();
        ComponentGraph newGraph = new ComponentGraph();
        int i = 0;

        for (Set<Node> cluster : concentratedNodes) {

            // node in new graph contains index of corresponding cluster as content and as id
            List<Integer> newContent = new ArrayList<>(Arrays.asList(i));
            Node newNode = createNode(newContent);

            newContentsStorage.add(newContent);
            newNode.id = i;
            newGraph.addNode(newNode);
            i++;
        }

        i = 0;

        for (Set<Node> cluster : concentratedNodes) {

            Set<Node> newLinks = new HashSet<>();
            Set<Node> clusterLinks = new HashSet<>();
            Node nodeFrom = newGraph.nodes.get(newContentsStorage.get(i));

            // filling the set for all ways from cluster
            for (Node node : cluster) {
                clusterLinks.addAll(links.get(node));
            }

            // we can be sure that node.id equals to concentratedNodes index of that cluster id
            for (Node node : clusterLinks) {

                int id = node.id;
                Node nodeTo = newGraph.nodes.get(newContentsStorage.get(id));

                // we must not register auto-loops for further correct sortNodes() work
                if (nodeTo != nodeFrom)
                    newLinks.add(nodeTo);
            }

            newGraph.links.put(nodeFrom, new ArrayList<>(newLinks));
            i++;
        }

        return newGraph;
    }

    public void dfs(Node node) {

        greyNodes.push(node);
        List<Node> adjacencies = links.get(node);

        for (Node neighbour : adjacencies) {

            //System.out.println("  ngbr " + neighbour.content);
            if (!blackListed.contains(neighbour))
                dfs(neighbour);
        }

        blackListed.add(greyNodes.pop());
    }

    public List<Node> sortNodes() {

        for (Node from : links.keySet()) {

            //System.out.println("from " + from.content);
            if (blackListed.contains(from))
                continue;

            dfs(from);
        }

        return this.blackListed;
    }

    private boolean isInf(double val, double inf) {

        if (inf < 0 && val < inf+1.0 ||
                inf > 0 && val > inf-1.0) {
            return true;
        }

        else return false;
    }

    // returns number of valid rows
    private int dpFill(double[][] dp, double inf, boolean inverseWeights) {

        // vNum + 1
        int rows = dp.length;

        // initializing
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < dp[i].length; j++) {
                dp[i][j] = inf;
            }
        }

        // shortest distance from first vertex
        // to itself consisting of 0 edges
        dp[0][0] = 0;

        // reorganizing links structure (reversing)
        Map<Node, List<Node>> fromNodes = new HashMap<>();

        for (Map.Entry<Node, List<Node>> entry : links.entrySet()) {

            Node node = entry.getKey();

            if (inverseWeights) { node.weight = -node.weight; }
            fromNodes.put(node, new ArrayList<>());
        }

        for (Map.Entry<Node, List<Node>> entry : links.entrySet()) {
            for (Node node : entry.getValue()) {

                fromNodes.get(node).add(entry.getKey());
            }
        }

        Map<Integer, List<Node>> idNodes = new HashMap<>();

        for (Map.Entry<Node, List<Node>> entry : fromNodes.entrySet()) {
            idNodes.put(entry.getKey().id, entry.getValue());
        }

        for (int i = 1; i < rows; i++)
        {
            for (int j = 0; j < dp[i].length; j++)
            {
                List<Node> jLinks = idNodes.get(j);

                for (int k = 0; k < jLinks.size(); k++)
                {
                    if (!isInf(dp[i-1][jLinks.get(k).id], inf))
                    {
                        double curr_wt = dp[i-1][jLinks.get(k).id] +
                                jLinks.get(k).weight;

                        if (isInf(dp[i][j], inf))
                            dp[i][j] = curr_wt;
                        else
                            dp[i][j] = Math.min(dp[i][j], curr_wt);
                    }
                }
            }
        }

        int rowsToIgnore = 0;

        for (int i = rows-1; i > 0; i--) {

            int infCounter = 0;

            for (int j = 0; j < dp[i].length; j++) {
                if (!isInf(dp[i][j], inf)) {
                    break;
                }

                else {
                    infCounter++;
                }
            }

            if (infCounter == dp[i].length) {
                rowsToIgnore++;
            }
        }

//        for (int i = 0; i < rows; i++) {
//            for (int j = 0; j < dp[i].length; j++) {
//                System.out.print(dp[i][j] + " ");
//            }
//
//            System.out.println();
//        }

//        System.out.println("Reduced " + rowsToIgnore);
        return rowsToIgnore;
    }

    // MMC = extreme min mean cycle
    // pls ensure that caller is Well Organized Graph
    private double getMmcWeight(boolean inverseWeights) {

        if (!this.isWOG) {

            System.err.println("getMinMeanCycleWeight: given graph is not WOG");
            return -1;
        }

        // number of vertices
        int vNum = this.links.size();
        double inf = -1000000.0;
        // matrix for storing weights of paths from 1st node to another
        double[][] dp = new double[vNum+1][vNum];
        int rowsToIgnore = dpFill(dp, inf, inverseWeights);

        double[] fracs = new double[vNum];

        for (int i = 0; i < vNum; i++) {

            fracs[i] = inf;
        }

        int lastRow = vNum - rowsToIgnore;

        for (int i = 0; i < vNum; i++) {

            // checking for nonInfinity
            if (!isInf(dp[lastRow][i], inf)) {
                for (int j = 0; j < lastRow; j++) {
                    if (!isInf(dp[j][i], inf)) {
                        fracs[i] = Math.max(((double) dp[lastRow][i]-dp[j][i])/(lastRow-j), fracs[i]);
                    }
                }
            }
        }

        double min = 0;

        for (int i = 0; i < vNum; i++) {
            if (!isInf(fracs[i], inf)) {

                min = fracs[i];
                break;
            }
        }

        for (int i = 0; i < vNum; i++) {

            if (!isInf(fracs[i], inf) &&
                    fracs[i] < min) {

                min = fracs[i];
            }
        }

        return min;
    }

    // XMC = extreme mean cycle (min or max)
    // ensure concentratedNodes is filled
    public void printXmcGraph() {

        // iterating over all components, then calculating XMC for each
        for (int i = 0; i < sccN; i++) {

            ComponentGraph scc = new ComponentGraph();
            Set<Node> nodePool = new HashSet<>(this.concentratedNodes.get(i));
            int id = 0;

            for (Node node : nodePool) {

                node.id = id++;
                scc.nodes.put(node.content, node);

                List<Node> nodeLinks = new ArrayList<>();
                scc.links.put(node, nodeLinks);

                for (Node ngbr : this.links.get(node)) {
                    if (nodePool.contains(ngbr)) {
                        nodeLinks.add(ngbr);
                    }
                }
            }

            scc.isWOG = true;
            System.out.println("[" + scc.getMmcWeight(false) + ", " +
                    (-scc.getMmcWeight(true)) + "]");
        }
    }

    public void fillPulsarWeights() {

        int i = 0;

        for (Node each : this.links.keySet()) {

            each.weight = (i%2==0)? 1.0: -1.0;
            i++;
        }
    }

    // ca is the CellularArea object that's associated with current graph
    public void fillJacobianWeights(CellularArea ca) {

        for (Node each : this.links.keySet()) {

            CellularArea finalCell = ca.getCellById(each.content);

            // getting middle point
            double x = (finalCell.getFinishX()-finalCell.getStartX())/2 + finalCell.getStartX();
            double y = (finalCell.getFinishY()-finalCell.getStartY())/2 + finalCell.getStartY();

            double TT  = 0.4 - 6/(1 + x*x + y*y);
            double TTX = 12*x/((1+x*x+y*y)*(1+x*x+y*y));
            double TTY = 12*y/((1+x*x+y*y)*(1+x*x+y*y));

            double dfdx = 0.9*(cos(TT)*(1 - y*TTX)  - x*TTX*sin(TT));
            double dfdy = 0.9*(sin(TT)*(-1 - x*TTY) - y*TTY*cos(TT));
            double dgdx = 0.9*(sin(TT)*(1 - y*TTX)  + x*TTX*cos(TT));
            double dgdy = 0.9*(cos(TT)*(1 + x*TTY)  - y*TTY*sin(TT));

            double b11 = dfdx*dfdx + dgdx*dgdx;
            double b12 = dfdx*dfdy + dgdx*dgdy;
            double b21 = dfdy*dfdx + dgdy*dgdx;
            double b22 = dfdy*dfdy + dgdy*dgdy;

            double dRoot = Math.sqrt((b11+b22)*(b11+b22) - 4*(b11*b22 - b12*b21));

            double k1 = (b11+b22+dRoot)/2;
            double k2 = (b11+b22-dRoot)/2;

            each.weight = Math.log(Math.max(k1, k2))/2;
        }
    }


    public void printContent() {

//        for (Node node : links.keySet()) {
//
//            System.out.println(node.content.toString() + ":");
//
//            for (Node i : links.get(node)) {
//                System.out.println("  " + i.content.toString());
//            }
//        }

        System.out.println("Overall graph vertices number: " + links.keySet().size());
    }
}
