package MainPackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import DataStructure.QueryParser;

public class QueryFirer {

	private static String input = ".";
	private static QueryParser qParser;
	
	public static void main(String[] args) throws IOException {
		
		BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
		qParser = new QueryParser();
		
		while(!"".equals(input)){
			System.out.print("RQL>>> ");
			input = bReader.readLine();
			qParser.init(input);
			//insert => Students(Id => 1 | Name => Ashish | RollNo => 20 | CGPA => 7.92);
			//insert => Students(Id => 2 | Name => Rucha Sawant | RollNo => 23 | CGPA => 9.3);
			//insert => Students(Id => 3 | Name => Amey | RollNo => 22 | CGPA => 8.5);
			//insert => Students(Id => 4 | Name => Purna | RollNo => 7 | CGPA => 7.2);
			//insert => Students(Id => 5 | Name => Pooja | RollNo => 4 | CGPA => 7);
			//insert => Students(Id => 6 | Name => Tanmayi | RollNo => 45 | CGPA => 7.5);
			//insert => Students(Id => 7 | Name => Atharva | RollNo => 50 | CGPA => 6.45);
			
			//create table Students (Id | int, Name | String, RollNo | int, CGPA | float);
			
			//select => Students(Id | Name | RollNo | CGPA);
			//select => Students( Id | Name | RollNo ) => where ( Id = 1 and Name = Ashish);
			
			//delete => Students(Id = 7);
		}
		
	}
	

}
