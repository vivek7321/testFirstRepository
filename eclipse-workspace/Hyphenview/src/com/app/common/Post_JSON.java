package com.app.common;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
public class Post_JSON {
	public static void main(String[] args) {
		Post_JSON.Post_JSON();
	}
	public static void Post_JSON() {
		
		
	        StringBuffer jsonString = new StringBuffer();
	        try {
	            String SelectQueryUrl = "http://206.189.130.200:5009/SelectQuery/";
	            URL url = new URL(SelectQueryUrl);
	            HttpURLConnection conselect = (HttpURLConnection) url.openConnection();

	 

	            conselect.setRequestMethod("GET");
	            conselect.setRequestProperty("Accept", "application/json");
	            conselect.setRequestProperty("Content-Type", "application/json");
	           
	            conselect.setDoInput(true);
	           conselect.setDoOutput(true);
	 

	            String fields = "distinct site_id, count(*)";
	            String condition = "" ;
	           
	            conselect.setRequestMethod("GET");
	            String payload = "{\r\n    \"action\": \"runtime\" ,\r\n    \"database\" :\"icon\" ,\r\n    \"username\" : \"root\" ,\r\n    \"password\": \"Win32.exe\" ,\r\n    \"host\":\"206.189.130.200\" ,\r\n    \"port\":\"3306\",\r\n    \"table\" :\"smsrecipientslist\" ,\r\n    \"fields\" :\"customerName\" ,\r\n    \"condition\":\"\"\r\n,\r\n    \"groupby\":\"customerName\"\r\n}";
	           
	            OutputStreamWriter writer = new OutputStreamWriter(conselect.getOutputStream());
	            writer.write(payload);
	            writer.close();
	            
	            System.out.println(conselect.getInputStream());
	            
			/*
			 * BufferedReader br = new BufferedReader(new
			 * InputStreamReader(conselect.getInputStream()));
			 * 
			 * 
			 * 
			 * String line; while ((line = br.readLine()) != null) {
			 * jsonString.append(line); //System.out.println(line); } br.close();
			 */
	           
	            
	            conselect.disconnect();
	            
	            
	            
	        } catch (Exception e) {
	            System.out.println(e);
	        }
	        System.out.println(jsonString.toString()) ;

	 


	        

	 

	    
		
		
		
		
//           String query_url = "http://206.189.130.200:5009/SelectQuery/";
//           String jsonInput = "{\"headers\": {\"Content-Type\": \"application/json\"}, \"statusCode\": \"200\", \"body\": \"Invalid Request : Incorrect Parameters\"}" ;
//           try {
//           JSONParser parser = new JSONParser();  
//       	
//           Object objectparser = parser.parse(jsonInput);
//          System.out.println(objectparser);
//           
//           org.json.simple.JSONObject jsonstr = (org.json.simple.JSONObject) parser.parse(objectparser.toString());
//          // System.out.println(jsonstr.get("body"));
//           //JSONObject json1 = (JSONObject) parser.parse(jsonInput);			           
//         
//           URL url = new URL(query_url);
//           HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//           conn.setConnectTimeout(5000);
//           conn.setRequestProperty("Content-Type", "application/json ; charset=UTF-8");
//           conn.setDoOutput(true);
//           conn.setDoInput(true);
//           conn.setRequestMethod("GET");
//          // OutputStream os = conn.getOutputStream();
//           try(OutputStream os = conn.getOutputStream()) {
//        	    byte[] input = jsonInput.getBytes("utf-8");
//        	    os.write(input, 0, input.length);			
//        	}
//          // os.close(); 
//           // read the response
//           InputStream in = new BufferedInputStream(conn.getInputStream());
//          // System.out.println(in);
//           String result = IOUtils.toString(in, "UTF-8");
//         //  System.out.println("result before Reading JSON Response");
//          // System.out.println(result);
////           System.out.println("result after Reading JSON Response");
////           JSONObject myResponse = new JSONObject(result);
////           System.out.println(myResponse);
////           JSONObject json1 = (JSONObject) parser.parse(result);	
////           System.out.println(json1);
//           
//           in.close();
//           conn.disconnect();
//           } catch (Exception e) {
//   			System.out.println(e);
//   		}
	}
}
