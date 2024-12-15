package edu.grinnell.csc207.blockchains;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;


/**
 * Some simple tests of our BlockChain class.
 *
 * @author Samuel A. Rebelsky
 */
public class TestBlockChain {
  // +-----------+---------------------------------------------------
  // | Utilities |
  // +-----------+

  /**
   * Get all the users as a sorted array.
   */
  static String[] users(BlockChain chain) {
    ArrayList<String> users = new ArrayList<String>();
    Iterator<String> uit = chain.users();
    while (uit.hasNext()) {
      users.add(uit.next());
    } // while
    String[] result = users.toArray(new String[] {});
    Arrays.sort(result);
    return result;
  } // users(BlockChain)

  /**
   * Assert that append fails (throws an Exception).
   */
  static void assertAppendFails(BlockChain chain, Block block, String msg) {
    try {
      chain.append(block);
      fail(msg);
    } catch (Exception e) {
      // Do nothing; we expect this.
    } // try/catch
  } // assertAppendFails(BlockChain, Block, String)

  /**
   * Assert that check fails (throws an Exception).
   */
  static void assertCheckFails(BlockChain chain, String msg) {
    try {
      chain.check();
      fail(msg);
    } catch (Exception e) {
      // Do nothing; we expect this.
    } // try/catch
  } // assertCheckFails(BlockChain, String)

  // +-------+-------------------------------------------------------
  // | Tests |
  // +-------+

  /**
   * If we create a new blockchain with a custom validator, is the hash of the first block valid?
   */
  @Test
  public void testValidateInitial() {
    HashValidator v = (hash) -> (hash.length() >= 2) && (hash.get(0) == 1) && (hash.get(1) == 2);
    BlockChain chain = new BlockChain(v);
    assertTrue(v.isValid(chain.getHash()), "initial block in newly-created blockchain is valid");
  } // testValidateInitial

  /**
   * If we try to remove the last element from a block chain with only one element, `remove` will
   * return false.
   */
  @Test
  public void testRemoveInitial() {
    BlockChain chain = new BlockChain((hash) -> true);
    assertEquals(1, chain.getSize(), "R: Chain starts with one block");
    assertFalse(chain.removeLast(), "R: Removing element from singleton");
    assertEquals(1, chain.getSize(), "R: Chain still has one block");
  } // testRemoveInitial()

  /**
   * Make sure that we can mine a block.
   */
  @Test
  public void testMine() {
    HashValidator v = (hash) -> (hash.length() >= 2) && (hash.get(0) == 2) && (hash.get(1) == 2);
    BlockChain chain = new BlockChain(v);
    Block block = chain.mine(new Transaction("Here", "There", 123));
    assertTrue(v.isValid(block.getHash()), "M: Check hash of newly mined block");
    assertEquals("Here", block.getTransaction().getSource(),
        "M: Check source of newly mined block");
    assertEquals("There", block.getTransaction().getTarget(),
        "M: Check target of newly mined block");
    assertEquals(123, block.getTransaction().getAmount(), "M: Check amount of newly mined block");
    assertEquals(chain.getHash(), block.getPrevHash(), "M: Check prevHash of newly mined block");
  } // testMine()

  /**
   * Make sure that we can append a few blocks.
   */
  @Test
  public void testAppend() {
    HashValidator v = (hash) -> (hash.length() >= 2) && (hash.get(0) == 2) && (hash.get(1) == 1);
    BlockChain chain = new BlockChain(v);
    Block block1 = chain.mine(new Transaction("", "A", 4321));
    chain.append(block1);
    assertEquals(2, chain.getSize(), "M: after appending a block");
    Block block2 = chain.mine(new Transaction("A", "B", 21));
    chain.append(block2);
    assertEquals(3, chain.getSize(), "M: after appending two blocks");
    Block block3 = chain.mine(new Transaction("A", "B", 43));
    chain.append(block3);
    assertEquals(4, chain.getSize(), "M: after appending three blocks");
  } // testAppend()

