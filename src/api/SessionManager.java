/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author maxim
 */
public class SessionManager extends HashMap<String, Object> implements Serializable {

  private String token;
  final private String key;
  private Request req;
  private PostgresDatabase database;

  public SessionManager(Request req) {
    this.key = "asid";
    this.req = req;
    this.token = req.cookies.getString(this.key);
    /*this.database = PostgresDatabase.getInstance();
    try {
      JSONArray arr;
      JSONObject params = new JSONObject();
      if (this.token != null) {
        params.put("session_token", this.token);
      }
      arr = database.exec("sec.post_session", params);
      //TODO: pass token
      String new_token = arr.getJSONObject(0).getString("session_token");
      if (token == null ? new_token != null : !token.equals(new_token)) {
        token = new_token;
        req.cookies.setCookie(this.key, this.token);
      }
    } catch (SQLException ex) {
      Logger.getLogger(SessionManager.class.getName()).log(Level.SEVERE, null, ex);
    }*/
  }

  public byte[] serialize() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream oss;
    try {
      oss = new ObjectOutputStream(bos);
      oss.writeObject(this);
      oss.close();
      return bos.toByteArray();
    } catch (IOException ex) {
      Logger.getLogger(SessionManager.class.getName()).log(Level.SEVERE, null, ex);
    }
    return new byte[0];
  }

  public void deserialize(String hash) {
    ByteArrayInputStream bis = new ByteArrayInputStream(hash.getBytes());
    try {
      ObjectInputStream ois = new ObjectInputStream(bis);
      this.putAll((HashMap) ois.readObject());
    } catch (IOException | ClassNotFoundException ex) {
      Logger.getLogger(SessionManager.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
