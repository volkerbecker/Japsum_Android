/**
 * 
 */
package de.amazingsax.japanese_sums;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;

/**
 * @author sax
 * Enthaelt das Spielfeld bestehend aus Summenbloecken und Feldelementen, stellt methoden zur Loesung bereit
 */
public class Playfield extends Thread {
	ProgressDialog progress;
	Handler handler;
	boolean solveable;
	boolean solutionFound;

	/**
	 * Komnstruktor, erschafft ein spielfeld
	 * @param playfieldsize gr��e des Spielfeldes
	 * @param maxnumber maximalwert der Eintraege
	 */
	public Playfield(byte playfieldsize,byte maxnumber,ArrayList<byte[] > ihblocks, ArrayList<byte[] > ivblocks,ProgressDialog sprogress,Handler shandler) {
		super();
		
		handler=shandler;
				
		progress=sprogress;
		this.playfieldsize=playfieldsize;
		this.maxnumber=maxnumber;
		
		solveable=true;
		solutionFound=false;
		
		if(ihblocks.size()!=playfieldsize || ivblocks.size()!=playfieldsize) {
			throw new IllegalArgumentException("number of sum blogs must equal number of lines/collums");
		}
		Feldelement.setMaxnumber(maxnumber);
		Feld = new Feldelement[playfieldsize][playfieldsize];
		
		hblocks=ihblocks; //die uebergebenen Werte als Objektwerte nutzen
		vblocks=ivblocks;
		
		ihblocks=null;
		ivblocks=null; // zugriff auf die Objektwerte von au�en unmoeglich machen
		sumPermutations.setMaxNumber(this.maxnumber, this.playfieldsize);
		for(byte i=0;i<playfieldsize;++i) {
			for(byte j=0;j<playfieldsize;++j) {
				Feld[i][j]=new Feldelement(); // alle Feldelemente "erschaffen"
			}
			//try {
			//vpertubations.add(new sumPermutations(vblocks.get(i))); // Die moeglichen Permutationen vorberechnen
			//hpertubations.add(new sumPermutations(hblocks.get(i)));
			//} 
			//catch (IllegalStateException e) {
			//	solveable=false;
			//}
		}  
	}
	
	
	@Override
	public void run() {		
		try {
			solutionFound = solve();
		} catch (IllegalStateException e) {
			solveable = false;
		}
		progress.dismiss();
		handler.sendMessage(new Message());
	}
	
	public boolean isSolveable() {
		return solveable;
	}
	
	public boolean isSolved() {
		return solutionFound;
	}
	
	public void displayPlayfield() {
		for(int i=0;i<playfieldsize;++i) { 
			for(int j=0;j<playfieldsize;++j) {
				System.out.print(" "+getFieldElement((byte)i, (byte)j).getValue());
				if(getFieldElement((byte)i, (byte)j).isFixed()) {
					System.out.print(".|");
				}
				else{
					System.out.print(" |");
				}
			}
			System.out.println("");
			for(int j=0;j<playfieldsize;++j) {
				System.out.print("----");
			}
			System.out.println("");
		}
		System.out.println("");
		System.out.println("");
	}
	
	public Feldelement getFieldElement(byte zeile,byte spalte) {
		return Feld[zeile][spalte];
	}
	
	public void setFixedValueAt(byte zeile,byte spalte,byte value) {
		if(value !=0) {
			for(byte i=0;i<playfieldsize;++i) {
				Feld[zeile][i].setValueImpossible(value);
				Feld[i][spalte].setValueImpossible(value);
			} //verbiete den wert in der gesamten Zeile und Spalte
		}
		Feld[zeile][spalte].setFixedValue(value); // Wert in der Zelle setzen, Wert in dieser Zelle wieder erlauben
		progress.incrementProgressBy(1);
	}
	
