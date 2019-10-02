import java.util.Comparator;
import java.util.LinkedHashMap;

public class MyComparator implements Comparator<LinkedHashMap<String, String>>{

	@Override
	public int compare(LinkedHashMap<String, String> o1, LinkedHashMap<String, String> o2) {

		double score1 = Double.parseDouble(o1.get("score"));

		double score2 = Double.parseDouble(o2.get("score"));

		int count1 = Integer.parseInt(o1.get("count"));

		int count2 = Integer.parseInt(o2.get("count"));

		String where1 = o1.get("where");

		String where2 = o2.get("where");

		if(Double.compare(score1, score2) < 0) {

			return 1;

		}
		else if(Double.compare(score1, score2) == 0){

			if(Integer.compare(count1, count2) < 0) {

				return 1;
			}
			else if(Integer.compare(count1, count2) == 0){

				return Integer.compare(where1.length(), where2.length());
			}
			else {

				return -1;
			}

		}
		else {
			return -1;
		}

	}



}
