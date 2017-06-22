package com.aj.hxh.tools.general;

public enum InputType {
		EMAIL,
		USERNAME,
		PHONE,
		NUMS, 
		AWORD,
		UNKNOWN;
		
		@Override
		  public String toString() {
		    switch(this) {
		      case EMAIL: return "mail";
		      case USERNAME: return "uname";		
		      case PHONE: return "phone";
		      case NUMS: return "nums";
		      case AWORD: return "aword";
		      case UNKNOWN: return "unknown";
		      default: throw new IllegalArgumentException();
		    }
		  }
		
	}