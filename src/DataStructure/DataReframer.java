package DataStructure;

public class DataReframer {
	
	public static String hexToData(String hexData){
		
		StringBuffer result = new StringBuffer();
		char[] charArray = hexData.toCharArray();
	    for(int i = 0; i < charArray.length; i=i+2) {
	        String st = ""+charArray[i]+""+charArray[i+1];
	        char ch = (char)Integer.parseInt(st, 16);
	        result.append(ch);
	    }
	    return result.toString();
		
	}
	
	public static String dataToHex(String data){
		
		StringBuffer sb = new StringBuffer();
	    char ch[] = data.toCharArray();
	    for(int i = 0; i < ch.length; i++) {
	    	String hexString = Integer.toHexString(ch[i]);
	        sb.append(hexString);
	    }
	    return sb.toString();
		
	}
	
}
