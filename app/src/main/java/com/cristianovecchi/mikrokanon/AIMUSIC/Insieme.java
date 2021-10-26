package com.cristianovecchi.mikrokanon.AIMUSIC;
/**
 *
 * @author Cristiano Vecchi
 */
// Altro approccio: Oggetto Insieme ( set di note ), e tutte le azioni che si possono fare su un set di note

import androidx.compose.ui.text.android.animation.SegmentType;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
public class Insieme {
    int insieme;
    static String[] arrayNote;
    public static final int[] Armoniche = {2048,8,128,4,512,2,64,1024,32,1,256,16,2048,8,128,4,512,2,64,1024,32,1,256,16};
    public static final int[] ArmonicheRetr = {16,256,1,32,1024,64,2,512,4,128,8,2048,16,256,1,32,1024,64,2,512,4,128,8,2048};
    //Costruttori
    public Insieme() {  //crea un insieme vuoto
        insieme = 0;
    }
    public Insieme(int ins) {
        insieme = ins;
    }



    // Azioni sull'insieme
    public void set(int ins) {
        insieme = ins;
    }
    public int get() {
        return insieme;
    }
    // Metodo Set+Get Object: setta l'insieme e restituisce se stesso come oggetto Insieme: es insieme.sg(151).trasponi(5) ;
    public Insieme sg(int ins) {
        set(ins);
        return this;
    }

    public boolean isEmpty() {
        if (insieme == 0) return true;
        return false;
    }
    public static boolean isEmpty(int numeroDaControllare) {
        if (numeroDaControllare == 0) return true;
        return false;
    }

    public void trasponi() {
        insieme = insieme * 2;
        if (insieme > 4095) insieme = insieme -4095; //Togli il bit pi� a sinistra e riaggiungilo a destra
    }
    public static int trasponiDiUno(int numeroDaTrasporre) {
        numeroDaTrasporre = numeroDaTrasporre * 2;
        if (numeroDaTrasporre > 4095) numeroDaTrasporre = numeroDaTrasporre - 4095;
        return numeroDaTrasporre;
    }

    public void trasponi(int intervallo) {
        if (intervallo == 0) return;
        while (intervallo < 0)  intervallo = intervallo + 12;
        while (intervallo > 11) intervallo = intervallo - 12;

        for( int i=0; i<intervallo; i++) trasponi();
    }
    public static int trasponi(int numDaTrasp, int intervallo) {
        if (intervallo == 0) return numDaTrasp;
        while (intervallo < 0)  intervallo = intervallo + 12;
        while (intervallo > 11) intervallo = intervallo - 12;

        for( int i=0; i<intervallo; i++) numDaTrasp=trasponiDiUno(numDaTrasp);
        return numDaTrasp;
    }

    public static int notaInRange(int notaDaControllare, int limMin, int limMax) {
        while (notaDaControllare < limMin) notaDaControllare = notaDaControllare + 12;
        while (notaDaControllare > limMax) notaDaControllare = notaDaControllare - 12;
        return notaDaControllare;
    }



    // Riceve un insieme di Note ( 12 Bit ) e lo trasforma in un Vector con i nomi delle note ( IN ORDINE RETROGRADO!!! )
    public Vector listaNoteRetr() {
        Vector listaNote = new Vector();
        int j = 2048;
        for (int i=11; i>-1; i--) {
            if ((insieme & j) !=0) listaNote.add(arrayNote[i]);
            j = j >> 1;
        }
        return listaNote;
    }
    public static Vector listaNoteRetr(int numero) {
        Vector listaNote = new Vector();
        int j = 2048;
        for (int i=11; i>-1; i--) {
            if ((numero & j) !=0) listaNote.add(arrayNote[i]);
            j = j >> 1;
        }
        return listaNote;
    }

    public static Vector listaNote(int numero) {
        Vector listaNote = new Vector();
        int j = 1;
        for (int i=0; i<12; i++) {
            if ((numero & j) !=0) listaNote.add(arrayNote[i]);
            j = j << 1;
        }
        return listaNote;
    }

    // Scegli l'arrayNote di riferimento (note, simboli, etc...)
    public static void setArrayNote(String[] arrayNoteChange) {
        arrayNote = arrayNoteChange;
    }

    public int contaNote() {
        int numNote = 0;
        int j = 1;
        for (int i=0; i<12; i++) {
            if ((insieme & j) !=0) numNote++;
            j = j << 1;
        }
        return numNote;
    }
    public static int contaNote(int numeroConNote) {
        int numNote = 0;
        int j = 1;
        for (int i=0; i<12; i++) {
            if ((numeroConNote & j) !=0) numNote++;
            j = j << 1;
        }
        return numNote;
    }


