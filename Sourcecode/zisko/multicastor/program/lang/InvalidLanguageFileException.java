package zisko.multicastor.program.lang;

@SuppressWarnings("serial")
public class InvalidLanguageFileException extends Exception {
	private String errorKey;
	private int errorIndex;
	private String[] keys;
	
	public InvalidLanguageFileException(int errorIndex,String errorKey,String[] keys){
		this.errorIndex=errorIndex;
		this.keys=keys;
		this.errorKey=errorKey;
	}

	public String getErrorKey() {
		return errorKey;
	}

	public int getErrorIndex() {
		return errorIndex;
	}

	public String[] getKeys() {
		return keys;
	}

	
}
