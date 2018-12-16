package company;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import java.nio.file.*;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Prints <agent1_type>;<agent1_share_number>;<agent1_initial_price>;<agent2_type>;<agent2_share_number>;<agent2_initial_price>(...);<final_price>
 */
public class CompanyInformationPrinter {
  private static final String PATH = "./data/";
  static ConcurrentHashMap<String, FileOutputStream> files = new ConcurrentHashMap<String, FileOutputStream>();

  private CompanyInformationPrinter() {
  }

  public synchronized static void addCompany(String company_name) {
    if (files.get(company_name) == null) {
      File file = new File(PATH + company_name + ".csv");
      try {
        file.createNewFile();
        FileOutputStream stream = new FileOutputStream(file, true);
        files.put(company_name, stream);
      }
      catch (Exception e) {
        System.err.println(e);
        System.exit(1);
      }
    }
  }

  public synchronized static void writeToCompany(String company, int agent_type, int share_n, double share_price) {
    FileOutputStream file = files.get(company);
    String           line = "" + agent_type + ";" + share_n + ";" + String.format("%.2f", share_price) + ";";

    try {
      write(line.getBytes(), file);
    }
    catch (Exception e) {
      System.err.println(e);
    }
  }

  public synchronized static void writeFinalCompanyPrice(String company, double price) {
    FileOutputStream file = files.get(company);
    String           line = String.format("%.2f", price) + "\n";

    try {
      write(line.getBytes(), file);
    }
    catch (Exception e) {
      System.err.println(e);
    }
  }

  static synchronized void write(byte[] bytes, FileOutputStream file) {
    try {
      boolean written = false;
      do {
        try {
          // Lock it!
          FileLock lock = file.getChannel().lock();
          try {
            // Write the bytes.
            file.write(bytes);
            written = true;
          } finally {
            // Release the lock.
            lock.release();
          }
        } catch (Exception ofle) {
          try {
            // Wait a bit
            Thread.sleep(0);
          } catch (Exception ex) {
            throw new Exception("Interrupted waiting for a file lock.");
          }
        }
      } while (!written);
    } catch (Exception ex) {
      System.err.println("Failed to lock " + ex);
    }
  }
}
