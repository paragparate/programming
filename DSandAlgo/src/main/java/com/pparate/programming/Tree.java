package com.pparate.programming;

/* #category: #tree #java #implementation
   #purpose : Tree implementation in Java
 */

import java.util.LinkedList;
import java.util.Queue;

public class Tree {
    public Node root;
    private int noOfElements;
    private int depth;
    private int maxWidth;

    public Tree() {
        this.root = null;
        this.noOfElements = 0;
        this.depth = 0;
        maxWidth = 0;
    }

    public Tree(int data) {
        this.root = new Node(data);
        this.noOfElements = 1;
        this.depth = 1;
        this.maxWidth = (new Integer(data)).toString().length();
    }

    public Tree(int[] data) {
        this.root = new Node(data[0]);
        this.noOfElements = 1;
        this.depth = 1;
        this.maxWidth = (new Integer(data[0])).toString().length();

        for (int i=1; i<data.length; i++) {
            this.addNode(data[i]);
        }

    }

    public void addNode(int data) {
        Node newNode = new Node(data);

        // find insertion node & add
        Node parentNode = findInsertionNode();

        if (parentNode.left == null) parentNode.left = newNode;
        else parentNode.right = newNode;

        // increment elements count
        this.noOfElements++;

        // check if level needs to be incremented
        // elements (max) = 2^depth - 1
        this.depth = getLevel(this.noOfElements);

        // update maxWidth for printing spaces if required
        int width = (new Integer(data)).toString().length();
        if (maxWidth < width)
            maxWidth = width;

    }

    public void deleteNode(int data) {

    }

    public void print() {
        traverseTree(TraversalType.LEVELORDER, TraversalAction.PRINT_TREE);
    }

    private Node findInsertionNode() {
        return traverseTree(TraversalType.LEVELORDER, TraversalAction.GET_INSERT_NODE);
    }

    private Node traverseTree(TraversalType traversalType, TraversalAction action) {

        switch (traversalType) {
            case LEVELORDER:
                Queue<Node> queue = new LinkedList<>();
                queue.add(this.root);
                int elementCount = 1;

                while (!queue.isEmpty()) {
                    Node currNode = queue.remove();
                    if (currNode.left == null) {
                        if (action == TraversalAction.GET_INSERT_NODE) return currNode;
                    }
                    else queue.add(currNode.left);

                    if (currNode.right == null) {
                        if (action == TraversalAction.GET_INSERT_NODE) return currNode;
                    }
                    else queue.add(currNode.right);

                    if (action == TraversalAction.PRINT_TREE)
                        printNode(currNode, elementCount++);
                }
        }
        return null;
    }

    private void printNode(Node node, int elementPosition) {
        // If you consider display area as grid or matrix
        // then total cells required will [depth x 2^(depth)]
        // leftGap = 2^(d-n) - 1 & space = 2^(d-n+1) - 1
        int currLevel = getLevel(elementPosition);
        int gap = (int) Math.pow(2, this.depth - currLevel) - 1;
        int space = (int) Math.pow(2, this.depth - currLevel + 1) - 1;

        // if this is starting element of level leave left gap
        int startPosition = (int)Math.ceil(Math.pow(2, currLevel - 1));
        if (elementPosition == startPosition) {
            // start in a new line
            System.out.println();
            System.out.print(repeat(repeat(" ", this.maxWidth), gap));
        }

        // print element padded by spaces to cover cell size = max width
        System.out.print(node.data);
        System.out.print(repeat(" ", this.maxWidth - (new Integer(node.data)).toString().length()));

        // print spaces after element
        System.out.print(repeat(repeat(" ", this.maxWidth), space));

    }

    private int getLevel(int elements) {
        return (int) ((Math.log10(elements)) / (Math.log10(2))) + 1;
    }

    private String repeat(String string, int count) {
        String returnString = "";
        while (--count >= 0)
            returnString += string;
        return returnString;
    }

    private enum TraversalAction {
        PRINT_TREE, FIND_NODE, GET_INSERT_NODE
    }

    private enum TraversalType {
        INORDER, PREORDER, POSTORDER, LEVELORDER
    }

    class Node {
        private int data;
        private Node left;
        private Node right;

        Node(int data) {
            this.data = data;
            this.left = null;
            this.right = null;
        }
    }
}

