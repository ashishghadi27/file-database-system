package Operations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DataStructure.DataReframer;
import DynamicClassLoader.JavaClassLoader;

public class DeleteOperation {
	
	private static String tableName, firstLine, condition;
	private static String[] where;
	private static String[] dataSequence;
	private static Map<String, String> map;
	private static List<Object> allData;

	public DeleteOperation(String table, String []where, String c){
		tableName = table;
		DeleteOperation.where = where;
		condition = c;
		map = new HashMap<String, String>();
		getMapFromFirstLine();
		getListFilled();
		deleteElement();
		writeToFile();
	}
	
	private static void writeToFile(){
		try{
			FileWriter fWriter = new FileWriter(new File(Constants.dbFilePath + tableName + ".root"));
			fWriter.write(DataReframer.dataToHex(firstLine));
			fWriter.write("\n");
			fWriter.write(getStringToWrite());
			fWriter.close();
			System.out.println("Data Deleted");
		}
		catch (IOException e) {
			// TODO: handle exception
		}
	}
	
	private static String getStringToWrite(){
		JavaClassLoader jLoader = new JavaClassLoader();
		Class<?> c = jLoader.loadMyClass("tables." + tableName);
		Method method;
		StringBuffer finalBuffer = new StringBuffer();
		
		try{	
			for(Object object: allData){
				StringBuilder sBuilder = new StringBuilder();
				for(String s: dataSequence){
					method = c.getDeclaredMethod("get" + s);
					Object data = method.invoke(object);
					sBuilder.append(data.toString() + ",");
				}
				finalBuffer.append(DataReframer.dataToHex(sBuilder.toString().substring(0, sBuilder.length() - 1)));
				finalBuffer.append("\n");
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return finalBuffer.toString();
		
	}
	
	private void deleteElement(){
		JavaClassLoader jLoader = new JavaClassLoader();
		Class<?> c = jLoader.loadMyClass("tables." + tableName);
		List<Object> copyList = new ArrayList<>(allData);
		for(Object object: copyList){
			boolean flag = true;
			for(int i = 0; i < where.length; i++){
				try{
					String []temp = where[i].split("=");
					Method method = c.getDeclaredMethod("get"+temp[0].trim());
					Object object2 = method.invoke(object);
					if(temp[1].trim().equals(object2.toString())){
						if(condition.equals("or")){
							allData.remove(object);
							break;
						}
					}
					else {
						flag = false;
					}
					
	        	}
				catch (Exception e) {
					// TODO: handle exception
				}
			}
			if(condition.equals("and") && flag) allData.remove(object);
		}
	}
	
	private void getListFilled(){
		JavaClassLoader jLoader = new JavaClassLoader();
		Class<?> c = jLoader.loadMyClass("tables." + tableName);
		allData = new ArrayList<Object>();
		try{
			FileReader reader = new FileReader(Constants.dbFilePath + tableName + ".root");
			BufferedReader br = new BufferedReader(reader);
			String line = br.readLine();
			Constructor<?> constructor = c.getConstructor();
			Object mObject = null;
	
			while((line = br.readLine()) != null){
				String []data = DataReframer.hexToData(line).split(",");
				mObject = constructor.newInstance();
				for(int i = 0; i < dataSequence.length; i++){
					String var = dataSequence[i];
					String content = data[i];
					Class<?> paraType = getDataTypeClass(var);
		        	Object dataObject = getDataObject(var, content);
		        	Method method = c.getDeclaredMethod("set"+var, paraType);
		        	method.invoke(mObject, dataObject);	
				}
				allData.add(mObject);
			}
			br.close();
			reader.close();
			
		}catch (IOException e) {
			// TODO: handle exception
		}
		catch (NoSuchMethodException e) {
			// TODO: handle exception
		}
		catch (InvocationTargetException e) {
			// TODO: handle exception
		}
		catch (IllegalAccessException e) {
			// TODO: handle exception
		}
		catch (InstantiationException e) {
			// TODO: handle exception
		}
		
	}
	
	private static void getMapFromFirstLine() {
		try{
			FileReader reader = new FileReader(Constants.dbFilePath + tableName + ".root");
			BufferedReader br = new BufferedReader(reader);
			firstLine = br.readLine();
			reader.close();
			br.close();
			getMapReady(DataReframer.hexToData(firstLine));
			
		}catch (IOException e) {
			// TODO: handle exception
		}
	}
	
	private static void getMapReady(String firstLine){
		DeleteOperation.firstLine = firstLine;
		String paraArr[] = firstLine.split("\\*");
		int i = 0;
		dataSequence = new String[paraArr.length];
		for(String s : paraArr){
			String []temp = s.split("\\|");
			map.put(temp[0].trim(), temp[1].trim());
			dataSequence[i++] = temp[0].trim();
		}
	}
	
	private static Object getDataObject(String var, String data){
		String val = map.get(var);
		if(val.equals("String")) return new String(data);
		else if (val.equals("int")) return new Integer(Integer.parseInt(data));
		else if (val.contains("double")) return new Double(Double.parseDouble(data));
		else if(val.contains("float")) return new Float(Float.parseFloat(data));
		else return null;
	}
	
	private static Class<?> getDataTypeClass(String var){
		String val = map.get(var);
		if(val.equals("String")) return String.class;
		else if (val.equals("int")) return Integer.class;
		else if (val.contains("double")) return Double.class;
		else if(val.contains("float")) return Float.class;
		else if(val.contains("LocalDate")) return LocalDate.class;
		else return null;
	}
	
}
