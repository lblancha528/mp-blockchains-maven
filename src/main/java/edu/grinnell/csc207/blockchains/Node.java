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

  public Node(Block block) {
    this.block = block;
    this.next = null;
  } // Node(Block)

  public Block getBlock() {
    return this.block;
  } // getBlock()

  public Node getNext() {
    return this.next;
  } // getNext()
} // class Node
