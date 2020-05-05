import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;


public class ParseCsvFile {
	
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";
    private static final int MAX_ORDERS_PER_TIME_INTERVAL = 3;   
    private static final int TIME_INTERVAL_IN_MILLISECONDS = 60000;
    private static final int NUMBER_OF_FIELDS_IN_ORDER_RECORD = 8;
    private static final String BROKER_LITERAL = "broker";    
    private static final String SEQ_ID_LITERAL = "sequence id";    

	// Generated output files
    private static final String ORDERS_REJECTED_FILE_CSV = System.getProperty("user.home")+"/OrdersRejected.csv";
    private static final String ORDERS_REJECTED_FILE_JSON = System.getProperty("user.home")+"/OrdersRejected.json";
    private static final String ORDERS_ACCEPTED_FILE_CSV = System.getProperty("user.home")+"/OrdersAccepted.csv";
    private static final String ORDERS_ACCEPTED_FILE_JSON = System.getProperty("user.home")+"/OrdersAccepted.json";
        
    // Order record field indexes
    private static final int TIME_STAMP = 0; 
    private static final int BROKER = 1; 
    private static final int SEQUENCE_ID = 2; 
    private static final int TYPE = 3; 
    private static final int SYMBOL = 4; 
    private static final int QUANTITY = 5; 
    private static final int PRICE = 6; 
    private static final int SIDE = 7; 

    
    private int maxOrdersPerTimeInterval;
    private int timeIntervalInMilliseconds;          
    private HashSet<String> setTradeSymbols = new HashSet<String>();
    private HashSet<String> setTradeFirms = new HashSet<String>();
    private HashMap<String, HashSet<String>> mapBroker = new HashMap<String, HashSet<String>>();
    private StringBuilder strBuilderAcceptOrders = new StringBuilder();
    private StringBuilder strBuilderRejectOrders = new StringBuilder();
    
    public ParseCsvFile(){
 	   
	   this.maxOrdersPerTimeInterval = MAX_ORDERS_PER_TIME_INTERVAL;
	   this.timeIntervalInMilliseconds = TIME_INTERVAL_IN_MILLISECONDS;
    }
    
	public HashSet<String> getSetTradeSymbols() {
		return setTradeSymbols;
	}

	public int getMaxOrdersPerTimeInterval() {
		return maxOrdersPerTimeInterval;
	}

	public void setMaxOrdersPerTimeInterval(int maxOrdersPerTimeInterval) {
		this.maxOrdersPerTimeInterval = maxOrdersPerTimeInterval;
	}

	public int getTimeIntervalInMilliseconds() {
		return timeIntervalInMilliseconds;
	}

	public void setTimeIntervalInMilliseconds(int timeIntervalInMilliseconds) {
		this.timeIntervalInMilliseconds = timeIntervalInMilliseconds;
	}

	public void setSetTradeSymbols(HashSet<String> setTradeSymbols) {
		this.setTradeSymbols = setTradeSymbols;
	}
	     
    public HashSet<String> getSetTradeFirms() {
		return setTradeFirms;
	}

	public void setSetTradeFirms(HashSet<String> setTradeFirms) {
		this.setTradeFirms = setTradeFirms;
	}
	
    public HashMap<String, HashSet<String>> getMapBroker() {
		return mapBroker;
	}
       
	public StringBuilder getStrBuilderAcceptOrders() {
		return strBuilderAcceptOrders;
	}

	public StringBuilder getStrBuilderRejectOrders() {
		return strBuilderRejectOrders;
	}
      
    public boolean validateSymbol(String str) {
    	if(! setTradeSymbols.contains(str)) 
    		return false;
    	
    	return true;    
    }
    
    public boolean validateFirm(String str) {
    	if(! setTradeFirms.contains(str)) 
    		return false;
    	
    	return true;    
    }
    
    public boolean validateSequenceId(String broker, String sequenceId) {
    	
    	HashSet<String> setBrokerOrders = mapBroker.get(broker);
    	
        if(setBrokerOrders == null ) {
        	
        	// new broker - sequence id for particular broker must be new
        	return true;
        }
        
        if(setBrokerOrders.contains(sequenceId)) {
        	
        	// reject order with duplicate sequence id                	
        	
        	return false;
        	
        }else {
            	
        	// valid unique sequence id 
        	return true;
        }
 
    }
       
    public boolean isNullOrEmpty(String str) {
    	if(str != null && !str.trim().isEmpty())
    		return false;
    	return true;
    }
    
