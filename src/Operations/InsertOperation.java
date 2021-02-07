package Operations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import DataStructure.DataReframer;
import DynamicClassLoader.JavaClassLoader;

public class InsertOperation {
	
	//insert => Students(Id => 1 | Name => Ashish | RollNo => 20 | CGPA => 7.92);
	
	private static String table;
	private static String []param;
	private static Map<String, String> map;
	private static String []dataSequence;
	
	public InsertOperation(String []param, String table){
		InsertOperation.table = table;
		InsertOperation.param = param;
		map = new HashMap<>();
		getMapFromFirstLine();
		writeToFile();
	}
	
	private static void writeToFile(){
		try{
			FileWriter fWriter = new FileWriter(new File(Constants.dbFilePath + table + ".root"), true);
			fWriter.write(DataReframer.dataToHex(getStringToWrite()));
			fWriter.write("\n");
			fWriter.close();
			System.out.println("Data Inserted");
		}
		catch (IOException e) {
			// TODO: handle exception
		}
	}
	
	private static String getStringToWrite(){
		JavaClassLoader jLoader = new JavaClassLoader();
		Class<?> c = jLoader.loadMyClass("tables." + table);
		Method method;
		Object object = createClassObject();
		StringBuilder sBuilder = new StringBuilder();
		String output = null;
		try{	
			for(String s: dataSequence){
				method = c.getDeclaredMethod("get" + s);
				Object data = method.invoke(object);
				sBuilder.append(data.toString() + ",");
			}
			output = sBuilder.toString().substring(0, sBuilder.length() - 1);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return output;
		
	}
	
	private static Object createClassObject(){
		try{
			JavaClassLoader jLoader = new JavaClassLoader();
			Class<?> c = jLoader.loadMyClass("tables." + table);
			Constructor<?> constructor = c.getConstructor();
	        Object myClassObject = constructor.newInstance();
	        for(String string : param){
	        	String tempArr[] = string.split("=>");
	        	String var = tempArr[0].trim();
	        	String data = tempArr[1].trim();
	        	Class<?> paraType = getDataTypeClass(var);
	        	Object dataObject = getDataObject(var, data);
	        	Method method = c.getDeclaredMethod("set"+var, paraType);
	        	method.invoke(myClassObject, dataObject);
	        }
	        return myClassObject;
		}catch(Exception e){
			//System.out.println("Here " + e.getMessage() + " " + e.getCause() + " ExceptionIS: ");
			e.printStackTrace();
		}
		return null;
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
			FileReader reader = new FileReader(Constants.dbFilePath + table + ".root");
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
		dataSequence = new String[paraArr.length];
		for(String s : paraArr){
			String []temp = s.split("\\|");
			map.put(temp[0].trim(), temp[1].trim());
			dataSequence[i++] = temp[0].trim();
		}
	}
}
