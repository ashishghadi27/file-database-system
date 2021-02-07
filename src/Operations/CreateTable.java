package Operations;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import DataStructure.DataReframer;

public class CreateTable {

	private String rawQuery, table;
	private Map<String, String> dynamicProperties = new HashMap<String, String>();
	
	public CreateTable(String query, String table){
		rawQuery = query;
		this.table = table;
		fillMap(rawQuery.trim().split(","));
	}
	
	private void fillMap(String []param){
		StringBuilder sBuilder = new StringBuilder();
		for(String s: param){
			String []temp = s.trim().split("\\|");
			dynamicProperties.put(temp[0].trim(), temp[1].trim());
			sBuilder.append(s + " *");
		}
		
		try {
			FileWriter fWriter = new FileWriter(Constants.dbFilePath + table + ".root");
			fWriter.write(DataReframer.dataToHex(sBuilder.toString().substring(0, sBuilder.length()-1)));
			fWriter.append("\n");
			fWriter.close();
			System.out.println("Table " + table + " created.");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		createSourceCode(dynamicProperties);
	}
	
	private void createSourceCode(Map<String, String> prop){
		
		StringBuffer varStringBuffer = new StringBuffer();
		StringBuffer getSetStringBuffer = new StringBuffer();
		String dataType = "";
		for(Map.Entry<String, String> entry : dynamicProperties.entrySet()){
			if(entry.getValue().equals("int")) dataType = "Integer";
			else if(entry.getValue().equals("float")) dataType = "Float";
			else if(entry.getValue().equals("double")) dataType = "Double";
			else dataType = "String";
			varStringBuffer.append("\tprivate " + dataType + " " + entry.getKey() + "; \n");
			getSetStringBuffer.append("\n\tpublic " + dataType + " get" + entry.getKey() + "() {\n \t\treturn " + entry.getKey() + ";\n \t}\n");
			getSetStringBuffer.append("\n\tpublic void" + " set" + entry.getKey() + "(" + dataType + " " + entry.getKey() + ") {\n \t\t this." + entry.getKey() + " = " + entry.getKey() + ";\n \t}\n");
		}
		createClassFile(varStringBuffer.toString() + getSetStringBuffer.toString());
	}
	
	private void createClassFile(String source){
		try{    
	           FileWriter fw=new FileWriter(Constants.tablePath + table + ".java");
	           fw.write("package tables;\n");
	           fw.write("public class " + table + " { \n\n");
	           fw.write(source);
	           fw.write("\n}");
	           fw.close();    
	    }
		catch(Exception e){
			System.out.println(e);
		}    
	}
	
}
