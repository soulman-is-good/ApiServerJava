/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import java.io.*;
import java.util.Properties;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maxim
 */
public class FreemarkerRenderer extends Renderer {

  private final Configuration config;
  private final Charset defaultEncoding;

  public FreemarkerRenderer(File viewPath, String encoding) throws IOException {
    config = new Configuration(Configuration.VERSION_2_3_22);
    config.setDefaultEncoding(encoding);
    config.setDirectoryForTemplateLoading(viewPath);
    config.setEncoding(Locale.forLanguageTag("ru"), encoding);
    defaultEncoding = Charset.forName(encoding);
  }

  @Override
  public void render(InputStream in, OutputStream out, Properties props, Charset enc) {
    InputStreamReader rs = new InputStreamReader(in);
    if (enc == null) {
      enc = Charset.forName(rs.getEncoding());
    }
    OutputStreamWriter ws = new OutputStreamWriter(out, enc);
    try {
      Configuration cf = new Configuration(Configuration.VERSION_2_3_22);
      cf.setDefaultEncoding(enc.name());
      Template t = new Template(String.valueOf(in.hashCode()), rs, cf);
      t.process(props, ws);
    } catch (IOException | TemplateException ex) {
      Logger.getLogger(FreemarkerRenderer.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  @Override
  void render(InputStream in, OutputStream out, Properties props) {
    render(in, out, props, null);
  }
  
  void render(String view, OutputStream out, HashMap props) {
    try {
      OutputStreamWriter ws = new OutputStreamWriter(out, Charset.forName("UTF-8"));
      Template tmpl = config.getTemplate(view);
      tmpl.process(props, ws);
    } catch (MalformedTemplateNameException ex) {
      Logger.getLogger(FreemarkerRenderer.class.getName()).log(Level.SEVERE, null, ex);
    } catch (ParseException ex) {
      Logger.getLogger(FreemarkerRenderer.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException | TemplateException ex) {
      Logger.getLogger(FreemarkerRenderer.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
