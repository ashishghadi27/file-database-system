package Operations;

import java.io.BufferedReader;
import java.io.FileReader;
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

public class SelectOperation {
	
	private static String tableName;
	private static String []param;
	private static String []dataSequenceArray;
	private static Map<String, String> map;
	private static List<Object> allData;
	
	public SelectOperation(String table, String []param, String []where, String c){
		tableName = table;
		SelectOperation.param = param;
		map = new HashMap<String, String>();
		getMapFromFirstLine();
		getListFilled();
		if(param[0].equals("*"))readList();
		else if (where == null) selectQueryParser();
		else selectQueryWithWhereParser(where, c);
	}
	
	private void selectQueryWithWhereParser(String []where, String condition){
		//System.out.println("IN WHERE: " + where[0]);
		JavaClassLoader jLoader = new JavaClassLoader();
		Class<?> c = jLoader.loadMyClass("tables." + tableName);
		
		List<Object> constrainedObject = new ArrayList<Object>();
		
		for(Object object: allData){
			boolean flag = true;
			for(int i = 0; i < where.length; i++){
				try{
					String []temp = where[i].split("=");
					Method method = c.getDeclaredMethod("get"+temp[0].trim());
					Object object2 = method.invoke(object);
					if(temp[1].trim().equals(object2.toString())){
						if(condition.equals("or")){
							constrainedObject.add(object);
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
			if(condition.equals("and") && flag) constrainedObject.add(object);
		}
		
		
		StringBuffer sBuffer = new StringBuffer();
		for(Object object: constrainedObject){
			for(int i = 0; i < param.length; i++){
				try{
					Method method = c.getDeclaredMethod("get"+param[i].trim());
					Object object2 = method.invoke(object);
					sBuffer.append(param[i].trim() + " => " + object2 + "\n");
	        	}
				catch (Exception e) {
					// TODO: handle exception
				}
			}
			sBuffer.append("\n");
		}
		System.out.println(sBuffer.toString());	
	}
	
	private void selectQueryParser(){
		JavaClassLoader jLoader = new JavaClassLoader();
		Class<?> c = jLoader.loadMyClass("tables." + tableName);
		StringBuffer sBuffer = new StringBuffer();
		for(Object object: allData){
			for(int i = 0; i < param.length; i++){
				try{
					Method method = c.getDeclaredMethod("get"+param[i].trim());
					Object object2 = method.invoke(object);
					sBuffer.append(param[i].trim() + " => " + object2 + "\n");
	        	}
				catch (Exception e) {
					// TODO: handle exception
				}
			}
			sBuffer.append("\n");
		}
		System.out.println(sBuffer.toString());	
	}
	
	private void readList(){
		JavaClassLoader jLoader = new JavaClassLoader();
		Class<?> c = jLoader.loadMyClass("tables." + tableName);
		StringBuffer sBuffer = new StringBuffer();
		for(Object object: allData){
			for(int i = 0; i < dataSequenceArray.length; i++){
				try{
					Method method = c.getDeclaredMethod("get"+dataSequenceArray[i]);
					Object object2 = method.invoke(object);
					sBuffer.append(dataSequenceArray[i] + " => " + object2 + "\n");
	        	}
				catch (Exception e) {
					// TODO: handle exception
				}
			}
			sBuffer.append("\n");
		}
		System.out.println(sBuffer.toString());
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
				for(int i = 0; i < dataSequenceArray.length; i++){
					String var = dataSequenceArray[i];
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
	
	private static void getMapFromFirstLine() {
		try{
			FileReader reader = new FileReader(Constants.dbFilePath + tableName + ".root");
			BufferedReader br = new BufferedReader(reader);
			String line = br.readLine();
			reader.close();
			br.close();
			getMapReady(DataReframer.hexToData(line));
			
		}catch (IOException e) {
			// TODO: handle exception
		}
	}
	
	private static void getMapReady(String firstLine){
		String paraArr[] = firstLine.split("\\*");
		int i = 0;
		dataSequenceArray = new String[paraArr.length];
		for(String s : paraArr){
			String []temp = s.split("\\|");
			dataSequenceArray[i++] = temp[0].trim();
			map.put(temp[0].trim(), temp[1].trim());
		}
	}
	
}
