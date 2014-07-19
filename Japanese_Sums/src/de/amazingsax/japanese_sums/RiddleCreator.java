package de.amazingsax.japanese_sums;

import java.util.ArrayList;

import android.app.ProgressDialog;



public class RiddleCreator extends Playfield {
	
	ProgressDialog progressOfcreating;

	public RiddleCreator(PlayfieldActivity _context,ProgressDialog progress,byte playfieldsize, byte maxnumber) {
		super(_context,playfieldsize, maxnumber);
		progressOfcreating = progress;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		createRiddle();
		progressOfcreating.dismiss();
	}
	
	public void createRiddle() {
		boolean riddleIsValid=false;
		do {
			deleteEntries();
			createEntries(); 		// Spielfeld f�llen
			if(calculateSumBlocks()) { 	// entsprechende Summen berechnen, wenn g�ltig
				deleteEntries(); 		// Spielfeld frei machen
				riddleIsValid=solve();  // L�sung auf eindeutigkeit und l�sbarkeit pr�fen 
			}
		} while(!riddleIsValid && !isInterrupted());
	}
	
	public byte[][] getSolution() {
		return getEntries();
	}
	
	// erstellt ein Spielfeld mit
	// zuf�lligen Eintr�gen
	// erwartet das spielfeld bei aufruf leer ist. 
	private void createEntries() {
		int[] zeilenOrder=randomOrder();
		for(byte i=0;i<playfieldsize;++i)
		{
			byte zeile=(byte)zeilenOrder[i];
			int[] spaltenOrder=randomOrder();
			byte testvalue;
			for(byte j=0;j<playfieldsize;++j) {
				byte spalte=(byte)spaltenOrder[j];
				do {
				   testvalue=LittleHelpers.myrandom(maxNumber);
				} while(!Feld[zeile][spalte].isValuePossible(testvalue));
				this.setFixedValueAt(zeile,spalte,testvalue);
				//this.displayPlayfield(); // fuers debuggen
			}
		}
		this.solveable=true; //Debugging
		this.solutionFound=true; // Debugging
	}
	
	//Bestimmt horzontale und Vertikale Summenbl�cke
	//erwartet ausgef�lltes spielfeld
	
	private boolean calculateSumBlocks() {
		
		hblocks = new ArrayList<byte[]>();
		vblocks = new ArrayList<byte[]>();

		final int maxSumNumber = playfieldsize; // Anzahl der maximalen Anzahl
		boolean retValue=true;
		
		// zuerst die Horizontalen
		for (int i = 0; i < playfieldsize; ++i) {
			byte[] summen = new byte[maxSumNumber];
			int blockNumber = 0;
			boolean wasLastNumberNotZero = false;
			for (int j = 0; j < playfieldsize; ++j) {
				if (Feld[i][j].getValue() != 0) {
					summen[blockNumber] += Feld[i][j].getValue();
					wasLastNumberNotZero = true;
				} else if (wasLastNumberNotZero) {
					++blockNumber;
					wasLastNumberNotZero = false;
				}
			}
			if (wasLastNumberNotZero) {
				blockNumber++;
			}
			if(blockNumber==0) {
				retValue=false;
			}
			byte[] uebergabe = new byte[blockNumber];
			for (int k = 0; k < blockNumber; ++k) {
				uebergabe[k] = summen[k];
			}
			hblocks.add(uebergabe);
		}
		
		// und dann die vertikalen Summen bestimmen

		for (int j = 0; j < playfieldsize; ++j) {
			byte[] summen = new byte[maxSumNumber];
			int blockNumber = 0;
			boolean wasLastNumberNotZero = false;
			for (int i = 0; i < playfieldsize; ++i) {
				if (Feld[i][j].getValue() != 0) {
					summen[blockNumber] += Feld[i][j].getValue();
					wasLastNumberNotZero = true;
				} else if (wasLastNumberNotZero) {
					++blockNumber;
					wasLastNumberNotZero = false;
				}
			}
			if (wasLastNumberNotZero) {
				blockNumber++;
			}
			if(blockNumber==0) {
				retValue=false;
			}
			byte[] uebergabe = new byte[blockNumber];
			for (int k = 0; k < blockNumber; ++k) {
				uebergabe[k] = summen[k];
			}
			vblocks.add(uebergabe);
		}
		return retValue;
	}
	
	private void deleteEntries() {
		for (byte i = 0; i < playfieldsize; ++i) {
			for (byte j = 0; j < playfieldsize; ++j) {
				Feld[i][j] = new Feldelement(); // alle Feldelemente
												// neu "erschaffen" alte sind ggf. Fall f�r den garbage collector
			}
		}
	}
	
	private int[] randomOrder() {
		int[] result = new int[playfieldsize];
		boolean[] used = new boolean[playfieldsize];
		for(int i=0;i<used.length;++i) {
			used[i]=false;
		}
		for(int i=0;i<result.length;++i) {
			int versuch;
			do{
				versuch = LittleHelpers.myrandom((byte) (playfieldsize-1));
			} while(used[versuch]);
			result[i]=versuch;
		}
		return result;
	}
	
	

}
