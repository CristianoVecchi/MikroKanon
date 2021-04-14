package com.cristianovecchi.mikrokanon.AIMUSIC;
public class ListaStrumenti {
	public static final String[] str = {
		"000 - Piano","001 - Bright Piano","002 - Electric Grand","003 - Honky Tonk Piano","004 - Electric Piano 1","005 - Electric Piano 2","006 - Harpsichord",
		"007 - Clavinet","008 - Celesta","009 - Glockenspiel","010 - Music Box","011 - Vibraphone","012 - Marimba","013 - Xylophone","014 - Tubular Bell","015 - Dulcimer",
		"016 - Hammond Organ","017 - Perc Organ","018 - Rock Organ","019 - Church Organ","020 - Reed Organ","021 - Accordion","022 - Harmonica","023 - Tango Accordion",
		"024 - Nylon Str Guitar","025 - Steel String Guitar","026 - Jazz Electric Gtr","027 - Clean Guitar","028 - Muted Guitar","029 - Overdrive Guitar","030 - Distortion Guitar","031 - Guitar Harmonics",
		"032 - Acoustic Bass","033 - Fingered Bass","034 - Picked Bass","035 - Fretless Bass","036 - Slap Bass 1","037 - Slap Bass 2","038 - Syn Bass 1","039 - Syn Bass 2",
		"040 - Violin","041 - Viola","042 - Cello",
		"043 - Contrabass","044 - Tremolo Strings","045 - Pizzicato Strings",
		"046 - Orchestral Harp","047 - Timpani","048 - Ensemble Strings",
		"049 - Slow Strings","050 - Synth Strings 1","051 - Synth Strings 2",
		"052 - Choir Aahs","053 - Voice Oohs","054 - Syn Choir",
		"055 - Orchestra Hit","056 - Trumpet","057 - Trombone",
		"058 - Tuba","059 - Muted Trumpet","060 - French Horn",
		"061 - Brass Ensemble","062 - Syn Brass 1","063 - Syn Brass 2",
		"064 - Soprano Sax","065 - Alto Sax","066 - Tenor Sax",
		"067 - Baritone Sax","068 - Oboe","069 - English Horn",
		"070 - Bassoon","071 - Clarinet","072 - Piccolo",
		"073 - Flute","074 - Recorder","075 - Pan Flute",
		"076 - Bottle Blow","077 - Shakuhachi","078 - Whistle",
		"079 - Ocarina","080 - Syn Square Wave","081 - Syn Saw Wave",
		"082 - Syn Calliope","083 - Syn Chiff","084 - Syn Charang",
		"085 - Syn Voice","086 - Syn Fifths Saw","087 - Syn Brass and Lead",
		"088 - Fantasia","089 - Warm Pad","090 - Polysynth",
		"091 - Space Vox","092 - Bowed Glass","093 - Metal Pad",
		"094 - Halo Pad","095 - Sweep Pad","096 - Ice Rain",
		"097 - Soundtrack","098 - Crystal","099 - Atmosphere",
		"100 - Brightness","101 - Goblins","102 - Echo Drops",
		"103 - Sci Fi","104 - Sitar","105 - Banjo",
		"106 - Shamisen","107 - Koto","108 - Kalimba",
		"109 - Bag Pipe","110 - Fiddle","111 - Shanai",
		"112 - Tinkle Bell","113 - Agogo","114 - Steel Drums",
		"115 - Woodblock","116 - Taiko Drum","117 - Melodic Tom",
		"118 - Syn Drum","119 - Reverse Cymbal","120 - Guitar Fret Noise",
		"121 - Breath Noise","122 - Seashore","123 - Bird",
		"124 - Telephone","125 - Helicopter","126 - Applause",
		"127 - Gunshot"
	};
	public static int getIndexByName(String name){

		for (int i = 0; i < str.length; i++) {
			if (name.equals(str[i])) return i;
		}
		return 0; //default PIANO
	}
}
