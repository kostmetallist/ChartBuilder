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

    // topological sorting data
    private Stack<Node>  greyNodes = new Stack<>();
    private List<Node> blackListed = new ArrayList<>();


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
