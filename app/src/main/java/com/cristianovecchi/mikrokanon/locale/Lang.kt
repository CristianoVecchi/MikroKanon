package com.cristianovecchi.mikrokanon.locale

import com.cristianovecchi.mikrokanon.AIMUSIC.*
import com.cristianovecchi.mikrokanon.composables.NoteNamesEn

enum class LANGUAGES(val language:String){
    ar("العربية"),
    de("Deutsch"),
    el("Ελληνικά"),en("English"),es("Español"),
    fr("Français"),
    hi("हिन्दी"),id("Bahasa Indonesia"),it("Italiano"),
    ko("한국어") ,jp("日本語"),
     pt("Português"), ru("Русский"),
    sw("Kiswahili"),zh("中文");
    companion object {
        fun languageNameFromDef(langDef: String): String {
            return when (langDef) {
                "ar" -> ar.language
                "de" -> de.language
                "el" -> el.language
                "en" -> en.language
                "es" -> es.language
                "fr" -> fr.language
                "ko" -> ko.language
                "jp" -> jp.language
                "id" -> id.language
                "it" -> it.language
                "pt" -> pt.language
                "ru" -> ru.language
                "sw" -> sw.language
                "zh" -> zh.language
                else -> en.language
            }
        }
    }
}


enum class NoteNamesPt {
    Dó,Ré,Mi,Fá,Sol,Lá,Si,EMPTY
}
enum class NoteNamesIt {
    Do,Re,Mi,Fa,Sol,La,Si,EMPTY
}
enum class NoteNamesFr {
    Ut,Ré,Mi,Fa,Sol,La,Si,EMPTY
}
enum class NoteNamesRu {
    До,Ре,Ми,Фа,Соль,Ля,Си,EMPTY
}
enum class NoteNamesEl {
    Ντο, Ρε, Μι, Φα, Σολ, Λα, Σι, EMPTY
}
//enum class NoteNamesAr {
//    دو, ري, مي, فا, صول, لا,سي , EMPTY
//}

val synthsNames = listOf("Square Wave","Saw Wave", "Calliope", "Chiff",
        "Charang", "Synth Voice", "Fifths Saw", "Brass and Lead",
    "Fantasia", "Warm Pad", "Polysynth", "Space Vox",
    "Bowed Glass", "Metal Pad", "Halo Pad", "Sweep Pad",
    "Ice Rain", "Soundtrack", "Crystal", "Atmosphere",
"Brightness", "Goblins", "Echo Drops", "Sci Fi")
val ensembleNamesAr = listOf("آلة وترية ذات قوس", "آلة نفخ خشبية", "سلسلة الأوركسترا", "آلة نفخ نحاسية","غاميلان", "ساكسفون", "فلوت",
    "ضعف القصب", "كلارينيت", "مزمار", "تشيلو", "بيانو","قيثار","بييرو","الباروك","أوتار نتف","Spooky")
val ensembleNamesDe = listOf("Streichinstrumente", "Holzblasinstrumente", "Streichorchester", "Blechblasinstrumente","Gamelan", "Saxophone", "Flöten",
    "Doppelblattinstrumente", "Klarinetten", "Fagotte", "Cellos", "Klavier","Harfe","Pierrot","Barockensemble","Zupfinstrument","Spooky")
val ensembleNamesEl = listOf("Έγχόρδα", "Ξύλινα πνευστά της συμφωνικής ορχήστρας", "Ορχήστρα εγχόρδων", "Χάλκινα πνευστά της συμφωνικής ορχήστρας","Gamelan", "Σαξόφωνα", "Φλάουτα",
    "Διπλά καλάμια", "Κλαρινέτ", "Φαγκότα", "Βιολοντσέλα", "Πιάνο","Άρπα", "Πιερότος","Μπαρόκ", "Ματαιωμένες χορδές","Spooky")
val ensembleNamesEn = listOf("Strings", "Woodwinds", "String orchestra", "Brass", "Gamelan", "Saxophones", "Flutes",
    "Double reeds", "Clarinets", "Bassoons", "Cellos", "Piano","Harp","Pierrot","Baroque","Plucked strings","Spooky")

val ensembleNamesEs = listOf("Cuerdas", "Instrumentos de viento madera", "Orquesta de cuerdas", "Instrumentos de viento metal","Gamelán", "Saxofones", "Flautas",
    "Cañas dobles", "Clarinetes", "Fagotes", "Violonchelos", "Piano","Arpa", "Pierrot","Barroco","Instrumentos de cuerda pulsada","Spooky")
val ensembleNamesKo = listOf("찰현악기", "목관악기", "현악 합주단", "금관악기","가믈란", "색소폰", "플루트",
    "더블 리드", "클라리넷", "바순", "첼로 스", "피아노","하프","피에로","바로크","발현악기","Spooky")
val ensembleNamesJp = listOf("弦楽", "木管楽器", "弦楽オーケストラ", "金管楽器","ガムラン","サックス", "フルート",
    "ダブルリード", "クラリネット", "ファゴット", "チェロ", "ピアノ","ハープ", "ピエロ", "バロック","撥弦楽器","Spooky")
val ensembleNamesId = listOf("Alat musik dawai membungkuk", "Instrumen musik tiup kayu", "Orkestra dawai", "Instrumen musik tiup logam", "Gamelan","Saxophone", "Seruling",
    "Alang-alang ganda", "Klarinet", "Bassoon", "Cellos", "Piano", "Harpa", "Pierrot", "Baroque", "Dawai yang dipetik","Spooky")
val ensembleNamesIt = listOf("Archi", "Legni", "Orchestra d'archi", "Ottoni","Gamelan", "Saxofoni", "Flauti",
    "Ance doppie", "Clarinetti", "Fagotti", "Violoncelli", "Pianoforte","Arpa","Pierrot","Barocco", "Corde pizzicate","Spooky")
val ensembleNamesPt = listOf("Cordas friccionadas", "Madeiras", "Orquestra de cordas", "Metais","Gamelão","Saxofones", "Flautas",
    "Palhetas duplas", "Clarinetes", "Fagotes", "Violoncelos", "Piano","Harpa", "Pierrot", "Barroco", "Cordas dedilhadas","Spooky")
val ensembleNamesFr = listOf("Cordes", "Bois", "Orchestre à cordes", "Cuivres", "Gamelan","Saxophones", "Flûtes",
    "Anches doubles", "Clarinettes", "Bassons", "Violoncelles", "Piano","Harpe", "Pierrot","Baroque","Cordes pincées","Spooky")
val ensembleNamesHi = listOf("Strings", "Woodwinds", "String orchestra", "Brass","गमेलन", "Saxophones", "Flutes",
    "Double reeds", "Clarinets", "Bassoons", "Cellos", "Piano","वीणा","Pierrot","Baroque","Plucked strings","Spooky")
val ensembleNamesRu = listOf("Струнные", "Деревянные духовые инструменты", "Струнный оркестр", "Медные духовые инструменты","Гамелан", "Саксофоны", "Флейты",
    "Двойной тростью", "Кларнеты", "Фаготы", "Виолончели", "Фортепиано","Арфа", "Пьеро","Барокко","Струнные щипковые инструменты","Spooky")
val ensembleNamesSw = listOf("Vyombo vilivyoinama", "Vyombo vya upepo vya mbao", "Orchestra ya ala za nyuzi", "Vyombo vya upepo vya chuma",
    "Gamelan", "Saxophones", "Zilizimbi","Mwanzi mara mbili", "Clarinets", "Bassoons", "Cellos", "Piano", "Kinubi",
    "Pierrot","Baroque", "Vyombo vya kamba vilivyokatwa","Spooky")