  /**
   * Make sure that we can iterate the blocks after appending them.
   */
  @Test
  public void testIterateBlocks() {
    HashValidator v = (hash) -> (hash.length() >= 2) && (hash.get(0) == 1) && (hash.get(1) == 3);
    BlockChain chain = new BlockChain(v);
    Block block1 = chain.mine(new Transaction("", "A", 4321));
    chain.append(block1);
    Block block2 = chain.mine(new Transaction("A", "B", 21));
    chain.append(block2);
    Block block3 = chain.mine(new Transaction("A", "B", 43));
    chain.append(block3);

    Iterator<Block> blocks = chain.blocks();
    assertTrue(blocks.hasNext(), "M: hasNext at beginning");
    blocks.next();
    assertTrue(blocks.hasNext(), "M: hasNext before block 1");
    assertEquals(block1, blocks.next(), "M: block 1");
    assertTrue(blocks.hasNext(), "M: hasNext before block 2");
    assertEquals(block2, blocks.next(), "M: block 2");
    assertTrue(blocks.hasNext(), "M: hasNext before block 3");
    assertEquals(block3, blocks.next(), "M: block 3");
    assertFalse(blocks.hasNext(), "M: hasNext at end");
  } // testIterateBlocks()

  /**
   * Make sure that we can iterate the blocks after appending them.
   */
  @Test
  public void testRemoveBlocks() {
    HashValidator v = (hash) -> (hash.length() >= 2) && (hash.get(0) == 2) && (hash.get(1) == 3);
    BlockChain chain = new BlockChain(v);
    Block block1 = chain.mine(new Transaction("", "A", 4321));
    chain.append(block1);
    Block block2 = chain.mine(new Transaction("A", "B", 21));
    chain.append(block2);
    Block block3 = chain.mine(new Transaction("A", "B", 43));
    chain.append(block3);

    assertEquals(4, chain.getSize(), "M: size after adding 3 blocks");
    assertTrue(chain.removeLast(), "E: removing from size-4 chain");
    assertEquals(3, chain.getSize(), "E: size after removing 1 block from 4chain");
    assertTrue(chain.removeLast(), "E: removing from size-3 chain");
    assertEquals(2, chain.getSize(), "E: size after removing 2 blocks from 4");
    assertTrue(chain.removeLast(), "E: removing from size-2 chain");
    assertEquals(1, chain.getSize(), "E: size after removing 3 blocks from 4");
    assertFalse(chain.removeLast(), "E: removing from size-1 chain");
  } // testRemoveBlocks()

  /**
   * Make sure that we can add and remove blocks.
   */
  @Test
  public void testRemoveAndAppendBlocks() {
    HashValidator v = (hash) -> (hash.length() >= 2) && (hash.get(0) == 3) && (hash.get(1) == 3);
    BlockChain chain = new BlockChain(v);
    Block block1 = chain.mine(new Transaction("", "A", 4321));
    chain.append(block1);
    Block block2 = chain.mine(new Transaction("A", "B", 21));
    chain.append(block2);
    Block block3 = chain.mine(new Transaction("A", "B", 43));
    chain.append(block3);

    assertTrue(chain.removeLast(), "E: removing from size-4 chain");
    assertTrue(chain.removeLast(), "E: removing from size-3 chain");
    block2 = chain.mine(new Transaction("A", "B", 21));
    chain.append(block2);
    assertTrue(chain.removeLast(), "M: removing from size-3 chain");
    block2 = chain.mine(new Transaction("A", "B", 21));
    chain.append(block2);
    block3 = chain.mine(new Transaction("A", "B", 43));
    chain.append(block3);
    assertTrue(chain.removeLast(), "M: removing from size-4 chain");
    block3 = chain.mine(new Transaction("A", "B", 43));
    chain.append(block3);

    Iterator<Block> blocks = chain.blocks();
    assertTrue(blocks.hasNext(), "M: hasNext at beginning");
    blocks.next();
    assertTrue(blocks.hasNext(), "M: hasNext before block 1");
    assertEquals(block1, blocks.next(), "block 1");
    assertTrue(blocks.hasNext(), "M: hasNext before block 2");
    assertEquals(block2, blocks.next(), "block 2");
    assertTrue(blocks.hasNext(), "M: hasNext before block 3");
    assertEquals(block3, blocks.next(), "block 3");
    assertFalse(blocks.hasNext(), "M: hasNext at end");
  } // testRemoveAndAppendBlocks()