    public boolean validateNullOrEmpty(String orderRecordLine) {
    	    
    	 //Get all tokens available in line
        String[] tokens = orderRecordLine.split(COMMA_DELIMITER);
               
        if (tokens.length > 0 && tokens.length >= NUMBER_OF_FIELDS_IN_ORDER_RECORD) {
        	               	              	
        	// validate field values are null or empty
        	if(isNullOrEmpty(tokens[BROKER]) || isNullOrEmpty(tokens[SEQUENCE_ID]) || 
        			isNullOrEmpty(tokens[TYPE]) || isNullOrEmpty(tokens[SYMBOL]) || 
        			isNullOrEmpty(tokens[QUANTITY]) || isNullOrEmpty(tokens[PRICE]) ||
        			isNullOrEmpty(tokens[SIDE])) {
    	
        		return true;
        	}
        	else {
        		return false;
        	}
        }
        else        	
        	return true;    
    }
        
    public void loadData(String filename, HashSet<String> hsData) {
    
        try {
        	
            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
              String data = myReader.nextLine();
              hsData.add(data);
            }
            myReader.close();
          } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
    }
    
    public void writeOderRecordToCsvFile(FileWriter fileWriterCsv, String brokerAndSeqId) {
        
        try {
        	
        	fileWriterCsv.append(brokerAndSeqId);
        	fileWriterCsv.append(NEW_LINE_SEPARATOR);
            
        } catch (Exception e) {
            System.out.println("Error writting order record to csv file.");
            e.printStackTrace();
        }
    }
    
    public void writeOderRecordToJsonFile(JsonGenerator jsonGen, String broker, String seqId) {
        
    	try {
        	
            jsonGen.writeStartObject();
            jsonGen.writeStringField(BROKER_LITERAL, broker);
            jsonGen.writeStringField(SEQ_ID_LITERAL, seqId);
            jsonGen.writeEndObject();
            
    	} catch (Exception e) {
            System.out.println("Error writting json object to json file.");
            e.printStackTrace();
    	}       
    }

    public void writeOderRecordToString(StringBuilder strBuilderCsv, String orderRecordLine) {
        
        try {
        	
        	strBuilderCsv.append(orderRecordLine);
        	strBuilderCsv.append(NEW_LINE_SEPARATOR);
            
        } catch (Exception e) {
            System.out.println("Error writting order record to string.");
            e.printStackTrace();
        }
    }
       

    // This method reads three files:
    // input files:
    // inputFileName - source csv orders file
    // symbolsFile - source symbols file to be loaded for orders validation
    // firmsFile - source firms file to be loaded for orders validation
    // writeToString - flag for JUnit test cases - true for test cases false otherwise
    //
    // output files:
    // OrdersAccepted.csv - generated output csv file containing broker and sequence id of valid orders
    // OrdersAccepted.json - generated output json file containing broker and sequence id of valid orders
    // OrdersRejected.csv - generated output csv file containing broker and sequence id of rejected orders
    // OrdersRejected.json - generated output json file containing broker and sequence id of rejected orders
    public void readCsvFile(String inputFileName, String symbolsFile, String firmsFile, boolean writeToString) {
    	
        BufferedReader fileReader = null;
        
        FileWriter fWriterAcceptOrderCsv = null;
        FileWriter fWriterRejectOrderCsv = null;
        
        JsonGenerator jsonGeneratorAcceptOrders = null;         
        JsonGenerator jsonGeneratorRejectOrders = null;
             
        try {
                          
            String line = "";
                         
            //Create the file reader
            fileReader = new BufferedReader(new FileReader(inputFileName));
                        
            fWriterAcceptOrderCsv = new FileWriter(ORDERS_ACCEPTED_FILE_CSV);                  
            fWriterRejectOrderCsv = new FileWriter(ORDERS_REJECTED_FILE_CSV);
            
            HashMap<String, Date> mapBrokerTimes = new HashMap<String, Date>(); 
            
            HashMap<String, Integer> mapBrokerOrderCounter = new HashMap<String, Integer>(); 
            
            System.out.println("  Loading symbols file : " + symbolsFile);
            loadData(symbolsFile, setTradeSymbols);   
            
            System.out.println("  Loading firms file : " + firmsFile);
            loadData(firmsFile, setTradeFirms);
            
            //===========================================================================
            System.out.println("   Reading from : " + inputFileName);
            System.out.println("   Parsing order records to : " + ORDERS_ACCEPTED_FILE_CSV);
            System.out.println("   Parsing order records to : " + ORDERS_REJECTED_FILE_CSV);
            System.out.println("   Parsing order records to : " + ORDERS_ACCEPTED_FILE_JSON);
            System.out.println("   Parsing order records to : " + ORDERS_REJECTED_FILE_JSON);           
            //===========================================================================
                      
            // create JSON generator for accepted orders
            JsonFactory jsonFactoryAcceptOrders = new JsonFactory();                         
            jsonGeneratorAcceptOrders = jsonFactoryAcceptOrders.createGenerator(new File(ORDERS_ACCEPTED_FILE_JSON), JsonEncoding.UTF8);
            
            // create JSON generator for rejected orders
            JsonFactory jsonFactoryRejectOrders = new JsonFactory();
            jsonGeneratorRejectOrders = jsonFactoryRejectOrders.createGenerator(new File(ORDERS_REJECTED_FILE_JSON), JsonEncoding.UTF8);
            
            // tokens for header of inputSource orders file
            String[] headerTokens = fileReader.readLine().split(COMMA_DELIMITER);

            // write header to output CSV files
            StringBuilder sbHeader = new StringBuilder(headerTokens[BROKER]);
            sbHeader.append(COMMA_DELIMITER);
            sbHeader.append(headerTokens[SEQUENCE_ID]);          
            fWriterAcceptOrderCsv.append(sbHeader.toString());
    		fWriterAcceptOrderCsv.append(NEW_LINE_SEPARATOR);	
    		fWriterRejectOrderCsv.append(sbHeader.toString());
            fWriterRejectOrderCsv.append(NEW_LINE_SEPARATOR);
            
            // format of timestamp in input orders file
            SimpleDateFormat sdfFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
                                 
            // Begin JSON arrays
            jsonGeneratorAcceptOrders.writeStartArray();
            jsonGeneratorRejectOrders.writeStartArray();
            
            int timeIntervalInMilliseconds = getTimeIntervalInMilliseconds();
            int maxOrdersPerTimeInterval = getMaxOrdersPerTimeInterval();
            
            //Read the file line by line starting from the second line
            while ((line = fileReader.readLine()) != null) {
            	
                //Get all tokens available in line
                String[] tokens = line.split(COMMA_DELIMITER);
                
                // build broker and order sequence id to be written in corresponding CSV files
                StringBuilder brokerAndOrderID = new StringBuilder(tokens[BROKER]);
                brokerAndOrderID.append(COMMA_DELIMITER);
                brokerAndOrderID.append(tokens[SEQUENCE_ID]);
                
                if(validateNullOrEmpty(line)) {
                        
                	writeOderRecordToCsvFile(fWriterRejectOrderCsv, brokerAndOrderID.toString());                      
                	writeOderRecordToJsonFile(jsonGeneratorRejectOrders, tokens[BROKER], tokens[SEQUENCE_ID]);
                	
                	if(writeToString) {
                		writeOderRecordToString(strBuilderRejectOrders, line);
                	}
                        
                	continue;
                }
                	
                // validate symbols            
                if(! validateSymbol(tokens[SYMBOL])) {
                		
                	// Reject - symbol not traded in exchange                		                        
                	writeOderRecordToCsvFile(fWriterRejectOrderCsv, brokerAndOrderID.toString());                                               
                	writeOderRecordToJsonFile(jsonGeneratorRejectOrders, tokens[BROKER], tokens[SEQUENCE_ID]);
                	
                	if(writeToString) {
                		writeOderRecordToString(strBuilderRejectOrders, line);
                	}
                        
                	continue;
                }
                	
                /*
               	// validate Firms - commented out - not part of requirements              
             	if(! validateFirm(tokens[BROKER])) {
                	
               		// Reject - firm not traded in exchange
                	fWriterRejectOrderCsv.append(line);
                   	fWriterRejectOrderCsv.append(NEW_LINE_SEPARATOR);
                        
                  	continue;
                }
                */
                	
                // each broker has a set of unique orders with unique sequence ids
                
            	HashSet<String> setBrokerOrders = mapBroker.get(tokens[BROKER]);
                if(setBrokerOrders == null ) {
                	setBrokerOrders = new HashSet<String>();
                	mapBroker.put(tokens[BROKER], setBrokerOrders);		
                }
                
                if(setBrokerOrders.contains(tokens[SEQUENCE_ID])) {
                	
                	// reject order with duplicate sequence id                	
                	
                	writeOderRecordToCsvFile(fWriterRejectOrderCsv, brokerAndOrderID.toString());                    	                        
                    writeOderRecordToJsonFile(jsonGeneratorRejectOrders, tokens[BROKER], tokens[SEQUENCE_ID]);
                    
                	if(writeToString) {
                		writeOderRecordToString(strBuilderRejectOrders, line);
                	}
                	
                	continue;
                	
                }else {
                    	
                	// valid unique sequence id - continue further processing
                	
                	Date currentTimeStamp = sdfFormat.parse(tokens[TIME_STAMP]);
                	
                	// first order for corresponding broker
                	// counter is needed for each broker considering requirement of 
                	// 3 orders per broker per minute
                	if(setBrokerOrders.size() == 0 ) {	
                		
                		// update global broker's timestamp
                		mapBrokerTimes.put(tokens[BROKER], currentTimeStamp);
                		
                		// reset global order counter for corresponding broker                  		
                		mapBrokerOrderCounter.put(tokens[BROKER], 0);
                	}
                	
                	// latest timestamp of corresponding broker - taken from global table
                	Date latestTimeStamp = mapBrokerTimes.get(tokens[BROKER]);
                
                	long timeDiff = currentTimeStamp.getTime() - latestTimeStamp.getTime();
                	
                	if(timeDiff > timeIntervalInMilliseconds) { // 60 seconds in milliseconds
                		                 
                		// order's timestamp is bigger than 60 seconds
                		// no need to check for limit of 3 orders per broker within 60 seconds
                		// update corresponding global fields and write record to proper files
                		
                		// update global broker's timestamp
                		mapBrokerTimes.put(tokens[BROKER], currentTimeStamp);
                		
                		// reset global order counter for corresponding broker
                		mapBrokerOrderCounter.put(tokens[BROKER], 1);
                		
                		// add sequence id to global set for corresponding broker
                		setBrokerOrders.add(tokens[SEQUENCE_ID]);
                		
                		
                		writeOderRecordToCsvFile(fWriterAcceptOrderCsv, brokerAndOrderID.toString());                    		                    		
                		writeOderRecordToJsonFile(jsonGeneratorAcceptOrders, tokens[BROKER], tokens[SEQUENCE_ID]);
                		
                    	if(writeToString) {
                    		writeOderRecordToString(strBuilderAcceptOrders, line);
                    	}
                		
                	}else {
                		
                		// order's timestamp is within 60 seconds
                		// check for order limitation per broker - 3 orders per minute
                		int tmpBrokerOrderCounter = mapBrokerOrderCounter.get(tokens[BROKER]);
                		                   	
                		if(tmpBrokerOrderCounter < maxOrdersPerTimeInterval) {
                			
                			// add sequence id to global set for corresponding broker
                    		setBrokerOrders.add(tokens[SEQUENCE_ID]);
                    		                        		
                    		// update global order counter for corresponding broker
                    		tmpBrokerOrderCounter++;                       	
                    		mapBrokerOrderCounter.put(tokens[BROKER], tmpBrokerOrderCounter);
                    		 
                    		
                    		writeOderRecordToCsvFile(fWriterAcceptOrderCsv, brokerAndOrderID.toString());                        		                        		
                    		writeOderRecordToJsonFile(jsonGeneratorAcceptOrders, tokens[BROKER], tokens[SEQUENCE_ID]);
                    		
                    		if(writeToString) {
                        		writeOderRecordToString(strBuilderAcceptOrders, line);
                        	}
                    		
                		}else {
                			
                			// only 3 orders per broker per minute are allowed 
                			                               
                            writeOderRecordToCsvFile(fWriterRejectOrderCsv, brokerAndOrderID.toString());                                                             
                            writeOderRecordToJsonFile(jsonGeneratorRejectOrders, tokens[BROKER], tokens[SEQUENCE_ID]);
                            
                            if(writeToString) {
                        		writeOderRecordToString(strBuilderRejectOrders, line);
                        	}
                		}                    		
                	}                                      	
                }                                                       
            }
            
            // End JSON arrays
            jsonGeneratorAcceptOrders.writeEndArray();
            jsonGeneratorRejectOrders.writeEndArray();                        
        } 
        catch (Exception e) {
            System.out.println("Error in File Parsing !!!");
            e.printStackTrace();
        } finally {
            try {
                fileReader.close();
                
                fWriterAcceptOrderCsv.flush();               
                fWriterAcceptOrderCsv.close();
                
                fWriterRejectOrderCsv.flush();
                fWriterRejectOrderCsv.close();
                
                jsonGeneratorAcceptOrders.close();
                jsonGeneratorRejectOrders.close();                               
                
            } catch (IOException e) {
                System.out.println("Error while closing file handlers !!!");
                e.printStackTrace();
            }
        }
    }	
    
	public static void main(String[] args) {
				 		
        String ordersFile = System.getProperty("user.home")+"/trades.csv";
        String symbolsFile = System.getProperty("user.home")+"/symbols.txt";
        String firmsFile = System.getProperty("user.home")+"/firms.txt";
        
        System.out.println("READ/PROCESS CSV FILE - STARTED:");
        
        ParseCsvFile parseObjCsv = new ParseCsvFile();
        parseObjCsv.readCsvFile(ordersFile, symbolsFile, firmsFile, false);
                
        System.out.println("READ/PROCESS CSV FILE - COMPLETED:");
	}
    
}
