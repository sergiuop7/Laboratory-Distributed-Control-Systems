/*
 * ServerSimplu.java
 */

/**
 * Class created by @author Mihai HULEA at Feb 23, 2005.
 * 
 * This class is part of the laborator2_serverclientmonofir project.
 * 
 * 1. Modificati aplicatia sever astfel incat dupa tratarea unui client acesta sa revina 
 * in astepatare pentru a procesa noi cereri. 
 * 
 * 2. Modificati aplicatia server astefl incat aceasta sa accepte conexiuni sosite 
 * numai de pe anumite IP-uri.
 */
package lab.scd.net.socket;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;



public class ServerSimplu {
  public static void main(String[] args) throws IOException {
    ServerSocket ss = null;
    Socket s = null;

    try {
      ss = new ServerSocket(1900);
      System.out.println("Server is waiting for connections...");

      while (true) {
        s = ss.accept();
        System.out.println("Client connected: " + s.getInetAddress() + ":" + s.getPort());

        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);

        // Read the numbers (x and y) from the client
        String numbers = in.readLine();
        String[] parts = numbers.split(" ");
        int x = Integer.parseInt(parts[0]);
        int y = Integer.parseInt(parts[1]);

        // Calculate the percentage
        double P = (double) y / (x * 100);

        // Send the result (P) back to the client
        out.println(P);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      ss.close();
      if (s != null)
        s.close();
    }
  }
}
