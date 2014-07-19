/**
 * 
 */
package de.amazingsax.japanese_sums;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @author sax Enthaelt das Spielfeld bestehend aus Summenbloecken und
 *         Feldelementen, stellt methoden zur Loesung bereit
 */
public class Playfield extends Thread {
	
	final static float empiricalMemoryFactor=(float)0.015; //abgeschaetzter Faktor zwischen Maximalem
												    //heap und anzahl der Permutationen
	
	private ProgressDialog progress;
	Handler handler;
	boolean solveable;
	boolean solutionFound;
	boolean isToComplex;
	PlayfieldActivity context;

	
	
	/**
	 * Komnstruktor, erschafft ein spielfeld
	 * 
	 * @param playfieldsize
	 *            grï¿½ï¿½e des Spielfeldes
	 * @param maxnumber
	 *            maximalwert der Eintraege
	 */
	public Playfield(PlayfieldActivity context,byte playfieldsize, byte maxnumber,
			ArrayList<byte[]> ihblocks, ArrayList<byte[]> ivblocks,
			ProgressDialog sprogress, Handler shandler) {
		super();
		
		this.context=context;
		handler = shandler;

		progress = sprogress;
		this.playfieldsize = playfieldsize;
		this.maxNumber = maxnumber;

		solveable = true;
		solutionFound = false;

		if (ihblocks.size() != playfieldsize
				|| ivblocks.size() != playfieldsize) {
			throw new IllegalArgumentException(
					"number of sum blogs must equal number of lines/collums");
		}
		Feldelement.setMaxnumber(maxnumber);
		Feld = new Feldelement[playfieldsize][playfieldsize];

		hblocks = ihblocks; // die uebergebenen Werte als Objektwerte nutzen
		vblocks = ivblocks;

		
		sumPermutations.setMaxNumber(this.maxNumber, this.playfieldsize);
		for (byte i = 0; i < playfieldsize; ++i) {
			for (byte j = 0; j < playfieldsize; ++j) {
				Feld[i][j] = new Feldelement(); // alle Feldelemente
												// "erschaffen"
			}
			
			// try {
			// vpertubations.add(new sumPermutations(vblocks.get(i))); // Die
			// moeglichen Permutationen vorberechnen
			// hpertubations.add(new sumPermutations(hblocks.get(i)));
			// }
			// catch (IllegalStateException e) {
			// solveable=false;
			// }
		}
	}
	
	public boolean isToComplex()  {
		return isToComplex; 
	}
	
	public ArrayList<byte[]> gethBlocks() {
		return hblocks;
	}
	
	public ArrayList<byte[]> getvBlocks() {
		return vblocks;
	}
	
