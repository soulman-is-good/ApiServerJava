/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

import com.sun.net.httpserver.Headers;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author maxim
 */
public class CookieManager extends HashMap<String, List<Cookie>> {

  private final Request req;
  private final String domain = "wvb.com";

  public CookieManager(Request req) {
    this.req = req;
    this.parseCookie(req.getHeader("cookie"));
  }

  private void parseCookie(String rawCookie) {
    if (rawCookie != null && !(rawCookie = rawCookie.trim()).equals("")) {
      String[] pairs = rawCookie.split(";");
      for (String pair : pairs) {
        String[] keyval = pair.split("=");
        if (keyval.length > 1) {
          this.addCookie(keyval[0].trim(), keyval[1].trim());
        }
      }
    }
  }

  private Date getExpires() {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MONTH, 1);
    return calendar.getTime();
  }

  public void clearCookie(String name) {
    //TODO: Set expire
  }

  public String getString(String key) {
    List<Cookie> list = this.getArray(key);
    if (list.isEmpty()) {
      return null;
    }
    return list.get(0).value;
  }

  public Cookie addCookie(String name, String value, Date expires, String path, String domain, boolean isSecure) {
    Cookie cookie = new Cookie(name, value, domain, expires, path, isSecure);
    List<Cookie> list = this.getArray(name);
    cookie.isNew = true;
    if (list.add(cookie)) {
      this.put(name, list);
      return cookie;
    }
    return null;
  }

  public Cookie addCookie(String name, String value) {
    return this.addCookie(name, value, this.getExpires(), null, this.domain, false);
  }

  public Cookie setCookie(String name, String value, Date expires, String path, String domain, boolean isSecure) {
    Cookie cookie = new Cookie(name, value, domain, expires, path, isSecure);
    List<Cookie> list = this.getArray(name);
    cookie.isNew = true;
    for (Cookie old : list) {
      System.out.println(old.value + "=" + cookie.value);
      if (old.value.equals(cookie.value)) {
        cookie.isNew = false;
      }
    }
    if(cookie.isNew) {
      list.clear();
      if (list.add(cookie)) {
        this.put(name, list);
        return cookie;
      }
    }
    return null;
  }

  public Cookie setCookie(String name, String value) {
    return this.setCookie(name, value, this.getExpires(), null, this.domain, false);
  }

  public void writeHeaders(Headers hs) {
    for (Map.Entry pair : this.entrySet()) {
      String name = (String) pair.getKey();
      List<Cookie> list = (ArrayList) pair.getValue();
      String result = "";
      for (Cookie cookie : list) {
        if (cookie.isNew) {
          result = cookie.toString();
        }
        //TODO: other parameters
      }
      if (!result.equals("")) {
        hs.add("set-cookie", result);
      }
    }
  }

  public List<Cookie> getArray(String name) {
    List<Cookie> list = this.get(name);
    if (list == null) {
      list = new ArrayList<>();
    }
    return list;
  }
}
