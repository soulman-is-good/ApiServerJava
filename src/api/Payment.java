/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Charge;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maxim
 */
public class Payment {
  
  public Charge create(int amount, String cardNumber, short month, short year) {
    HashMap<String, Object> charge = new HashMap<>();
    //TODO: charge.put with https://stripe.com/docs/api/java#create_charge
    try {
      return Charge.create(charge);
    } catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException | APIException ex) {
      Logger.getLogger(Payment.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }
  
  private Payment(String key) {
    Stripe.apiKey = key;
  }
  
  public static Payment getInstance(String key) {
    return PaymentHolder.getInstance(key);
  }
  
  private static class PaymentHolder {

    private static Payment INSTANCE;
    
    private static Payment getInstance(String key) {
      if(INSTANCE == null) {
        INSTANCE = new Payment(key);
      }
      return INSTANCE;
    }
    private static Payment getInstance() {
      return getInstance(null);
    }
  }
}