	// Konstruktor für Spielfeld ohne eintraege, wird zur erschaffung eines neuen Spielfeldes genutzt
	public Playfield(PlayfieldActivity context,byte playfieldsize, byte maxnumber) {
		super();
		this.context=context;
		this.playfieldsize = playfieldsize;
		this.maxNumber = maxnumber;  // Spielfeldgroesse und Maximalwert setzen

		Feldelement.setMaxnumber(maxnumber);
		Feld = new Feldelement[playfieldsize][playfieldsize];

		
		sumPermutations.setMaxNumber(this.maxNumber, this.playfieldsize);
		for (byte i = 0; i < playfieldsize; ++i) {
			for (byte j = 0; j < playfieldsize; ++j) {
				Feld[i][j] = new Feldelement(); // alle Feldelemente
												// "erschaffen"
			}
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
		for (int i = 0; i < playfieldsize; ++i) {
			for (int j = 0; j < playfieldsize; ++j) {
				System.out.print(" "
						+ getFieldElement((byte) i, (byte) j).getValue());
				if (getFieldElement((byte) i, (byte) j).isFixed()) {
					System.out.print(".|");
				} else {
					System.out.print(" |");
				}
			}
			System.out.println("");
			for (int j = 0; j < playfieldsize; ++j) {
				System.out.print("----");
			}
			System.out.println("");
		}
		System.out.println("");
		System.out.println("");
	}

	public Feldelement getFieldElement(byte zeile, byte spalte) {
		return Feld[zeile][spalte];
	}

	public void setFixedValueAt(byte zeile, byte spalte, byte value) {
		if (value != 0) {
			for (byte i = 0; i < playfieldsize; ++i) {
				Feld[zeile][i].setValueImpossible(value);
				Feld[i][spalte].setValueImpossible(value);
			} // verbiete den wert in der gesamten Zeile und Spalte
		}
		Feld[zeile][spalte].setFixedValue(value); // Wert in der Zelle setzen,
													// Wert in dieser Zelle
													// wieder erlauben
		if(progress!=null) {
			progress.incrementProgressBy(1);
		}
	}

	public boolean tryLines(boolean hv, int line,ArrayList<sumPermutations> hpertubations, ArrayList<sumPermutations> vpertubations) {
		sumPermutations lineToTest;
		boolean retvalue = false;

		ArrayList<boolean[]> inThislinepossible = new ArrayList<boolean[]>();
		for (int i = 0; i < playfieldsize; ++i) {
			inThislinepossible.add(new boolean[maxNumber + 1]);
		}

		if (hv)
			lineToTest = hpertubations.get(line);
		else
			lineToTest = vpertubations.get(line);

		byte[] recentpermutation = new byte[playfieldsize];

		int zeile = 0;
		int spalte = 0;
		while (lineToTest.getnextpermuation(recentpermutation)) {
			for (int i = 0; i < playfieldsize; ++i) {
				if (hv) {
					zeile = line;
					spalte = i;
				} else {
					spalte = line;
					zeile = i;
				}

				if (!Feld[zeile][spalte].isValuePossible(recentpermutation[i])) {
					lineToTest.removeRecentPermutation();
					retvalue = true;
					break;
				} else {
					inThislinepossible.get(i)[recentpermutation[i]] = true;
				}
			}
		}

		for (int i = 0; i < playfieldsize; ++i) {
			for (byte j = 0; j <= maxNumber; ++j) {
				if (!inThislinepossible.get(i)[j]) {
					if (hv) {
						zeile = line;
						spalte = i;
					} else {
						spalte = line;
						zeile = i;
					}
					if (Feld[zeile][spalte].isValuePossible(j)) {
						// if(zeile==)
						retvalue = true;
						Feld[zeile][spalte].setValueImpossible(j);
						byte[] wert = new byte[1];
						if (Feld[zeile][spalte].isValueAlreadyFixed(wert)) {
							this.setFixedValueAt((byte) zeile, (byte) spalte,
									wert[0]);
						}
					}

				}
			}
		}

		return retvalue;
	}

	public boolean solve() throws IllegalStateException {
		ArrayList<sumPermutations> hpertubations = new ArrayList<sumPermutations>();
		ArrayList<sumPermutations> vpertubations = new ArrayList<sumPermutations>();
		boolean somthinChanges = false;
		int numberOFpermutations=0;
		Runtime rt = Runtime.getRuntime();
		long maxMemory = rt.maxMemory();
		
		//sumPermutations.resetOverAllNumberOfPermutations();

		vpertubations.clear();
		hpertubations.clear();
		if(progress!=null) {
			handler.post( new Runnable() {

				@Override
				public void run() {
					context.progressdialog.setMessage(context.getResources().getString(
							R.string.progressPermut));
				}
			}
			);
				progress.setMax(2*playfieldsize);
				progress.setProgress(0);
		}
		
				
		for (byte i = 0; i < playfieldsize; ++i) {
			if(isInterrupted()) return false;
			sumPermutations tmpsumpermutation=new sumPermutations(vblocks.get(i));
			numberOFpermutations+=tmpsumpermutation.determineNumberOfpermurtations();
			vpertubations.add(tmpsumpermutation);
			if(isInterrupted()) return false;
			tmpsumpermutation=new sumPermutations(hblocks.get(i));
			numberOFpermutations+=tmpsumpermutation.determineNumberOfpermurtations();
			hpertubations.add(tmpsumpermutation);
		} //komplexitaet abschaetzen
		Log.d("amazing","permutations: "+numberOFpermutations);
		
		
		
		if(maxMemory*empiricalMemoryFactor<numberOFpermutations) {
			isToComplex=true;
			return false;
		} else isToComplex=false;
		
		
		
		for (byte i = 0; i < playfieldsize; ++i) {
			if(isInterrupted()) return false;
			vpertubations.get(i).calculateAllPermutyations();
			if(progress!=null) {
				progress.incrementProgressBy(1);
			}
			if(isInterrupted()) return false;
			hpertubations.get(i).calculateAllPermutyations();
			if(progress!=null) {
				progress.incrementProgressBy(1);
			}
		}
		
		
		
		if(progress!=null) {
			handler.post( new Runnable() {

				@Override
				public void run() {
					context.progressdialog.setMessage(context.getResources().getString(
							R.string.progressdialog));
				}
			}
			);
	
			progress.setProgress(0);
			progress.setMax(playfieldsize*playfieldsize);
		}

		do {
			somthinChanges = false;
			for (int i = 0; i < playfieldsize; ++i) {
				if(isInterrupted()) return false;
				somthinChanges |= tryLines(true, i,hpertubations,vpertubations);
				// displayPlayfield();
				if(isInterrupted()) return false;
				somthinChanges |= tryLines(false, i,hpertubations,vpertubations);
				// displayPlayfield();
			}
			// displayPlayfield();

			for (int i = 0; i < playfieldsize; ++i) {
				for (int j = 0; j < playfieldsize; ++j) {
					byte[] wert = new byte[1];
					if (!Feld[i][j].isFixed()) {
						if (Feld[i][j].isValueAlreadyFixed(wert)) {
							this.setFixedValueAt((byte) i, (byte) j, wert[0]);
							somthinChanges = true;
						}
					}
				}
			}
			// displayPlayfield();
		} while (somthinChanges);
		boolean retalue = true;
		for (int i = 0; i < playfieldsize; ++i) {
			for (int j = 0; j < playfieldsize; ++j) {
				retalue &= Feld[i][j].isFixed();
			}
		}
		return retalue;
	}

	public byte[][] getEntries() {
		byte[][] result = new byte[playfieldsize][playfieldsize];
		for (int i = 0; i < playfieldsize; ++i) {
			for (int j = 0; j < playfieldsize; ++j) {
				result[i][j] = Feld[i][j].getValue();
			}
		}
		return result;
	}

	protected byte playfieldsize;
	protected byte maxNumber;
	protected Feldelement[][] Feld; // Array of fieldelements

	protected ArrayList<byte[]> hblocks;
	protected ArrayList<byte[]> vblocks;

	

	// private summen // hier bloecke fuer die Summen einfuegen.

}
