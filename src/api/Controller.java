/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maxim
 */
public class Controller {

  protected Request req;
  protected Response res;
  private String action;
  protected WebUser user;
  protected String layout = "layouts/main.html";
  protected FreemarkerRenderer renderer;

  static Controller handle(Response res) throws InstantiationException, IllegalAccessException {
    String[] path = res.getRequestURI().getPath().split("/");
    String controller = (path.length > 1 ? String.valueOf(path[1].charAt(0)).toUpperCase() + path[1].substring(1) : "Index") + "Controller";
    String action = "action" + (path.length > 2 ? String.valueOf(path[2].charAt(0)).toUpperCase() + path[2].substring(1) : "Index");
    Class<?> NewController;
    Controller x;
    try {
      NewController = Class.forName("wvbapp.controllers." + controller);
      x = (Controller) NewController.newInstance();
      x.setAction(action);
    } catch (ClassNotFoundException ex) {
      x = new NotFoundController();
      x.setAction("actionIndex");
    }
    return x;
  }

  public Controller() {
    try {
      renderer = new FreemarkerRenderer(new File("./views"), "UTF-8");
    } catch (IOException ex) {
      Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  protected void beforeAction() {
  }

  protected void afterAction() {
  }

  public void send(String response, int status) {
    Charset enc = Charset.forName("UTF-8");
    byte[] body;
    res.setStatus(status);
    res.setHeader("Content-Type", "text/html; charset=utf-8");
    try {
      body = IOUtils.convert(response.getBytes(), enc);
    } catch (IOException ex) {
      body = new byte[0];
      Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
    }
    res.setBody(body);
    res.end();
  }

  public void send(String response) {
    this.send(response, 200);
  }

  public void render(InputStream viewFile, Properties props) {
    Charset enc = Charset.forName("UTF-8");
    res.setStatus(200);
    res.setHeader("Content-Type", "text/html; charset=utf-8");
    if (props == null) {
      props = new Properties();
    }
    if (this.layout != null) {
      InputStream layoutFile;
      try {
        layoutFile = ViewManager.getInstance().getTemplate(this.layout);
      } catch (FileNotFoundException ex) {
        layoutFile = new ByteArrayInputStream("${__content__}".getBytes());
        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
      }
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      renderer.render(viewFile, out, props);
      try {
        props.setProperty("__content__", out.toString("UTF-8"));
      } catch (UnsupportedEncodingException ex) {
        props.setProperty("__content__", out.toString());
        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
      }
      renderer.render(layoutFile, res.getBodyOutputStream(), props, enc);
      try {
        layoutFile.close();
      } catch (IOException ex) {
        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
      }
    } else {
      renderer.render(viewFile, res.getBodyOutputStream(), props, enc);
    }
    try {
      viewFile.close();
    } catch (IOException ex) {
      Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
    }
    res.end();
  }

  public void render(String viewName, HashMap props) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    res.setStatus(200);
    res.setHeader("Content-Type", "text/html; charset=utf-8");
    if (props == null) {
      props = new HashMap();
    }
    if (layout != null) {
      renderer.render(viewName, out, props);
      try {
        props.put("__content__", out.toString("UTF-8"));
      } catch (UnsupportedEncodingException ex) {
        props.put("__content__", out.toString());
        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
      }
      out = new ByteArrayOutputStream();
      renderer.render(layout, out, props);
      res.setBody(out.toByteArray());
    } else {
      out = new ByteArrayOutputStream();
      renderer.render(viewName, out, props);
      res.setBody(out.toByteArray());
    }
    res.end();
  }

  public void render(String viewName) {
    HashMap<String, Object> props = new HashMap<>();
    render(viewName, props);
  }

  public void redirect(String url) {
    res.setStatus(301);
    res.setHeader("Location", url);
    res.end();
  }

  public void init(Request request, Response response) {
    this.req = request;
    this.res = response;
  }

  final public void run() {
    java.lang.reflect.Method method;
    Controller ctrl = this;
    try {
      beforeAction();
      method = ctrl.getClass().getMethod(action);
      if (method == null) {
        this.send("Not found", 404);
      } else {
        method.invoke(ctrl);
      }
      afterAction();
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public Response getResponse() {
    return res;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }
}
