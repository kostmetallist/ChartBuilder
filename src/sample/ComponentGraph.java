package sample;

import java.util.*;

public class ComponentGraph {

    public class Node {
        // index and lowLink for tarjan -- node is unvisited by default;
        // id represents some additional info about node, e.g. cluster index for which this node belongs,
        // initial value -1 as default non-marked node.
        public int index = -1, lowLink = -1, id = -1;
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
                    elem.id = clusterIndex++;
                }

                concentratedNodes.add(cycle);
                System.out.println("SCC detected with size " + cycle.size());
            }
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