    // Metodi di aggiunta e sottrazione note che ricevono sia un altro oggetto Insieme sia un int
    public void aggiungi(int noteDaAggiungere) {
        insieme = insieme | noteDaAggiungere;
    }
    public void aggiungi(Insieme noteDaAggiungere) {
        insieme = insieme | ( noteDaAggiungere.get() );
    }
    public void sottrai(int noteDaSottrarre) {
        int vereNoteDaSottrarre = insieme & noteDaSottrarre; // Toglie le note che gi� non ci sono
        insieme = insieme ^ vereNoteDaSottrarre; //Controllare se XOR funziona e come
    }
    public void sottrai(Insieme noteDaSottrarre) {
        int vereNoteDaSottrarre = insieme & (noteDaSottrarre.get());
        insieme = insieme ^ vereNoteDaSottrarre; //Controllare se XOR funziona e come
    }
    public static int aggiungi(int numIniz, int noteDaAgg) {
        numIniz = numIniz | noteDaAgg;
        return numIniz;
    }
    public static int sottrai(int numIniz, int noteDaSottr) {
        int vereNoteDaSottrarre = numIniz & noteDaSottr;
        numIniz = numIniz ^ vereNoteDaSottrarre;
        return numIniz;
    }




    public void inverti(int notaPerno) {
    }
    public void negativo() {
        //insieme = (!insieme) & 2147483647 ; //controllare questo numero che deve togliere gli ultimi bit all'int
    }

    // ricerca una o pi� fondamentali di un insieme
    public int fond() {
        int fondamental=0; //DA CAMBIARE!!!!
        // Algoritmo di ricerca di una o pi� fondamentali:
        //si fa dare un array di 12 int da calcArm e valuta quali sono i pi� alti
        //aggiungendoli in un unico int
        return fondamental;
    }
    // calcola il valore armonico di ogni nota, anche delle assenti
    public int[] calcArm() {
        return new int[1];//DA CAMBIARE!!!!!
    }

    // analisi() analizza gli intervalli di un insieme e restituisce
    // un array di 6 intervalli con le loro quantit�.
    public int[] analisi() {
        return new int[1];//DA CAMBIARE!!!
    }


    // confronta due int (a bit singolo!) e stabilisci se l'intervallo � presente in un set di intervalli;
    // se c'� ritorna l'intervallo, se no ritorna -1(confronto impossibile) o 0 (intervallo non presente)


    public static int isInSet( int nota1, int nota2, int setIntervalli) {

        int intervallo = interv(nota1,nota2);
        if (intervallo == -1) return -1;
        int powi = 1;
        int[] arrayInt = {1, 3, 5 ,9 ,17 ,33 ,65};
        for(int i = 0; i<7; i++) {
            if (((powi & setIntervalli)!=0) && (intervallo == arrayInt[i]))
                return intervallo;
            powi = powi << 1;
        }
        return 0;
    }

    public static Integer[] TREND_ASCENDANT_DYNAMIC = {1,2,11,10,3,9,4,8,5,7,6,0};
    public static Integer[] TREND_DESCENDANT_DYNAMIC = {11,10,1,2,9,3,8,4,7,5,6,0};
    public static Integer[] TREND_ASCENDANT_STATIC = {0,1,2,11,10,3,9,4,8,5,7,6};
    public static Integer[] TREND_DESCENDANT_STATIC = {0,11,10,1,2,9,3,8,4,7,5,6};
    public static Integer[] extractDirectionsFromIntervalSet(Integer[] intervalSet, Integer[] trend){
        Vector list = new Vector();
        Integer[] dirs = trend;
        for (int j = 0; j < trend.length; j++) {
            for (int i = 0; i < intervalSet.length; i++) {
                int interval = intervalSet[i];
                    if(intervalSet[i].equals(dirs[j])) {
                        list.add(interval);
                        break;
                    }
                }
        }
        return (Integer[]) list.toArray(new Integer[0]);
    }

    public static boolean isIntervalInSet(int[] intervalSet, int pitch1, int pitch2){
        int interval = Math.abs(pitch2 - pitch1);
        for (int i : intervalSet){
            if (interval == i) return true;
        }
        return false;
    }
    @NotNull
    public static Integer[] getPossibleAbsPitches(@NotNull int[] otherPitches, @NotNull int[] intervalSet) {
        Set<Integer> set = new LinkedHashSet<>();
        check: for (int i=0 ; i < 12 ; i++){
            for (int otherPitch : otherPitches) {
                if (!isIntervalInSet(intervalSet, otherPitch, i)) {
                    continue check;
                }
            }
            set.add(i);
        }
        return  (Integer[]) set.toArray(new Integer[0]);
    }
    @NotNull
    public static Integer[] orderAbsPitchesByTrend(@NotNull Integer[] pitches, @NotNull int start, Integer[] trend) {
        Vector<Integer> list = new Vector<>();
        int newStart = start;
       int count = 0;
       while (count < pitches.length){

           check:   for (Integer interval : trend) {
               for (int j = 0; j < pitches.length; j++) {
                   Integer note = pitches[j];
                   if(note == null) continue;
                   if (Math.abs(newStart - note) == interval) {
                       list.add(note);
                       newStart = note;
                       pitches[j] = null;
                       count++;
                       break check;
                   }
               }
           }
           count++;
       }

        return  (Integer[]) list.toArray(new Integer[0]);
    }
    // NOT -1 allowed
    public static int[] invertAbsPitches(int[] pitches){
        if(pitches.length < 2) return pitches;
        int[] result = new int[pitches.length];
        int pivot = pitches[0];
        result[0] = pivot;
        for(int i = 1; i<pitches.length; i++){

            int interval = (pitches[i]-pivot) * -1;
            result[i] = pivot + interval;
            if(result[i] > 11) result[i] -= 12;
            if(result[i] < 0) result[i] += 12;
        }
        return result;
    }

