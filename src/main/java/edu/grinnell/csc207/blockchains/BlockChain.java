package edu.grinnell.csc207.blockchains;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A full blockchain.
 *
 * @author Lily Blanchard
 * @author AJ Trimble
 */
public class BlockChain implements Iterable<Transaction> {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The front of the list;
   */
  Node front;

  /**
   * The number of blocks in the list.
   */
  int size;

  /**
   * The back of the list.
   */
  Node back;

  /**
   * Validator.
   */
  HashValidator check;

  /** To store existing people and their balances. */
  HashMap<String, Integer> balances = new HashMap<String, Integer>(10);

  /** A dummy list for use in isCorrect() and check(). */
  HashMap<String, Integer> dummyList = new HashMap<String, Integer>(10);

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new blockchain using a validator to check elements.
   *
   * @param check The validator used to check elements.
   */
  public BlockChain(HashValidator check) {
    this.front = new Node(new Block(0, new Transaction(null, null, 0), null, check));
    this.size = 1;
    this.back = this.front;
    this.check = check;
  } // BlockChain(HashValidator)

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Check if a transaction is valid. A transaction is valid if the source exists and has a balance
   * equal to or greater than the transaction amount. Since this is just a helper for check() and
   * isCorrect(), it also updates the dummyList if the transaction is valid.
   * 
   * @param p1 sender
   * @param p2 reciever
   * @param amt the amount to send
   * @return whether the transaction is valid
   */
  public boolean checkTransaction(String p1, String p2, int amt) {
    if (dummyList.containsKey(p1) && dummyList.get(p1) >= amt) {
      dummyList.replace(p1, dummyList.get(p1) - amt);
      if (dummyList.containsKey(p2)) {
        dummyList.replace(p2, dummyList.get(p2) + amt);
      } else {
        dummyList.put(p2, amt);
      } // if target exists
      return true;
    } else {
      return false;
    } // if
  } // checkTransaction(String, String, int)

  public Node getFront() {
    return this.front;
  } // getFront()

  public Node getBack() {
    return this.back;
  } // getBack()

  public HashMap<String, Integer> getBalances() {
    return balances;
  }
  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+

  /**
   * Mine for a new valid block for the end of the chain, returning that block.
   *
   * @param t The transaction that goes in the block.
   *
   * @return a new block with correct number, hashes, and such.
   */
  public Block mine(Transaction t) {
    return new Block(size + 1, t, this.back.block.getHash(), check);
  } // mine(Transaction)

  /**
   * Get the number of blocks curently in the chain.
   *
   * @return the number of blocks in the chain, including the initial block.
   */
  public int getSize() {
    return this.size;
  } // getSize()

  /**
   * Add a block to the end of the chain.
   *
   * @param blk The block to add to the end of the chain.
   *
   * @throws IllegalArgumentException if (a) the hash is not valid, (b) the hash is not appropriate
   *         for the contents, or (c) the previous hash is incorrect.
   */
  public void append(Block blk) throws IllegalArgumentException {
    if (!check.isValid(blk.getHash())) {
      System.out.println(blk.getHash());
      throw new IllegalArgumentException("Fails validator: `" + blk.getNonce() + "`");
    } // if

    if (!blk.getHash().equals(blk.computeHash())) {
      throw new IllegalArgumentException("Hashes inequal.");
    } // if

    if (!blk.getPrevHash().equals(back.block.getHash())) {
      System.out.println("" + back.block.getHash() + " " + blk.getPrevHash());
      throw new IllegalArgumentException("Prev hash wrong.");
    } // if

    this.back.next = new Node(blk);
    this.back = this.back.next;
    this.size++;
    if (balances.containsKey(blk.getTransaction().getSource())) {
      balances.replace(blk.getTransaction().getSource(),
          balances.get(blk.getTransaction().getSource()) - blk.getTransaction().getAmount());
    } // if source exists
    if (balances.containsKey(blk.getTransaction().getTarget())) {
      balances.replace(blk.getTransaction().getTarget(),
          balances.get(blk.getTransaction().getTarget()) + blk.getTransaction().getAmount());
    } else {
      balances.put(blk.getTransaction().getTarget(), blk.getTransaction().getAmount());
    } // if target exists
  } // append()

