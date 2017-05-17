package com.stta.TestSuiteLogin;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.By;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.stta.utility.Read_XLS;
import com.stta.utility.SuiteUtility;

//import org.openqa.selenium.*;



//SuiteOneCaseOne Class Inherits From SuiteOneBase Class.
//So, SuiteOneCaseOne Class Is Child Class Of SuiteOneBase Class And SuiteBase Class.
public class InvalidUserLogin<WebeElement> extends LoginBase{
	Read_XLS FilePath = null;
	String SheetName = null;
	String TestCaseName = null;	
	String ToRunColumnNameTestCase = null;
	String ToRunColumnNameTestData = null;
	String TestDataToRun[]=null;
	static boolean TestCasePass=true;
	static int DataSet=-1;	
	static boolean Testskip=false;
	static boolean Testfail=false;
	SoftAssert s_assert =null;	
	
	@BeforeTest
	public void checkCaseToRun() throws IOException{
		//Calling init() function from SuiteBase class to Initialize .xls Files
		init();			
		//To set SuiteOne.xls file's path In FilePath Variable.
		FilePath = TestCaseListExcelOne;		
		TestCaseName = this.getClass().getSimpleName();	
		//SheetName to check CaseToRun flag against test case.
		SheetName = "TestCasesList";
		//Name of column In TestCasesList Excel sheet.
		ToRunColumnNameTestCase = "CaseToRun";
		//Name of column In Test Case Data sheets.
		ToRunColumnNameTestData = "DataToRun";
		//Bellow given syntax will Insert log In applog.log file.
		Add_Log.info(TestCaseName+" : Execution started.");
		
		//To check test case's CaseToRun = Y or N In related excel sheet.
		//If CaseToRun = N or blank, Test case will skip execution. Else It will be executed.
		if(!SuiteUtility.checkToRunUtility(FilePath, SheetName,ToRunColumnNameTestCase,TestCaseName)){
			Add_Log.info(TestCaseName+" : CaseToRun = N for So Skipping Execution.");
			//To report result as skip for test cases In TestCasesList sheet.
			SuiteUtility.WriteResultUtility(FilePath, SheetName, "Pass/Fail/Skip", TestCaseName, "SKIP");
			//To throw skip exception for this test case.
			throw new SkipException(TestCaseName+"'s CaseToRun Flag Is 'N' Or Blank. So Skipping Execution Of "+TestCaseName);
		}	
		//To retrieve DataToRun flags of all data set lines from related test data sheet.
		TestDataToRun = SuiteUtility.checkToRunUtilityOfData(FilePath, TestCaseName, ToRunColumnNameTestData);
	}
	
	//Accepts 4 column's String data In every Iteration.
	@Test(dataProvider="LoginCaseTwoData")
	public void LoginCaseTwoTest(String DataCol1,String DataCol2,String DataCol3,String DataCol4){
		
		DataSet++;
		
		//Created object of testng SoftAssert class.
		s_assert = new SoftAssert();
		
		//If found DataToRun = "N" for data set then execution will be skipped for that data set.
		if(!TestDataToRun[DataSet].equalsIgnoreCase("Y")){	
			Add_Log.info(TestCaseName+" : DataToRun = N for data set line "+(DataSet+1)+" So skipping Its execution.");
			//If DataToRun = "N", Set Testskip=true.
			Testskip=true;
			throw new SkipException("DataToRun for row number "+DataSet+" Is No Or Blank. So Skipping Its Execution.");
		}
		
		//If found DataToRun = "Y" for data set then bellow given lines will be executed.
		//To Convert data from String to Integer
		String username = DataCol1;
		String password = DataCol2;
		//String ValueThree = DataCol3;
		String ExpectedResultString = DataCol4;
	
//To Initialize browser.
		loadWebBrowser();	
		
		//To navigate to URL. It will read site URL from Param.properties file
		driver.get(Param.getProperty("siteURL"));
		//driver.manage().deleteAllCookies();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

//Begin Login Cases
//This Test Case checks if Login Fails
//--------------------------------------------------------------------------------------
Add_Log.info("-------------------- Data -------------------------");
		Add_Log.info("User Name:"+username);
		Add_Log.info("Password:"+password);
Add_Log.info("-----------------Starting Test Case Script Execution--------------------");
		getElementByID("username").sendKeys(username);
		getElementByID("password").sendKeys(password);
		getElementByName("submit").click();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		getElementByID("username").sendKeys(username);
		getElementByID("password").sendKeys(password);
		getElementByName("submit").click();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		String ActualResultString = getElementByClass("ErrorMsg").getAttribute("class");
				

	   //Check
Add_Log.info("Actual is >>"+ActualResultString+"<<");
Add_Log.info("Expected is >>"+ExpectedResultString+"<<");
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
Add_Log.info("-------------------- Reporting Results -------------------------");	
//End of Test Case
//---------------------------------------------------------------------------------------------------------------			
		//Compare actual and expected values.
		if(!(ActualResultString.equals(ExpectedResultString))){
			//If expected and actual results not match, Set flag Testfail=true.
			Testfail=true;	
			//If result Is fail then test failure will be captured Inside s_assert object reference.
			//This soft assertion will not stop your test execution.
			s_assert.assertEquals(ActualResultString,ExpectedResultString);
		}
				
		
		if(Testfail){
			//At last, test data assertion failure will be reported In testNG reports and It will mark your test data, test case and test suite as fail.
			s_assert.assertAll();		
		}
	}
	
