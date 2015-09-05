/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maxim
 */
public class ViewManager extends FileMapper {

  private ViewManager() {
    super();
    String cwd = ".";
    try {
      cwd = new File(".").getCanonicalPath();
    } catch (IOException ex) {
      Logger.getLogger(ViewManager.class.getName()).log(Level.SEVERE, null, ex);
    }
    this.directory = cwd + "/views";
    fillTemplates();
  }

  public FileInputStream getTemplate(String name) throws FileNotFoundException {
    return this.getFileInputStream(name);
  }
  
  public static ViewManager getInstance() {
    return ViewManager.ViewManagerHolder.INSTANCE;
  }

  private static class ViewManagerHolder {

    private static final ViewManager INSTANCE = new ViewManager();
  }
}
