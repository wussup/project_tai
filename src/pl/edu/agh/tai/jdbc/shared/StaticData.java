package pl.edu.agh.tai.jdbc.shared;

public final class StaticData {
	
	private static String  APP_KEY = "apm3dfhibte9645";
	private static String APP_SECRET = "unq7i41xib8rnto";
	private static String PROJECT_NAME ="TAI2013";

	public static String getPROJECT_NAME() {
		return PROJECT_NAME;
	}

	public static void setPROJECT_NAME(String pROJECT_NAME) {
		PROJECT_NAME = pROJECT_NAME;
	}

	public static String getAPP_SECRET() {
		return APP_SECRET;
	}

	public static void setAPP_SECRET(String aPP_SECRET) {
		APP_SECRET = aPP_SECRET;
	}

	public static String getAPP_KEY() {
		return APP_KEY;
	}

	public static void setAPP_KEY(String aPP_KEY) {
		APP_KEY = aPP_KEY;
	}

}
