package api;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maxim
 */
public class AssetsManager extends FileMapper {

  private HashMap<String, HashMap<String, Object>> cache;
  private Response res;

  private AssetsManager() {
    super();
    String cwd = ".";
    cache = new HashMap<>();
    try {
      cwd = new File(".").getCanonicalPath();
    } catch (IOException ex) {
      Logger.getLogger(ViewManager.class.getName()).log(Level.SEVERE, null, ex);
    }
    this.directory = cwd + "/assets";
    fillTemplates();
  }

  public HashMap getCache(String name) {
    if (!cache.containsKey(name)) {
      cache.put(name, new HashMap<String, Object>());
    }
    return cache.get(name);
  }

  public void setResponse(Response res) {
    this.res = res;
  }

  static boolean handle(final Response res) {
    final AssetsManager asset = AssetsManager.getInstance();
    final String path = res.getRequestURI().getPath().substring(1);
    File file = null;
    FileInputStream fileio = null;
    String contenttype = null;
    ByteArrayOutputStream oss;
    long size = 0;
    HashMap prop = asset.getCache(path);
    asset.setResponse(res);
    if (!prop.containsKey("buffer")) {
      oss = new ByteArrayOutputStream();
      Charset enc = Charset.forName("UTF-8");
      file = asset.getFile(path);
      if (file != null) {
        try {
          contenttype = Files.probeContentType(file.toPath()) + "; charset=utf-8";
          fileio = new FileInputStream(file);
          IOUtils.pipe(fileio, oss, enc);
          fileio.close();
        } catch (FileNotFoundException ex) {
          Logger.getLogger(AssetsManager.class.getName()).log(Level.SEVERE, null, ex);
          return false;
        } catch (IOException ex) {
          Logger.getLogger(AssetsManager.class.getName()).log(Level.SEVERE, null, ex);
          return false;
        }
      } else {
        return false;
      }
      prop.put("buffer", oss);
      prop.put("size", size);
      prop.put("contenttype", contenttype);
    } else {
      oss = (ByteArrayOutputStream) prop.get("buffer");
      contenttype = (String) prop.get("contenttype");
      size = (long) prop.get("size");
    }
    asset.sendResponse(res, contenttype, oss, size);
    return true;
  }

  private void sendResponse(final Response res, final String contenttype, final ByteArrayOutputStream oss, final long size) {
    new Thread(new Runnable() {

      @Override
      public void run() {
        //TODO: Accept-Encoding: gzip
        res.setStatus(200);
        res.setHeader("Content-Type", contenttype);
        res.setBody(oss.toByteArray());
        res.end();
      }
    }).start();
  }

  public static AssetsManager getInstance() {
    return AssetsManager.AssetsManagerHolder.INSTANCE;
  }

  static class AssetsManagerHolder {

    public static final AssetsManager INSTANCE = new AssetsManager();
  }

}
