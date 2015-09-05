/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;

/**
 *
 * @author maxim
 */
public class Api {

  int port;
  String host = null;
  HttpServer server;
  static Map<String, String> env;
  
  public Api(String[] args) throws IOException {
    port = 7070;
    host = null;
//    if(args[0] != null) {
//      port = Integer.valueOf(args[0]);
//    }
//    if(args[1] != null) {
//      host = args[1];
//    }
    server = HttpServer.create(new InetSocketAddress(port), 0);
  }

  public Api start() {
    server.setExecutor(null);
    server.start();
    System.out.printf("%s %s:%d\r\n", "Server started at", host, port);
    return this;
  }

  public Api mapRoutes() throws IOException {
    if(server == null) {
      throw new IOException("Server not initialized");
    }
    server.createContext("/", new Router());
    return this;
  }

  /**
   * @param args the command line arguments
   * @throws java.io.IOException
   */
  public static void main(String[] args) throws IOException {
    Api.env = System.getenv();
    Api app = new Api(args);
    app.mapRoutes().start();
  }
}
