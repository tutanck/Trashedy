package tproject.tools.services;

public interface ServiceCodes {
	
	/**
	 * BUSINESS ERRORS CODES -** */
	
	/* unavailable resources -1* */
	int USERNAME_IS_TAKEN=-11;
	int EMAIL_IS_TAKEN = -12;
	int PHONE_IS_TAKEN = -13;
	
	/* unknown resources -2* */
	int UNKNOWN_RESOURCE = -20;
	int UNKNOWN_EMAIL_ADDRESS=-21;
	int WRONG_LOGIN_PASSWORD=-22;
	int UNKNOWN_USER = -23;
	
	/*bad context -3* */
	int USER_NOT_CONFIRMED = -33;
	
	/* invalid formats -4* */
	
	/* existing relationships -5* */
	int EXISTING_RESOURCE = -50;
	int EXISTING_FOLLOW_REL = -51;
	int EXISTING_USER_GROUP_NAME = -52;
	
	/*admin error*/
	int MRP_DOUBLING=-111;
	int ALERT_NOT_SENT=-112;

	
}
