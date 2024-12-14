package edu.grinnell.csc207.blockchains;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Blocks to be stored in blockchains.
 *
 * @author Lily Blanchard
 * @author AJ Trimble
 * @author Samuel A. Rebelsky
 */
public class Block {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /** The block number. */
  private int num;

  /** The transaction. */
  public Transaction transaction;

  /** The previous hash. */
  private Hash prevHash = null;

  /** The nonce. */
  public long nonce;

  /** The hash of this block. */
  private Hash thisHash;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new block from the specified block number, transaction, and previous hash, mining to
   * choose a nonce that meets the requirements of the validator.
   *
   * @param num The number of the block.
   * @param transaction The transaction for the block.
   * @param prevHash The hash of the previous block.
   * @param check The validator used to check the block.
   */
  public Block(int num, Transaction transaction, Hash prevHash, HashValidator check) {
    this(num, transaction, prevHash, (long) 0);
    thisHash = computeHash();
    while (!check.isValid(thisHash)) {
      nonce++;
      thisHash = computeHash();
    } // while
  } // Block(int, Transaction, Hash, HashValidator)

  /**
   * Create a new block, computing the hash for the block.
   *
   * @param num The number of the block.
   * @param transaction The transaction for the block.
   * @param prevHash The hash of the previous block.
   * @param nonce The nonce of the block.
   */
  public Block(int num1, Transaction transaction1, Hash prevHash1,
      long nonce1) /* throws NoSuchAlgorithmException */ {
    num = num1;
    transaction = transaction1;
    prevHash = prevHash1;
    nonce = nonce1;
    thisHash = computeHash();

  } // Block(int, Transaction, Hash, long)

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Compute the hash of the block given all the other info already stored in the block.
   * 
   * @return the hash
   */
  public Hash computeHash() {
    MessageDigest md;
    try {
      md = MessageDigest.getInstance("sha-256");
      byte[] numbytes = ByteBuffer.allocate(Integer.BYTES).putInt(num).array();
      md.update(numbytes);
      if (getTransaction().getSource() != null) {
        md.update(getTransaction().getSource().getBytes());
      } // if source exists
      if (getTransaction().getTarget() != null) {
        md.update(getTransaction().getTarget().getBytes());
      } // if target exists
      byte[] amtbytes = ByteBuffer.allocate(Integer.BYTES).putInt(getTransaction().getAmount()).array();
      md.update(amtbytes);
      if (getPrevHash() != null) {
        md.update(getPrevHash().getBytes());
      } // if prevHash exists
      byte[] noncebytes = ByteBuffer.allocate(Long.BYTES).putLong(getNonce()).array();
      md.update(noncebytes);

      return new Hash(md.digest());
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return null;
  } // computeHash()

  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+

  /**
   * Get the number of the block.
   *
   * @return the number of the block.
   */
  public int getNum() {
    return num;
  } // getNum()

  /**
   * Get the transaction stored in this block.
   *
   * @return the transaction.
   */
  public Transaction getTransaction() {
    return transaction;
  } // getTransaction()

  /**
   * Get the nonce of this block.
   *
   * @return the nonce.
   */
  public long getNonce() {
    return nonce;
  } // getNonce()

  /**
   * Get the hash of the previous block.
   *
   * @return the hash of the previous block.
   */
  public Hash getPrevHash() {
    return prevHash;
  } // getPrevHash

  /**
   * Get the hash of the current block.
   *
   * @return the hash of the current block.
   */
  public Hash getHash() {
    return thisHash;
  } // getHash

  /**
   * Get a string representation of the block.
   *
   * @return a string representation of the block.
   */
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append("Block ");
    str.append(num);
    str.append(" (Transaction: [Source: ");
    str.append(transaction.getSource());
    str.append(", Target: ");
    str.append(transaction.getTarget());
    str.append(", Amount: ");
    str.append(transaction.getAmount());
    str.append(", Nonce: ");
    str.append(nonce);
    str.append(", prevHash: ");
    str.append(prevHash);
    str.append(", hash: ");
    str.append(thisHash);
    return str.toString();
  } // toString()
} // class Block
