package edu.grinnell.csc207.main;

import edu.grinnell.csc207.blockchains.Block;
import edu.grinnell.csc207.blockchains.BlockChain;
import edu.grinnell.csc207.blockchains.HashValidator;
import edu.grinnell.csc207.blockchains.Transaction;

import edu.grinnell.csc207.util.IOUtils;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * A simple UI for our BlockChain class.
 *
 * @author Lily Blanchard
 * @author AJ Trimble
 * @author Samuel A. Rebelsky
 */
public class BlockChainUI {
  // +-----------+---------------------------------------------------
  // | Constants |
  // +-----------+

  /**
   * The number of bytes we validate. Should be set to 3 before submitting.
   */
  static final int VALIDATOR_BYTES = 3;

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Print out the instructions.
   *
   * @param pen The pen used for printing instructions.
   */
  public static void instructions(PrintWriter pen) {
    pen.println("""
        Valid commands:
          mine: discovers the nonce for a given transaction
          append: appends a new block onto the end of the chain
          remove: removes the last block from the end of the chain
          check: checks that the block chain is valid
          users: prints a list of users
          balance: finds a user's balance
          transactions: prints out the chain of transactions
          blocks: prints out the chain of blocks (for debugging only)
          help: prints this list of commands
          quit: quits the program""");
  } // instructions(PrintWriter)

  // +------+--------------------------------------------------------
  // | Main |
  // +------+

  /**
   * Run the UI.
   *
   * @param args Command-line arguments (currently ignored).
   */
  public static void main(String[] args) throws Exception {
    PrintWriter pen = new PrintWriter(System.out, true);
    BufferedReader eyes = new BufferedReader(new InputStreamReader(System.in));

    // Set up our blockchain.
    HashValidator validator = (h) -> {
      if (h.length() < VALIDATOR_BYTES) {
        return false;
      } // if
      for (int v = 0; v < VALIDATOR_BYTES; v++) {
        if (h.get(v) != 0) {
          return false;
        } // if
      } // for
      return true;
    };
    BlockChain chain = new BlockChain(validator);

    instructions(pen);

    boolean done = false;

    String source;
    String target;
    int amount;

    while (!done) {
      pen.print("\nCommand: ");
      pen.flush();
      String command = eyes.readLine();
      if (command == null) {
        command = "quit";
      } // if

      switch (command.toLowerCase()) {
        case "append":
          pen.printf("Source (return for deposit): ");
          pen.flush();
          source = eyes.readLine();
          pen.printf("Target: ");
          pen.flush();
          target = eyes.readLine();
          pen.printf("Amount: ");
          pen.flush();
          amount = Integer.valueOf(eyes.readLine());
          pen.printf("Nonce: ");
          pen.flush();
          long nonce = Long.valueOf(eyes.readLine());
          Transaction trans = new Transaction(source, target, amount);
          Block blk =
              new Block(chain.getSize() + 1, trans, chain.getBack().getBlock().getHash(), nonce);
          chain.append(blk);
          break;

        case "balance":
          pen.printf("User: ");
          String user = eyes.readLine();
          if (chain.getBalances().containsKey(user)) {
            pen.printf(user + "'s balance is " + chain.getBalances().get(user));
          } else {
            pen.printf(user + " does not exist");
          } // if
          break;

        case "blocks":
          Iterator<Block> blocks = chain.blocks();
          while (blocks.hasNext()) {
            Block curr = blocks.next();
            if (curr.getTransaction().getSource() == null) {
              pen.printf(
                  "Block %d (Transactions: [Deposit, Target: %s, Amount: %d], Nonce: %d, prevHash: %s, hash: %s)",
                  curr.getNum(), curr.getTransaction().getTarget(),
                  curr.getTransaction().getAmount(), curr.getNonce(), curr.getPrevHash().toString(),
                  curr.getHash().toString());
            } else {
              pen.printf(
                  "Block %d (Transactions: [Source %s, Target: %s, Amount: %d], Nonce: %d, prevHash: %s, hash: %s)",
                  curr.getNum(), curr.getTransaction().getSource(),
                  curr.getTransaction().getTarget(), curr.getTransaction().getAmount(),
                  curr.getNonce(), curr.getPrevHash().toString(), curr.getHash().toString());
            } // if no source
          } // while
          break;

        case "check":
          if (chain.isCorrect()) {
            pen.printf("The blockchain checks out.");
          } else {
            pen.printf("The blockchain does NOT check out.");
          } // if
          break;

        case "help":
          instructions(pen);
          break;

        case "mine":
          source = IOUtils.readLine(pen, eyes, "Source (return for deposit): ");
          target = IOUtils.readLine(pen, eyes, "Target: ");
          amount = IOUtils.readInt(pen, eyes, "Amount: ");
          Block b = chain.mine(new Transaction(source, target, amount));
          pen.println("\nUse nonce: " + b.getNonce());
          break;

        case "quit":
          done = true;
          break;

        case "remove":
          if (chain.removeLast()) {
            pen.printf("Removed last element.");
          } else {
            pen.printf("Failed to remove last element.");
          } // if
          break;

        case "transactions":
          Iterator<Transaction> transactions = chain.iterator();
          while (transactions.hasNext()) {
            Transaction curr = transactions.next();
            if (curr.getSource() == null) {
              pen.printf("[Deposit, Target: %s, Amount: %d]", curr.getTarget(), curr.getAmount());
            } else {
              pen.printf("[Source: %s, Target: %s, Amount: %d]", curr.getSource(), curr.getTarget(),
                  curr.getAmount());
            } // if
          } // while
          break;

        case "users":
          Iterator<String> users = chain.users();
          while (users.hasNext()) {
            pen.printf(users.next());
          } // while
          break;

        default:
          pen.printf("Invalid command: '%s'. Try again.\n", command);
          break;
      } // switch
    } // while

    pen.printf("\nGoodbye\n");
    eyes.close();
    pen.close();
  } // main(String[])
} // class BlockChainUI

// add check to expand people[]
