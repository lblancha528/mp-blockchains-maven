package edu.grinnell.csc207.blockchains;

/**
 * A node of linked lists. Contains a block.
 */
public class Node {
  /**
   * The block.
   */
  Block block;

  /**
   * The next pointer.
   */
  Node next;

  /**
   * Creates a new node with the provided block.
   *
   * @param block1
   */
  public Node(Block block1) {
    this.block = block1;
    this.next = null;
  } // Node(Block)

  /**
   * Returns the block of this node.
   *
   * @return this block
   */
  public Block getBlock() {
    return this.block;
  } // getBlock()

  /**
   * Returns the next node.
   *
   * @return the next node
   */
  public Node getNext() {
    return this.next;
  } // getNext()
} // class Node
