/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import org.json.JSONArray;
import org.json.JSONObject;
import org.postgresql.util.PGobject;

/**
 *
 * @author maxim
 */
public class PostgresDatabase extends Database {

  public static PostgresDatabase getInstance(String connectionString, String user, String password) {
    return PostgresDatabaseHolder.getInstance(connectionString, user, password);
  }

  public static PostgresDatabase getInstance() {
    return PostgresDatabaseHolder.getInstance(null, null, null);
  }

  protected PostgresDatabase(String connectionString, String user, String password) {
    super(connectionString, user, password);
  }

  public List execSQL(String query, List params) throws SQLException {
    List<HashMap<String, Object>> result;
    result = new ArrayList<>();
    ResultSet rs = null;
    ResultSetMetaData meta = null;
    Statement st = null;
    PreparedStatement pst = null;
    Connection con;
    con = connect("org.postgresql.Driver");
    if (params == null) {
      st = con.createStatement();
      rs = st.executeQuery(query);
      meta = rs.getMetaData();
    } else {
      pst = con.prepareStatement(query);
      pst.setQueryTimeout(15);
      int i = 1;
      for (Object param : params) {
        if (param instanceof JSONObject) {
          PGobject obj = new PGobject();
          obj.setType("json");
          obj.setValue(((JSONObject) param).toString());
          pst.setObject(i++, obj);
        } else {
          pst.setObject(i++, param);
        }
      }
      try {
        rs = pst.executeQuery();
        meta = rs.getMetaData();
      } catch (Exception e) {
        System.err.println(e);
      }
    }
    int len = meta.getColumnCount();
    for (int i = 1; i < len + 1; i++) {
      HashMap<String, Object> tmp = new HashMap<>();
      rs.next();
      mapType(tmp, meta.getColumnName(i), meta.getColumnTypeName(i), rs, i);
      result.add(tmp);
    }
    if (pst != null) {
      pst.close();
    }
    if (st != null) {
      st.close();
    }
    con.close();
    return result;
  }

  public JSONArray exec(String function_name, JSONObject parameters) throws SQLException {
    List<HashMap<String, Object>> result;
    HashMap map;
    List arr = new ArrayList();
    String qm = "";
    String[] func = function_name.split("\\.");
//    if (parameters != null) {
//      for (Object parameter : parameters) {
//        qm += "?,";
//      }
//    }
    if (parameters != null) {
      arr.add(parameters);
    }
    qm = "SELECT " + function_name + "(?)";
    result = execSQL(qm, arr);
    map = (HashMap) result.get(0);
    return map.isEmpty()?new JSONArray():(JSONArray) (map.get(func[1]));
  }

  public JSONArray exec(String function_name) throws SQLException {
    return exec(function_name, null);
  }

  private void mapType(HashMap result, String name, String type, ResultSet rs, int i) throws SQLException {
    TimeZone zone = TimeZone.getDefault();
    Calendar cal = Calendar.getInstance(zone);
    //TODO: Array types https://github.com/tada/pljava/wiki/Default-type-mapping
    switch (type.toLowerCase()) {
      case "json":
        String str = rs.getString(i);
        if (str != null && !("".equals(str))) {
          if(str.charAt(0) == '{') {
            JSONArray a = new JSONArray();
            result.put(name, a.put(new JSONObject(rs.getString(i))));
          } else if(str.charAt(0) == '[') {
            result.put(name, new JSONArray(rs.getString(i)));
          }
        }
        break;
      case "bool":
        result.put(name, rs.getBoolean(i));
        break;
      case "\"char\"":
        result.put(name, rs.getByte(i));
        break;
      case "int2":
        result.put(name, rs.getShort(i));
        break;
      case "int4":
        result.put(name, rs.getInt(i));
        break;
      case "int8":
        result.put(name, rs.getLong(i));
        break;
      case "float4":
        result.put(name, rs.getFloat(i));
        break;
      case "float8":
        result.put(name, rs.getDouble(i));
        break;
      case "date":
        result.put(name, rs.getDate(i));
        break;
      case "time":
        result.put(name, rs.getTime(i, cal));
        break;
      case "timez":
        result.put(name, rs.getTime(i));
        break;
      case "timestamp":
        result.put(name, rs.getTimestamp(i, cal));
        break;
      case "timestampz":
        result.put(name, rs.getTimestamp(i));
        break;
      case "\"char\"[]":
      case "bytea":
        result.put(name, rs.getBytes(i));
        break;
      case "char":
      case "varchar":
      case "text":
      case "name":
        result.put(name, rs.getString(i));
        break;
      default:
        result.put(name, rs.getObject(i));
    }
  }

  private static class PostgresDatabaseHolder {

    private static PostgresDatabase INSTANCE;

    private static PostgresDatabase getInstance(String connectionString, String user, String password) {
      if (INSTANCE == null) {
        INSTANCE = new PostgresDatabase(connectionString, user, password);
      }
      return INSTANCE;
    }
  }
}

class JsonSQLType implements SQLType {

  @Override
  public String getName() {
    return "json";
  }

  @Override
  public String getVendor() {
    return "json";
  }

  @Override
  public Integer getVendorTypeNumber() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