val ensembleNamesZh = listOf("弦乐", "木管乐器", "弦乐团", "銅管樂器","甘美蘭", "薩氏管", "长笛",
    "双簧管", "单簧管", "巴松管", "大提琴", "钢琴","豎琴","皮埃罗","巴洛克","撥弦樂器","Spooky")

private var zodiacPlanets = listOf("\u2644", "\u2643", "\u2642","\u2640","\u263F","\u263D","\u2609")
private var zodiacPlanetsEmojis = listOf("\u1fA90","\u9795","\u9794","\u9792","\u9791","\u1F31C","\u1F31E") // are different on xiaomi
private val zodiacSignsEmojis = listOf("\u2648","\u2649","\u264A","\u264B","\u264C","\u264D","\u264E","\u264F","\u2650","\u2651","\u2652","\u2653",)
private val zodiacSigns = listOf("♈︎","♉︎","♊︎","♋︎","♌︎","♍︎","♎︎","♏︎","♐︎","♑︎","♒︎","♓︎",)
fun getZodiacPlanets(emojis: Boolean): List<String>{
    return zodiacPlanets
//    return if(emojis) {
//        if(android.os.Build.VERSION.SDK_INT >=android.os.Build.VERSION_CODES.O) zodiacPlanets
//        else zodiacPlanets
//    } else zodiacPlanets
}
fun getZodiacSigns(emojis: Boolean): List<String>{
    return if(emojis) return zodiacSignsEmojis else zodiacSigns
}
fun getGlissandoSymbols(): Pair<String,String>{
    return if(android.os.Build.VERSION.SDK_INT >=android.os.Build.VERSION_CODES.O)
        Pair("\uD834\uDDB1", "\uD834\uDDB2")
    else Pair("➚", "➘")
}
fun getVibratoSymbol(): String {
    return if(android.os.Build.VERSION.SDK_INT >=android.os.Build.VERSION_CODES.O)
      "\u223f"
    else "~"
}
fun getNoteAndRestSymbols(): List<String> {
    return if(android.os.Build.VERSION.SDK_INT >=android.os.Build.VERSION_CODES.O)
        listOf("\uD834\uDD60" ,"\uD834\uDD3E")
    else listOf("♪", "-")
}

fun createGlissandoIntervals(doublingNames: List<String>): List<String>{
    val symbols = getGlissandoSymbols()
    val asc = symbols.first
    val desc = symbols.second
    return listOf("${doublingNames[0]}$asc", "${doublingNames[0]}$desc",
        "${doublingNames[1]}$asc", "${doublingNames[1]}$desc", "${doublingNames[2]}$asc", "${doublingNames[2]}$desc",)
}
fun getDynamicSymbols(): List<String>{
    return if(android.os.Build.VERSION.SDK_INT >=android.os.Build.VERSION_CODES.O)
        listOf("\uD834\uDDC8","\uD834\uDD8F\uD834\uDD8F\uD834\uDD8F\uD834\uDD8F","\uD834\uDD8F\uD834\uDD8F\uD834\uDD8F","\uD834\uDD8F\uD834\uDD8F",
        "\uD834\uDD8F","\uD834\uDD90\uD834\uDD8F","\uD834\uDD90\uD834\uDD91","\uD834\uDD91",
        "\uD834\uDD91\uD834\uDD91","\uD834\uDD91\uD834\uDD91\uD834\uDD91","\uD834\uDD91\uD834\uDD91\uD834\uDD91\uD834\uDD91","\uD834\uDD91\uD834\uDD91\uD834\uDD91\uD834\uDD91\uD834\uDD91",
        "\uD834\uDD92", "\uD834\uDD93")
    else listOf("0","pppp","ppp","pp",  "p","mp","mf","f", "ff", "fff","ffff","fffff","<",">")
}
fun getOctaveSymbols(): List<String>{
    return listOf("➘15","➘8", "", "➚8","➚15")
}
fun getRibattutoSymbols(): List<String>{
    return listOf("", "\"", "\"\'", "\"\"")
}

val doublingDe = listOf("kleine Sekunde", "große Sekunde", "kleine Terz", "große Terz", "Quarte",
    "übermäßige Quarte", "Quinte", "kleine Sexte", "große Sexte", "kleine Septime", "große Septime",
    "Oktave", "kleine None", "große None", "kleine Dezime", "große Dezime", "Undezime",
    "übermäßige Undezime", "Duodezime", "kleine Tredezime ", "große Tredezime ", "kleine Quartdezime", "große Quartdezime", "Doppeloktave")
val doublingEl = listOf("Μικρή δευτέρα","Μεγάλη δευτέρα", "Μικρή τρίτη", "Μεγάλη τρίτη", "Καθαρή τετάρτη",
    "Αυξημένη τετάρτη", "Καθαρή πέμπτη", "Μικρή έκτη", "Μεγάλη έκτη", "Μικρή εβδόμη", "Μεγάλη εβδόμη",
    "Οκτάβα", "Μικρή ένατη", "Μεγάλη ένατη", "Μικρή δέκατη", "Μεγάλη δέκατη", "Καθαρή ενδέκατη",
    "Αυξημένη ενδέκατη", "Καθαρή δωδέκατη", "Μικρή δέκατη τρίτη", "Μεγάλη δέκατη τρίτη", "Μικρή δέκατη τέταρτη", "Μεγάλη δέκατη τέταρτη", "Καθαρή δέκατη πέμπτη")
val doublingEn = listOf("minor 2nd","Major 2nd", "minor 3rd", "Major 3rd", "4th",
    "Augm. 4th", "5th", "minor 6th", "Major 6th", "minor 7th", "Major 7th",
    "Octave", "minor 9th", "Major 9th", "minor 10th", "Major 10th", "11th",
    "Augm. 11th", "12th", "minor 13th", "Major 13th", "minor 14th", "Major 14th", "Double Octave")
val doublingId = listOf("Minor kedua","Mayor kedua", "Minor ketiga", "Mayor ketiga", "Sempurna keempat",
    "Tambah keempat", "Sempurna kelima", "Minor keenam", "Mayor keenam", "Minor ketujuh", "Mayor ketujuh",
    "Oktaf","Minor kedua + oktaf","Mayor kedua + oktaf", "Minor ketiga + oktaf", "Mayor ketiga + oktaf", "Sempurna keempat + oktaf",
    "Tambah keempat + oktaf", "Sempurna kelima + oktaf", "Minor keenam + oktaf", "Mayor keenam + oktaf",
    "Minor ketujuh + oktaf", "Mayor ketujuh + oktaf",
    "Oktaf ganda" )
val doublingIt = listOf("2ª minore","2ª Maggiore", "3ª minore", "3ª Maggiore", "4ª",
    "4ª Aumentata", "5ª", "6ª minore", "6ª Maggiore", "7ª minore", "7ª Maggiore",
    "Ottava", "9ª minore", "9ª Maggiore", "10ª minore", "10ª Maggiore", "11ª",
    "11ª Aumentata", "12ª", "13ª minore", "13ª Maggiore", "14ª minore", "14ª Maggiore", "Ottava doppia")
val doublingFr = listOf("2ème mineur", "2ème Majeur", "3ème mineur", "3ème Majeur", "4e",
    "4e Augmentée", "5e", "6e mineure", "6e Majeure", "7ème mineure", "7ème Majeure",
    "Octave", "9ème mineur", "9ème Majeur", "10ème mineur", "10ème Majeur", "11ème",
    "11ème Augmentée", "12ème", "13ème mineur", "13ème Majeur", "14ème mineur", "14ème Majeur", "Double octave")
val doublingEs = listOf("2a menor", "2a Mayor", "3a menor", "3a Mayor", "4a",
    "4a Aumentada", "5a", "6a menor", "6a Mayor", "7a menor", "7a Mayor",
    "Octava", "9a menor", "9a Mayor", "10a menor", "10a Mayor", "11a",
    "11a Aumentada", "12a", "13a menor", "13a Mayor", "14a menor", "14a Major", "Doble octava")
