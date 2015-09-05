/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 *
 * @author maxim
 */
public abstract class Renderer {
  abstract void render(InputStream in, OutputStream out, Properties props, Charset encoding);
  abstract void render(InputStream in, OutputStream out, Properties props);
}
