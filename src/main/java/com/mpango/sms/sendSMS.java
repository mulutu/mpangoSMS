/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mpango.sms;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author jamulutu
 */
public class sendSMS {

    private String _username = "mulutu";
    private String _apiKey = "29f30729c7f3c570a2a003dcb79981c51df8aabd4e17570d59230ece7207b9c5";
    private String _environment;
    private int responseCode;
    private String _msgOriginator = "MADUQA.COM";
    


    private static DBConnector db;
    private static Connection con;

    public sendSMS() {
    }

    public void sendMessage() {
        
        db = DBConnector.getDbCon();
        con = db.conn;
        
        AfricasTalkingGateway smsgw = new AfricasTalkingGateway(_username, _apiKey);

        String SQL = "SELECT id, destination_msisdn, message FROM sms_alerts WHERE sent_status = 0";

        Statement st = null;
        ResultSet rs = null;

        try {
            st = con.prepareStatement(SQL);
            rs = st.executeQuery(SQL);
            while (rs.next()) {
                int msgId = rs.getInt("id");
                String recepient = rs.getString("destination_msisdn");
                String message = rs.getString("message");

                JSONArray resp = smsgw.sendMessage(recepient, message, _msgOriginator, 1);
                System.out.println("RESP -> " + resp);

                for (int i = 0; i < resp.length(); i++) {
                    JSONObject resObj = resp.getJSONObject(i);
                    String number = resObj.getString("number");
                    String cost = resObj.getString("cost");
                    String messageParts = resObj.getString("messageParts");
                    String messageId = resObj.getString("messageId");
                    String status = resObj.getString("status");
                    String statusCode = resObj.getString("statusCode");
                    //  array item 0 :: {"number":"+254720844418","cost":"KES 0.8000","messageParts":1,"messageId":"ATXid_7c34d9a50211aa8dece923e6c3763635","status":"Success","statusCode":101}

                    if (status.equalsIgnoreCase("Success")) {
                        updateSMS(msgId, status);
                    }
                }
            }
            rs.close();
            //con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            Logger.getLogger(sendSMS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void updateSMS(int msgId, String respMsg) {
        String SQL = "UPDATE sms_alerts SET date_sent=NOW(), sent_status=1, delivery_response = '" + respMsg + "' WHERE id = " + msgId;
        SQLUpdate(SQL, con);
    }

    public int SQLUpdate(String SQL, Connection con) {
        int result = 0;
        Statement sttm = null;
        try {
            sttm = con.prepareStatement(SQL);
            int rowsUpdated = sttm.executeUpdate(SQL);
            if (rowsUpdated > 0) {
                result = 1;
            }
            sttm.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //DbUtils.closeQuietly(rs);
            //DbUtils.closeQuietly(sttm);
            //DbUtils.closeQuietly(con);
        }
        return result;
    }
}
