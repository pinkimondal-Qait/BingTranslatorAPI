package com.bingtranslator.accesstoken;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import com.qait.bingautomate.BingTranslator;


public class AdmAuthentication {

	public static final String DatamarketAccessUri = "https://datamarket.accesscontrol.windows.net/v2/OAuth2-13";
	private String clientId;
	private String clientSecret;
	private String request;
	private AdmAccessToken token;
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getClientSecret() {
		return clientSecret;
	}
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}
	public String getRequest() {
		return request;
	}
	public void setRequest(String request) {
		this.request = request;
	}
	public AdmAccessToken getToken() {
		return token;
	}
	public void setToken(AdmAccessToken token) {
		this.token = token;
	}
	
	
	public AdmAuthentication(String clientId, String clientSecret) throws IOException
     {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	    this.clientId= URLEncoder.encode(this.clientId,"UTF-8");
	    this.clientSecret= URLEncoder.encode(this.clientSecret,"UTF-8");
		this.request= "grant_type=client_credentials&client_id="+ this.clientId +"&client_secret="+ this.clientSecret +"&scope=http://api.microsofttranslator.com";
     }
     public AdmAccessToken getAccessTokenUsingPost(String DatamarketAccessUri,String request) throws IOException, ParseException{
		
		URL url= new URL(DatamarketAccessUri);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
	    conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
	    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		wr.writeBytes(request);
		wr.flush();
		wr.close();

		int responseCode = conn.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + request);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		   new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		//print result
		System.out.println(response.toString());
		JSONParser parser = new JSONParser();
		JSONObject object= (JSONObject) parser.parse(response.toString());
		AdmAccessToken admToken= new AdmAccessToken();
		String tokenType= (String) object.get("token_type");
		String accessToken=(String) object.get("access_token");
		String expiresIn=(String)object.get("expires_in");
		String scope= (String)object.get("scope");
		admToken.setAccessToken(accessToken);
		admToken.setTokenType(tokenType);
		admToken.setExpiresIn(expiresIn);
		admToken.setScope(scope);
		setToken(admToken);
		conn.disconnect();
		return this.getToken();
	}
	
	public String translateText(String fromCode,String toCode,String text) throws IOException{
		String uri = "http://api.microsofttranslator.com/v2/Http.svc/Translate?";
		String request= "text="+URLEncoder.encode(text,"UTF-8")+ "&from=" + fromCode + "&to=" + toCode;
		String authToken = "Bearer" + " " + this.getToken().getAccessToken();
		URL url= new URL(uri + request);
		System.out.println(authToken);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setRequestMethod("GET");
		conn.setRequestProperty("Authorization", authToken);
		conn.setRequestProperty("Accept", "application/json");
		int responseCode = conn.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("GET parameters : "+request);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		//print result
		String response1=new String(response);
		conn.disconnect();
	    return response1;
	}
	

public ArrayList<String> readInputAndCodes(String csvFile, String csvCodes) throws Exception{
	
	String resultFromAPI=null;
	String toCode=null,fromCode=null;
	ArrayList<String> ouptutFromAPI=new ArrayList<>();
	String from=null,to=null,text = null,line,code=null,country=null,line1;
	HashMap<String,String> hashMap=new HashMap<String,String>();
	BufferedReader br = new BufferedReader(new FileReader(csvFile));
	BufferedReader br1 = new BufferedReader(new FileReader(csvCodes));
	int j=0;
	 while ((line1 = br1.readLine()) != null) {
			//System.out.println("Raw CSV data: " + line);
			if (line1 != null) {
				String[] splitData = line1.split(",");
				for (int i = 0; i < splitData.length; i++) {
					if (!(splitData[i] == null) || !(splitData[i].length() == 0)) {
						 code=splitData[0].trim();
						 country=splitData[1].trim();
				       }
					}
				hashMap.put(country, code);
				
			}
	    }
	while ((line = br.readLine()) != null) {
		System.out.println("Raw CSV data: " + line);
			if (line != null) {
				String[] splitData = line.split(",");
				for (int i = 0; i < splitData.length; i++) {
					if (!(splitData[i] == null) || !(splitData[i].length() == 0)) {
						 from=splitData[0].trim();
						 to=splitData[1].trim();
				         text= splitData[2].trim();
				      
					}
				}
				j++;
		}
			if(from.isEmpty()){
				 from=BingTranslator.lang_from_DropDown.get(j-1);
			}			
//			fromCode = Language.valueOf(from.toUpperCase());
//			fromCode.toString();
//			toCode = Language.valueOf(to.toUpperCase());
//			toCode.toString();
			if(from!=null){
				if(hashMap.containsKey(from)){
				  fromCode=hashMap.get(from);	
				}
			}
			if(to!=null){
				if(hashMap.containsKey(to)){
				 toCode=hashMap.get(to);	
				}
			}
			System.out.println("CSV data: " + fromCode+" "+toCode+" "+text);
			String response=translateText(fromCode,toCode,text);
				 resultFromAPI=xmlToStringRetriever(response);
			    System.out.println("Result From API"+ resultFromAPI);
				ouptutFromAPI.add(resultFromAPI);
		}
	return ouptutFromAPI;
  
}

public String xmlToStringRetriever(String xmlString) throws ParserConfigurationException, SAXException, IOException{
	DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    InputSource is = new InputSource();
    is.setCharacterStream(new StringReader(xmlString));
    Document doc = db.parse(is);
    org.w3c.dom.Element e= doc.getDocumentElement();
    String str= e.getTextContent();
	return str;
}

public ArrayList<String> apiAutomate() throws Exception {
	ArrayList<String> resultFromAPI=new ArrayList<>();
	String clientId="Pinki_Mondal-123";
	String clientSecret="EsE5y9AhnnU5erB5VM0uyM7lX7Y1ZsWsmGd7fejEkmI=";
	String csvFileInput = "/home/pinkimondal/Downloads/input.csv";
	String csvFileCodes = "/home/pinkimondal/Downloads/codes.csv";
	try {
		AdmAuthentication authToken= new AdmAuthentication(clientId, clientSecret);
		try {
			AdmAccessToken token=authToken.getAccessTokenUsingPost(DatamarketAccessUri, authToken.getRequest());
			System.out.println();
			System.out.println(token);
			System.out.println();
			System.out.println(token.getAccessToken());
			resultFromAPI=authToken.readInputAndCodes(csvFileInput,csvFileCodes);
		} catch (ParseException e) {
				e.printStackTrace();
		}
	} catch (IOException e) {
		e.printStackTrace();
	}	
	System.out.println(resultFromAPI);
return resultFromAPI;
}
}
