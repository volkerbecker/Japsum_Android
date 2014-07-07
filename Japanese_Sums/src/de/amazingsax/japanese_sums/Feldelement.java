package de.amazingsax.japanese_sums;

/** 
Enthaelt konkreten und moegliche Werte fuer einen Eintrag im Spielfeld 
 */
public class Feldelement {
	
	
	public static void setMaxnumber(byte maxnumber) {
		_maxnumber=maxnumber;
		
	}
	/**
	 *standart Konstruktor, befor eine Instanz der Klasse erzeugt wird, muss der maximalwert 
	 * initialisiert sein.
	 */
	public Feldelement(){
		if(_maxnumber==0) {
			throw new IllegalStateException("Call setMaxnumber(byte maxnumber) before creating a Feldelement!");
		}
		possiblevalues = new boolean[_maxnumber+1]; // Feld fuer moegliche Werte erzeugen
		for(int z=0 ; z<=_maxnumber; ++z) {
			possiblevalues[z]=true;                              // am anfang ist nichts ausgeschlossen
		}
	}; // standard constructor
	
	/**
	 * ist das Feldelement schon fixiert?
	 * @return true wenn ja sonst false
	 */	
	public boolean isFixed() {
		return _isfixed;
	}
	/**
	 * Fragt ob ob Wert moeglich ist
	 * @param wert zu testender Wert
	 * @return wahr wenn moeglich sonst false
	 */
	public boolean isValuePossible(byte wert) {
		return possiblevalues[wert];
	}
	
	/**
	 * schließt bestimmten Wert fuer diese Zelle aus
	 * @param wert Wert der nicht mehr moeglich ist
	 */
	public void setValueImpossible(byte wert) {
		possiblevalues[wert]=false;
	}
	
	/**
	 * Setzt bestimmten Wert in Zelle 
	 * @param valueToSet Wert der gesetzt werden soll
	 */
	public void setFixedValue(byte valueToSet) {
		for(byte i=0;i<=_maxnumber;++i) {
			if(i!=valueToSet) {
				possiblevalues[i]=false;
			} else {
				possiblevalues[i]=true;
			}
		}
		value=valueToSet;
		_isfixed=true;
	}
	
	public byte getValue() {
		return value;
	}
	
	public boolean isValueAlreadyFixed(byte[] wert) {
		int numberOfposssibilities=0;
		byte lastnumber=0;
		for(byte j=0;j <= _maxnumber;++j) {
			if( isValuePossible(j) ) { 
				++numberOfposssibilities;
				lastnumber=j;
			}
		}
		switch (numberOfposssibilities) {
			case 0:
				throw new IllegalStateException("there is no solution");
				//break; // exeption werfen
			case 1:
				//this.setFixedValue(lastnumber);
				wert[0]=lastnumber;
				return true;
			default:
				return false;
		}
		//return false;
	}

	
	private byte value=0; // initial wird das Feld mit 0 belegt;
	private static int _maxnumber;
	private boolean _isfixed=false; 
	private boolean[] possiblevalues;
}
