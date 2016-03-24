/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author maxim
 */
public class Response extends EventEmitter {

  private final HttpExchange he;
  private final String method = "GET";
  private final Charset encoding = Charset.forName("UTF-8");
  private final long time;
  private final Request req;
  private final HashMap<String, String> headers;
  private short status = 200;
  private byte[] body;
  private boolean handled = false;

  public Response(HttpExchange ex, Request req) {
    time = System.currentTimeMillis();
    he = ex;
    this.req = req;
    body = new byte[0];
    headers = new HashMap<>();
    headers.put("Content-Type", "text/plain");
  }

  public URI getRequestURI() {
    return he.getRequestURI();
  }

  public void end() {
    try {
      OutputStream os;
      this.writeHeaders(he.getResponseHeaders());
      this.req.cookies.writeHeaders(he.getResponseHeaders());
      he.sendResponseHeaders(this.getStatus(), this.size());
      os = he.getResponseBody();
      this.pipe(os);
      os.close();
      handled = true;
      this.emit("end", this);
      System.out.printf("%s %d %s %s %d ms\r\n", he.getRequestMethod(), this.getStatus(), he.getRequestURI().getPath(), he.getRemoteAddress(), System.currentTimeMillis() - time);
      he.close();
    } catch (IOException ex) {
      Logger.getLogger(Response.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public Request getRequest() {
    return req;
  }

  void pipe(OutputStream os) throws IOException {
    os.write(body);
  }

  OutputStream getBodyOutputStream() {
    OutputStream io = new ResponseOutputStream(body, new Callback() {

      @Override
      void call(Object... args) {
        body = (byte[]) args[0];
      }
    });

    return io;
  }

  InputStream getBody() {
    InputStream io = new ByteArrayInputStream(body);
    return io;
  }

  void setBody(byte[] data) {
    body = data;
  }

  void setBody(String data) {
    setBody(data.getBytes());
  }

  void setBody(InputStream data) {
    try {
      data.read(body);
    } catch (IOException ex) {
      Logger.getLogger(Response.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  void sendError(Exception err, int code) {
    JSONObject msg = new JSONObject(err);
    msg.put("error", err.getMessage());
    msg.put("stack", err.getCause().getMessage());
    this.setStatus(code);
    this.setBody(msg.toString());
    this.end();
  }

  void sendError(Exception err) {
    this.sendError(err, 500);
  }

  String getHeader(String name) {
    return headers.get(name);
  }

  void setHeader(String name, String value) {
    headers.put(name, value);
  }

  void writeHeaders(Headers hs) {
    Set<String> keys = headers.keySet();
    hs.clear();
    for (String name : keys) {
      hs.add(name, headers.get(name));
    }
  }

  int getStatus() {
    return status;
  }

  void setStatus(int code) {
    status = (short) code;
  }

  boolean isHandled() {
    return handled;
  }

  int size() {
    return body.length;
  }
}

class ResponseOutputStream extends OutputStream {

  Callback update;

  byte[] result;

  public ResponseOutputStream(byte[] body, Callback cb) {
    result = body.clone();
    update = cb;
  }

  @Override
  public void write(int b) throws IOException {
    byte[] chunk = ByteBuffer.allocate(4).putInt(b).array();
    IOUtils.reverse(chunk);
    chunk = IOUtils.trim(chunk);
    byte[] _new_res = new byte[result.length + chunk.length];
    System.arraycopy(result, 0, _new_res, 0, result.length);
    System.arraycopy(chunk, 0, _new_res, result.length, chunk.length);
    result = _new_res;
    update.call(result);
  }

  @Override
  public void close() throws IOException {
  }
}
