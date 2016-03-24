/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Router class Make routes through application like controller/action etc.
 *
 * @author maxim
 */
public class Router implements HttpHandler {

  final PostgresDatabase database;

  Router() {
    String dburl = Api.env.get("DATABASE_URL");
    Pattern dbreg = Pattern.compile("//(.+):(.+)@");
    Matcher m = dbreg.matcher(dburl);
    m.find();
    String found = m.group();
    String[] creds = found.split(":");
    dburl = dburl.replace(found, "").replace("postgres:", "postgresql://");
    database = PostgresDatabase.getInstance("jdbc:" + dburl, creds[0].replaceAll("^//", ""), creds[1].replaceAll("@$", ""));
  }

  /**
   * Handling user's requests
   *
   * @param he
   * @throws IOException
   */
  @Override
  public void handle(final HttpExchange he) throws IOException {
    System.out.println(he.getRequestURI().getPath());
    final Request req = new Request(he);
    final Response res = new Response(he, req);
    String schema = "public";
    String func = null;
    String url = req.getURI().getPath().substring(1);
    String[] path = url.split("/");
    if ("/favicon.ico".equals(url) || path.length < 2) {
      res.setStatus(404);
      res.setBody("Not Found");
      res.end();
      return;
    }
    schema = path[0];
    func = path[1];
    try {
      JSONObject params = null;
      //TODO: allow only approved
      res.setHeader("Access-Control-Allow-Origin", req.getHeader("origin"));
      res.setHeader("Access-Control-Allow-Credentials", "true");
      if(null != req.getMethod()) switch (req.getMethod()) {
        case "OPTIONS":
        case "HEAD":
          res.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, PATCH, HEAD");
          res.setHeader("Access-Control-Allow-Headers", "origin, content-type, accept, x-csrf-token, x-sessionId, x-ajax, x-projectkey");
          res.end();
          return;
        case "GET":
          params = new JSONObject(req.getQueryParameters());
          break;
        default:
          params = new JSONObject(req.getBodyParameters());
          break;
      }
      JSONArray json = database.exec(schema + "." + req.getMethod().toLowerCase() + "_" + func, params);
      res.setBody(json.toString());
      res.end();
    } catch (SQLException ex) {
      //TODO: dev stack trace print
      res.sendError(ex);
    }
  }
}
