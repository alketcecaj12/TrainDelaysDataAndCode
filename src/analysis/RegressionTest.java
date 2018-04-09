package analysis;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.regression.SimpleRegression;
public class RegressionTest {
	
	public static void main (String [] args) throws Exception {
		
		
		String file = ""; 
		
		double[][] data = {  {2, 5 }, {3, 7 }, {5, 11 }, {6, 5},{ 1, 3 }, {4, 14 }};
		SimpleRegression regression = new SimpleRegression();
		regression.addData(data);
		double pred_val = regression.predict(10);
		double tot_sum_sq = regression.getTotalSumSquares();
		System.out.println(tot_sum_sq);
		System.out.println(pred_val);
	}

}