val doublingRu = listOf("Секунда малая", "Секунда большая", "Терция малая", "Терция большая", "Кварта",
"Кварта увеличенная", "Квинта", "Секста малая", "Секста большая", "Септима малая", "Септима большая",
"Октава", "Нона малая", "Нона большая", "Децима малая", "Децима большая", "Ундецима",
"Ундецима увеличенная", "Дуодецима", "Терцдецима малая", "Терцдецима большая",
    "Квартдецима малая", "Квартдецима большая", "Квинтдецима ")
val doublingKo = listOf("단2도","장2도","단3도","장3도","완전4도",
    "트라이톤","완전5도","단6도","장6도","단7도","장7도",
    "완전8도","단9도","장9도","단10도","장10도","완전11도",
    "완전8도+트라이톤","완전12도","단13도","장13도","단14도","장14도",
    "완전15도")
val doublingJp = listOf("短2度","長2度","短3度","長3度","4度",
"増4度","5度","短6度","長6度","短7度","長7度",
"8度","短9度","長9度","短10度","長10度","11度",
    "増11度","12度","短13度","長13度","短14度","長14度","15度")
val doublingZh = listOf("小二度","大二度","小三度","大三度","纯四度",
    "增四度","纯五度","小六度","大六度","小七度","大七度",
    "纯八度","小九度","大九度","小十度","大十度","纯十一度",
    "增十一度","纯十二度","小十三度","大十三度","小十四度","大十四度",
    "纯十五度")
val doublingSw = listOf("Ndogo ya pili", "Kubwa ya pili", "Ndogo ya tatu", "Kubwa ya tatu", "Ya nne",
    "Ya nne kupindukia", "Ya tano", "Ndogo ya sita", "Kubwa ya sita", "Ndogo ya saba", "Kubwa ya saba",
    "Octave", "Ndogo Hakuna", "Kubwa Hakuna", "Ndogo ya zaka", "Kubwa ya zaka", "Kumi na moja",
    "Kumi na moja kupindukia", "Kumi na mbili", "Ndogo kumi na tatu", "Kubwa kumi na tatu",
    "Ndogo kumi na nne", "Kubwa kumi na nne", "Octave mara mbili")
val doublingPt = listOf("2ª menor","2ª Maior", "3ª menor", "3ª Maior", "4ª",
    "4ª Aumentada", "5ª", "6ª menor", "6ª Maior", "7ª menor", "7ª Maior",
    "Oitava", "9ª menor", "9ª Maior", "10ª menor", "10ª Maior", "11ª",
    "11ª Aumentada", "12ª", "13ª menor", "13ª Maior", "14ª menor", "14ª Maior", "Oitava dupla")
val functionNamesEn = listOf("Wave 3", "Wave 4", "Wave 6")
val intervalSetIt = listOf("2m\n7M","2M\n7m","3m\n6M","3M\n6m","4\n5","4A\n5d","U\n8")
val intervalSetEn = listOf("m2\nM7","M2\nm7","m3\nM6","M3\nm6","4\n5","A4\nd5","U\n8")
val intervalSetDe = listOf("k2\nG7","G2\nk7","k3\nG6","G3\nk6","4\n5","Ü4\nv5","1\n8")
val intervalSetRu = listOf("2м\n7В","2В\n7м","3м\n6В","3В\n6м","4\n5","4У\n5у","1\n8")
fun getIntervalsForTranspose(intervalSet: List<String> = intervalSetEn): List<String>{
    val split = intervalSet.map{ it.split("\n")}
    return listOf(split[6][0], split[0][0], split[1][0], split[2][0], split[3][0], split[4][0],
        split[5][0], split[4][1], split[3][1], split[2][1], split[1][1], split[0][1])
}
val rowFormsMap = mapOf(
    1 to "O", 2 to "I" , 3 to "R", 4 to "RI", -1 to "O |", -2 to "I |" , -3 to "R |", -4 to "RI |"
)
val melodyTypeMap = mapOf(
    0 to "L", 1 to "H"
)
val rangeTypeMap = mapOf(
    0 to "@", 1 to "I", 2 to "[--]", 3 to "[-]", 4 to "[ ]"
)
val legatoTypeMap = mapOf(
    0 to "S+", 1 to "S", 2 to "P", 3 to "A", 4 to "L", 5 to "L+"
)

