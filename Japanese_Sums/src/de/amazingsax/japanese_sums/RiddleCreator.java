package de.amazingsax.japanese_sums;

import java.util.ArrayList;


public class RiddleCreator extends Playfield {

	public RiddleCreator(PlayfieldActivity _context,byte playfieldsize, byte maxnumber) {
		super(_context,playfieldsize, maxnumber);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
	}
	
	// erstellt ein Spielfeld mit
	// zufälligen Einträgen
	// erwartet das spielfeld bei aufruf leer ist. 
	public void createEntries() {
		for(byte i=0;i<playfieldsize;++i)
		{
			byte testvalue;
			for(byte j=0;j<playfieldsize;++j) {
				do {
				   testvalue=LittleHelpers.myrandom(maxNumber);
				} while(!Feld[i][j].isValuePossible(testvalue));
				this.setFixedValueAt(i,j,testvalue);
				//this.displayPlayfield(); // fuers debuggen
			}
		}
		this.solveable=true; //Debugging
		this.solutionFound=true; // Debugging
	}
	
	//Bestimmt horzontale und Vertikale Summenblöcke
	//erwartet ausgefülltes spielfeld
	
	public boolean calculateSumBlocks() {
		
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
			byte[] uebergabe = new byte[blockNumber];
			for (int k = 0; k < blockNumber; ++k) {
				uebergabe[k] = summen[k];
			}
			vblocks.add(uebergabe);
		}
		return retValue;
	}
	
	public void deleteEntries() {
		for (byte i = 0; i < playfieldsize; ++i) {
			for (byte j = 0; j < playfieldsize; ++j) {
				Feld[i][j] = new Feldelement(); // alle Feldelemente
												// neu "erschaffen" alte sind ggf. Fall für den garbage collector
			}
		}
	}
	
	

}