    // ritorna true se la direzione � giusta
    public static boolean checkDirezioneArmonica(int nota1, int nota2){
        if ((contaNote(nota1)!=1) || (contaNote(nota2)!=1)) return false;
        if (nota1 == nota2) return true; //Intervallo di 8a-unisono
        int interv = aggiungi(nota1,nota2);
        while ( (interv & 1) != 1){
            interv = interv >>1;
        }
        if(nota1 < nota2){
            switch (interv) {
                case 3: return true;
                case 5: return true ;
                case 9: return false;
                case 17: return false;
                case 33: return true;
                case 65: return true;
                case 129: return false ;
                case 257: return true;
                case 513: return true;
                case 1025: return false ;
                case 2049: return false;
            }
        } else {
            switch (interv) {
                case 3: return false;
                case 5: return false ;
                case 9: return true;
                case 17: return true;
                case 33: return false;
                case 65: return true;
                case 129: return true ;
                case 257: return false;
                case 513: return false;
                case 1025: return true ;
                case 2049: return true;
            }
        }



        return false;
    }

    // interv(nota1, nota2) trova l'intervallo tra due note nella sua forma base(1, 3, 5, 9, 17, 33, 65)
    // ritorna -1 se i parametri non sono note singole o sono 0
    public static int interv( int nota1, int nota2) {
        //controlla che siano note singole

        if ((contaNote(nota1)!=1) || (contaNote(nota2)!=1)) return -1;
        if (nota1 == nota2) return 1; //Intervallo di 8a-unisono
        nota1 = aggiungi(nota1,nota2);
        int intervallo=0;
        while(intervallo==0) {
            switch (nota1) {
                case 3: intervallo = 3; break;
                case 5: intervallo = 5; break;
                case 9: intervallo = 9; break;
                case 17: intervallo = 17; break;
                case 33: intervallo = 33; break;
                case 65: intervallo = 65; break;
                case 129: intervallo = 33; break;
                case 257: intervallo = 17; break;
                case 513: intervallo = 9; break;
                case 1025: intervallo = 5; break;
                case 2049: intervallo = 3; break;

            }
            nota1 = trasponiDiUno(nota1);
        }
        return intervallo;
    }
    // riceve un array di 7 boolean e costruisce un numero che rappresenta il set di intervalli utilizzati
    public static int costruisciSetIntervalli(boolean[] opzioni) {
        int set = 0;
        for(int i=0; i<7; i++) {
            if (opzioni[i]==true) set = set + ((int)(Math.pow(2.0,(double)i)));
        }

        return set;
    }

