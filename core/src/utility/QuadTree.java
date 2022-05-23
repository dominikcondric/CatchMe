package utility;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import ecs.components.PhysicsComponent;
import ecs.components.PhysicsComponent.Fixture;

public class QuadTree implements Iterable<utility.QuadTree.Node> {
	
	// Single node of a tree
	public class Node {
		final float width, height;
		final Vector2 referencePoint;
		Node[] children;
		Array<Fixture> elements;
		
		public Node(Vector2 referencePoint, float width, float height) {
			this.width = width;
			this.height = height;
			this.referencePoint = referencePoint;
			clear();
		}
		
		public int getElementCount() {
			if (elements != null)
				return elements.size; 
			return 0;
		}
		
		public Array<Fixture> getComponents() {
			return elements;
		}
		
		void clear() {
			if (elements != null)
				elements.clear();
			
			children = null;
		}
	}
	
	// Member variables
	private Node root;
	
	// Member methods
	public QuadTree(Vector2 referencePoint, float maxWidth, float maxHeight) {
		setRootParams(referencePoint, maxWidth, maxHeight);
	}
	
	public void setRootParams(Vector2 referencePoint, float maxWidth, float maxHeight) {
		root = new Node(referencePoint, maxWidth, maxHeight);
	}
	
	public void print() {
		printNode(root, 0);
		System.out.println("----------------------------------");
	}
	
	private void printNode(Node node, int i) {
		if (node.children != null) {
			for (Node n : node.children) {
				printNode(n, i+1);
			}
		}
		
		System.out.println(String.format("Tree depth: %d, node count: %d", i, node.getElementCount()));
	}
	
	public void clear() {
		root.clear();
	}
	
	public void addComponent(PhysicsComponent component) {
		for (Fixture f : component.getFixtureList()) 
			addElementToNode(f, root);
	}
	
	private void addElementToNode(Fixture fixture, Node node) {
		Rectangle boundingRect = fixture.getBoundingRectangle();
		node = findMatchingNode(boundingRect, node);
		
		if (node.elements == null) {
			node.elements = new Array<>();
		}
		
		node.elements.add(fixture);
	}
		
	private Node findMatchingNode(Rectangle rect, Node node) {
		Vector2 halfNodeCoords = new Vector2(node.referencePoint).add(node.width / 2.f, node.height / 2.f); 
		if (rect.x < halfNodeCoords.x && rect.x + rect.width > halfNodeCoords.x ||
				rect.y < halfNodeCoords.y && rect.y + rect.height > halfNodeCoords.y) {
			return node;
		}
		
		if (node.children == null) {
			divideNode(node);
		}
		
		int i = 0;
		if (rect.x > halfNodeCoords.x)
			i += 1;
		
		if (rect.y > halfNodeCoords.y)
			i += 2;
		
		return findMatchingNode(rect, node.children[i]);
	}
	
	private void divideNode(Node node) {
		Node[] nodes = new Node[4];
		for (int i = 0; i < 4; ++i) {
			Vector2 referencePoint = new Vector2();
			referencePoint.x = node.referencePoint.x + (i%2 * node.width / 2.f);
			referencePoint.y = node.referencePoint.y + (i/2 * node.height / 2.f);
			nodes[i] = new Node(referencePoint, node.width / 2.f, node.height / 2.f);
		}
		
		node.children = nodes;
	}
	
	private void findElementNodes(ArrayList<Node> nodes, Node node) {
		if (node.elements != null) {
			nodes.add(node);
		}
		
		if (node.children != null)
			for (int i = 0; i < 4; ++i) 
				findElementNodes(nodes, node.children[i]);
	}
	
	/**
	 * Returns the array of all elements that are in the current node and its descendant nodes
	 * @param components Mutable list of PhysicsComponent elements
	 * @param node Node to start the search from
	 */
	public void findAllSiblingsAndChildrenElements(Array<Fixture> fixtures, Node node) {
		if (node.elements != null)
				fixtures.addAll(node.elements);
		
		if (node.children != null) {
			for (int i = 0; i < 4; ++i)
				findAllSiblingsAndChildrenElements(fixtures, node.children[i]);
		}
	}

	@Override
	public Iterator<Node> iterator() {
		ArrayList<Node> elementNodes = new ArrayList<Node>();
		findElementNodes(elementNodes, root);
		return elementNodes.iterator();
	}
}
