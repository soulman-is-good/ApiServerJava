/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maxim
 */
public class FileMapper {

  private HashMap<String, File> files;

  protected String directory = ".";

  protected FileMapper() {
    files = new HashMap<>();
  }

  protected void fillTemplates() {
    try {
      readTemplates(new File(directory), "");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(ViewManager.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private void readTemplates(final File folder, String currentDir) throws FileNotFoundException {
    for (final File fileEntry : folder.listFiles()) {
      if (fileEntry.isDirectory()) {
        readTemplates(fileEntry, currentDir + fileEntry.getName() + "/");
      } else {
        files.put(currentDir + fileEntry.getName(), fileEntry);
      }
    }
  }

  public FileInputStream getFileInputStream(String name) throws FileNotFoundException {
    File file = files.get(name);
    if(file == null) {
      return null;
    }
    return new FileInputStream(file);
  }

  public File getFile(String name) {
    return files.get(name);
  }

  public static FileMapper getInstance() {
    return FileMapper.FileMapperHolder.INSTANCE;
  }

  private static class FileMapperHolder {

    private static final FileMapper INSTANCE = new FileMapper();
  }
}
