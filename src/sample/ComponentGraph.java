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

    // service method
    public Node createNode(List<Integer> content) {
        return new Node(content);
    }

    public void addNode(Node node) {

        this.nodes.put(node.content, node);
        this.graph.put(node, new ArrayList<>());
    }

    public void addLink(Node from, Node to) {
        this.graph.get(from).add(to);
    }

    // graph internal data
    HashMap<List<Integer>, Node> nodes = new HashMap<>();
    HashMap<Node, List<Node>>    graph = new HashMap<>();

    private int index = 0;

    // for a DFS
    private ArrayDeque<Node> visited = new ArrayDeque<>();
    private HashSet<List<Integer>> stack = new HashSet<>();

    // stores strongly connected components
    // from the algorithm pass
    private HashSet<HashSet<Node>> scc = new HashSet<>();

    // returns set of scc
    public HashSet<HashSet<Node>> tarjan() {

        for (Node n : nodes.values()) {
            if (n != null && n.index == -1) {
                strongConnect(n);
            }
        }

        return scc;
    }

    private void strongConnect(Node node) {

        node.index = index;
        node.lowLink = index;
        index += 1;

        visited.push(node);
        stack.add(node.content);

        List<Node> neighbours = graph.get(node);

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
            if (cycle.size() > 1) {
                scc.add(cycle);
            }
        }
    }
}