    // riceve due valori di note e restituisce l'ottava pi� semplice: 0 stessa ottava, 1 ottava superiore, -1 ottava inferiore
    public static int trovaOttavaLineare(int notaPrec, int notaSeg) {
        if (Math.abs(notaSeg-notaPrec)>5) {
            if (notaPrec>notaSeg) return 1;
            else return -1;
        }
        return 0;
    }
    public static int trovaOttavaAmpia(int notaPrec, int notaSeg) {
        if (notaPrec==notaSeg) return 0;
        if (Math.abs(notaSeg-notaPrec)<6) {
            if (notaPrec<notaSeg) return -1;
            else return 1;
        }
        return 0;
    }
    // MIDI: A0 = 21, C4 = 60, C8 = 108
    // octave 4 = central
    public static int[] findMelody(int octave, int[] absPitches, int lowerLimit, int upperLimit, int melodyType){
        octave++;
        int[] melody = new int[absPitches.length];
        int length = absPitches.length;
        int index = 0;
        while (index < length && absPitches[index] == -1){
            melody[index++] = -1;
        }
        if (index == length) {return melody;}
        melody[index]=notaInRange(absPitches[index] + octave * 12, lowerLimit,upperLimit);
        int lastNote = melody[index];
        if (index == length -1 ) {return melody;}
        if(melodyType == 0){
            for (int i = index; i<length-1; i++ ){
                int checkNote = absPitches[i+1];
                if(checkNote==-1){
                    melody[i+1]=-1;
                } else {
                    checkNote = checkNote + octave * 12;
                    int newNote = notaInRange(lastNote
                            +(checkNote-lastNote)+(12*Insieme.trovaOttavaLineare(lastNote, checkNote)),lowerLimit,upperLimit);
                    melody[i+1] = newNote;
                    lastNote = newNote;
                }
            }
        } else if(melodyType == 1){
            for (int i = index; i<length-1; i++ ){
                int checkNote = absPitches[i+1];
                if(checkNote==-1){
                    melody[i+1]=-1;
                } else {
                    checkNote = checkNote + octave  * 12;
                    int newNote = notaInRange(lastNote
                            +(checkNote-lastNote)+(12*Insieme.trovaOttavaAmpia(lastNote, checkNote)),lowerLimit,upperLimit);
                    melody[i+1] = newNote;
                    lastNote = newNote;
                }
            }
        }
        return melody;
    }
    public static int[] sequenzaLineare(int ottavaIniz, int[] valoriNote, int min, int max){
        int[] seqAltezze = new int[valoriNote.length];
        if (valoriNote[0]==0) seqAltezze[0] = -1;
        else seqAltezze[0] = (ottavaIniz*12+3) + valoriNote[0];
        for(int i = 1; i<valoriNote.length; i++) {
            if (valoriNote[i]==0) {seqAltezze[i] = -1;continue;}
            int j = i-1;
            do {
                if(valoriNote[j]!=0) {
                    seqAltezze[i]=notaInRange(seqAltezze[j]+(valoriNote[i]-valoriNote[j])+(12*Insieme.trovaOttavaLineare(valoriNote[j], valoriNote[i])),min,max);
                    break;
                }
                j--;
            } while (j>-1);
            if (j==-1) seqAltezze[i] = notaInRange((ottavaIniz*12+3) + valoriNote[i],min,max);
        }
        return seqAltezze;

    }
    public static int[] sequenzaAmpia(int ottavaIniz, int[] valoriNote,int min,int max){
        int[] seqAltezze = new int[valoriNote.length];
        if (valoriNote[0]==0) seqAltezze[0] = -1;
        else seqAltezze[0] = (ottavaIniz*12+3) + valoriNote[0];
        for(int i = 1; i<valoriNote.length; i++) {
            if (valoriNote[i]==0) {seqAltezze[i] = -1;continue;}
            int j = i-1;
            do {
                if(valoriNote[j]!=0) {
                    seqAltezze[i]=notaInRange(seqAltezze[j]+(valoriNote[i]-valoriNote[j])+(12*Insieme.trovaOttavaAmpia(valoriNote[j], valoriNote[i])),min,max);
                    break;
                }
                j--;
            } while (j>-1);
            if (j==-1) seqAltezze[i] = notaInRange((ottavaIniz*12+3) + valoriNote[i],min,max);
        }
        return seqAltezze;

    }

    // restituisce un Dodecabyte
    public static int fromMidiPitchToAbstractNote(int midiPitch) {

        int[] abstractNotes = {256,512,1024,2048,1,2,4,8,16,32,64,128};
        return abstractNotes[midiPitch%12];

    }

