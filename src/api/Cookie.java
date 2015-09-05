/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author maxim
 */
public class Cookie {

  public String name;
  public String value;
  public String domain;
  public String path = "/";
  public Date expiryDate;
  public boolean isSecure;
  public boolean httpOnly = true;
  public boolean isNew = false;

  public Cookie(String name, String value, String domain, Date expiryDate, String path, boolean isSecure) {
    this.name = name;
    this.value = value;
    this.expiryDate = expiryDate;
    this.domain = domain;
    this.isSecure = isSecure;
    if (path != null && !path.equals("")) {
      this.path = path;
    }
  }

  public String getExpireDate() {
    TimeZone tz = TimeZone.getTimeZone("GMT");
    DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy hh:mm:ss z");
    df.setTimeZone(tz);
    return df.format(this.expiryDate);
  }

  public String toString() {
    return name + "=" + value + "; Path=" + path + "; Domain=" + domain + "; Expires=" + getExpireDate() + (httpOnly ? "; HttpOnly" : "") + (isSecure ? "; secure" : "");
  }
}
