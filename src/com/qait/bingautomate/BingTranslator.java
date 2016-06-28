package com.qait.bingautomate;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

public class BingTranslator {
	public static ArrayList<String>  lang_from_DropDown=new ArrayList<>();
	public ArrayList<String> automateBingMethod() throws Exception {
		String downloadDir = System.getProperty("user.home") + "//Downloads";
		String csvFile = "/home/pinkimondal/Downloads/input.csv";
		//Driver Initialization
        System.setProperty("webdriver.chrome.driver", downloadDir+"//chromedriver");
        WebDriver driver=new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://www.bing.com/translator");
        ArrayList<String> translateFromBing=read(csvFile,driver);
		return translateFromBing;
        
}

public ArrayList<String> read(String csvFile, WebDriver driver) throws Exception
   {
	String[] first_drop_down_value=null;
	
	ArrayList<String> bingOutput=new ArrayList<>();
	String translate=null;
	String from=null,to=null,text = null,line;
	BufferedReader br = new BufferedReader(new FileReader(csvFile));
    //Get the list of languages
    List<WebElement> languages=driver.findElements(By.className("LS_HeaderTitle"));
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
				System.out.println("CSV data: " + from+" "+to+" "+text);
				//String first_xpath = "(//td[text()='"+from+"'])[1]";
				String second_xpath = "(//td[text()='"+to+"'])[2]";
	    		 Thread.sleep(2000);
			     driver.findElement(By.className("srcTextarea")).sendKeys(text);
			      Thread.sleep(3000);
			      languages.get(1).click();
			      first_drop_down_value=driver.findElement(By.className("LS_HeaderTitle")).getText().split(" ");
			      lang_from_DropDown.add(first_drop_down_value[0].trim());
			      driver.findElement(By.xpath(second_xpath)).click();
			      Thread.sleep(2000);
			      translate=driver.findElement(By.id("destText")).getText();
			      System.out.println(translate+ " DropDown "+first_drop_down_value);
			      bingOutput.add(translate);
			      driver.findElement(By.className("srcTextarea")).clear();
			      Thread.sleep(2000);
  		
			}
		}
 	System.out.println(bingOutput);
 	System.out.println(lang_from_DropDown);
 	
 	return (bingOutput);
 }
}


	
