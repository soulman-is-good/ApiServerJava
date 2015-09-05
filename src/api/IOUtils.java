/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 *
 * @author maxim
 */
class IOUtils {

  /**
   * Transfers data from InputStream to OutputStream
   *
   * @param in some input stream
   * @param out some output stream
   * @param encoding
   * @return total amount of bytes transfered
   * @throws IOException
   */
  public static long pipe(InputStream in, OutputStream out, Charset encoding) throws IOException {
    char[] buffer = new char[1024];
    long total = 0;
    int len;
    InputStreamReader io = new InputStreamReader(in);
    if (encoding == null) {
      encoding = Charset.forName(io.getEncoding());
    }
    len = io.read(buffer);
    while (len != -1) {
      byte[] tmp = encoding.encode(CharBuffer.wrap(buffer, 0, len)).array();
      len = tmp.length - 1;
      //remove zero bytes from the end
      while (len > 0 && tmp[len--] == 0);
      len += 1;
      total += len;
      out.write(tmp, 0, len);
      buffer = new char[1024];
      len = io.read(buffer);
    }
    return total;
  }

  public static long pipe(InputStream in, OutputStream out) throws IOException {
    return pipe(in, out, null);
  }

  public static byte[] convert(byte[] in, Charset encoding) throws IOException {
    ByteArrayInputStream bin = new ByteArrayInputStream(in);
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    pipe(bin, bout, encoding);
    return bout.toByteArray();
  }

  public static byte[] trim(byte[] in) {
    int len = in.length - 1;
    while (len > 0 && in[len] == 0) {
      len--;
    }
    return Arrays.copyOf(in, len + 1);
  }

  public static void reverse(byte[] array) {
    if (array == null) {
      return;
    }
    int i = 0;
    int j = array.length - 1;
    byte tmp;
    while (j > i) {
      tmp = array[j];
      array[j] = array[i];
      array[i] = tmp;
      j--;
      i++;
    }
  }

  public static byte[] charToByteArray(char[] buffer, int length) {
    byte[] b = new byte[length << 1];
    for (int i = 0; i < length; i++) {
      int bpos = i << 1;
      b[bpos] = (byte) ((buffer[i] & 0xFF00) >> 8);
      b[bpos + 1] = (byte) (buffer[i] & 0x00FF);
    }
    return b;
  }

}