    public static int[] calcolaFond(int insieme){
        int[] valFond = {0,0,0,0,0,0,0,0,0,0,0,0};
        for (int i=0; i<12; i++) { //tutti i valFond
            for (int j=0; j<12; j++) {
                if ( ((int)Math.pow(2.0, (double)j) & insieme) > 0) valFond[i]= valFond[i]+Armoniche[12-i+j];
            }
        }
        //System.out.println(valFond);
        return valFond;
    }
    public static int[] trovaFond(int insieme) {
        int[] valFond = calcolaFond(insieme);
        int[] listaFond;
        int numFond=0;
        int max=0;
        for(int i=0; i<12; i++) {
            if (valFond[i]==max) numFond++;
            if (valFond[i]>max) {max=valFond[i]; numFond=1;}

        }
        listaFond = new int[numFond];
        int j=-1;
        for(int i=0; i<numFond; i++) {
            do {
                j++;
            } while (valFond[j]!=max);
            //listaFond[i]= 1 << j;
            listaFond[i]=j;
        }
        return listaFond;
    }
    public static boolean[] analizzaArmoniche(int Oldinsieme) {
        int[] listaFond = trovaFond(Oldinsieme);
        int indiceFond = listaFond[0];
        //System.out.println("VecchioInsieme: "+Oldinsieme);
        //System.out.println("indiceFond: "+indiceFond);
        int insieme = Insieme.trasponi(Oldinsieme,12-indiceFond);
        //System.out.println("NUOVOinsieme: "+insieme);
        boolean[] armoniche = new boolean[12];
        if ((1 & insieme)>0) armoniche[0]=true;// Do
        if ((2 & insieme)>0) armoniche[8]=true;// Do#
        if ((4 & insieme)>0) armoniche[4]=true;// Re
        if ((8 & insieme)>0) armoniche[9]=true;// Re#
        if ((16 & insieme)>0) armoniche[2]=true;//Mi
        if ((32 & insieme)>0) armoniche[10]=true;//Fa

        if ((64 & insieme)>0) armoniche[5]=true;//Fa#
        if ((128 & insieme)>0) armoniche[1]=true;//Sol
        if ((256 & insieme)>0) armoniche[6]=true;//Sol#
        if ((512 & insieme)>0) armoniche[11]=true;//La
        if ((1024 & insieme)>0) armoniche[3]=true;//La#
        if ((2048 & insieme)>0) armoniche[7]=true;//Si

        return armoniche;
    }
    /*public static Color getColoreArmonico1(int insieme){
    	boolean[] armoniche = analizzaArmoniche(insieme);
    	int red=15; int green=15; int blue=15;
    	if (armoniche[0]) red= red | 128;
    	if (armoniche[1]) green= green | 128 ;
    	if (armoniche[2]) blue= blue | 128;

    	if (armoniche[3]) red= red | 64;
    	if (armoniche[4]) green= green | 64;
    	if (armoniche[5]) blue= blue | 64;

    	if (armoniche[6]) red= red | 32;
    	if (armoniche[7]) green= green | 32;
    	if (armoniche[8]) blue= blue | 32;

    	if (armoniche[9]) red= red | 16;
    	if (armoniche[10]) green= green | 16;
    	if (armoniche[11]) blue= blue | 16;

    	return new Color(red,green,blue);

    }
    public static Color getColoreArmonico2(int insieme){ //Semplice RGB

    	int red=0; int green=0; int blue=0;
    	if ((1 & insieme)>0) red= red | (128+8);
    	if ((2 & insieme)>0) red= red | (64+4) ;
    	if ((4 & insieme)>0) red= red | (32+2);
    	if ((8 & insieme)>0) red= red | (16+1);

    	if ((16 & insieme)>0) green= green | (128+8);
    	if ((32 & insieme)>0) green= green | (64+4);
    	if ((64 & insieme)>0) green= green | (32+2);
    	if ((128 & insieme)>0)green= green | (16+1);

    	if ((256 & insieme)>0) blue= blue | (128+8);
    	if ((512 & insieme)>0) blue= blue | (64+4);
    	if ((1024 & insieme)>0) blue= blue | (32+2);
    	if ((2048 & insieme)>0) blue= blue | (16+1);



    	return new Color(red,green,blue);

    }
	public static Color getColoreArmonico3(int insieme){ //COLORI PRIMARI da migliorare
		int red=0; int yellow=0; int blue=0;
		int RGBred=0; int RGBgreen=0; int RGBblue=0;
    	if ((1 & insieme)>0) red= red | (128+8);
    	if ((2 & insieme)>0) red= red | (64+4) ;
    	if ((4 & insieme)>0) red= red | (32+2);
    	if ((8 & insieme)>0) red= red | 16;

    	if ((16 & insieme)>0) {yellow= yellow | (128+8); }
    	if ((32 & insieme)>0) {yellow= yellow | (64+4); }
    	if ((64 & insieme)>0) {yellow= yellow | (32+2); }
    	if ((128 & insieme)>0){ yellow= yellow | 16; }

    	if ((256 & insieme)>0) blue= blue | (128+8);
    	if ((512 & insieme)>0) blue= blue | (64+4);
    	if ((1024 & insieme)>0) blue= blue | (32+2);
    	if ((2048 & insieme)>0) blue= blue | 16;

    	// casi
    	System.out.println("PRIMA Red: " + red+"  Yellow: "+yellow+"  Blue: "+blue);
    	if(red==0 && yellow==0 && blue ==0){RGBred=0;RGBgreen=0 ; RGBblue=0;}

    	if(red>0 && yellow==0 && blue ==0){RGBred=red;RGBgreen=0 ;RGBblue=0;}
    	if(red==0 && yellow==0 && blue >0){RGBred=0;RGBgreen=0 ;RGBblue=blue;}
    	if(red==0 && yellow>0 && blue ==0){RGBred=yellow;RGBgreen=yellow ;RGBblue=0;}

    	if(red>0 && yellow>0 && blue ==0){RGBred=red;RGBgreen=yellow*2/3 ;RGBblue=0;}//ARANCIONE
    	if(red>0 && yellow==0 && blue >0){RGBred=red;RGBgreen=0; RGBblue=blue;}//VIOLA
    	if(red==0 && yellow>0 && blue >0){RGBred=0;RGBgreen=(yellow+blue)/2; RGBblue=0;}//VERDE

    	if(red>0 && yellow>0 && blue >0){RGBred=red;RGBgreen=yellow; RGBblue=blue;}
    	System.out.println("DOPO  RGBRed: " + RGBred+"  RGBGreen: "+RGBgreen+"  RGBBlue: "+RGBblue);
    	return new Color(RGBred,RGBgreen,RGBblue);

	}
	public static Color getColoreArmonico4(int insieme){ //RGB su grigio

    	int red=15; int green=15; int blue=15;
    	if ((1 & insieme)>0) red= red | (128);
    	if ((2 & insieme)>0) red= red | (64) ;
    	if ((4 & insieme)>0) red= red | (32);
    	if ((8 & insieme)>0) red= red | (16);

    	if ((16 & insieme)>0) green= green | (128);
    	if ((32 & insieme)>0) green= green | (64);
    	if ((64 & insieme)>0) green= green | (32);
    	if ((128 & insieme)>0)green= green | (16);

    	if ((256 & insieme)>0) blue= blue | (128);
    	if ((512 & insieme)>0) blue= blue | (64);
    	if ((1024 & insieme)>0) blue= blue | (32);
    	if ((2048 & insieme)>0) blue= blue | (16);



    	return new Color(red,green,blue);

    }
    public static Color getColoreArmonico5(int insieme){
        int red=0; int green=0; int blue=0;
        if (insieme==0) return new Color(0,0,0);
        int[] r={255,232,209,186,163,140,117,94,71,48,25,0};
        int[] g={71,48,25,0,255,232,209,186,163,140,117,94};
        int[] b={163,140,117,94,71,48,25,0,255,232,209,186};
        int cont=0;
        for (int i =0; i<12; i++){
            if(((int)Math.pow(2, i) & insieme)>0){
                cont++;
                red+=r[i];green+=g[i];blue+=b[i];
            }
        }

        return new Color(red/cont,green/cont,blue/cont);

    }
    */
    public static int[][] getTabulaIntervalliVettori(int[][] tab) {
        int[][] resTab = new int[tab.length][];
        for (int y=0; y<tab.length; y++) {
            resTab[y]= new int[tab[y].length];
            // cerca precedente che non sia una pausa, se non lo trova salta il ciclo interno riempiendo la riga di 10
            int prec = 0; //indice del precedente
            ricerca:
            if (tab[y][prec] == 0) {
                for (int i = tab[y].length-1; i>0 ;i--){
                    if (tab[y][i]>0) {prec= i; break ricerca;}
                }

            }
            if (prec == 1 && tab[y][prec]==0) { // non � stato trovato nessun precedente: la riga � vuota.
                for (int i = 0 ; i<tab[y].length; i++) tab[y][i]=9;
                continue; //va alla prossima riga.
            }

            for (int x=0; x<tab[y].length-1; x++) {
                resTab[y][x]=getIntervalloVettore(tab[y][x],tab[y][x+1]);

                if (resTab[y][x]==8) {
                    resTab[y][x]=getIntervalloVettore(tab[y][prec],tab[y][x+1]);

                }
                if (tab[y][x]>0) prec = x;

            }
            //confronta l'ultimo con il primo
            resTab[y][tab[y].length-1]=getIntervalloVettore(tab[y][tab[y].length-1],tab[y][0]);
            //if ((resTab[y][tab[y].length-1]!=9)&&(resTab[y][tab[y].length-1]!=10)) prec = resTab[y][0];
            if (resTab[y][tab[y].length-1]==8) {
                resTab[y][tab[y].length-1]=getIntervalloVettore(tab[y][prec],tab[y][0]);

            }
        }
        //Stampa resTab
        for (int i =0; i<resTab.length; i++){
            for (int j = 0 ; j<resTab[i].length;j++) {System.out.print(" "+resTab[i][j]);}
            System.out.println("TabulaIntervalliVettori");
        }
        return resTab;
    }