  /**
   * Attempt to remove the last block from the chain.
   *
   * @return false if the chain has only one block (in which case it's not removed) or true
   *         otherwise (in which case the last block is removed).
   */
  public boolean removeLast() {
    if (size > 1) {
      Node here = this.front;
      while (here.next != this.back) {
        here = here.next;
      } // while

      // update balances
      if (balances.containsKey(here.next.getBlock().getTransaction().getSource())) {
        balances.replace(here.next.getBlock().getTransaction().getSource(),
            balances.get(here.next.getBlock().getTransaction().getSource())
                + here.next.getBlock().getTransaction().getAmount());
      } // if source exists
      if (balances.containsKey(here.next.getBlock().getTransaction().getTarget())) {
        balances.replace(here.next.getBlock().getTransaction().getTarget(),
            balances.get(here.next.getBlock().getTransaction().getTarget())
                - here.next.getBlock().getTransaction().getAmount());
      } else {
        balances.put(here.next.getBlock().getTransaction().getTarget(),
            here.next.getBlock().getTransaction().getAmount());
      } // if target exists

      here.next = null;
      this.back = here;
      size--;
      return true;
    } else {
      return false;
    } // if
  } // removeLast()

  /**
   * Get the hash of the last block in the chain.
   *
   * @return the hash of the last sblock in the chain.
   */
  public Hash getHash() {
    return this.back.block.getHash();
  } // getHash()

  /**
   * Determine if the blockchain is correct in that (a) the balances are legal/correct at every
   * step, (b) that every block has a correct previous hash field, (c) that every block has a hash
   * that is correct for its contents, and (d) that every block has a valid hash.
   *
   * @return true if the blockchain is correct and false otherwise.
   */
  public boolean isCorrect() {
    dummyList.clear();
    Node here = this.front;
    Hash prevHolder = this.front.getBlock().getPrevHash();
    while (here != null) {
      if (check.isValid(here.block.getHash())
          && here.block.computeHash().equals(here.block.getHash())
          && here.block.getPrevHash().equals(prevHolder)
          && checkTransaction(here.block.getTransaction().getSource(),
              here.block.getTransaction().getTarget(), here.block.getTransaction().getAmount())) {
        prevHolder = here.getBlock().getHash();
        here = here.next;
      } else {
        return false;
      } // if
    } // while
    return true;
  } // isCorrect()

  /**
   * Determine if the blockchain is correct in that (a) the balances are legal/correct at every
   * step, (b) that every block has a correct previous hash field, (c) that every block has a hash
   * that is correct for its contents, and (d) that every block has a valid hash.
   *
   * @throws Exception If things are wrong at any block.
   */
  public void check() throws Exception {
    dummyList.clear();
    Node here = this.front;
    while (here.next != null) {
      if (check.isValid(here.block.getHash())
          && here.block.computeHash().equals(here.block.getHash())
          && here.block.getPrevHash().equals(back.block.getHash())
          && checkTransaction(here.block.getTransaction().getSource(),
              here.block.getTransaction().getTarget(), here.block.getTransaction().getAmount())) {
      } else {
        throw new Exception("Block " + here.block.getNum() + " is invalid.\n");
      } // if
    } // while
  } // check()

  /**
   * Return an iterator of all the people who participated in the system.
   *
   * @return an iterator of all the people in the system.
   */
  public Iterator<String> users() {
    HashSet<String> users = new HashSet<>();

    for (Transaction trans : this) {
      if (!trans.getTarget().isEmpty()) {
        users.add(trans.getTarget());
      } // if
    } // for

    return users.iterator();
  } // users()

  /**
   * Find one user's balance.
   *
   * @param user The user whose balance we want to find.
   *
   * @return that user's balance (or 0, if the user is not in the system).
   */
  public int balance(String user) {
    if (balances.containsKey(user)) {
      return balances.get(user);
    } else {
      return 0;
    } // if
  } // balance()

  /**
   * Get an interator for all the blocks in the chain.
   *
   * @return an iterator for all the blocks in the chain.
   */
  public Iterator<Block> blocks() {
    return new Iterator<Block>() {
      private Node here = BlockChain.this.front;

      public boolean hasNext() {
        return this.here != null;
      } // hasNext()

      public Block next() {
        Block hereBlock = this.here.block;
        this.here = this.here.next;
        return hereBlock;
      } // next()
    };
  } // blocks()

  /**
   * Get an interator for all the transactions in the chain.
   *
   * @return an iterator for all the blocks in the chain.
   */
  public Iterator<Transaction> iterator() {
    return new Iterator<Transaction>() {
      private Node here = BlockChain.this.front;

      public boolean hasNext() {
        return this.here != null;
      } // hasNext()

      public Transaction next() {
        Transaction hereTrans = this.here.block.getTransaction();
        this.here = this.here.next;
        return hereTrans;
      } // next()
    };
  } // iterator()

} // class BlockChain