	public boolean tryLines(boolean hv,int line) {
		sumPermutations lineToTest;
		boolean retvalue=false;
		
		ArrayList<boolean[]> inThislinepossible=new ArrayList<boolean[]>();
		for(int i=0;i<playfieldsize;++i) {
			inThislinepossible.add(new boolean[maxnumber+1]);
		}
		
		if(hv)
			lineToTest = hpertubations.get(line);
		else
			lineToTest = vpertubations.get(line);
		
		byte[] recentpermutation= new byte[playfieldsize];
		
		int zeile=0;
		int spalte=0;
		while(lineToTest.getnextpermuation(recentpermutation)) {
			for(int i=0;i<playfieldsize;++i) {
				if(hv) {
					zeile=line;
					spalte=i;
					} else {
						spalte=line;
						zeile=i;
					}
				
				if(!Feld[zeile][spalte].isValuePossible(recentpermutation[i])){
					lineToTest.removeRecentPermutation();
					retvalue=true;
					break;
				} else {
					inThislinepossible.get(i)[recentpermutation[i]]=true;
				}	
			}
		}
		
		for(int i=0;i<playfieldsize;++i) {
			for(byte j=0;j<=maxnumber;++j) {
				if(!inThislinepossible.get(i)[j]) {
					if(hv) {
						zeile=line;
						spalte=i;
						} else {
							spalte=line;
							zeile=i;
						}
					if(Feld[zeile][spalte].isValuePossible(j)){
						//if(zeile==)
						retvalue = true;
						Feld[zeile][spalte].setValueImpossible(j);
						byte[] wert = new byte[1];
						if(Feld[zeile][spalte].isValueAlreadyFixed(wert) ) {
							this.setFixedValueAt((byte)zeile, (byte)spalte, wert[0]);
						}
					}
					
				}
			}
		}
				
	return retvalue;	
	}
	
	public boolean solve() throws IllegalStateException {
		boolean somthinChanges=false;
		
		
		vpertubations.clear();
		hpertubations.clear();
		for(byte i=0;i<playfieldsize;++i) {
			vpertubations.add(new sumPermutations(vblocks.get(i))); // Die moeglichen Permutationen vorberechnen
			hpertubations.add(new sumPermutations(hblocks.get(i)));
		} // Die Vorberechnung der Summen aus dem Konstruktor hierher verlagert, da sonst der Hautpthread blockiert wird.
		
		do{
			somthinChanges=false;
			for(int i=0;i<playfieldsize;++i) {
				somthinChanges |= tryLines(true,i);
			//	displayPlayfield();
				somthinChanges |= tryLines(false,i);
			//	displayPlayfield();
			}
			//displayPlayfield();
		
			for(int i=0;i<playfieldsize;++i) {
				for(int j=0;j<playfieldsize;++j) {
					byte[] wert= new byte[1];
					if(!Feld[i][j].isFixed()) {
						if(Feld[i][j].isValueAlreadyFixed(wert) ) {
							this.setFixedValueAt((byte)i, (byte)j, wert[0]);
							somthinChanges=true;
						}
					}
				}
			}
			//displayPlayfield();
		} while(somthinChanges);
		boolean retalue=true;
		for(int i=0;i<playfieldsize;++i) {
			for(int j=0;j<playfieldsize;++j) {
				retalue &= Feld[i][j].isFixed();
			}
		}
		return retalue;
	}
	
	public byte[][] getEntries()
	{
		byte[][] result = new byte[playfieldsize][playfieldsize];
		for(int i=0;i<playfieldsize;++i) {
			for(int j=0;j<playfieldsize;++j) {
				result[i][j]=Feld[i][j].getValue();
			}
		}
		return result;
	}
	
	
	private byte playfieldsize,maxnumber;
	private Feldelement[][] Feld; // Array of fieldelements
	
	private ArrayList<byte[]> hblocks;
	private ArrayList<byte[]> vblocks;
	
	private ArrayList<sumPermutations> hpertubations=new ArrayList<sumPermutations>();
	private ArrayList<sumPermutations> vpertubations=new ArrayList<sumPermutations>();
	
	
//	private summen // hier bloecke fuer die Summen einfuegen. 

}