    // accetta numero di item (pausa = 0, Do =1, Do# = 2, etc.)
    // restituisce un numero da 1 a 5 positivo(in su) o negativo(in gi�); 6 per la 4a aum.;
    // 0 per l'unisono(c'� una pausa di mezzo); 7 per una ripetizione contigua;
    // 9 per una pausa alla seconda nota, 8 per una pausa alla prima nota (CONFRONTO INPOSSIBILE, cercare altre prec. e succ.)
    // (10 per pause a tutte e due le note)NO! solo9;
    public static int getIntervalloVettore(int prec, int succ){
        if (succ==0 && prec ==0) return 9;
        if (succ==0) return 9 ;// pausa alla seconda nota

        if (prec==0) return 8;
        int somma = succ-prec;
        if (somma>-6 && somma <6) return somma;
        if(somma==-6 || somma ==6) return 6; // 4aum.

        if (somma>6)return -12+somma;
        if (somma<-6)return 12+somma;
        return somma;
    }
    public static int[] analizzaSeguiti(int[] melody, int pauseValue){
        int[] seguiti = new int[melody.length];
        for(int i=0; i<melody.length; i++){
            if (melody[i]==pauseValue){
                seguiti[i]=0;
                continue;
            }
            seguiti[i]= 1;
            for(int j=i+1; j<melody.length; j++){
                if(melody[j]==pauseValue) {
                    break;
                }
                seguiti[i]++;

            }
        }
        /*for(int i=0;i<seguiti.length;i++){
            System.out.print(seguiti[i]+" ");

        }
        System.out.println();*/
        return seguiti;

    }
    public static Vector<Integer> getInv(Vector<Integer> confr) {
        Vector<Integer> canoneInverso = new Vector<Integer>();
        //System.out.println("Prima della trasformazione "+ confr);
        for (Iterator<Integer> iterator = confr.iterator(); iterator.hasNext();) {
            Integer nota =  iterator.next();
            Integer nuovaNota;
            //System.out.println("Prima della trasformazione note"+ nota);
            if(nota.intValue()<6 && nota.intValue()>-6) {
                nuovaNota = new Integer(nota.intValue()*-1);
            } else nuovaNota = new Integer(nota.intValue());
            canoneInverso.add(nuovaNota);
            //System.out.println("Dopo la trasformazione note"+ nota);
        }

        //System.out.println("Dopo la trasformazione "+ confr);
        return canoneInverso;

    }
    /*public static Vector<Integer[]> doTokens(int[] melody ,int pauseValue ){

        Vector<Integer[]> tokens = new Vector<Integer[]>();
       Vector<Integer> token = new Vector<Integer>();
        for(int i =0; i<melody.length; i++){
            if(melody[i]==pauseValue) { //Cerca l'inizio del token
               continue;
            } else {
                 token.removeAllElements();
                token.add(new Integer(melody[i]));
                for(int j=i+1; j<melody.length; j++){ //Carica il token
                    if(melody[j]==pauseValue){
                        i=j;
                        Integer[] foundToken = new Integer[token.size()];
                        for(int q=0;q<token.size();q++){
                            foundToken[q]=token.elementAt(q);
                        }

                        tokens.add(foundToken);

                        break;
                    }
                    token.add(new Integer(melody[j]));
                     if (j==melody.length-1){

                        Integer[] foundToken = new Integer[token.size()];
                        for(int q=0;q<token.size();q++){
                            foundToken[q]=token.elementAt(q);
                          }
                        tokens.add(foundToken);
                     }
            }
        }
        }
        for(int i=0; i<tokens.size(); i++){
            Integer[] tok =tokens.elementAt(i);
            for(int j=0; j<tok.length; j++){
                System.out.print(tok[j]+" ");
            }
            System.out.println();


     }
        return tokens;

    }*/
    public static Vector<Integer> getInvRetr(Vector<Integer> confr) {
        Vector<Integer> canoneInvRetr = new Vector<Integer>();

        //System.out.println("Prima della trasformazione "+ confr);
        for (Iterator<Integer> iterator = confr.iterator(); iterator.hasNext();) {
            Integer nota =  iterator.next();
            //System.out.println("Prima della trasformazione note"+ nota);

            Integer nuovaNota = new Integer(nota.intValue());
            canoneInvRetr.insertElementAt(nuovaNota, 0);
        }
        //System.out.println("Dopo la trasformazione note"+ nota);


        //System.out.println("Dopo la trasformazione "+ confr);
        return canoneInvRetr;
    }
    public static Vector<Integer> getRetr(Vector<Integer> confr) {
        Vector<Integer> canoneRetr = getInvRetr(getInv(confr));

        return canoneRetr;
    }
    static public int rotate(int nTimes, int dByte){
        for(int i=0; i<nTimes; i++){
            dByte <<=1;
            if((dByte & 0B1000000000000 )== 0B1000000000000 ) dByte= dByte+1;
        }
        return dByte;
    }

