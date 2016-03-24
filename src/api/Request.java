/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maxim
 */
public class Request extends EventEmitter {

  private HttpExchange he;
  private Headers headers;
  private byte[] body;
  private String method;
  private URI uri;
  private Map<String, String> bodyParameters;
  private Map<String, String> queryParameters;
  public final CookieManager cookies;
  public final SessionManager session;
  public final WebUser user;

  public Request(HttpExchange he) {
    this.he = he;
    this.bodyParameters = new HashMap();
    this.queryParameters = new HashMap();
    headers = he.getRequestHeaders();
    body = new byte[0];
    uri = he.getRequestURI();
    try {
      if (he.getRequestBody().available() > 0) {
        readBody(he.getRequestBody());
        parseBody();
      }
      queryParameters = parseUrl(uri.getQuery());
    } catch (IOException ex) {
      Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
    }
    method = he.getRequestMethod().toUpperCase();
    cookies = new CookieManager(this);
    session = new SessionManager(this);
    user = new WebUser(this);
  }

  public String getHeader(String name) {
    return headers.getFirst(name);
  }

  public URI getURI() {
    return uri;
  }

  public String getMethod() {
    return method;
  }

  public String getQueryParameter(String name) {
    return queryParameters.get(name);
  }

  public String getBodyParameter(String name) {
    return bodyParameters.get(name);
  }

  public Map getBodyParameters() {
    return bodyParameters;
  }

  public Map getQueryParameters() {
    return queryParameters;
  }

  public byte[] getBody() {
    return body;
  }

  private Map parseUrl(String url) throws UnsupportedEncodingException {
    Map<String, String> result = new HashMap();
    if (url == null) {
      return result;
    }
    String[] pairs = url.split("&");
    for (String pair : pairs) {
      String[] kv = URLDecoder.decode(pair, "UTF-8").split("=");
      result.put(kv[0], kv[1]);
    }
    return result;
  }

  private void readBody(InputStream is) throws IOException {
    byte[] b = new byte[1024];
    while (-1 != is.read(b)) {
      int old_len = body.length;
      b = IOUtils.trim(b);
      body = Arrays.copyOf(body, old_len + b.length);
      System.arraycopy(b, 0, body, old_len, b.length);
    }
  }

  private void parseBody() throws UnsupportedEncodingException {
    //TODO: encoding
    String qBody = new String(body);
    String contenttype = headers.getFirst("content-type");
    if ("application/x-www-form-urlencoded".equals(contenttype)) {
      bodyParameters = parseUrl(qBody);
    }
  }
}
