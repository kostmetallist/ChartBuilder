package sample;

import java.util.*;

public class ComponentGraph {

    public class Node {
        // -1, -1, node is unvisited by default.
        public int index = -1, lowLink = -1;
        public List<Integer> content;

        public Node(List<Integer> content) {
            this.content = content;
        }

        public String toString() {
            return content.toString();
        }
    }

    // graph internal data
    // TODO maybe introduce LinkedHashMap to 'nodes' (due to predictable iteration order)
    HashMap<List<Integer>, Node> nodes = new HashMap<>();
    HashMap<Node, List<Node>>    links = new HashMap<>();

    private int index = 0;

    private ArrayDeque<Node> visited = new ArrayDeque<>();
    private HashSet<List<Integer>> stack = new HashSet<>();
    private HashSet<Node> sccElements = new HashSet<>();


    // service method
    public Node createNode(List<Integer> content) {
        return new Node(content);
    }

    public void addNode(Node node) {

        this.nodes.put(node.content, node);
        this.links.put(node, new ArrayList<>());
    }

    public void addLink(Node from, Node to) {
        this.links.get(from).add(to);
    }

    public HashMap<List<Integer>, Node> getNodes() {
        return this.nodes;
    }

    public HashMap<Node, List<Node>> getLinks() {
        return this.links;
    }

    // returns set of scc
    public HashSet<Node> tarjan() {

        for (Node n : nodes.values()) {
            if (n != null && n.index == -1) {
                strongConnect(n);
            }
        }

        return sccElements;
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

            while (true) {

                Node p = visited.pop();
                stack.remove(p.content);
                sccElements.add(p);

                if (p == node) {
                    break;
                }
            }
        }
    }

    // be sure that sccElements is filled,
    // otherwise method call will be useless
    public Set<Node> detectIsolated() {

        Set<Node> isolated = new HashSet<>();

        for (Node node : links.keySet()) {

            if (!sccElements.contains(node)) { isolated.add(node); }
        }

        return isolated;
    }
}