    static public int rotateLeft(int dodecaByte){

        dodecaByte <<=1;
        if((dodecaByte & 0B1000000000000 )== 0B1000000000000 ) dodecaByte= dodecaByte+1;

        return dodecaByte;
    }

    static public int rotateRight(int dodecaByte){
        if((dodecaByte & 0B1 )!=0) {
            return (dodecaByte >> 1) & 0B100000000000;
        }
        return dodecaByte >>1;
    }
    static public int searchClosestBit(int startBit, int dodecaByte){
        int up = startBit; int down = startBit;
        for (int i = 1; i < 7; i++) {
            up = rotateLeft(up);
            if ((up & dodecaByte) != 0) return i; //found above
            down = rotateRight(down);
            if ((down &dodecaByte) != 0) return -i;// found below
        }
        return startBit; // nothing found, let the same

    }
    static public int[] findPassagenotes(int noteA, int noteB, int dByte){
        Vector<Integer> results = new Vector();
        for (int i = noteA+1; i <noteB; i++) {
            int noteByte = 1 << (i%12);
            if ((noteByte & dByte) != 0 ) results.add(i);
        }
        return convertIntegerVector(results);
    }

    static public int[] convertIntegerVector(Vector<Integer> vector){
        int[] res = new int[vector.size()];
        for (int i = 0; i <vector.size(); i++) {
            res[i] = vector.get(i);
        }
        return res;
    }
    public static int findUpperChordNote(int nextNotePitch, int dByte) {
        int res = -1;
        for (int i = nextNotePitch+1; i < nextNotePitch+12 ; i++) {
            int noteByte = 1 << i%12;
            if ((noteByte & dByte) != 0 ) return i;
        }
        return res;
    }
    public static int findLowerChordNote(int nextNotePitch, int dByte) {
        int res = -1;

        for (int i = nextNotePitch-1; i > nextNotePitch-13 ; i--) {
            //int noteByte = 2048 >> (12-(i%12));
            int noteByte = 1 << i%12;
            if ((noteByte & dByte) != 0 ) return i;
        }
        return res;
    }

    public static int transposeAbsPitch(int pitch, int transpose){
        if (pitch == -1) return -1;
        pitch += transpose;
        if(pitch > 11) return pitch - 12;
        if(pitch < 0) return pitch + 12;
        return pitch;
    }