data class Lang( // English by default
    val noteNames: List<String> = NoteNamesEn.values().map { it.toString() },
    val intervalSet: List<String> = intervalSetEn,
    val enterSomeNotes: String = "Enter some notes!",
    val choose2ndSequence: String = "Choose the second sequence!",
    val repeatSequence: String = "Repeat the sequence",
    val selectSpecialFunction: String = "Select a Special Function!",
    val specialFunctionNames: List<String> = functionNamesEn,
    val OKbutton: String = "OK",
    val selectEnsemble: String = "Select an ensemble!",
    val ensembleNames: List<String> = ensembleNamesEn,

    val selectRhythm: String = "Select a rhythm!",
    val selectDoubling: String = "Select some intervals for doubling!",
    val doublingNames: List<String> = doublingEn,
    val beatsPerMinute: String = "Beats Per Minute",
    val ensemble: String = "Ensemble",
    val selectRange: String = "Select a range type!",
    val range: String = "Range",
    val rangeOptions: List<String> = listOf("Free", "Of the instrument", "Delimited", "Almost closed", "Closed"),
    val selectArticulation: String = "Select an articulation type!",
    val articulation: String = "Articulation",
    val articulationOptions: List<String> = listOf("Staccatissimo", "Staccato", "Portato", "Articolato", "Legato", "Legatissimo"),
    val selectMelody: String = "Select a melody type!",
    val melody: String = "Melody",
    val melodyOptions: List<String> = listOf("Linear", "Broad"),
    val glissando: String = if(android.os.Build.VERSION.SDK_INT >=android.os.Build.VERSION_CODES.O)
        "\uD834\uDDB1\uD834\uDDB2\uD834\uDDB1"
    else "Glissando",
    val selectGlissando: String = "Select some intervals for glissando!",
    val vibrato: String = "Vibrato",
    val selectVibrato: String = "Select the vibrato intensity!",
    val nuances: String = "Nuances",
    val nuancesOptions: List<String> = listOf("None", "Exalt short notes", "Exalt long notes"),
    val selectNuances: String = "Select dynamic nuances!",
    val dynamic: String = "Dynamic",
    val selectDynamicAlterations: String ="Select the dynamic alterations!",
    val bpm: String = "BPM",
    val bpmAlterations: String = "BPM alterations",
    val selectBpmAlterations: String = "Select the BPM alterations!",
    val alterationOptions: List<String> = listOf("BPM", "BPM *2", "BPM /2",
        "BPM *2 BPM", "BPM /2 BPM", "BPM *2 BPM /2", "BPM /2 BPM *2"),
    val rhythm: String = "Rhythm",
    val rhythmShuffle: String = "Rhythm shuffle",
    val partsShuffle: String = "Parts shuffle",
    val rowForms: String = "Row forms",
    val selectRowForms: String = "Select Row forms!",
    val original: String = "Original",
    val retrograde: String = "Retrograde",
    val inverse: String = "Inverse",
    val invRetrograde: String = "Inv-Retrograde",
    val ritornello: String = if(android.os.Build.VERSION.SDK_INT >=android.os.Build.VERSION_CODES.O)
        String(Character.toChars(0x1D106)) + "  " + String(Character.toChars(0x1D107))
                            else "Ritornello",
    val selectRitornello: String = "Select how many repetitions!",
    val transpose: String = "Transpose",
    val selectTranspositions: String = "Select transpositions!",
    val doubling: String = "Doubling",
    val audio8D: String = "8D AUDIO",
    val selectAudio8D: String = "Select voices for 8D AUDIO!",
    val spreadWherePossible: String = "Spread where possible",
    val deepSearch: String = "Deep search in four-part canons",
    val horIntervalSet: String = "Free part intervals",
    val selectIntervalsForFP: String = "Select the melodic intervals for the Fioritura and the free parts!",
    val FPremember: String = "(  ☀  ∼➚  ∼➘  -➚  -➘  )   ",
    val detector: String = "Detector",
    val selectIntervalsToDetect: String = "Select intervals to detect!",
    val detectorExtension: String = "Detector extension",
    val selectDetectorExtension: String = "Select the extension for the detector!",
    val exportMidi: String = "Export MIDI",
    val playToCreate: String = "Play a counterpoint to create a MIDI file!",
    val language: String = "Language",
    val zodiac: String = "Zodiac",
    val zodiacOptions: List<String> = listOf("Planets", "Signs", "Emojis"),
    val selectZodiac: String = "Use these zodiac symbols: ",
    val credits: String = "Credits"
    ){


    companion object {
        fun provideLanguage(lang: String): Lang {
            return when (lang){
                "ar" -> arabian()
                "de" -> german()
                "el" -> greek()
                "en" -> english()
                "es" -> spanish()
                "fr" -> french()
                "hi" -> hindi()
                "ko" -> korean()
                "jp" -> japanese()
                "id" -> bahasa()
                "it" -> italian()
                "pt" -> portugues()
                "ru" -> russian()
                "sw" -> kiswahili()
                "zh" -> chinese()
                else -> Lang()
            }
        }
        fun english(): Lang {
            return Lang()
        }
        fun french(): Lang {
            return Lang(
                noteNames = NoteNamesFr.values().map { it.toString() },
                intervalSet = intervalSetIt,
                ensemble = "Ensemble ", // let a space because in french [word] [_] [:|!|?] [_]
                bpm = "BPM ",
                enterSomeNotes = "Entrez quelques notes !",
                choose2ndSequence = "Choisissez la deuxième séquence !",
                repeatSequence = "Répéter la séquence",
                selectEnsemble = "Choisissez un ensemble !",
                ensembleNames = ensembleNamesFr,
                selectRange = "Sélectionnez un type de gamme !",
                range = "Gamme ",
                rangeOptions = listOf("Libre", "De l'instrument", "Délimitée", "Presque fermée", "Fermée"),
                selectMelody ="Sélectionnez un type de mélodie !",
                melody = "Mélodie ",
                melodyOptions = listOf("Linéaire","Large"),
                nuances = "Nuances",
                selectNuances = "Choisissez les nuances pour la dynamique !",
                nuancesOptions = listOf("Rien", "En relief les notes courtes", "En relief les notes longues"),
                beatsPerMinute = "Battements par minute ",
                selectRhythm = "Choisissez un rythme !",
                selectDoubling = "Choisissez des intervalles pour doubler !",
                doublingNames = doublingFr,
                rhythm = "Rythme ", // let a space because in french [word] [_] [:|!|?] [_]
                rhythmShuffle  = "Mélanger le rythme",
                partsShuffle  = "Mélanger les voix",
                rowForms = "Formes sérielles ",
                selectRowForms = "Choisissez des formes sérielles !",
                original = "Originelle",
                retrograde  = "Rétrograde",
                inverse  = "Miroir",
                invRetrograde  = "Miroir du rétrograde",
                selectRitornello = "Choisissez le nombre de répétitions !",
                transpose = "Transposer",
                selectTranspositions = "Sélectionnez les transpositions !",
                doubling  = "Redoublements ", // let a space because in french [word] [_] [:|!|?] [_]
                spreadWherePossible  = "Répandre là où c'est possible",
                deepSearch  = "Recherche poussée dans les canons à quatre voix",
                horIntervalSet = "Intervalles de voix libres",
                selectIntervalsForFP = "Choisissez les intervalles mélodiques pour les voix libres!",
                detector = "Détector",
                selectIntervalsToDetect = "Choisissez les intervalles à détecter !",
                detectorExtension = "Extension du détecteur",
                selectDetectorExtension = "Choisissez l'extension du détecteur !",
                exportMidi  = "Exporter le fichier MIDI",
                language  = "Langue ",
                zodiac = "Zodiaque ",
                zodiacOptions = listOf("Planètes", "Signes", "Emojis"),
                selectZodiac = "Utilisez ces symboles du zodiaque : "
            )
        }
        fun italian(): Lang {
            return Lang(
                noteNames = NoteNamesIt.values().map { it.toString() },
                intervalSet = intervalSetIt,
                enterSomeNotes = "Digita delle note!",
                choose2ndSequence = "Scegli la seconda sequenza!",
                repeatSequence = "Ripeti la sequenza",
                selectEnsemble = "Scegli un ensemble!",
                ensembleNames = ensembleNamesIt,
                range = "Estensione",
                selectRange = "Scegli un'estensione!",
                rangeOptions = listOf("Libera", "Dello strumento", "Delimitata", "Quasi chiusa", "Chiusa"),
                selectMelody ="Seleziona un tipo di melodia!",
                melody = "Melodia",
                melodyOptions = listOf("Lineare","Ampia"),
                nuancesOptions = listOf("Nessuna", "In rilievo le note brevi", "In rilievo le note lunghe"),
                nuances = "Nuances",
                selectNuances = "Scegli le nuances per la dinamica!",
                beatsPerMinute = "Pulsazioni al minuto",
                selectRhythm = "Scegli un ritmo!",
                selectDoubling = "Scegli degli intervalli per il raddoppio!",
                doublingNames = doublingIt,
            rhythm = "Ritmo",
             rhythmShuffle  = "Mescola il ritmo",
             partsShuffle  = "Mescola le parti",
                rowForms = "Forme seriali",
                selectRowForms = "Scegli le forme seriali!",
                original = "Originale",
                retrograde  = "Retrogrado",
                inverse  = "Inverso",
                invRetrograde  = "Inverso del retrogrado",
                selectRitornello = "Scegli quante ripetizioni!",
                transpose = "Trasponi",
                selectTranspositions = "Scegli le trasposizioni!",
             doubling  = "Raddoppi",
             spreadWherePossible  = "Estendi dove è possibile",
             deepSearch  = "Ricerca approfondita nei canoni a quattro parti",
                horIntervalSet = "Intervalli delle parti libere",
                selectIntervalsForFP = "Scegli gli intervalli melodici per le fioriture e le parti libere!",
                detector = "Rilevatore",
                selectIntervalsToDetect = "Scegli gli intervalli da rilevare!",
                detectorExtension = "Raggio del rilevatore",
                selectDetectorExtension = "Scegli il raggio del rilevatore!",
             exportMidi  = "Esporta il file MIDI",
             language  = "Lingua",
                zodiac = "Zodiaco",
                zodiacOptions = listOf("Pianeti", "Segni", "Emojis"),
                selectZodiac = "Usa questi simboli zodiacali:"
            )
        }
        fun spanish(): Lang {
            return Lang(
                noteNames = NoteNamesIt.values().map { it.toString() },
                intervalSet = intervalSetIt,
                enterSomeNotes = "¡Escribe algunas notas!",
                choose2ndSequence = "¡Elige la segunda secuencia!",
                repeatSequence = "Repite la secuencia",
                selectEnsemble = "¡Elige un ensemble!",
                ensembleNames = ensembleNamesEs,
                range = "Extensión",
                selectRange = "¡Elija una extensión!",
                rangeOptions = listOf("Libre", "Del instrumento", "Limitada", "Casi cerrada", "Cerrada"),
                selectMelody ="¡Selecciona un tipo de melodía!",
                melody = "Melodía",
                melodyOptions = listOf("Lineal","Amplia"),
                nuances = "Matices",
                selectNuances = "¡Elige los matices para la dinámica!",
                nuancesOptions = listOf("Sin matices", "En relieve las notas breves.", "En relieve las notas largas."),
                beatsPerMinute = "Pulsos por minuto",
                selectRhythm = "¡Elige un ritmo!",
                selectDoubling = "¡Elija intervalos para duplicar!",
                doublingNames = doublingEs,
                rhythm = "Ritmo",
                rhythmShuffle  = "Mezclar el ritmo",
                partsShuffle  = "Mezclar las voces ",
                rowForms = "Formas de serie",
                selectRowForms = "¡Elija las formas de serie!",
                original = "Original",
                retrograde  = "Retrógrado",
                inverse  = "Inversión",
                invRetrograde  = "Retrógrado de la inversión",
                selectRitornello = "¡Elige cuántas repeticiones!",
                transpose = "Trasponer",
                selectTranspositions = "¡Elija las transposiciones!",
                doubling  = "Duplicaciones",
                spreadWherePossible  = "Difundir donde sea posible",
                deepSearch  = "Búsqueda profunda en cánones de cuatro partes",
                horIntervalSet = "Intervalos de voces libres",
                selectIntervalsForFP = "Elige los intervalos melódicos de las voces libres!",
                detector = "Detector",
                selectIntervalsToDetect = "¡Elija los intervalos a detectar!",
                detectorExtension = "Extensión del detector",
                selectDetectorExtension = "Elija la extensión del detector!",
                exportMidi  = "Esporta el archivo MIDI",
                language  = "Lengua",
                zodiac = "Zodíaco",
                zodiacOptions = listOf("Planetas", "Signos", "Emojis"),
                selectZodiac = "Utilice estos símbolos del zodíaco:"
            )
        }
        fun german(): Lang {
            return Lang(
                noteNames = NoteNamesEn.values().map { it.toString() },
                intervalSet = intervalSetDe,
                enterSomeNotes = "Tippe ein paar Noten ein!",
                choose2ndSequence = "Wähle die zweite Sequenz!",
                repeatSequence = "Wiederhole die Sequenz",
                selectEnsemble = "Wähle ein Ensemble!",
                ensembleNames = ensembleNamesDe,
                range = "Reichweite",
                selectRange = "Wähle eine Reichweite!",
                rangeOptions = listOf("Kostenlose", "Des Instruments", "Schmale", "Fast geschlossene", "Geschlossene"),
                selectMelody ="Wählen Sie einen Melodietyp!",
                melody = "Melodie",
                melodyOptions = listOf("Lineare","Breite"),
                nuances = "Nuancen",
                selectNuances = "Wählen Sie die Nuancen für die Dynamik!",
                nuancesOptions = listOf("Keine Nuance", "Die kurzen Töne sind im Relief", "Die langen Töne sind im Relief"),
                beatsPerMinute = "Beats Per Minute",
                selectRhythm = "Wähle einen Rhythmus!",
                selectDoubling = "Wähle die Intervalle für die Verdoppelung!",
                doublingNames = doublingDe,
                rhythm = "Rhythmus",
                rhythmShuffle  = "Gemischter Rhythmus",
                partsShuffle  = "Gemischte Stimmen",
                rowForms = "Reihenformen",
                selectRowForms = "Wählen Sie Reihenformen!",
                original = "Grundreihe",
                retrograde  = "Krebs",
                inverse  = "Umkehrung",
                invRetrograde  = "Krebsumkehrung",
                selectRitornello = "Wählen Sie, wie viele Wiederholungen!",
                transpose = "Transponieren",
                selectTranspositions = "Wählen Sie die Transpositionen!",
                doubling  = "Verdoppelungen",
                spreadWherePossible  = "Nach Möglichkeit verlängern",
                deepSearch  = "Tiefensuche in vierstimmigen Kanons",
                horIntervalSet = "Intervalle freier Stimmen",
                selectIntervalsForFP = "Wählen Sie die melodischen Intervalle der freien Stimmen!",
                detector = "Detektor",
                selectIntervalsToDetect = "Wählen Sie die zu erkennenden Intervalle!",
                detectorExtension = "Erweiterung des Detektors",
                selectDetectorExtension = "Wählen Sie die Detektorerweiterung!",
                exportMidi  = "Exportieren Sie die MIDI-Datei",
                language  = "Sprache",
                zodiac = "Tierkreis",
                zodiacOptions = listOf("Planeten", "Sternzeichen", "Emojis"),
                selectZodiac = "Verwenden Sie diese Sternzeichen:"
            )
        }
        fun russian(): Lang {
            return Lang(
                noteNames = NoteNamesRu.values().map { it.toString() },
                intervalSet = intervalSetRu,
                enterSomeNotes = "Введите ноты!",
                choose2ndSequence = "Выберите вторую последовательность!",
                repeatSequence = "Повторите последовательность",
                ensemble = "Ансамбль",
                bpm = "БПМ",
                selectEnsemble = "Выбери ансамбль!",
                ensembleNames = ensembleNamesRu,
                selectRange = "Выберите расширение!",
                range = "Расширение",
                rangeOptions = listOf("Бесплатно", "По инструменту", "С разделителями", "Почти закрыто", "Закрыто"),
                selectMelody ="Выберите тип мелодии!",
                melody = "Мелодия",
                melodyOptions = listOf("Линейная","Широкая"),
                nuances = "Нюансы",
                selectNuances = "Выбирайте нюансы по динамике!",
                nuancesOptions = listOf("Без нюансов", "В рельефе короткие ноты", "В рельефе длинные ноты"),
                beatsPerMinute = "Удары в минуту",
                selectRhythm = "Выберите ритм!",
                selectDoubling = "Выбирайте интервалы для удвоения!",
                doublingNames = doublingRu,
                rhythm = "Ритм",
                rhythmShuffle  = "Смешанный ритм",
                partsShuffle  = "Смешанные голоса",
                rowForms = "Серийные формы",
                selectRowForms = "Выбирайте серийные формы!",
                original = "Оригинал",
                retrograde  = "Ракоход",
                inverse  = "Инверсия",
                invRetrograde  = "Ракоход-инверсия",
                selectRitornello = "Выбирайте количество повторов!",
                transpose = "Транспонировать",
                selectTranspositions = "Выбирайте транспозиции!",
                doubling  = "Двойной",
                spreadWherePossible  = "По возможности расширяйте",
                deepSearch  = "Глубокий поиск в четырехголосых канонах",
                horIntervalSet = "Интервалы свободных голосов",
                selectIntervalsForFP = "Выберите мелодические интервалы свободных голосов!",
                detector = "Детектор",
                selectIntervalsToDetect = "Выберите интервалы для обнаружения!",
                detectorExtension = "Расширение детектора",
                selectDetectorExtension = "Выберите расширение детектора!",
                exportMidi  = "Экспорт файла МИДИ",
                language  = "Язык",
                zodiac = "Зодиак",
                zodiacOptions = listOf("Планеты", "Знаки", "Emojis"),
                selectZodiac = "Используйте эти символы зодиака:"
            )
        }
        fun korean(): Lang {
            return Lang(
                noteNames = NoteNamesIt.values().map { it.toString() },
                enterSomeNotes = "음표를 입력하세요!",
                choose2ndSequence = "두 번째 시퀀스를 선택하십시오!",
                repeatSequence = "순서를 반복하십시오",
                selectEnsemble = "앙상블을 선택하십시오!",
                ensembleNames = ensembleNamesKo,
                range = "확장",
                selectRange = "확장자를 선택하십시오!",
                rangeOptions = listOf("무료", "도구 중", "구분된", "거의 닫힘", "닫힘"),
                selectMelody ="멜로디의 종류를 선택하세요!",
                melody = "멜로디",
                melodyOptions = listOf("선형","넓은"),
                nuances = "뉘앙스",
                selectNuances = "역학에 대한 뉘앙스를 선택하십시오!",
                nuancesOptions = listOf("뉘앙스가 없습니다", "안도의 짧은 음표", "긴 음표에 안심"),
                beatsPerMinute = "분당 박동",
                ensemble = "앙상블",
                selectRhythm = "리듬을 선택하세요!",
                selectDoubling = "배가 간격을 선택하십시오!",
                doublingNames = doublingKo,
                rhythm = "율",
                rhythmShuffle  = "혼합 된 리듬",
                partsShuffle  = "혼성",
                rowForms = "직렬 형식",
                selectRowForms = "직렬 모양을 선택하십시오!",
                original = "원래의",
                retrograde  = "뒤로 걷다",
                inverse  = "역 동작",
                invRetrograde  = "움직임을 뒤집고 뒤로 걸어",
                selectRitornello = "반복 횟수를 선택하십시오!",
                transpose = "바꾸어 놓다",
                selectTranspositions = "조옮김을 선택하십시오!",
                doubling  = "복식",
                spreadWherePossible  = "가능한 한 확장",
                deepSearch  = "네 가지 음성 음악 캐논에서 심층 검색",
                horIntervalSet = "자유로운 목소리의 간격",
                selectIntervalsForFP = "자유로운 목소리의 멜로디 간격을 선택하십시오!",
                detector = "탐지기",
                selectIntervalsToDetect = "감지할 간격을 선택하십시오!",
                detectorExtension = "감지기의 확장",
                selectDetectorExtension = "감지기 확장 선택!",
                exportMidi  = "MIDI 파일 내보내기",
                language  = "언어",
                zodiac = "황도 십이궁",
                zodiacOptions = listOf("행성", "조디악의 징후", "Emojis"),
                selectZodiac = "다음 조디악 기호를 사용하십시오:"
            )
        }
        fun japanese(): Lang {
            return Lang(
                noteNames = NoteNamesIt.values().map { it.toString() },
                enterSomeNotes = "音符を入力してください！",
                choose2ndSequence = "2番目のシーケンスを選択してください！",
                repeatSequence = "シーケンスを繰り返します",
                selectEnsemble = "アンサんブルを選いしてください！",
                ensembleNames = ensembleNamesJp,
                range = "拡大",
                selectRange = "拡張子を選択してください！",
                rangeOptions = listOf("無料", "楽器の", "区切り", "ほぼ閉じている", "閉じている"),
                selectMelody = "メロディーの種類を選択してください！",
                melody = "メロディー",
                melodyOptions = listOf("直線的な","幅広い"),
                nuances = "ニュアンス",
                selectNuances = "ダイナミクスのニュアンスを選択してください！",
                nuancesOptions = listOf("ニュアンスはありません", "安心して短い音符", "安心して長い音符"),
                beatsPerMinute = "分あたりの拍数",
                ensemble = "アンサンブル",
                selectRhythm = "リズムを選んでください！",
                selectDoubling = "倍増の間隔を選択してください！",
                doublingNames = doublingJp,
                rhythm = "リズム",
                rhythmShuffle  = "混合リズム",
                partsShuffle  = "混合声",
                rowForms = "シリアルフォーム",
                selectRowForms = "シリアル形状を選択してください！",
                original = "オリジナル",
                retrograde  = "後ろ向きに歩く",
                inverse  = "間隔の反転",
                invRetrograde  = "後ろ向きに歩くことで間隔を逆にする",
                selectRitornello = "担当者の数を選択してください！",
                transpose = "転置",
                selectTranspositions = "転置を選択してください！",
                doubling  = "ダブルス",
                spreadWherePossible  = "可能な場合は延長する",
                deepSearch  = "4つの音声カノンでの詳細検索",
                horIntervalSet = "自由な声の間隔",
                selectIntervalsForFP = "フリーボイスのメロディー間隔を選択してください！",
                detector = "検出器",
                selectIntervalsToDetect = "検出する間隔を選択してください！",
                detectorExtension = "検出器の拡張",
                selectDetectorExtension = "検出器の拡張機能を選択します！",
                exportMidi  = "MIDIファイルをエクスポートする",
                language  = "言語",
                zodiac = "干支",
                zodiacOptions = listOf("惑星", "干支の兆候", "Emojis"),
                selectZodiac = "次の星座記号を使用します:"
            )
        }
        fun chinese(): Lang {
            return Lang(
                noteNames = NoteNamesIt.values().map { it.toString() },
                enterSomeNotes = "键入一些音符时间",
                choose2ndSequence = "选择第二个序列！",
                repeatSequence = "重复序列",
                selectEnsemble = "选择合奏！",
                ensembleNames = ensembleNamesZh,
                range = "延期",
                selectRange = "选择扩展名！",
                rangeOptions = listOf("免费","乐器的","分隔的","几乎关闭","关闭"),
                selectMelody = "选择一种旋律！",
                melody = "旋律",
                melodyOptions = listOf("线性旋律","宽广的旋律"),
                nuances = "细微差别",
                selectNuances = "选择动态的细微差别",
                nuancesOptions = listOf("没有细微差别", "在浮雕的短音符", "长长的音符让人浮想联翩"),
                beatsPerMinute = "每分钟节拍",
                ensemble = "合奏",
                selectRhythm = "选择节奏！",
                selectDoubling = "选择度数加倍！",
                doublingNames = doublingZh,
                rhythm = "韵律",
                rhythmShuffle  = "混合节奏",
                partsShuffle  = "混合的声音",
                rowForms = "系列表格",
                selectRowForms = "选择系列形状！",
                original = "原来的",
                retrograde  = "向后走",
                inverse  = "反转间隔",
                invRetrograde  = "向后走并反转间隔",
                selectRitornello = "选择多少次！",
                transpose = "转置",
                selectTranspositions = "选择换位！",
                doubling  = "双打",
                spreadWherePossible  = "尽可能延长",
                deepSearch  = "四个语音标准中的深度搜索",
                horIntervalSet = "自由声音的间隔",
                selectIntervalsForFP = "选择自由声音的旋律音程！",
                detector = "探测器",
                selectIntervalsToDetect = "选择要检测的间隔！",
                detectorExtension = "探测器的扩展",
                selectDetectorExtension = "选择检测器扩展！",
                exportMidi  = "导出 MIDI 文件",
                language  = "语",
                zodiac = "十二生肖",
                zodiacOptions = listOf("行星", "十二生肖", "Emojis"),
                selectZodiac = "使用这些生肖符号："
            )
        }

        fun arabian(): Lang {
            return Lang(
                noteNames = listOf("دو","ري","مي","فا","صول","لا","سي"),
                enterSomeNotes = "!اكتب بعض النوتات الموسيقية",
                choose2ndSequence = "!اختر التسلسل الثاني",
                repeatSequence = "كرر التسلسل",
                ensemble ="الفرقة",
                selectEnsemble = "!اختر مجموعة",
                ensembleNames = ensembleNamesAr,
                range = "تمديد",
                selectRange = "اختر امتدادًا!",
                rangeOptions = listOf("مجانا", "من الصك","المحصورة","شبه مغلق","مغلق"),
                selectMelody = "حدد نوع اللحن!",
                melody = "لحن",
                melodyOptions = listOf("لحن خطي","لحن واسع"),
                nuances = "الفروق الدقيقة",
                selectNuances = "اختر الفروق الدقيقة للديناميكيات!",
                nuancesOptions = listOf("لا فارق بسيط", "في الإغاثة الملاحظات القصيرة", "الملاحظات الطويلة غير واضحة"),
                beatsPerMinute = "نبضة في الدقيقة",
                selectRhythm = "!اختر إيقاعًا",
                selectDoubling = "!اختر فترات لمضاعفة",
                doublingNames = doublingIt,
                rhythm = "إيقاع",
                rhythmShuffle  = "إيقاع مختلط",
                partsShuffle  = "اصوات مختلطة",
                rowForms = "الأشكال التسلسلية",
                selectRowForms = "!اختر الأشكال التسلسلية",
                original = "إبداعي",
                retrograde  = "المشي إلى الخلف",
                inverse  = "اعكس الحركات",
                invRetrograde  = "اعكس الحركات وامش للخلف",
                selectRitornello = "!اختر عدد الممثلين",
                transpose = "تبديل موضع",
                selectTranspositions = "!اختر التبديلات",
                doubling  = "الزوجي",
                spreadWherePossible  = "قم بالتمديد حيثما أمكن ذلك",
                deepSearch  = "بحث عميق في أربعة شرائع صوتية",
                horIntervalSet = "فترات من الأصوات الحرة",
                selectIntervalsForFP = "!اختر الفترات اللحنية للأصوات المجانية",
                detector = "كاشف",
                selectIntervalsToDetect = "!اختر الفترات المراد اكتشافها",
                detectorExtension = "امتداد الكاشف",
                selectDetectorExtension = "!اختر امتداد الكاشف",
                exportMidi  = "تصدير ملف MIDI",
                language  = "لغة",
                zodiac = "الأبراج الفلكية",
                zodiacOptions = listOf("الكواكب", "علامات البروج", "Emojis"),
                selectZodiac = ":استخدم رموز الأبراج هذه"
            )
        }
        fun greek(): Lang {
            return Lang(
                noteNames = NoteNamesEl.values().map { it.toString() },
                intervalSet = intervalSetEn,
                enterSomeNotes = "Πληκτρολογήστε μερικές νότες!",
                choose2ndSequence = "Επιλέξτε τη δεύτερη ακολουθία!",
                repeatSequence = "Επαναλάβετε την ακολουθία",
                ensemble ="Σύνολο",
                bpm = "ΒΠΜ",
                selectEnsemble = "Επιλέξτε ένα σύνολο!",
                ensembleNames = ensembleNamesEl,
                range = "Εύρος",
                selectRange = "Επιλέξτε ένα εύρος!",
                rangeOptions = listOf("Δωρεάν", "Από το όργανο", "Οριοθετημένο", "Σχεδόν κλειστό", "Κλειστό"),
                selectMelody = "Επιλέξτε έναν τύπο μελωδίας!",
                melody = "Μελωδία",
                melodyOptions = listOf("Γραμμική","Ευρεία"),
                nuances = "Αποχρώσεις",
                selectNuances = "Επιλέξτε τις αποχρώσεις για τη δυναμική!",
                nuancesOptions = listOf("Καμία απόχρωση", "Ανακουφισμένες οι σύντομες νότες", "Ανακουφισμένες οι μακριές νότες"),
                beatsPerMinute = "Χτυπάει ανά λεπτό",
                selectRhythm = "Επιλέξτε ρυθμό!",
                selectDoubling = "Επιλέξτε διαστήματα για διπλασιασμό!",
                doublingNames = doublingEl,
                rhythm = "Ρυθμός",
                rhythmShuffle  = "Μικτός ρυθμός",
                partsShuffle  = "Φωνές μικτές",
                rowForms = "Σειριακές φόρμες",
                selectRowForms = "Επιλέξτε σειριακά σχήματα!",
                original = "Πρωτότυπο",
                retrograde  = "Τον καρκίνο",
                inverse  = "Την αναστροφή ",
                invRetrograde  = "Την καρκινική αναστροφή",
                selectRitornello = "Επιλέξτε πόσες επαναλήψεις!",
                transpose = "Μεταθέτω",
                selectTranspositions = "Επιλέξτε τις μεταθέσεις!",
                doubling  = "Διπλασιάζω",
                spreadWherePossible  = "Επεκτείνετε όπου είναι δυνατόν",
                deepSearch  = "Βαθιά αναζήτηση σε τέσσερις φωνητικούς κανόνες",
                horIntervalSet = "Διαστήματα ελεύθερων φωνών",
                selectIntervalsForFP = "Επιλέξτε τα μελωδικά διαστήματα των ελεύθερων φωνών!",
                detector = "Ανιχνευτής",
                selectIntervalsToDetect = "Επιλέξτε τα διαστήματα που θα εντοπιστούν!",
                detectorExtension = "Επέκταση ανιχνευτή",
                selectDetectorExtension = "Επιλέξτε την επέκταση ανιχνευτή!",
                exportMidi  = "Εξαγωγή του αρχείου MIDI",
                language  = "Γλώσσα",
                zodiac = "Ζωδιακός κύκλος",
                zodiacOptions = listOf("Πλανήτες", "Ζώδια", "Emojis"),
                selectZodiac = "Χρησιμοποιήστε αυτά τα ζώδια:"
            )
        }
        fun kiswahili(): Lang {
            return Lang(
                noteNames = NoteNamesIt.values().map { it.toString() },
                intervalSet = intervalSetIt,
                enterSomeNotes = "Chapa maelezo kadhaa!",
                choose2ndSequence = "Chagua mlolongo wa pili!",
                repeatSequence = "Kurudia mlolongo",
                selectEnsemble = "Chagua mkusanyiko!",
                ensembleNames = ensembleNamesSw,
                range = "Mbalimbali",
                selectRange = "Chagua masafa!",
                rangeOptions = listOf("Bure","Ya chombo","Imepunguzwa","Karibu Imefungwa","Imefungwa"),
                selectMelody = "Chagua aina ya wimbo!",
                melody = "Melody",
                melodyOptions = listOf("Ya laini","Pana"),
                nuances = "Nuances",
                selectNuances = "Chagua nuances kwa mienendo!",
                nuancesOptions = listOf("Hakuna nuance", "Kwa utulivu maelezo mafupi ya muziki", "Kwa misaada noti ndefu za muziki"),
                beatsPerMinute = "Beats kwa dakika",
                selectRhythm = "Chagua dansi!",
                selectDoubling = "Chagua vipindi vya kuongezeka mara mbili!",
                doublingNames = doublingSw,
                rhythm = "Mdundo",
                rhythmShuffle  = "Mdundo mchanganyiko",
                partsShuffle  = "Sauti zilizochanganywa",
                rowForms = "Aina za serial",
                selectRowForms = "Chagua maumbo ya serial!",
                original = "Asili",
                retrograde  = "Tembea nyuma",
                inverse  = "Kubadili harakati",
                invRetrograde  = "Tembea nyuma na kubadili harakati",
                selectRitornello = "Chagua reps ngapi!",
                transpose = "Kubadilisha",
                selectTranspositions = "Chagua mabadiliko!",
                doubling  = "Maradufu",
                spreadWherePossible  = "Panua panapowezekana",
                deepSearch  = "Utafutaji wa kina katika kanuni nne za sauti",
                horIntervalSet = "Vipindi vya sauti za bure",
                selectIntervalsForFP = "Chagua vipindi vya sauti ya sauti za bure!",
                detector = "Kigunduzi",
                selectIntervalsToDetect = "Chagua vipindi vya kugunduliwa!",
                detectorExtension = "Ugani wa detector",
                selectDetectorExtension = "Chagua kiendelezi cha kipelelezi!",
                exportMidi  = "Hamisha faili ya MIDI",
                language  = "Lugha",
                zodiac = "Zodiac",
                zodiacOptions = listOf("Sayari", "Ishara", "Emojis"),
                selectZodiac = "Tumia alama hizi za zodiac:"
            )
        }
        fun portugues(): Lang {
            return Lang(
                noteNames = NoteNamesPt.values().map { it.toString() },
                intervalSet = intervalSetIt,
                enterSomeNotes = "Digite algumas notas!",
                choose2ndSequence = "Escolha a segunda sequência!",
                repeatSequence = "Repita a sequência",
                ensemble ="Conjunto",
                selectEnsemble = "Escolha um conjunto!",
                ensembleNames = ensembleNamesPt,
                range = "Faixa",
                selectRange = "Escolha um intervalo!",
                rangeOptions = listOf("Livre","Do instrumento","Delimitado","Quase fechado","Fechado"),
                selectMelody = "Selecione um tipo de melodia!",
                melody = "Melodia",
                melodyOptions = listOf("Linear","Ampla"),
                nuances = "Nuances",
                selectNuances = "Escolha as nuances para a dinâmica!",
                nuancesOptions = listOf("Sem nuances", "Em relevo as notas curtas", "Em relevo as notas longas"),
                beatsPerMinute = "Batimentos por minuto",
                selectRhythm = "Escolha um ritmo!",
                selectDoubling = "Escolha intervalos para dobrar!",
                doublingNames = doublingPt,
                rhythm = "Ritmo",
                rhythmShuffle  = "Misture o ritmo",
                partsShuffle  = "Misture as vozes ",
                rowForms = "Formas seriais",
                selectRowForms = "Escolha formas seriais!",
                original = "Original",
                retrograde  = "Retrogradação",
                inverse  = "Inversão",
                invRetrograde  = "Inverso de retrogradação",
                selectRitornello = "Escolha quantas repetições!",
                transpose = "Transpor",
                selectTranspositions = "Escolha as transposições!",
                doubling  = "Duplas",
                spreadWherePossible  = "Estenda onde for possível",
                deepSearch  = "Pesquisa profunda em cânones de quatro vozes",
                horIntervalSet = "Intervalos de vozes livres",
                selectIntervalsForFP = "Escolha os intervalos melódicos de vozes livres",
                detector = "Detector",
                selectIntervalsToDetect = "Escolha os intervalos a serem detectados!",
                detectorExtension = "Extensão do detector",
                selectDetectorExtension = "Escolha a extensão do detector!",
                exportMidi  = "Exporte o arquivo MIDI",
                language  = "Língua",
                zodiac = "Zodíaco",
                zodiacOptions = listOf("Planetas", "Signos", "Emojis"),
                selectZodiac = "Use estes símbolos do zodíaco:"
            )
        }
        fun bahasa(): Lang {
            return Lang(
                noteNames = NoteNamesIt.values().map { it.toString() },
                intervalSet = intervalSetEn,
                enterSomeNotes = "Ketik beberapa not!",
                choose2ndSequence = "Scegli la seconda sequenza!",
                repeatSequence = "Pilih urutan kedua!",
                ensemble = "Ansambel",
                selectEnsemble = "Pilih ansambel!",
                ensembleNames = ensembleNamesId,
                range = "Jangkauan",
                selectRange = "Pilih rentang!",
                rangeOptions = listOf("Tak terbatas","Dari instrumen","Dibatasi", "Hampir Ditutup", "Tutup"),
                selectMelody = "Pilih jenis melodi!",
                melody = "Melodi",
                melodyOptions = listOf("Linier","Yang luas"),
                nuances = "Nuansa",
                selectNuances = "Pilih nuansa untuk dinamika!",
                nuancesOptions = listOf("Tidak ada nuansa", "Relief catatan musik pendek", "Relief catatan musik yang panjang"),
                beatsPerMinute = "Detak per menit",
                selectRhythm = "Pilih irama!",
                selectDoubling = "Pilih interval untuk menggandakan!",
                doublingNames = doublingId,
                rhythm = "Irama",
                rhythmShuffle  = "Campurkan irama",
                partsShuffle  = "Campur suara",
                rowForms = "Bentuk serial",
                selectRowForms = "Pilih bentuk serial!",
                original = "Asli",
                retrograde  = "Berjalan mundur",
                inverse  = "Membalikkan gerakan",
                invRetrograde  = "Membalikkan gerakan dan berjalan mundur",
                selectRitornello = "Pilih berapa banyak repetisi!",
                transpose = "Mengubah urutan",
                selectTranspositions = "Pilih transposisi!",
                doubling  = "Ganda",
                spreadWherePossible  = "Perluas jika memungkinkan",
                deepSearch  = "Pencarian mendalam dalam empat kanon suara",
                horIntervalSet = "Interval suara bebas",
                selectIntervalsForFP = "Pilih interval melodi dari suara bebas!",
                detector = "Detektor",
                selectIntervalsToDetect = "Pilih interval yang akan dideteksi!",
                detectorExtension = "Ekstensi detektor",
                selectDetectorExtension = "Pilih ekstensi detektor!",
                exportMidi  = "Ekspor file MIDI",
                language  = "Bahasa",
                zodiac = "Zodiak",
                zodiacOptions = listOf("Planet", "Tanda-tanda", "Emojis"),
                selectZodiac = "Gunakan simbol zodiak ini:"
            )
        }
        fun hindi(): Lang {
            return Lang(
                noteNames = NoteNamesIt.values().map { it.toString() },
                intervalSet = intervalSetIt,
                enterSomeNotes = "Digita delle note!",
                choose2ndSequence = "Scegli la seconda sequenza!",
                repeatSequence = "Ripeti la sequenza",
                selectEnsemble = "Scegli un ensemble!",
                ensembleNames = ensembleNamesIt,
                range = "Estensione",
                selectRange = "Scegli un'estensione!",
                rangeOptions = listOf("Libera", "Dello strumento", "Delimitata", "Quasi chiusa", "Chiusa"),
                selectMelody ="Seleziona un tipo di melodia!",
                melody = "Melodia",
                melodyOptions = listOf("Lineare","Ampia"),
                nuancesOptions = listOf("Nessuna", "In rilievo le note brevi", "In rilievo le note lunghe"),
                nuances = "Nuances",
                selectNuances = "Scegli le nuances per la dinamica!",
                beatsPerMinute = "Pulsazioni al minuto",
                selectRhythm = "Scegli un ritmo!",
                selectDoubling = "Scegli degli intervalli per il raddoppio!",
                doublingNames = doublingIt,
                rhythm = "Ritmo",
                rhythmShuffle  = "Mescola il ritmo",
                partsShuffle  = "Mescola le parti",
                rowForms = "Forme seriali",
                selectRowForms = "Scegli le forme seriali!",
                original = "Originale",
                retrograde  = "Retrogrado",
                inverse  = "Inverso",
                invRetrograde  = "Inverso del retrogrado",
                selectRitornello = "Scegli quante ripetizioni!",
                transpose = "Trasponi",
                selectTranspositions = "Scegli le trasposizioni!",
                doubling  = "Raddoppi",
                spreadWherePossible  = "Estendi dove è possibile",
                deepSearch  = "Ricerca approfondita nei canoni a quattro parti",
                horIntervalSet = "Intervalli delle parti libere",
                selectIntervalsForFP = "Scegli gli intervalli melodici per le fioriture e le parti libere!",
                detector = "Rilevatore",
                selectIntervalsToDetect = "Scegli gli intervalli da rilevare!",
                detectorExtension = "Raggio del rilevatore",
                selectDetectorExtension = "Scegli il raggio del rilevatore!",
                exportMidi  = "Esporta il file MIDI",
                language  = "Lingua",
                zodiac = "Zodiaco",
                zodiacOptions = listOf("Pianeti", "Segni", "Emojis"),
                selectZodiac = "Usa questi simboli zodiacali:"
            )
        }
    }
}
