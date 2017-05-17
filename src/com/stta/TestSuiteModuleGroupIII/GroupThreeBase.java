package com.stta.TestSuiteModuleGroupIII;
import java.io.IOException;
import org.testng.SkipException;
import org.testng.annotations.BeforeSuite;
import com.stta.TestSuiteBase.SuiteBase;
import com.stta.utility.Read_XLS;
import com.stta.utility.SuiteUtility;

public class GroupThreeBase extends SuiteBase{
	Read_XLS FilePath = null;
	String SheetName = null;
	String SuiteName = null;
	String ToRunColumnName = null;	
	

	@BeforeSuite
	public void checkSuiteToRun() throws IOException{
		//Calling init() function from SuiteBase class to Initialize .xls Files
		init();	

		FilePath = TestSuiteListExcel;
		SheetName = "SuitesList";
		SuiteName = "TestSuiteModuleGroupIII";
		ToRunColumnName = "SuiteToRun";
		
		//Write to logfile
		Add_Log.info("Execution started for Module Group III (Taxpayer and Revenue Accounting)");
		Add_Log.info("------------------------------------------------------------------------------");
		
		//If SuiteToRun !== "y" then SuiteOne will be skipped from execution.
		if(!SuiteUtility.checkToRunUtility(FilePath, SheetName,ToRunColumnName,SuiteName)){	
			Add_Log.info("SuiteToRun = N for "+SuiteName+" So Skipping Execution.");
			//To report SuiteOne as 'Skipped' In SuitesList sheet of TestSuiteList.xls If SuiteToRun = N.
			SuiteUtility.WriteResultUtility(FilePath, SheetName, "Skipped/Executed", SuiteName, "Skipped");
			//It will throw SkipException to skip test suite's execution and suite will be marked as skipped In testing report.
			throw new SkipException(SuiteName+"'s SuiteToRun Flag Is 'N' Or Blank. So Skipping Execution Of "+SuiteName);
		}
		//To report SuiteOne as 'Executed' In SuitesList sheet of TestSuiteList.xls If SuiteToRun = Y.
		SuiteUtility.WriteResultUtility(FilePath, SheetName, "Skipped/Executed", SuiteName, "Executed");		
	}		
}