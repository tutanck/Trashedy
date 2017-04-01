package tools.services;

public interface ServiceCodes {
	/**
	 * STATUS CODES */
	int _ISSUE=-1;
	int _KANPEKI=0;
	
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
	int INVALID_USERNAME_FORMAT = -41;
	int INVALID_EMAIL_FORMAT = -42;
	int INVALID_PASS_FORMAT = -43;
	
	/*admin error*/
	int MRP_DOUBLING=-111;
	
}