  /**
   * Check balances.
   */
  @Test
  public void testBalances() {
    HashValidator v = (hash) -> (hash.length() >= 2) && (hash.get(0) == 64) && (hash.get(1) == 64);
    BlockChain chain = new BlockChain(v);
    assertEquals(0, chain.balance("A"), "M: A's initial balance");
    assertEquals(0, chain.balance("B"), "M: B's initial balance");
    assertEquals(0, chain.balance("C"), "M: C's initial balance");

    chain.append(chain.mine(new Transaction("", "A", 100)));
    assertEquals(100, chain.balance("A"), "M: A's second balance");
    assertEquals(0, chain.balance("B"), "M: B's second balance");
    assertEquals(0, chain.balance("C"), "M: C's second balance");

    chain.removeLast();
    assertEquals(0, chain.balance("A"), "E: A's third balance");
    assertEquals(0, chain.balance("B"), "E: B's third balance");
    assertEquals(0, chain.balance("C"), "E: C's third balance");

    chain.append(chain.mine(new Transaction("", "A", 50)));

    assertEquals(50, chain.balance("A"), "E: A's fourth balance");
    assertEquals(0, chain.balance("B"), "E: B's fourth balance");
    assertEquals(0, chain.balance("C"), "E: C's fourth balance");

    chain.append(chain.mine(new Transaction("", "A", 50)));

    assertEquals(100, chain.balance("A"), "E: A's fifth balance");
    assertEquals(0, chain.balance("B"), "E: B's fifth balance");
    assertEquals(0, chain.balance("C"), "E: C's fifth balance");

    chain.append(chain.mine(new Transaction("A", "B", 20)));

    assertEquals(80, chain.balance("A"), "E: A's sixth balance");
    assertEquals(20, chain.balance("B"), "E: B's sixth balance");
    assertEquals(0, chain.balance("C"), "E: C's sixth balance");

    chain.append(chain.mine(new Transaction("", "C", 50)));

    assertEquals(80, chain.balance("A"), "E: A's seventh balance");
    assertEquals(20, chain.balance("B"), "E: B's seventh balance");
    assertEquals(50, chain.balance("C"), "E: C's seventh balance");

    chain.append(chain.mine(new Transaction("C", "B", 30)));

    assertEquals(80, chain.balance("A"), "E: A's eighth balance");
    assertEquals(50, chain.balance("B"), "E: B's eighth balance");
    assertEquals(20, chain.balance("C"), "E: C's eighth balance");
  } // testBalances()

  /**
   * Test the list of users.
   */
  @Test
  public void testUsers() {
    HashValidator v = (hash) -> (hash.length() >= 2) && (hash.get(0) == 64) && (hash.get(1) == 64);
    BlockChain chain = new BlockChain(v);

    assertArrayEquals(new String[] {}, users(chain), "No users");

    chain.append(chain.mine(new Transaction("", "B", 100)));

    assertArrayEquals(new String[] {"B"}, users(chain), "E: B");

    chain.append(chain.mine(new Transaction("B", "D", 10)));

    assertArrayEquals(new String[] {"B", "D"}, users(chain), "E: B and D");

    chain.append(chain.mine(new Transaction("B", "C", 10)));

    assertArrayEquals(new String[] {"B", "C", "D"}, users(chain), "E: B-D");

    chain.append(chain.mine(new Transaction("B", "C", 10)));

    assertArrayEquals(new String[] {"B", "C", "D"}, users(chain),
        "E: A-D, with extra C (not a different user)");

    chain.append(chain.mine(new Transaction("C", "D", 10)));

    assertArrayEquals(new String[] {"B", "C", "D"}, users(chain), "E: B-D, with extra C and D");

    chain.append(chain.mine(new Transaction("D", "E", 10)));

    assertArrayEquals(new String[] {"B", "C", "D", "E"}, users(chain),
        "E: B-E, with extra C and D");

    chain.append(chain.mine(new Transaction("B", "A", 10)));

    assertArrayEquals(new String[] {"A", "B", "C", "D", "E"}, users(chain),
        "E: A-E, with extra C and D");
  } // testUsers()

  /**
   * Test a long valid sequence.
   */
  @Test
  public void testValidityValid() throws Exception {
    HashValidator v = (hash) -> (hash.length() >= 2) && (hash.get(0) == 0) && (hash.get(1) == 32);
    BlockChain chain = new BlockChain(v);

    assertTrue(chain.isCorrect());
    chain.check();

    chain.append(chain.mine(new Transaction("", "A", 100)));
    assertTrue(chain.isCorrect(), "M: Mine first");
    chain.check();

    chain.append(chain.mine(new Transaction("", "B", 100)));
    assertTrue(chain.isCorrect(), "M: Mine second");
    chain.check();

    chain.append(chain.mine(new Transaction("A", "B", 10)));
    assertTrue(chain.isCorrect(), "M: Mine third");
    chain.check();

    chain.append(chain.mine(new Transaction("B", "C", 10)));
    assertTrue(chain.isCorrect(), "M: Mine fourth");
    chain.check();

    chain.append(chain.mine(new Transaction("C", "D", 10)));
    assertTrue(chain.isCorrect(), "M: Mine fifth");
    chain.check();
  } // testValidityValid()

