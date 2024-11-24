package edu.grinnell.csc207.blockchains;

/**
 * Things that validate hashes.
 *
 * @author Samuel A. Rebelsky
 */
public interface HashValidator {
  /**
   * Determine if a hash meets some criterion.
   *
   * @param hash The hash we're checking.
   *
   * @return true if the hash is valid and false otehrwise.
   */
  boolean isValid(Hash hash);

} // interface HashValidator
