package DataStructure;

import Operations.CreateTable;
import Operations.DeleteOperation;
import Operations.InsertOperation;
import Operations.SelectOperation;

public class QueryParser {
	
	public QueryParser(){
		
	}
	
	public QueryParser(String query){
		
	}
	
	public void init(String query){
		if(query.startsWith("create")) createTableQueryParser(query);
		else if(query.startsWith("insert")) insertQueryParser(query);
		else if(query.startsWith("select")) selectQueryParser(query);
		else if(query.startsWith("delete")) deleteQueryParser(query);
	}
	
	private static void createTableQueryParser(String query){
		String rawQuery, table;
		String []querySplit = query.split("\\(");
		String []tableHolder = querySplit[0].split(" ");
		table = tableHolder[tableHolder.length - 1];
		rawQuery = querySplit[1].replace(")", "");
		rawQuery = rawQuery.replace(";", "");
		new CreateTable(rawQuery, table);
	}
	
	private static void insertQueryParser(String query){
		String parameters, tableName;
		String []querySplit = query.split("\\(");
		tableName = querySplit[0].split("=>")[1].trim();
		parameters = querySplit[1].replace(")", "");
		parameters = parameters.replace(";", "");
		String paramArray[] = parameters.split("\\|");
		new InsertOperation(paramArray, tableName);
	}
	
	private static void selectQueryParser(String query){
		String parameters, tableName, where;
		String []querySplit = query.split("=>");
		String []tableParaArray = querySplit[1].trim().split("\\(");
		tableName = tableParaArray[0].trim();
		parameters = tableParaArray[1].trim().replace(")", "");
		parameters = parameters.replace(";", "");
		String paramArray[] = parameters.trim().split("\\|");
		try{
			where = querySplit[2].replace(")", "");
			where = where.replace(";", "");
			where = where.replace("where", "");
			where = where.replace("(", "");
			where = where.trim();
			if(where.contains("and"))new SelectOperation(tableName, paramArray, where.split("and"), "and");
			else new SelectOperation(tableName, paramArray, where.split("or"), "or");
		}
		catch (ArrayIndexOutOfBoundsException e) {
			new SelectOperation(tableName, paramArray, null, null); 
		}	
	}
	
	private static void deleteQueryParser(String query){
		//delete => Students(ID => 10);
		String tableName, param;
		String []querySplit = query.split("=>");
		String []deleteParaArray = querySplit[1].trim().split("\\(");
		tableName = deleteParaArray[0].trim();
		param = deleteParaArray[1].trim();
		param = param.replace(")", "");
		param = param.replace(";", "");
		if(param.contains("and"))new DeleteOperation(tableName, param.split("and"), "and");
		else new DeleteOperation(tableName, param.split("or"), "or");
	}
	
}
