import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ParseCsvFileTest {

	@Test
	void testLoadSymbolsData() {
		ParseCsvFile p1 = new ParseCsvFile();
		String symbolsFile = System.getProperty("user.home")+"/symbols.txt";
		p1.loadData(symbolsFile, p1.getSetTradeSymbols()); 
		
		// check for "BRIC" symbol in loaded symbols table
		assertTrue(p1.validateSymbol("BRIC"));
	}
	
	@Test
	void testValidateSequenceId() {
		
		ParseCsvFile p2 = new ParseCsvFile();
		String ordersFile = System.getProperty("user.home")+"/csvTestFiles/trades_duplicateSequenceId.csv";
		String symbolsFile = System.getProperty("user.home")+"/symbols.txt";
		String firmsFile = System.getProperty("user.home")+"/firms.txt";
		p2.readCsvFile(ordersFile, symbolsFile, firmsFile, true);
		
		// orders with unique sequence ids (1 and 2) for Fidelity broker accepted
		String strAcceptOrdersFromFile = "10/5/2017 10:00:00,Fidelity,1,2,BARK,100,1.195,Buy\n" + 
				"10/5/2017 10:00:01,Fidelity,2,2,BARK,100,1.195,Buy\n";		
		assertEquals(strAcceptOrdersFromFile, p2.getStrBuilderAcceptOrders().toString());
		
		// order with duplicated order id (1) for same Fidelity broker rejected
		String strRejecOrderFromFile = "10/5/2017 10:00:02,Fidelity,1,2,BARK,100,1.195,Buy\n";		
		assertEquals(strRejecOrderFromFile, p2.getStrBuilderRejectOrders().toString());		
	}

	@Test
	void testValidateSymbol() {
				
		ParseCsvFile p3 = new ParseCsvFile();
		String ordersFile = System.getProperty("user.home")+"/csvTestFiles/trades_validateSymbols.csv";
		String symbolsFile = System.getProperty("user.home")+"/symbols.txt";
		String firmsFile = System.getProperty("user.home")+"/firms.txt";
		p3.readCsvFile(ordersFile, symbolsFile, firmsFile, true);
		
		// orders with symbol "CARD" is a valid traded symbol - Accepted
		String strAcceptOrdersFromFile = "10/5/2017 10:00:01,Charles Schwab,1,2,CARD,200,6.855,Sell\n";		
		assertEquals(strAcceptOrdersFromFile, p3.getStrBuilderAcceptOrders().toString());
		
		// orders with symbol "ABCD" is NOT a valid traded symbol - Rejected
		String strRejecOrderFromFile = "10/5/2017 10:00:02,AXA Advisors,1,K,ABCD,5000,30.7,Sell\n";		
		assertEquals(strRejecOrderFromFile, p3.getStrBuilderRejectOrders().toString());		
	}

	@Test
	void testMaxThreeOrdersPerBrokerPerMinute() {
				
		ParseCsvFile p4 = new ParseCsvFile();
		String ordersFile = System.getProperty("user.home")+"/csvTestFiles/trades_ThreeOrdersPerMinute.csv";
		String symbolsFile = System.getProperty("user.home")+"/symbols.txt";
		String firmsFile = System.getProperty("user.home")+"/firms.txt";
		p4.readCsvFile(ordersFile, symbolsFile, firmsFile, true);		
		
		// fourth order for Fidelity broker within 1 minute - rejected
		// only three orders are allowed per broker per minute - as per requirement
		String strRejecOrderFromFile = "10/5/2017 10:00:03,Fidelity,4,2,BARK,100,1.195,Buy\n";		
		assertEquals(strRejecOrderFromFile, p4.getStrBuilderRejectOrders().toString());		
	}
	
	@Test
	void testMissingBrokerValue() {
				
		ParseCsvFile p5 = new ParseCsvFile();
		String ordersFile = System.getProperty("user.home")+"/csvTestFiles/trades_missingBroker.csv";
		String symbolsFile = System.getProperty("user.home")+"/symbols.txt";
		String firmsFile = System.getProperty("user.home")+"/firms.txt";
		p5.readCsvFile(ordersFile, symbolsFile, firmsFile, true);
				
		// order rejected due to missing broker		
		String strRejecOrderFromFile = "10/5/2017 10:00:01,,1,2,BARK,100,1.195,Buy\n";		
		assertEquals(strRejecOrderFromFile, p5.getStrBuilderRejectOrders().toString());		
	}
			
	@Test
	void testLoadFirmsData() {
		ParseCsvFile p6 = new ParseCsvFile();
		String firmsFile = System.getProperty("user.home")+"/firms.txt";
		p6.loadData(firmsFile, p6.getSetTradeFirms()); 
		
		// check for "TD Ameritrade" firm in loaded firms table
		assertTrue(p6.validateFirm("TD Ameritrade"));
	}

	@Test
	void testSetMaxOrdersPerTimeInterval() {
		ParseCsvFile p7 = new ParseCsvFile();
		p7.setMaxOrdersPerTimeInterval(4);
		assertEquals(4, p7.getMaxOrdersPerTimeInterval());
	}
	
	@Test
	void testSetTimeIntervalInMilliseconds() {
		ParseCsvFile p8 = new ParseCsvFile();
		p8.setTimeIntervalInMilliseconds(80000);
		assertEquals(80000, p8.getTimeIntervalInMilliseconds());
	}
	
	@Test
	void testdefaultMaxOrdersPerTimeInterval() {
		ParseCsvFile p9 = new ParseCsvFile();
		assertEquals(3, p9.getMaxOrdersPerTimeInterval());
	}
	
	@Test
	void testdefaultTimeIntervalInMilliseconds() {
		ParseCsvFile p10 = new ParseCsvFile();
		assertEquals(60000, p10.getTimeIntervalInMilliseconds());
	}
			
	@Test
	void testMissingSymbolValue() {
			
		ParseCsvFile p11 = new ParseCsvFile();
		String ordersFile = System.getProperty("user.home")+"/csvTestFiles/trades_missingSymbol.csv";
		String symbolsFile = System.getProperty("user.home")+"/symbols.txt";
		String firmsFile = System.getProperty("user.home")+"/firms.txt";
		p11.readCsvFile(ordersFile, symbolsFile, firmsFile, true);
				
		// order rejected due to missing symbol		
		String strRejecOrderFromFile = "10/5/2017 10:00:01,Fidelity,1,2,,100,1.195,Buy\n";		
		assertEquals(strRejecOrderFromFile, p11.getStrBuilderRejectOrders().toString());				
	}
	
	@Test
	void testMissingQuantityValue() {
			
		ParseCsvFile p12 = new ParseCsvFile();
		String ordersFile = System.getProperty("user.home")+"/csvTestFiles/trades_missingQuantity.csv";
		String symbolsFile = System.getProperty("user.home")+"/symbols.txt";
		String firmsFile = System.getProperty("user.home")+"/firms.txt";
		p12.readCsvFile(ordersFile, symbolsFile, firmsFile, true);
				
		// order rejected due to missing symbol		
		String strRejecOrderFromFile = "10/5/2017 10:00:01,Fidelity,1,2,BARK,,1.195,Buy\n";		
		assertEquals(strRejecOrderFromFile, p12.getStrBuilderRejectOrders().toString());				
	}
	
	@Test
	void testMissingPriceValue() {
			
		ParseCsvFile p13 = new ParseCsvFile();
		String ordersFile = System.getProperty("user.home")+"/csvTestFiles/trades_missingPrice.csv";
		String symbolsFile = System.getProperty("user.home")+"/symbols.txt";
		String firmsFile = System.getProperty("user.home")+"/firms.txt";
		p13.readCsvFile(ordersFile, symbolsFile, firmsFile, true);
				
		// order rejected due to missing symbol		
		String strRejecOrderFromFile = "10/5/2017 10:00:00,Fidelity,1,2,BARK,100,,Buy\n";		
		assertEquals(strRejecOrderFromFile, p13.getStrBuilderRejectOrders().toString());				
	}
	
	@Test
	void testMissingSideValue() {
			
		ParseCsvFile p14 = new ParseCsvFile();
		String ordersFile = System.getProperty("user.home")+"/csvTestFiles/trades_missingSide.csv";
		String symbolsFile = System.getProperty("user.home")+"/symbols.txt";
		String firmsFile = System.getProperty("user.home")+"/firms.txt";
		p14.readCsvFile(ordersFile, symbolsFile, firmsFile, true);
				
		// order rejected due to missing symbol		
		String strRejecOrderFromFile = "10/5/2017 10:00:01,Fidelity,1,2,BARK,100,1.195,\n";		
		assertEquals(strRejecOrderFromFile, p14.getStrBuilderRejectOrders().toString());				
	}
	
	@Test
	void testMissingTypeValue() {
			
		ParseCsvFile p15 = new ParseCsvFile();
		String ordersFile = System.getProperty("user.home")+"/csvTestFiles/trades_missingType.csv";
		String symbolsFile = System.getProperty("user.home")+"/symbols.txt";
		String firmsFile = System.getProperty("user.home")+"/firms.txt";
		p15.readCsvFile(ordersFile, symbolsFile, firmsFile, true);
				
		// order rejected due to missing symbol		
		String strRejecOrderFromFile = "10/5/2017 10:00:01,Fidelity,1,,BARK,100,1.195,Buy\n";		
		assertEquals(strRejecOrderFromFile, p15.getStrBuilderRejectOrders().toString());				
	}
	
	@Test
	void testMissingSequenceIdValue() {
			
		ParseCsvFile p16 = new ParseCsvFile();
		String ordersFile = System.getProperty("user.home")+"/csvTestFiles/trades_missingSequenceId.csv";
		String symbolsFile = System.getProperty("user.home")+"/symbols.txt";
		String firmsFile = System.getProperty("user.home")+"/firms.txt";
		p16.readCsvFile(ordersFile, symbolsFile, firmsFile, true);
				
		// order rejected due to missing symbol		
		String strRejecOrderFromFile = "10/5/2017 10:00:00,Fidelity,,2,BARK,100,1.195,Buy\n";		
		assertEquals(strRejecOrderFromFile, p16.getStrBuilderRejectOrders().toString());				
	}
}