  /**
   * Test append of bad blocks.
   */
  @Test
  public void testAppendBad() throws Exception {
    HashValidator v = (hash) -> (hash.length() >= 2) && (hash.get(0) == 7) && (hash.get(1) == 11);
    BlockChain chain = new BlockChain(v);

    assertTrue(chain.isCorrect());
    chain.check();

    Block block = chain.mine(new Transaction("", "A", 100));
    block.transaction = new Transaction("", "A", 1000);
    assertAppendFails(chain, block, "E: Appending block with modified amount");

    block = chain.mine(new Transaction("", "A", 100));
    block.transaction = new Transaction("", "B", 100);
    assertAppendFails(chain, block, "E: Appending block with modified recipient");

    block = chain.mine(new Transaction("", "A", 100));
    ++block.nonce;
    assertAppendFails(chain, block, "E: Appending block with modified nonce");
  } // testAppendBad()

  /**
   * Test financially senseless transactions.
   */
  @Test
  public void testInvalidTransactions() throws Exception {
    HashValidator v = (hash) -> (hash.length() >= 2) && (hash.get(0) == 10) && (hash.get(1) == 10);
    BlockChain chain = new BlockChain(v);

    // Set up a valid chain of transactions
    chain.append(chain.mine(new Transaction("", "A", 100)));
    chain.append(chain.mine(new Transaction("", "B", 100)));
    chain.append(chain.mine(new Transaction("B", "A", 50)));

    // Add an invalid transaction
    chain.append(chain.mine(new Transaction("B", "A", 100)));
    assertFalse(chain.isCorrect(), "E: B transferred too much");
    assertCheckFails(chain, "E: B transferred too much");

    // Remove that transaction.
    assertTrue(chain.removeLast(), "E: removing invalid transaction");
    assertTrue(chain.isCorrect(), "E: after removing invalid transaction");
    chain.check();

    // Add a valid transaction.
    chain.append(chain.mine(new Transaction("A", "C", 10)));
    assertTrue(chain.isCorrect(), "E: added valid transaction");
    chain.check();

    // Add an invalid transaction and then a valid transaction.
    chain.append(chain.mine(new Transaction("B", "A", 200)));
    chain.append(chain.mine(new Transaction("A", "C", 10)));
    assertFalse(chain.isCorrect(), "E: B transferred too much");
    assertCheckFails(chain, "E: B transferred too much");

    // Remove the valid transaction.
    assertTrue(chain.removeLast(), "E: removing valid transaction");
    assertFalse(chain.isCorrect(), "E: B transferred too much");
    assertCheckFails(chain, "E: B transferred too much");

    // Remove the invalid transaction
    assertTrue(chain.removeLast(), "E: removing invalid transaction");
    assertTrue(chain.isCorrect(), "E: after removing invalid transaction");
    chain.check();

    // Add a different invalid transaction
    chain.append(chain.mine(new Transaction("D", "A", 10)));
    assertFalse(chain.isCorrect(), "E: D has no money");
    assertCheckFails(chain, "E: D has no money");
    assertTrue(chain.removeLast(), "E: removing invalid transaction");
    assertTrue(chain.isCorrect(), "E: after removing invalid transaction");
    chain.check();

    // And another
    chain.append(chain.mine(new Transaction("A", "B", -10)));
    assertFalse(chain.isCorrect(), "E: Negative transfer");
    assertCheckFails(chain, "E: Negative transfer");
    assertTrue(chain.removeLast(), "E: removing invalid transaction");
    assertTrue(chain.isCorrect(), "E: after removing invalid transaction");
    chain.check();
  } // testInvalidTransactions()

  /**
   * Test modifying a valid chain.
   */
  @Test
  public void testModifiedChain() throws Exception {
    HashValidator v = (hash) -> (hash.length() >= 2) && (hash.get(0) == 11) && (hash.get(1) == 11);
    BlockChain chain = new BlockChain(v);

    chain.append(chain.mine(new Transaction("", "F", 100)));
    chain.append(chain.mine(new Transaction("", "G", 100)));
    chain.append(chain.mine(new Transaction("G", "F", 10)));
    assertEquals(110, chain.balance("F"), "F's balance in correct chain");
    assertTrue(chain.isCorrect(), "initial chain is correct");
    chain.check();

    Iterator<Block> blocks = chain.blocks();
    blocks.next();
    blocks.next().transaction = new Transaction("", "F", 1000);
    assertEquals(1010, chain.balance("F"), "E: F's balance in modified chain");
    assertFalse(chain.isCorrect(), "E: modified chain is incorrect");
    assertCheckFails(chain, "E: modified chain is incorrect");
  } // testModifiedChain()

} // class TestBlockChain
