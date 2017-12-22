package com.vijay.mf.nav;

public class MutualFund {
	private Integer schemeCode;
	private String schemeName;
	private String nav;
	private String date;

	public Integer getSchemeCode() {
		return schemeCode;
	}

	public void setSchemeCode(Integer schemeCode) {
		this.schemeCode = schemeCode;
	}

	public String getSchemeName() {
		return schemeName;
	}

	public void setSchemeName(String schemeName) {
		this.schemeName = schemeName;
	}

	public String getNav() {
		return nav;
	}

	public void setNav(String nav) {
		this.nav = nav;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "MutualFund [schemeCode=" + schemeCode + ", schemeName=" + schemeName + ", nav=" + nav + ", date=" + date
				+ "]";
	}

}
