package com.palmergames.bukkit.TownyChat.channels;



public class channelFormats {

	/**
	 * Constructor
	 * 
	 * @param name
	 */
	public channelFormats(String name) {
		super();
		this.name = name.toLowerCase();
	}

	private String name, GLOBAL, TOWN, NATION, COALITION, DEFAULT;

	/**
	 * @return a clone of this channelFormats
	 */
	public channelFormats clone(String name) {
		
		channelFormats clone = new channelFormats(name.toLowerCase());
		
		clone.setGLOBAL(this.getGLOBAL());
		clone.setTOWN(this.getTOWN());
		clone.setNATION(this.getNATION());
		clone.setCOALITION(this.getCOALITION());
		clone.setDEFAULT(this.getDEFAULT());
		
		return clone;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *						the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the gLOBAL
	 */
	public String getGLOBAL() {
		return GLOBAL;
	}

	/**
	 * @param GLOBAL
	 *						the gLOBAL to set
	 */
	public void setGLOBAL(String GLOBAL) {
		this.GLOBAL = GLOBAL;
	}

	/**
	 * @return the TOWN
	 */
	public String getTOWN() {
		return this.TOWN;
	}

	/**
	 * @param TOWN
	 *						the TOWN to set
	 */
	public void setTOWN(String TOWN) {
		this.TOWN = TOWN;
	}

	/**
	 * @return the NATION
	 */
	public String getNATION() {
		return this.NATION;
	}

	/**
	 * @param NATION
	 *						the NATION to set
	 */
	public void setNATION(String NATION) {
		this.NATION = NATION;
	}
	
	/**
	 * @return the COALITION
	 */
	public String getCOALITION() {
		return this.COALITION;
	}

	/**

	 * @param COALITION
	 *						the COALITION to set
	 */
	public void setCOALITION(String COALITION) {
		this.COALITION = COALITION;
	}

	/**
	 * @return the DEFAULT
	 */
	public String getDEFAULT() {
		return this.DEFAULT;
	}

	/**
	 * @param DEFAULT
	 *						the dEFAULT to set
	 */
	public void setDEFAULT(String DEFAULT) {
		this.DEFAULT = DEFAULT;
	}
}
