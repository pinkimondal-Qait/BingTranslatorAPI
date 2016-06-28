package com.qait.testng;

import java.util.ArrayList;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.bingtranslator.accesstoken.AdmAuthentication;
import com.qait.bingautomate.BingTranslator;

public class BingTranslatorTestNG {
	String translateFromBinga =  "Hye";
	String translateFromAPIa = "Hye";
	 ArrayList<String> translateFromBing=new ArrayList<>();
	 ArrayList<String> translateFromAPI=new ArrayList<>();
	@BeforeTest
	public void initializeBingAutomate() throws Exception{
		String clientId="Pinki_Mondal-123";
		String clientSecret="EsE5y9AhnnU5erB5VM0uyM7lX7Y1ZsWsmGd7fejEkmI=";
		BingTranslator bing=new BingTranslator();
		  translateFromBing=bing.automateBingMethod();
		AdmAuthentication api=new AdmAuthentication(clientId,clientSecret);
		translateFromAPI=api.apiAutomate();
	}
		
	@Test
  public void testBing() {
      for(int i=0;i<translateFromBing.size();i++){
		Assert.assertEquals(translateFromBing.get(i), translateFromAPI.get(i) , "Not match");
		Reporter.log("Matched", true);
      }
  }
}
