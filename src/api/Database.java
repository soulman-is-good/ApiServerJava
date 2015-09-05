/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maxim
 */
public abstract class Database {
  protected String connectionString;
  protected String user;
  protected String password;

  protected Database(String connectionString, String user, String password) {
    this.connectionString = connectionString;
    this.user = user;
    this.password = password;
  }
  
  public Connection connect(String DriverClass) {
    try {
      if(DriverClass != null) {
        Class.forName(DriverClass);
      }
      return DriverManager.getConnection(connectionString, user, password);
    } catch (SQLException | ClassNotFoundException ex) {
      Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }
}