    // 2m/7M 2M/7m 3m/6M 3M/6m 4/5 4e 8
    static int[] positionOfIntervals = {6, 0, 1, 2, 3, 4, 5, 4, 3, 2, 1, 0};
    public static void incrementIntervalCount(@NotNull ArrayList<Integer> intervalCount, int pitchToCheck, @NotNull List<Integer> absPitches) {
        for(int i=0; i<absPitches.size(); i++){
            int absPitch = absPitches.get(i);
            if( absPitch == -1) continue;
            int interval = Math.abs(absPitch - pitchToCheck);
            int intervalPosition = positionOfIntervals[interval];
            intervalCount.set(intervalPosition, intervalCount.get(intervalPosition) + 1);
        }
    }
    public static int intervalSetDifference(@NotNull ArrayList<Integer> intervalCount, @NotNull List<Integer> intervalSet){
        int difference = 0;
        if(intervalCount.get(0) != 0 && !intervalSet.containsAll(Arrays.asList(1, 11))) difference++;
        if(intervalCount.get(1) != 0 && !intervalSet.containsAll(Arrays.asList(2, 10))) difference++;
        if(intervalCount.get(2) != 0 && !intervalSet.containsAll(Arrays.asList(3, 9))) difference++;
        if(intervalCount.get(3) != 0 && !intervalSet.containsAll(Arrays.asList(4, 8))) difference++;
        if(intervalCount.get(4) != 0 && !intervalSet.containsAll(Arrays.asList(5, 7))) difference++;
        if(intervalCount.get(5) != 0 && !intervalSet.contains(6)) difference++;
        if(intervalCount.get(6) != 0 && !intervalSet.contains(0)) difference++;
        return difference;
    }
    public static int intervalSetDifferenceCount(@NotNull ArrayList<Integer> intervalCount, @NotNull List<Integer> intervalSet){
        int count = 0;
        if(intervalCount.get(0) != 0 && !intervalSet.containsAll(Arrays.asList(1, 11))) count = count + intervalCount.get(0) ;
        if(intervalCount.get(1) != 0 && !intervalSet.containsAll(Arrays.asList(2, 10))) count = count + intervalCount.get(1) ;
        if(intervalCount.get(2) != 0 && !intervalSet.containsAll(Arrays.asList(3, 9))) count = count + intervalCount.get(2) ;
        if(intervalCount.get(3) != 0 && !intervalSet.containsAll(Arrays.asList(4, 8))) count = count + intervalCount.get(3) ;
        if(intervalCount.get(4) != 0 && !intervalSet.containsAll(Arrays.asList(5, 7))) count = count + intervalCount.get(4) ;
        if(intervalCount.get(5) != 0 && !intervalSet.contains(6)) count = count + intervalCount.get(5) ;
        if(intervalCount.get(6) != 0 && !intervalSet.contains(0)) count = count + intervalCount.get(6) ;
        return count;
    }

    @NotNull
    public static Integer[] convertIntervalCountToIntervalSet(@NotNull ArrayList<Integer> intervalCount) {
        Set<Integer> intervalSet = new TreeSet<>();
        if(intervalCount.get(0) != 0 ) intervalSet.addAll(Arrays.asList(0,11));
        if(intervalCount.get(1) != 0 ) intervalSet.addAll(Arrays.asList(2,10));
        if(intervalCount.get(2) != 0 ) intervalSet.addAll(Arrays.asList(3,9));
        if(intervalCount.get(3) != 0 ) intervalSet.addAll(Arrays.asList(4,8));
        if(intervalCount.get(4) != 0 ) intervalSet.addAll(Arrays.asList(5,7));
        if(intervalCount.get(5) != 0 ) intervalSet.addAll(Arrays.asList(6));
        if(intervalCount.get(6) != 0 ) intervalSet.addAll(Arrays.asList(0));
        return (Integer[]) intervalSet.toArray(new Integer[0]);
    }
    public static int getBendFromInterval(int interval){
        switch(interval){
            case -1: return 7000;
            case 1: return 16383;

        }
        return -1;// 0 4096 8192 12288 (14335) 16383
    }
    public static int[] checkIntervalsInPitches(@NotNull int[] pitches, int[] intervals){
        int nPitches = pitches.length;
        int nIntervals = intervals.length;
        int[] result = new int[nPitches];
        Arrays.fill(result, 0);
        int indexPitch = 0; int nextPitch = 0;
        if(nIntervals== 0 || nPitches == 1 ) return result;
            for(int i=0; i < nPitches-1; i++){
                indexPitch = pitches[i]; nextPitch = pitches[i+1];
                if (nextPitch == -1 || indexPitch == -1) continue;
                for (int interval : intervals) {
                    if (nextPitch - indexPitch == interval) {
                        result[i] = interval;
                        break;
                    }
                }
        }
        indexPitch = pitches[nPitches-1]; nextPitch = pitches[0];
        if (nextPitch != -1 || indexPitch != -1) {
            for (int interval : intervals) {
                if (nextPitch - indexPitch  == interval) {
                    result[nPitches - 1] = interval;
                    break;
                }
            }
        }
        System.out.println("glissandoChecks: " + Arrays.toString(result));
        return result;
    }
}
