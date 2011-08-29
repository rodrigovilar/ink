package org.ink.eclipse.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleTreeNode<T> {

	private final T value;
	private List<SimpleTreeNode<T>> children;

	public SimpleTreeNode(T value) {
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	public List<SimpleTreeNode<T>> getChildren() {
		return children == null ? Collections.<SimpleTreeNode<T>>emptyList() : Collections.unmodifiableList(children);
	}

	public void addChild(T child) {
		addChildNode(new SimpleTreeNode<T>(child));
	}

	public void addChildNode(SimpleTreeNode<T> childNode) {
		if (children == null) {
			children = new ArrayList<SimpleTreeNode<T>>();
		}
		children.add(childNode);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SimpleTreeNode)) {
			return false;
		}
		SimpleTreeNode<?> other = (SimpleTreeNode<?>) obj;
		return other.value == null ? this.value == null : other.value.equals(this.value);
	}

	@Override
	public int hashCode() {
		return value == null ? 0 : value.hashCode();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		toStringHelper(this, 0, builder);
		return builder.toString();
	}

	private void toStringHelper(SimpleTreeNode<T> node, int level, StringBuilder builder) {
		for (int i = 0; i < level; i++) {
			builder.append('\t');
		}
		builder.append(node.value).append('\n');
		for (SimpleTreeNode<T> childNode : node.getChildren()) {
			toStringHelper(childNode, level + 1, builder);
		}
	}
}