package edu.grinnell.csc207.blockchains;

import java.lang.reflect.Array;

/**
 * Encapsulated hashes.
 *
 * @author Lily Blanchard
 * @author AJ Trimble
 * @author Samuel A. Rebelsky
 */
public class Hash {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /** The contents of the hash. */
  byte[] contents;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new encapsulated hash.
   *
   * @param data The data to copy into the hash.
   */
  public Hash(byte[] data) {
    this.contents = data;
  } // Hash(byte[])

  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+

  /**
   * Determine how many bytes are in the hash.
   *
   * @return the number of bytes in the hash.
   */
  public int length() {
    return this.contents.length;
  } // length()

  /**
   * Get the ith byte.
   *
   * @param i The index of the byte to get, between 0 (inclusive) and length() (exclusive).
   *
   * @return the ith byte
   */
  public byte get(int i) {
    return this.contents[i];
  } // get()

  /**
   * Get a copy of the bytes in the hash. We make a copy so that the client cannot change them.
   *
   * @return a copy of the bytes in the hash.
   */
  public byte[] getBytes() {
    byte[] copy = new byte[this.length()];
    copy = this.contents;
    return copy;
  } // getBytes()

  /**
   * Convert to a hex string.
   *
   * @return the hash as a hex string.
   */
  public String toString() {
    String str = "";
    for (int i = 0; i < this.length(); i++) {
      str = str + Byte.toUnsignedInt(this.contents[i]);
    } // for
    return String.format("%02X", str);
  } // toString()

  /**
   * Determine if this is equal to another object.
   *
   * @param other The object to compare to.
   *
   * @return true if the two objects are conceptually equal and false otherwise.
   */
  public boolean equals(Object other) {
    if (other instanceof Hash) {
      Hash o = (Hash) other;
      return Array.equals(o.contents, this.contents);
    } else {
      return false;
    } // if
  } // equals(Object)

  /**
   * Get the hash code of this object.
   *
   * @return the hash code.
   */
  public int hashCode() {
    return this.toString().hashCode();
  } // hashCode()
} // class Hash
