/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

/**
 *
 * @author maxim
 */
public class WebUser {

  private final Request req;
  private final String key;

  WebUser(Request req) {
    this.key = "juser.";
    this.req = req;
  }

  public boolean isAuthorized() {
    return false;
  }

  public Object get(String name) {
    return req.session.get(key + name);
  }

  public Object set(String name, Object value) {
    return req.session.put(name, value);
  }
}
