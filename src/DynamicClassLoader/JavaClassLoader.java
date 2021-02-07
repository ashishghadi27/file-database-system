package DynamicClassLoader;


public class JavaClassLoader extends ClassLoader {
	
	public Class<?> loadMyClass(String classBinName){
        
        try {
             
            // Create a new JavaClassLoader 
            ClassLoader classLoader = this.getClass().getClassLoader();
             
            // Load the target class using its binary name
            Class<?> loadedMyClass = classLoader.loadClass(classBinName);
            return loadedMyClass;
            
 
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
         
    }
	
	
}