	//@AfterMethod method will be executed after execution of @Test method every time.
	@AfterMethod
	public void reporterDataResults(){		
		if(Testskip){
			Add_Log.info(TestCaseName+" : Reporting test data set line "+(DataSet+1)+" as SKIP In excel.");
			//If found Testskip = true, Result will be reported as SKIP against data set line In excel sheet.
			SuiteUtility.WriteResultUtility(FilePath, TestCaseName, "Pass/Fail/Skip", DataSet+1, "SKIP");
		}
		else if(Testfail){
			Add_Log.info(TestCaseName+" : Reporting test data set line "+(DataSet+1)+" as FAIL In excel.");
			//To make object reference null after reporting In report.
			s_assert = null;
			//Set TestCasePass = false to report test case as fail In excel sheet.
			TestCasePass=false;	
			//If found Testfail = true, Result will be reported as FAIL against data set line In excel sheet.
			SuiteUtility.WriteResultUtility(FilePath, TestCaseName, "Pass/Fail/Skip", DataSet+1, "FAIL");			
		}else{
			Add_Log.info(TestCaseName+" : Reporting test data set line "+(DataSet+1)+" as PASS In excel.");
			//If found Testskip = false and Testfail = false, Result will be reported as PASS against data set line In excel sheet.
			TestCasePass=true;
			SuiteUtility.WriteResultUtility(FilePath, TestCaseName, "Pass/Fail/Skip", DataSet+1, "PASS");
			
		}
		//At last make both flags as false for next data set.
		Testskip=false;
		Testfail=false;
		//TestCasePass=false;
	}
	
	//This data provider method will return 4 column's data one by one In every Iteration.
	@DataProvider
	public Object[][] LoginCaseTwoData(){
		//To retrieve data from Data 1 Column,Data 2 Column,Data 3 Column and Expected Result column of SuiteOneCaseOne data Sheet.
		//Last two columns (DataToRun and Pass/Fail/Skip) are Ignored programatically when reading test data.
		return SuiteUtility.GetTestDataUtility(FilePath, TestCaseName);
	}	
	
	//To report result as pass or fail for test cases In TestCasesList sheet.
	@AfterTest
	
	public void closeBrowser(){
		//To Close the web browser at the end of test.
		closeWebBrowser();
		if(TestCasePass){
			Add_Log.info(TestCaseName+" : Reporting test case as PASS In excel.");
			SuiteUtility.WriteResultUtility(FilePath, SheetName, "Pass/Fail/Skip", TestCaseName, "PASS");
		}
		else{
			Add_Log.info(TestCaseName+" : Reporting test case as FAIL In excel.");
			SuiteUtility.WriteResultUtility(FilePath, SheetName, "Pass/Fail/Skip", TestCaseName, "FAIL");			
		}
	}
}
