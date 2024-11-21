package edu.grinnell.csc207.blockchains;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

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
  static int num;

  /** The transaction. */
  static Transaction transaction;

  /** The previous hash. */
  static Hash prevHash = null;

  /** The nonce. */
  static long nonce;

  /** The hash of this block. */
  static Hash thisHash;

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
    this.num = num;
    this.transaction = transaction;
    this.prevHash = prevHash;
    boolean foundNonce = false;
    while (!foundNonce) {
      Long rand = new Random().nextLong();
      this.nonce = rand;
      MessageDigest md;
      try {
        md = MessageDigest.getInstance("sha-256");
        byte[] numbytes = ByteBuffer.allocate(Integer.BYTES).putInt(num).array();
        md.update(numbytes);
        md.update(transaction.getSource().getBytes());
        md.update(transaction.getTarget().getBytes());
        byte[] amtbytes =
            ByteBuffer.allocate(Integer.BYTES).putInt(transaction.getAmount()).array();
        md.update(amtbytes);
        if (prevHash != null) {
          md.update(prevHash.getBytes());
        } // if prevHash exists
        byte[] noncebytes = ByteBuffer.allocate(Long.BYTES).putLong(nonce).array();
        md.update(noncebytes);

        this.thisHash = new Hash(md.digest());
      } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
      }

      if (check.isValid(this.thisHash)) {
        foundNonce = true;
      } // if
      md = null;
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
  public Block(int num, Transaction transaction, Hash prevHash,
      long nonce) /* throws NoSuchAlgorithmException */ {
    this.num = num;
    this.transaction = transaction;
    this.prevHash = prevHash;
    this.nonce = nonce;
    computeHash();

  } // Block(int, Transaction, Hash, long)

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Compute the hash of the block given all the other info already stored in the block.
   */
  static void computeHash() {
    MessageDigest md;
    try {
      md = MessageDigest.getInstance("sha-256");
      byte[] numbytes = ByteBuffer.allocate(Integer.BYTES).putInt(num).array();
      md.update(numbytes);
      md.update(transaction.getSource().getBytes());
      md.update(transaction.getTarget().getBytes());
      byte[] amtbytes = ByteBuffer.allocate(Integer.BYTES).putInt(transaction.getAmount()).array();
      md.update(amtbytes);
      if (prevHash != null) {
        md.update(prevHash.getBytes());
      } // if prevHash exists
      byte[] noncebytes = ByteBuffer.allocate(Long.BYTES).putLong(nonce).array();
      md.update(noncebytes);

      thisHash = new Hash(md.digest());
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
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
    return this.num;
  } // getNum()

  /**
   * Get the transaction stored in this block.
   *
   * @return the transaction.
   */
  public Transaction getTransaction() {
    return this.transaction;
  } // getTransaction()

  /**
   * Get the nonce of this block.
   *
   * @return the nonce.
   */
  public long getNonce() {
    return this.nonce;
  } // getNonce()

  /**
   * Get the hash of the previous block.
   *
   * @return the hash of the previous block.
   */
  Hash getPrevHash() {
    return this.prevHash;
  } // getPrevHash

  /**
   * Get the hash of the current block.
   *
   * @return the hash of the current block.
   */
  Hash getHash() {
    return this.thisHash;
  } // getHash

  /**
   * Get a string representation of the block.
   *
   * @return a string representation of the block.
   */
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append("Block ");
    str.append(this.num);
    str.append(" (Transaction: [Source: ");
    str.append(this.transaction.getSource());
    str.append(", Target: ");
    str.append(this.transaction.getTarget());
    str.append(", Amount: ");
    str.append(this.transaction.getAmount());
    str.append(", Nonce: ");
    str.append(this.nonce);
    str.append(", prevHash: ");
    str.append(this.prevHash);
    str.append(", hash: ");
    str.append(this.thisHash);
    return str.toString();
  } // toString()
} // class Block
